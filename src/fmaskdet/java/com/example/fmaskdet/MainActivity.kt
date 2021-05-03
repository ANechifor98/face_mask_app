package com.example.fmaskdet

import android.app.Activity
import com.example.fmaskdet.R
import android.os.Build
import android.os.Bundle
import android.content.ContentUris
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide.with
import kotlinx.android.synthetic.main.activity_main.*
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.Frame
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.Interpreter
import kotlin.math.min

//private const val REQUEST_CODE = 30
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // listener for take photo button
        photoselect.setOnClickListener {
            ImagePicker.create(this) // jitpack.io
                    .showCamera(true) // camera icon shown- easier way of adding camera to the button
                    .single() // only one image can be chosen
                    .start() // start the events
        }
        // listener for select image button
        imageselect.setOnClickListener {
            ImagePicker.create(this)
                    .showCamera(false) // Don't need camera
                    .single()
                    .start()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { //imagePicker handling the requestCode, resultCode and data
            val selectedimage: Image? = ImagePicker.getFirstImageOrNull(data) //imagePicker only chooses the last image clicked- only one image

            if (selectedimage != null) { // if the value is not equal to 0 then:
                val store = ContentUris.withAppendedId( // allows camera to save picture into gallery in app to then select
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selectedimage.id
                )

                //bitmapversion image that gets passed through to be able to be called by Bitmap and set it in the ImageView
                var bitmapversion = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) //.P version for decoding - to use decodeBitmap
                {
                    MediaStore.Images.Media.getBitmap(contentResolver, store)
                } else {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, store)) // decode bitmap
                }

                // calling Bitmap to create scale and then set the updated bitmap to view in ImageView
                bitmapversion = Bitmap.createScaledBitmap(bitmapversion, 700, (bitmapversion.height / (bitmapversion.width/700F)).toInt(), true)
                viewimage.setImageBitmap(bitmapversion)

                // images' bitmap copied
                val currentBitmap = bitmapversion.copy(Bitmap.Config.ARGB_8888, true )

                // using bitmap above, make a canvas using the images' bitmap
                val bitmap1 = createBitmap(currentBitmap.width, currentBitmap.height, Bitmap.Config.RGB_565)
                val canvas1 = Canvas(bitmap1)
                canvas1.drawBitmap(currentBitmap, 0F, 0F, null) //draw bitmap of canvas using currentBitmap

                // Creating a face detector
                val myfacedetector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false)
                    .build()
                if (!myfacedetector.isOperational) { // if the face detector is not operational, send a message saying that it failed
                    AlertDialog.Builder(this)
                        .setMessage("The face detector failed!")
                        .show()
                    return
                }

                // Detect the faces in the bitmap of the image
                val aFrame = Frame.Builder().setBitmap(currentBitmap).build() // build a frame on currentBitmap(image)
                val face = myfacedetector.detect(aFrame) // value to detect faces

                // display the defined faces detected
                for (i in 0 until face.size()) {
                    val aFace = face.valueAt(i)
                    val leftofface = aFace.position.x
                    val rightofface = leftofface + aFace.width
                    val topofface = aFace.position.y
                    val bottomofface = topofface + aFace.height
                    val bitmapAdjusted = Bitmap.createBitmap(currentBitmap, leftofface.toInt(), topofface.toInt(),
                        if (rightofface.toInt() < currentBitmap.width) {
                            aFace.width.toInt()
                        } else { currentBitmap.width - leftofface.toInt() },
                        if (bottomofface.toInt() < currentBitmap.height) {
                            aFace.height.toInt()
                        } else { currentBitmap.height - topofface.toInt()})

                    // call Paint function
                    val paintIt = Paint()
                    paintIt.strokeWidth = 3F
                    paintIt.style = Paint.Style.STROKE // create a line box around the face

                    // call result class whether there is a mask or not
                    val tag = result(bitmapAdjusted)
                    var guess = ""
                    val mask = tag["Mask"]?: 0F
                    val nomask = tag["NoMask"]?: 0F

                    if (mask > nomask){
                        paintIt.setColor(Color.GREEN)
                        guess = "Mask : " + String.format("%.0f", mask*100) + "%"
                    } else {
                        paintIt.setColor(Color.RED)
                        guess = "No Mask : " + String.format("%.0f", nomask*100) + "%"
                    }
                    paintIt.setTextSize(aFace.width/7)
                    paintIt.setTextAlign(Align.CENTER)
                    canvas1.drawText(guess, leftofface, topofface-10F, paintIt)
                    canvas1.drawRoundRect(RectF(leftofface, topofface, rightofface, bottomofface), 2F, 2F, paintIt)
                }
                viewimage.setImageDrawable(BitmapDrawable(resources, bitmap1))
                // Release face detector
                myfacedetector.release()
            }
        }
    }

    // class to call tflite model
    private fun result(input: Bitmap): MutableMap<String, Float> {
        // load files
        val tfliteFile = FileUtil.loadMappedFile(this, "model.tflite")
        val tfmodel = Interpreter(tfliteFile, Interpreter.Options()) // Interpreter called on tflite model
        val labelsFile = FileUtil.loadLabels(this, "labels.txt")

        // type of data and shape of the images
        val datatypeOfInput = tfmodel.getInputTensor(0).dataType()
        val shapeOfInput = tfmodel.getInputTensor(0).shape()
        val datatypeOfOutput = tfmodel.getOutputTensor(0).dataType()
        val shapeOfOutput = tfmodel.getOutputTensor(0).shape()

        var bufferinput = TensorImage(datatypeOfInput)
        val bufferOutput = TensorBuffer.createFixedSize(shapeOfOutput, datatypeOfOutput)

        // pre-processing the image as integers
        val sizeAdjust = min(input.width, input.height)
        val processedImage = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(sizeAdjust, sizeAdjust))
            .add(ResizeOp(shapeOfInput[1], shapeOfInput[2], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()

        // load the image to apply the pre-processing
        bufferinput.load(input)
        bufferinput = processedImage.process(bufferinput)

        // run the model on the image
        tfmodel.run(bufferinput.buffer, bufferOutput.buffer.rewind())

        // receive results of the model
        val tagOutput = TensorLabel(labelsFile, bufferOutput)

        // return the output as a float
        val tag = tagOutput.mapWithFloatValue
        return tag
    }
}