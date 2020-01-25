package com.finalyearproject.medicare.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.BuildConfig
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.retrofit.DoctorServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_request_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestReportActivity : AppCompatActivity() {

    var mRequestData: JsonObject = JsonObject()
    lateinit var requestInterface: DoctorServiceApi
    lateinit var mDialog: AppProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_report)

        mDialog = AppProgressDialog(this)
        setDebugData()

        request_report_button.setOnClickListener {
            if (getValidData()) {
                mDialog.show()
                requestReportToServer()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDebugData() {
        if (BuildConfig.DEBUG) {
            patient_email_edit_text.text =
                Editable.Factory.getInstance().newEditable("KCFmNbBZe5YZQYHhc2CPHzLAgZl2")
        }
    }

    private fun requestReportToServer() {
        requestInterface = ServiceBuilder.buildService(DoctorServiceApi::class.java)
        val serverCall = requestInterface.requestReport(mRequestData)
        serverCall.enqueue(object : Callback<ResponseModel> {
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                mDialog.dismiss()
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                mDialog.dismiss()
                if (response.isSuccessful) {
                    val resData = response.body()
                    Toast.makeText(
                        this@RequestReportActivity,
                        resData!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (response.code() == 409) {
                    var gson = GsonBuilder().create()
                    val resData: ResponseModel =
                        gson.fromJson(response.errorBody()!!.string(), ResponseModel::class.java)
                    Toast.makeText(
                        this@RequestReportActivity,
                        resData.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun getValidData(): Boolean {
        return when {
            patient_email_edit_text.text.toString().isEmpty() -> {
                patient_email_input_layout.error = "Enter valid patient id."
                patient_email_input_layout.requestFocus()
                false
            }
            report_title_edit_text.text.toString().isEmpty() -> {
                report_title_input_layout.error = "Enter valid patient id."
                report_title_input_layout.requestFocus()
                false
            }
            report_desc_edit_text.text.toString().isEmpty() -> {
                report_desc_input_layout.error = "Enter valid patient id."
                report_desc_input_layout.requestFocus()
                false
            }
            laboratory_email_edit_text.text.toString().isEmpty() -> {
                laboratory_email_input_layout.error = "Enter valid patient id."
                laboratory_email_input_layout.requestFocus()
                false
            }
            AppSharedPreference(this).getString(Constants.PREF_USER_TYPE) != "doctor" -> {
                Toast.makeText(this, "You're not Doctor.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                mRequestData.addProperty("patientId", patient_email_edit_text.text.toString())
                mRequestData.addProperty(
                    "doctorId",
                    AppSharedPreference(this).getString(Constants.PREF_USER_ID)
                )
                mRequestData.addProperty(
                    "doctorName",
                    AppSharedPreference(this).getString(Constants.PREF_USER_NAME)
                )
                mRequestData.addProperty("reportTitle", report_title_edit_text.text.toString())
                mRequestData.addProperty("reportDescription", report_desc_edit_text.text.toString())
                mRequestData.addProperty(
                    "laboratoryEmail",
                    laboratory_email_edit_text.text.toString()
                )
                mRequestData.addProperty("collectingStatus", "pending")

                true
            }
        }
    }
}
