package com.example.myapplication

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*
import androidx.core.content.FileProvider.*
import java.text.SimpleDateFormat

const val SELECTED_URI = "SELECTED_URI"

var CAMERA_SELECTED = false

const val FILE_REQUEST_CODE = 1
const val CAMERA_REQUEST_CODE = 2


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    public var selectedImgUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    selectedImgUri = Uri.parse(currentPhotoPath)
                    Log.d("ACTIVITY_URI", selectedImgUri.toString())
                    imageView.setImageURI(Uri.parse(currentPhotoPath))
                    toast("Resim Kaydedildi!  ${currentPhotoPath}")
                    CAMERA_SELECTED = true
                }
            }

            FILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    selectedImgUri = data!!.data
                    imageView.setImageURI(selectedImgUri);

                    toast("Resim seçildi! ${selectedImgUri.toString()}")
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized code", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getImgFromGallery(view: View){
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, FILE_REQUEST_CODE)
    }

    fun getImgFromCamera(view: View){
        dispatchTakePictureIntent()
    }

    lateinit var currentPhotoPath: String

     private  fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = getUriForFile(
                        getApplicationContext(),
                        "androidx.core.content.FileProvider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("CURRENT_PHOTO_PATH", currentPhotoPath)
        }
    }


    fun sendMessage(view: View) {
        // intend provides runtime binding between separate components
        var validChoose = false

        // when e cevir
        if (!compressCheck.isChecked && !segmantationCheck.isChecked){
            toast("Bir seçenek seç!")
        } else {
            validChoose = true
        }

        if (compressCheck.isChecked && segmantationCheck.isChecked){
            toast("Sadece birini seçebilirsin!")
        } else {
            validChoose = true
        }

        if (selectedImgUri != null &&  validChoose){
            val intent = Intent(this, SendFile::class.java).apply {
                //putExtra(EXTRA_MESSAGE , "Mesaj")
                var operation: String
                if (compressCheck.isChecked == true){
                    operation = "COMPRESSION"
                } else {
                    operation = "SEGMENTATION"
                }

                if(selectedImgUri != null) {
                    putExtra(SELECTED_URI, selectedImgUri.toString())
                    selectedImgUri = null
                }
                if (CAMERA_SELECTED == true){
                    putExtra("SELECTED_SOURCE","CAMERA")
                    putExtra("SELECTED_OPERATION", operation)
                    putExtra("COMPRESS_RATE", editCompressionRate.text.toString())
                    CAMERA_SELECTED = false
                } else {
                    putExtra("SELECTED_SOURCE","FILE")
                    putExtra("SELECTED_OPERATION", operation)
                    putExtra("COMPRESS_RATE", editCompressionRate.text.toString())

                }
            }
            startActivity(intent)
        }


        else {
            toast("Resim seçilmedi!")
        }
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}