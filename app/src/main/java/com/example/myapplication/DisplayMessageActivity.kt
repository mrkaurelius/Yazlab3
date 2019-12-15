package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.android.synthetic.main.activity_display_message.*


class DisplayMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val imgUri = Uri.parse(intent.getStringExtra(SELECTED_URI))


        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.textView).apply {
            text = message
        }
        Log.d("URI" ,imgUri.toString())
        if(imgUri != null){
            imageView2.setImageURI(imgUri)
        }


        // !!! burada resmi kendisini ve ozelliklerini goster
        // uri geçti
        //val myUri = Uri.parse(intent.getStringExtra("imageUri"))
        //Log.d("Param" , myUri.toString())
        // bu kaynagı apiye gonder
        // gelen sonucu nerede gor ?
        // yada onceki yerde gonder sonucu burada gor ?


    }
}
