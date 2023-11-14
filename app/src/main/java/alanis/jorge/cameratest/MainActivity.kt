package alanis.jorge.cameratest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment

import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    lateinit var camera: Button
    lateinit var gallery: Button
    lateinit var imageView: ImageView
    lateinit var result: TextView

    private val imageSize = 32

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera = findViewById(R.id.button)
        gallery = findViewById(R.id.button2)
        result = findViewById(R.id.result)
        imageView = findViewById(R.id.imageView)

        // Paso 1, llamar al camaera click listener/intent
        camera.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        //Paso 4, mandar llamar la galeria para que se abra
        gallery.setOnClickListener {
            val cameraIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        }
    }

    // Paso 2, mandar llamar la funcion on activity result, procesar automaticamente la imagen y guardamos la imagen en un archivo y la agregamos a la galeria
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 3) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                // Aquí guardas la imagen en un archivo
                val savedImageFile = saveImageToFile(imageBitmap)

                // Agrega la imagen a la galería
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(savedImageFile.absolutePath),
                    null
                ) { path, uri ->
                    if (uri == null) {
                        // Manejo del caso en que uri es null (por ejemplo, mostrar un mensaje de error)
                    } else {
                        // Aquí puedes manejar el resultado si lo necesitas
                    }
                }
            }
        }
    }

    //Paso 3, El bitmap lo transformamos a un archivo jpg

    private fun saveImageToFile(bitmap: Bitmap): File {
        val filename = "captured_image_${System.currentTimeMillis()}.jpg"
        val publicDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!publicDir.exists()) {
            publicDir.mkdirs()
        }
        val file = File(publicDir, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return file
    }
}