package com.example.fmaskdet

import android.app.Activity
import com.example.fmaskdet.R
import android.content.ContentUris
import android.content.Intent
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.min

//private const val REQUEST_CODE = 30
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageselect.setOnClickListener { // listener for the select image button
            ImagePicker.create(this) // .create for multiple uses
                .single() // only one image can be chosen
                .showCamera(true) // camera icon shown- easier way of adding camera to the button
                .start() // start the events
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { //imagePicker handling the requestCode, resultCode and data
            val selectedimage: Image? = ImagePicker.getFirstImageOrNull(data) //imagePicker only chooses the last image clicked- only one image

            if (selectedimage != null) { // if the value is not equal to 0 then:
                val store = ContentUris.withAppendedId( // allows camera to save picture into gallery in app to then select
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // allows image that has been selected to display
                    selectedimage.id
                )

                var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) //.P version for decoding - to use decodeBitmap
                {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, store))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, store)
                }

                bitmap = Bitmap. createScaledBitmap(bitmap, 500, (bitmap.height / (bitmap.width/500F)).toInt(), true)
                viewimage.setImageBitmap(bitmap)

                val inputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true )

                // Get a Paint instance
                val myRectPaint = Paint()
                myRectPaint.strokeWidth = 2F
                myRectPaint.style = Paint.Style.STROKE

                // Create a canvas using the dimensions from the image's bitmap
                val tempBitmap = createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.RGB_565)
                val tempCanvas = Canvas(tempBitmap)
                tempCanvas.drawBitmap(inputBitmap, 0F, 0F, null)

                // Create a FaceDetector
                val faceDetector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false)
                    .build()
                if (!faceDetector.isOperational) {
                    AlertDialog.Builder(this)
                        .setMessage("face detector failed!")
                        .show()
                    return
                }

                // Detect the faces
                val frame = Frame.Builder().setBitmap(inputBitmap).build()
                val faces = faceDetector.detect(frame)

                // Mark out the identified face
                for (i in 0 until faces.size()) {
                    val thisFace = faces.valueAt(i)
                    val left = thisFace.position.x
                    val top = thisFace.position.y
                    val right = left + thisFace.width
                    val bottom = top + thisFace.height
                    val bitmapCropped = Bitmap.createBitmap(inputBitmap,
                        left.toInt(),
                        top.toInt(),
                        if (right.toInt() > inputBitmap.width) {
                            inputBitmap.width - left.toInt()
                        } else {
                            thisFace.width.toInt()
                        },
                        if (bottom.toInt() > inputBitmap.height) {
                            inputBitmap.height - top.toInt()
                        } else {
                            thisFace.height.toInt()
                        })
                    val tag = predict(bitmapCropped)
                    var guess = ""
                    val mask = tag["Mask"]?: 0F // changed this
                    val nomask = tag["NoMask"]?: 0F // changed this

                    if (mask > nomask){ // changed this
                        myRectPaint.setColor(Color.GREEN)
                        guess = "Mask : " + String.format("%.1f", mask*100) + "%"
                    } else {
                        myRectPaint.setColor(Color.RED)
                        guess = "No Mask : " + String.format("%.1f", nomask*100) + "%"
                    }
                    myRectPaint.setTextSize(thisFace.width/8)
                    myRectPaint.setTextAlign(Align.LEFT)
                    tempCanvas.drawText(guess, left, top-9F, myRectPaint)
                    tempCanvas.drawRoundRect(RectF(left, top, right, bottom), 2F, 2F, myRectPaint)
                }
                viewimage.setImageDrawable(BitmapDrawable(resources, tempBitmap))
                // Release the FaceDetector
                faceDetector.release()
            }
        }
    }


    private fun predict(input: Bitmap): MutableMap<String, Float> {
        // load model
        val modelFile = FileUtil.loadMappedFile(this, "model.tflite")
        val model = Interpreter(modelFile, Interpreter.Options())
        val labels = FileUtil.loadLabels(this, "labels.txt")

        // data type
        val imageDataType = model.getInputTensor(0).dataType()
        val inputShape = model.getInputTensor(0).shape()

        val outputDataType = model.getOutputTensor(0).dataType()
        val outputShape = model.getOutputTensor(0).shape()

        var inputImageBuffer = TensorImage(imageDataType)
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType)

        // preprocess
        val cropSize = min(input.width, input.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(inputShape[1], inputShape[2], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()

        // load image
        inputImageBuffer.load(input)
        inputImageBuffer = imageProcessor.process(inputImageBuffer)

        // run model
        model.run(inputImageBuffer.buffer, outputBuffer.buffer.rewind())

        // get output
        val tagOutput = TensorLabel(labels, outputBuffer)

        val tag = tagOutput.mapWithFloatValue
        return tag
    }
}