package com.example.myfirstapp

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var btnImp: Button
    private lateinit var btnCmp: Button
    private lateinit var btnTrash: Button
    private lateinit var btnDwn: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null


    companion object {
        const val IMAGE_REQUEST_CODE = 1
        const val requestCode = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnImp = findViewById<Button>(R.id.btnImp)
        imageView = findViewById<ImageView>(R.id.image_view)
        btnTrash = findViewById<Button>(R.id.btnTrash)
        btnCmp = findViewById<Button>(R.id.btnCmp)
        btnDwn = findViewById<Button>(R.id.btnDwn)


        btnTrash.visibility = View.GONE


        btnImp.setOnClickListener() { //Con questo metodo importo l'immagine dalla galleria

            pickImageGallery()

        }

        btnCmp.setOnClickListener {

            if (imageUri != null) {

                val drawable = imageView.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                val compressedImage = compressionImage(bitmap, 80)
                val compressedBitmap = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)
                imageView.setImageBitmap(compressedBitmap)
                Toast.makeText(this, "Immagine correttamente compressa", Toast.LENGTH_SHORT).show()


            } else {

                Toast.makeText(this, "Importa un'immagine", Toast.LENGTH_SHORT).show()

            }
        }

        btnTrash.setOnClickListener {

            if (imageUri == null){

                Toast.makeText(this, "Importa un'immagine", Toast.LENGTH_SHORT).show()

            }else{

                imageView.setImageDrawable(null)
                imageUri = null
                Toast.makeText(this, "Immagine rimossa", Toast.LENGTH_SHORT).show()

            }

        }

        btnDwn.setOnClickListener {

            if (imageUri != null) {

                dwnImage()

            } else {

                Toast.makeText(this, "Importa un'immagine", Toast.LENGTH_SHORT).show()

            }

        }





    }

    private fun pickImageGallery() {

        try {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)

        }catch (e: java.lang.Exception){

            Toast.makeText(this, "Immagine non supportata!", Toast.LENGTH_SHORT).show()

        }

    }

   fun compressionImage(sourceBitmap: Bitmap, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        sourceBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }





    private fun dwnImage() {

        if (imageUri != null) {

            val resolver = applicationContext.contentResolver
            val source = MediaStore.Images.Media.getBitmap(resolver, imageUri)
            val fileName = "imgCmp_${System.currentTimeMillis()}.jpeg"

            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/MyApp")
            }

            val uri = resolver.insert(contentUri, contentValues)

            uri?.let { targetUri ->
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    source.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    Toast.makeText(this, "Immagine salvata nella galleria", Toast.LENGTH_SHORT).show()

                }

            }

            }else{

            Toast.makeText(this, "Importa un'immagine", Toast.LENGTH_SHORT).show()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
            btnTrash.visibility = View.VISIBLE

        }

    }

}












