package com.finalyearproject.medicare.activities

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.MedicineAdapter
import com.finalyearproject.medicare.helpers.*
import com.finalyearproject.medicare.models.Medicine
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.models.Treatment
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.DoctorServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_add_treatment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTreatmentActivity : AppCompatActivity() {

    lateinit var mTreatment: Treatment
    lateinit var requestInterface: DoctorServiceApi
    private lateinit var userRequestInterface: AuthService
    lateinit var mDialog: AppProgressDialog

    private val mMedicineList = ArrayList<Medicine>()
    private val mMedicineAdapter = MedicineAdapter(this@AddTreatmentActivity, mMedicineList)

    private var patientId: String? = ""
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_treatment)

        mDialog = AppProgressDialog(this)

        initView()
    }

    private fun initView() {
        scan_barcode_surface.setOnClickListener {
            scanningView.startAnimation()
            cameraSource!!.start(scan_barcode_surface!!.holder)
        }

        medicine_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AddTreatmentActivity)
            adapter = mMedicineAdapter
        }

        val validDose = arrayListOf<String>(
            "0001",
            "0010",
            "0100",
            "1000",
            "0011",
            "0110",
            "1100",
            "1001",
            "1010",
            "0101",
            "0111",
            "1110",
            "1101",
            "1011",
            "1111"
        )

        add_medicine_image_view.setOnClickListener {
            medicine_name_layout.isErrorEnabled = false
            medicine_dose_layout.isErrorEnabled = false
            medicine_time_layout.isErrorEnabled = false

            when {
                medicine_name_edit_text.text.toString().isEmpty() -> {
                    medicine_name_layout.error = "Enter medicine name"
                    medicine_name_edit_text.requestFocus()
                }
                !validDose.contains(medicine_dose_edit_text.text.toString()) -> {
                    medicine_dose_layout.error = "Enter valid dose"
                    medicine_dose_edit_text.requestFocus()
                }
                medicine_time_edit_text.text.toString().isEmpty() -> {
                    medicine_time_layout.error = "Enter medicine time"
                    medicine_time_edit_text.requestFocus()
                }
                else -> {
                    mMedicineList.add(
                        Medicine(
                            medicine_name_edit_text.text.toString(),
                            medicine_dose_edit_text.text.toString(),
                            medicine_time_edit_text.text.toString()
                        )
                    )
                    medicine_name_edit_text.text!!.clear()
                    medicine_dose_edit_text.text!!.clear()
                    medicine_time_edit_text.text!!.clear()
                    medicine_name_edit_text.requestFocus()
                    mMedicineAdapter.notifyDataSetChanged()
                }
            }
        }

        add_treatment_to_server_button.setOnClickListener {
            if (getValidData()) {
                AppAlerts().showAlertWithAction(
                    this,
                    "Confirm",
                    "Do you really want to add treatment?",
                    "Yes",
                    "Edit Something!",
                    DialogInterface.OnClickListener { dialog, which ->
                        addTreatmentToServer()
                    },
                    true
                )
            }
        }
    }

    private fun addTreatmentToServer() {
        mDialog.show("Treatment Adding...")
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(DoctorServiceApi::class.java)
        val serverCall = requestInterface.addTreatment(mTreatment)
        serverCall.enqueue(object : Callback<ResponseModel> {
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                mDialog.dismiss()
                AppAlerts().showAlertMessage(
                    this@AddTreatmentActivity,
                    "Error",
                    "Something is Wrong!"
                )
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                mDialog.dismiss()
                when (response.code()) {
                    200 -> {
                        val resData = response.body()
                        AppAlerts().showAlertMessage(
                            this@AddTreatmentActivity,
                            "",
                            resData!!.message.toString()
                        )
                    }
                    else -> {
                        responseErrorHandlerOfCode400(
                            this@AddTreatmentActivity,
                            response.errorBody()!!.string()
                        )
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
            treatment_title_edit_text.text.toString().isEmpty() -> {
                treatment_title_input_layout.error = "Enter valid treatment title."
                treatment_title_input_layout.requestFocus()
                false
            }
            treatment_desc_edit_text.text.toString().isEmpty() -> {
                treatment_desc_input_layout.error = "Enter valid treatment details."
                treatment_desc_input_layout.requestFocus()
                false
            }
            medical_store_edit_text.text.toString().isEmpty() -> {
                medical_store_input_layout.error = "Enter valid laboratory email."
                medical_store_input_layout.requestFocus()
                false
            }
            AppSharedPreference(this).getString(Constants.PREF_USER_TYPE) != Constants.USER_DOCTOR -> {
                Toast.makeText(this, "You're not a Doctor.", Toast.LENGTH_SHORT).show()
                false
            }
            mMedicineList.size == 0 -> {
                Toast.makeText(this, "Please add some Medicine.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                mTreatment = Treatment(
                    patientId!!,
                    AppSharedPreference(this).getString(Constants.PREF_USER_ID),
                    AppSharedPreference(this).getString(Constants.PREF_USER_NAME),
                    treatment_title_edit_text.text.toString(),
                    treatment_desc_edit_text.text.toString(),
                    mMedicineList,
                    "",
                    medical_store_edit_text.text.toString(),
                    Constants.STATUS_PENDING
                )
                true
            }
        }
    }

    private fun initialiseDetectorsAndSources() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1080, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        scan_barcode_surface!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@AddTreatmentActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(scan_barcode_surface!!.holder)
                    } else {
                        Toast.makeText(application, "Permission Required..", Toast.LENGTH_SHORT)
                            .show()
                        ActivityCompat.requestPermissions(
                            this@AddTreatmentActivity,
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
                    this@AddTreatmentActivity,
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
                            window.decorView.findViewById(android.R.id.content),
                            "User not Found or Invalid QR code.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        //Restart QR scanner
                        scanningView.startAnimation()
                        cameraSource!!.start(scan_barcode_surface!!.holder)
                    }
                    else -> {
                        Toast.makeText(
                            this@AddTreatmentActivity,
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
