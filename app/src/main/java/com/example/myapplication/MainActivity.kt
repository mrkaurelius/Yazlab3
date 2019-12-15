package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.log
import java.nio.file.Files.size
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




const val EXTRA_MESSAGE = "com.example.myapplication.MESSAGE"
const val SELECTED_URI = "SELECTED_URI"

const val FILE_REQUEST_CODE = 1
const val CAMERA_REQUEST_CODE = 2


class MainActivity : AppCompatActivity() {
    // MainActivity is entry-point for app
    // !!! izinlerde problem var
    // !!! kodu uniform bir hale getir


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    public var selectedImgUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null){
                    val img = data.extras?.get("data") as Bitmap?
                    //imageView.setImageBitmap(img)

                    val wrapper = ContextWrapper(applicationContext)

                    var file = wrapper.getDir("images", Context.MODE_PRIVATE)
                    // file system olayını anla
                    file = File(file, "${UUID.randomUUID()}.jpg")

                    try {
                        val stream: OutputStream = FileOutputStream(file)
                        // daha iyi bir null check ne olabilir
                        if (img != null) {
                            img.compress(Bitmap.CompressFormat.WEBP, 100 ,stream)
                        }
                        stream.flush()
                        stream.close()
                        selectedImgUri = Uri.parse(file.absolutePath)
                        toast("Image saved!  ${file.absolutePath}")
                    } catch (e: IOException) {
                        e.printStackTrace()
                        toast("Error to save image!")
                    }
                }
            }

            FILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    selectedImgUri = data!!.data
                    imageView.setImageURI(selectedImgUri);
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
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }


    fun sendMessage(view: View) {
        //val editText = findViewById<EditText>(R.id.editText)
        //val message = editText.text.toString()
        // intend provides runtime binding between separate components
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE , "Mesaj")
            if(selectedImgUri != null) {
                putExtra(SELECTED_URI, selectedImgUri.toString())
            }
        }
        startActivity(intent)
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}