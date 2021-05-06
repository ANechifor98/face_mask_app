# type into terminal python train_mask_detector.py --dataset dataset
# import the necessary packages
import os
import argparse
import unittest
import sklearn
import nltk
import numpy as nump
from imutils import paths
from sklearn.preprocessing import LabelBinarizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.preprocessing.image import img_to_array
from tensorflow.keras.preprocessing.image import load_img
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.layers import AveragePooling2D
from tensorflow.keras.layers import Dropout
from tensorflow.keras.layers import Flatten
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import Input
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input

# set a initial_learning_rate, amount of cycles to train for and the size of the batch
initial_learn_rate = 0.0001
cycles = 2
size_of_batch = 32

# create arg parser, then parse args
arg_parser = argparse.ArgumentParser()
arg_parser.add_argument("-d", "--dataset")
arg_parser.add_argument("-m", "--model", default = "face_mask_detector.model")
arguments = vars(arg_parser.parse_args())

# get pictures from dataset directory and make a list of images w/ their classes
print("LOADING IMAGES")
image_paths = list(paths.list_images(arguments["dataset"]))
data = []
class_labels = []

# iterate through each path
for image_path in image_paths:
	# get class from the class folder name, load image as 224x224px picture and preprocess picture
	class_label = image_path.split(os.path.sep)[-2]
	image = load_img(image_path, target_size = (224, 224))
	image = img_to_array(image)
	image = preprocess_input(image)

	# update class_labels lists + data
	data.append(image)
	class_labels.append(class_label)

# conversion of data + class_labels to NumPy array, encode class_labels
data = nump.array(data, dtype = "float32")
class_labels = nump.array(class_labels)
lb = LabelBinarizer()
class_labels = lb.fit_transform(class_labels)
class_labels = to_categorical(class_labels)

# create img_data_generator
img_data_generator = ImageDataGenerator(rotation_range = 20, zoom_range = 0.15, width_shift_range = 0.2,
	height_shift_range = 0.2, shear_range = 0.15, horizontal_flip = True, fill_mode = "nearest")

# split train and test data 85/15
(train_x, test_x, train_y, test_y) = train_test_split(data, class_labels, test_size = 0.15, stratify = class_labels)

# create base_model
base_model = MobileNetV2(weights = "imagenet", include_top = False, input_tensor = Input(shape = (224, 224, 3)))

# create head_of_model
head_of_model = base_model.output
head_of_model = AveragePooling2D(pool_size = (7, 7))(head_of_model)
head_of_model = Flatten(name = "flatten")(head_of_model) #flatten input layer so it is possible to each neuron
head_of_model = Dense(128, activation = "relu")(head_of_model) #rectify linear unit, 128 neurons
head_of_model = Dropout(0.5)(head_of_model) # 
head_of_model = Dense(2, activation = "softmax")(head_of_model) #2 neuron output layer, pick values for each neuron, probability that the network thinks it is a certain value so it adds up to 1

# put the head_of_model on top of base (this creates the model to be trained)
model = Model(inputs = base_model.input, outputs = head_of_model)

# stop base_model from being trained during initial training loop only
for layer in base_model.layers:
	layer.trainable = False

# compilation of model, create optimizer based on Adam algorithm
print("COMPILING MODEL")
optimizer = Adam(lr = initial_learn_rate, decay = initial_learn_rate / cycles)
model.compile(loss = "binary_crossentropy", optimizer = optimizer, metrics = ["accuracy"])

# training of model
print("TRAINING")
H = model.fit(img_data_generator.flow(train_x, train_y, batch_size = size_of_batch), steps_per_epoch = len(train_x) // size_of_batch,
	validation_data = (test_x, test_y), validation_steps = len(test_x) // size_of_batch, epochs = cycles) 

# create predictions
print("TESTING ACCURACY")
predictions = model.predict(test_x, batch_size = size_of_batch)

# for every image in testing data, select the index of label with highest predicted probability
predictions = nump.argmax(predictions, axis = 1)

# print classification report
print(classification_report(test_y.argmax(axis = 1), predictions, target_names = lb.classes_))

# save current facial recognition model
print("STORING CURRENT FACIAL RECOGNITION MODEL")
model.save(arguments["model"], save_format = "h5")
