package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUserBarcode()

        user_name_text.text = AppSharedPreference(this).getString(Constants.PREF_USER_NAME)

        if (AppSharedPreference(this).getString(Constants.PREF_USER_PROFILE_URL) != "") {
            profile_image.visibility = View.VISIBLE
            Picasso.get()
                .load(AppSharedPreference(this).getString(Constants.PREF_USER_PROFILE_URL))
                .into(profile_image)
        }
    }

    private fun setUserBarcode() {

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(
                AppSharedPreference(this).getString(Constants.PREF_USER_ID),
                BarcodeFormat.QR_CODE,
                500,
                500
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            //Set code to ImageView
            qr_code.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
