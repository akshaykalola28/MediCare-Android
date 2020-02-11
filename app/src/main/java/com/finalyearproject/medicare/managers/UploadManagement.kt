package com.finalyearproject.medicare.managers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
object UploadManagement {

    const val UPLOAD_PATH_PROFILE = "profiles"
    const val UPLOAD_PATH_REPORT = "reports"

    fun uploadImageFromImageView(imageView: ImageView, path: String): UploadTask {
        val storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a child reference
        val imagesRef: StorageReference? = storageRef.child(path)

        val fileName = "${path}_${System.currentTimeMillis()}.jpg"
        val spaceRef = imagesRef!!.child(fileName)

        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()

        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return spaceRef.putBytes(data)
    }

    fun uploadImageFromUri(uri: Uri, path: String): UploadTask {
        val storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a child reference
        val imagesRef: StorageReference? = storageRef.child(path)

        val fileName = "${path}_${System.currentTimeMillis()}.jpg"
        val spaceRef = imagesRef!!.child(fileName)

        return spaceRef.putFile(uri)
    }
}