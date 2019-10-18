package application.ayannah.com.camera_demo_firebase

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


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
        btnUpload.setOnClickListener {
            uploadImage()
        }
    }


    fun imageSelector() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading")
            progressDialog.show()

            val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapShot ->
                    val progress =
                        100.0 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                }
        }
    }

    //Control what happens when an image is selected
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos)
                val bitmapdata = bos.toByteArray()
                //bytearray to bitmap
                val bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
                val lengthbmp = bmp.byteCount
                Log.d("SIZE", lengthbmp.toString())
                //display to image view

                imageView.setImageBitmap(
                    bmp
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
