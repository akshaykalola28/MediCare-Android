package com.finalyearproject.medicare.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUserBarcode()

        user_name_text.text = AppSharedPreference(this).getString(Constants.PREF_USER_NAME)
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
