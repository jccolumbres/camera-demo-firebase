package application.ayannah.com.camera_demo_firebase

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log


class MainActivity : AppCompatActivity() {
    private var PICK_IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init firebase
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        btnSelect.setOnClickListener {
            imageSelector()
        }
    }


    fun imageSelector() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    fun uploadImage() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            filePath = data.data
            try {
                //open gallery to select image
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val bos = ByteArrayOutputStream()
                //scalesize of bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 1, bos)
                val bitmapdata = bos.toByteArray()
                //bytearray to bitmap
                val bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
                val lengthbmp = bmp.byteCount
                Log.d("SIZE",lengthbmp.toString())
                //display to image view

                imageView.setImageBitmap(bmp
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
