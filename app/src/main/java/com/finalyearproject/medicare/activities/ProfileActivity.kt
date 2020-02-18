package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private var requestInterface: AuthService? = null
    private lateinit var mDialog: AppProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUserBarcode()

        user_name_text.text = AppSharedPreference(this).getString(Constants.PREF_USER_NAME)

        mDialog = AppProgressDialog(this)
        mDialog.show()
        getUserData()
    }

    private fun getUserData() {
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(AuthService::class.java)
        val requestCall =
            requestInterface!!.getUserData(
                Constants.DB_EMAIL,
                AppSharedPreference(this).getString(Constants.PREF_USER_EMAIL)
            )
        requestCall.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                mDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(
                    this@ProfileActivity,
                    "Please try again...",
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                mDialog.dismiss()
                when {
                    response.isSuccessful -> {
                        val responseData: User = response.body()!!

                        account_header_text.visibility = View.VISIBLE
                        user_name_text.text = "${responseData.firstName} ${responseData.lastName}"
                        user_number_text.text = responseData.phoneNumber
                        user_email_text.text = responseData.email
                        if (responseData.profileUrl != "") {
                            profile_image.visibility = View.VISIBLE
                            Picasso.get()
                                .load(responseData.profileUrl)
                                .into(profile_image)
                        }
                        user_type_text.text = responseData.user_type
                    }
                    response.code() == 401 -> {
                        Snackbar.make(
                            this@ProfileActivity.currentFocus!!,
                            "Something is wrong.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Please try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
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
