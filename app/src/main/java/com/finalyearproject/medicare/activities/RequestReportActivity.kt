package com.finalyearproject.medicare.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.finalyearproject.medicare.BuildConfig
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.DoctorServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_request_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestReportActivity : AppCompatActivity() {

    var mRequestData: JsonObject = JsonObject()
    lateinit var requestInterface: DoctorServiceApi
    lateinit var userRequestInterface: AuthService
    lateinit var mDialog: AppProgressDialog

    private var patientId: String? = ""

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_report)

        mDialog = AppProgressDialog(this)
        //setDebugData()

        request_report_button.setOnClickListener {
            if (getValidData()) {
                mDialog.show("Report Requesting...")
                requestReportToServer()
            }
        }

        scan_barcode_surface.setOnClickListener {
            scanningView.startAnimation()
            cameraSource!!.start(scan_barcode_surface!!.holder)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDebugData() {
        if (BuildConfig.DEBUG) {
            patient_email_text.text =
                Editable.Factory.getInstance().newEditable("KCFmNbBZe5YZQYHhc2CPHzLAgZl2")
        }
    }

    private fun requestReportToServer() {
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(DoctorServiceApi::class.java)
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

    private fun initialiseDetectorsAndSources() {
        //Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1080, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        scan_barcode_surface!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                //Toast.makeText(application, "Created.", Toast.LENGTH_SHORT).show()
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@RequestReportActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //Toast.makeText(application, "Start.", Toast.LENGTH_SHORT).show()
                        cameraSource!!.start(scan_barcode_surface!!.holder)
                    } else {
                        Toast.makeText(application, "Permission Required..", Toast.LENGTH_SHORT)
                            .show()
                        ActivityCompat.requestPermissions(
                            this@RequestReportActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            1000
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode?> {
            override fun release() {
                /*Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()*/
                Log.d("SCANNER", "To prevent memory leaks barcode scanner has been stopped")
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode?>) {
                val barcodes: SparseArray<Barcode?>? = detections.detectedItems
                if (barcodes!!.size() != 0) {
                    patient_email_text.post {
                        patient_email_text.text = Editable.Factory.getInstance()
                            .newEditable(barcodes.valueAt(0)!!.displayValue)
                        patientId = barcodes.valueAt(0)!!.displayValue
                        cameraSource!!.stop()
                        scanningView.stopAnimation()
                        getUserData(patientId!!)
                    }
                }
            }
        })
    }

    private fun getValidData(): Boolean {
        return when {
            patientId!!.isEmpty() -> {
                Toast.makeText(this, "Scan the Qr code of patient", Toast.LENGTH_SHORT).show()
                false
            }
            report_title_edit_text.text.toString().isEmpty() -> {
                report_title_input_layout.error = "Enter valid report title."
                report_title_input_layout.requestFocus()
                false
            }
            report_desc_edit_text.text.toString().isEmpty() -> {
                report_desc_input_layout.error = "Enter valid report descrption."
                report_desc_input_layout.requestFocus()
                false
            }
            laboratory_email_edit_text.text.toString().isEmpty() -> {
                laboratory_email_input_layout.error = "Enter valid laboratory email."
                laboratory_email_input_layout.requestFocus()
                false
            }
            AppSharedPreference(this).getString(Constants.PREF_USER_TYPE) != "doctor" -> {
                Toast.makeText(this, "You're not a Doctor.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                mRequestData.addProperty("patientId", patientId)
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

    private fun getUserData(id: String) {
        mDialog.show("Getting Patient Info...")
        userRequestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(AuthService::class.java)
        val requestCall = userRequestInterface.getUserData(Constants.DB_USER_ID, id)
        requestCall.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                mDialog.dismiss()
                t.printStackTrace()
                Toast.makeText(
                    this@RequestReportActivity,
                    "Please try again...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                mDialog.dismiss()
                when {
                    response.isSuccessful -> {
                        val responseData: User = response.body()!!
                        user_name_text.text = "${responseData.firstName} ${responseData.lastName}"
                    }
                    response.code() == 401 -> {
                        Snackbar.make(
                            this@RequestReportActivity.currentFocus!!,
                            "Something is wrong.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            this@RequestReportActivity,
                            "Please try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(application, "Permission Granted", Toast.LENGTH_SHORT).show()
                        cameraSource!!.start(scan_barcode_surface!!.holder)
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(application, "Permission Denied.", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }
}
