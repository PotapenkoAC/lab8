package com.example.lab_8_camera

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : Activity() {

    private val REQUEST_CODE_PHOTO = 1
    private val REQUEST_CODE_VIDEO = 2
    private lateinit var currentPhotoPath: String
    private val TAG = "myLogs"

    private lateinit var ivPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivPhoto = findViewById(R.id.ivPhoto)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (ContextCompat.checkSelfPermission(
                this,
                permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission.CAMERA), 50)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(this, arrayOf(permission.WRITE_EXTERNAL_STORAGE), 50)

    }


    fun onClickPhoto(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_PHOTO)
                }
            }
        }
    }

    fun onClickVideo(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_VIDEO)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        intent: Intent?
    ) {
        Log.d("SHIT", "Start onActivityResult")
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == -1) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null")
                } else {
                    val bndl = intent.extras
                    if (bndl != null) {
                        val obj = intent.extras!!.get("data")
                        if (obj is Bitmap) {
                            val bitmap = obj as Bitmap?
                            ivPhoto.setImageBitmap(bitmap)
                            Log.d(
                                TAG, "bitmap " + bitmap!!.width + " x "
                                        + bitmap.height
                            )
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled")
            }
        }

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null")
                } else {
                    Log.d(TAG, "Video uri: " + intent.data!!)
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled")
            }
        }
    }


    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

}