package com.example.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.*
import android.os.Environment
import android.util.Base64
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.toolbox.HttpHeaderParser
import kotlinx.android.synthetic.main.activity_display_message.*
import java.nio.charset.Charset


class SendFile : AppCompatActivity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val imgUri = Uri.parse(intent.getStringExtra(SELECTED_URI))
        val selectedTag = intent.getStringExtra("SELECTED_SOURCE")
        val selectedOperation = intent.getStringExtra("SELECTED_OPERATION")
        val compressRate = intent.getStringExtra("COMPRESS_RATE").toInt()

        // assagidaki senaryoların testlerini yap

        var isCameraSelected = false
        var isCompressionSelected = false

        if (selectedTag.equals("CAMERA")) {
            isCameraSelected = true
        }

        if (selectedOperation.equals("COMPRESSION")) {
            isCompressionSelected = true
        }

        val encodedImage: String

        if (isCameraSelected){
            val file = File(imgUri.path)
            //val bytes = loadFile(fileNamfilee)
            val fileBytes = file.readBytes()
            val encoded = Base64.encodeToString(fileBytes, Base64.DEFAULT)
            encodedImage = encoded
            //Log.d("BASE64", encoded)

        } else {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp.jpg")
            val fos = FileOutputStream(file)

            val `is` = getApplicationContext().getContentResolver().openInputStream(imgUri)
            val buffer = ByteArray(1024)
            var len = 0
            try {
                if (`is` != null) {
                    len = `is`.read(buffer)
                }
                while (len != -1) {
                    fos.write(buffer, 0, len)
                    if (`is` != null) {
                        len = `is`.read(buffer)
                    }
                }

                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val fileBytes = file.readBytes()
            val encoded = Base64.encodeToString(fileBytes, Base64.DEFAULT)
            encodedImage = encoded
            //Log.d("BASE64", encoded)

        }

        val httpResponse = findViewById<TextView>(R.id.httpResponse).apply {
            //text = "Lambda Response ${lambdaResponse}"
            //Log.d("BASE64",encodedImage)
        }

        // make http requests
        if (isCompressionSelected){
            val url = "http://34.229.7.129/compress"
            try {
                val requestQueue = Volley.newRequestQueue(this)
                val jsonBody = JSONObject()
                jsonBody.put("base64_string", encodedImage)
                jsonBody.put("compress_rate", compressRate)
                val mRequestBody = jsonBody.toString()

                val stringRequest = object : StringRequest(Request.Method.POST, url,
                        Response.Listener { response -> Log.i("LOG_RESPONSE", response)
                        val encodedBase64response = Base64.decode(JSONObject(response) .getString("base64_ret"), Base64.DEFAULT)
                        val responseBitmap = BitmapFactory.decodeByteArray(encodedBase64response, 0 ,encodedBase64response.size)

                        httpResponse.text = responseBitmap.byteCount.toString()
                        imageViewResult.setImageBitmap(responseBitmap)

                    },
                    Response.ErrorListener { error -> Log.e("LOG_RESPONSE", error.toString()) }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray? {
                        try {
                            return mRequestBody?.toByteArray(charset("utf-8"))
                        } catch (uee: UnsupportedEncodingException) {
                            VolleyLog.wtf(
                                "Unsupported Encoding while trying to get the bytes of %s using %s",
                                mRequestBody,
                                "utf-8"
                            )
                            return null
                        }

                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                        var responseString = ""
                        if (response != null) {
                            responseString = response.statusCode.toString()
                        }

                        val json = String(
                            response?.data ?: ByteArray(0)
                        )

                        return Response.success(
                            json,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    }
                }

                stringRequest.setRetryPolicy(
                    DefaultRetryPolicy(
                        20000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                requestQueue.add(stringRequest)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {

            val url = "http://34.229.7.129/segmentation"
            try {
                val requestQueue = Volley.newRequestQueue(this)
                val jsonBody = JSONObject()
                jsonBody.put("base64_string", encodedImage)
                val mRequestBody = jsonBody.toString()

                val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener { response -> Log.i("LOG_RESPONSE", response)
                        val encodedBase64response = Base64.decode(JSONObject(response) .getString("base64_ret"), Base64.DEFAULT)
                        val responseBitmap = BitmapFactory.decodeByteArray(encodedBase64response, 0 ,encodedBase64response.size)

                        httpResponse.text = responseBitmap.byteCount.toString()
                        imageViewResult.setImageBitmap(responseBitmap)

                    },
                    Response.ErrorListener { error -> Log.e("LOG_RESPONSE", error.toString()) }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    @Throws(AuthFailureError::class)
                    override fun getBody(): ByteArray? {
                        try {
                            return mRequestBody?.toByteArray(charset("utf-8"))
                        } catch (uee: UnsupportedEncodingException) {
                            VolleyLog.wtf(
                                "Unsupported Encoding while trying to get the bytes of %s using %s",
                                mRequestBody,
                                "utf-8"
                            )
                            return null
                        }

                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                        var responseString = ""
                        if (response != null) {
                            responseString = response.statusCode.toString()
                        }

                        val json = String(
                            response?.data ?: ByteArray(0)
                        )

                        return Response.success(
                            json,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    }
                }

                stringRequest.setRetryPolicy(
                        DefaultRetryPolicy(
                            30000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                    )
                requestQueue.add(stringRequest)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
