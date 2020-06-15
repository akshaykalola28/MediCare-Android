package com.finalyearproject.medicare.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.akshaykalola.skydialog.SkyDialog
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.FragmentPagerAdapter
import com.finalyearproject.medicare.fragments.ReportListFragment
import com.finalyearproject.medicare.fragments.TreatmentListFragment
import com.finalyearproject.medicare.helpers.AppAlerts
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.helpers.responseErrorHandlerOfCode400
import com.finalyearproject.medicare.models.HistoryModel
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.AuthService
import com.finalyearproject.medicare.retrofit.DoctorServiceApi
import com.finalyearproject.medicare.retrofit.PatientServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_check_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckHistoryActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var requestInterface: DoctorServiceApi
    private lateinit var patientInterface: PatientServiceApi
    private lateinit var userRequestInterface: AuthService
    lateinit var mDialog: SkyDialog

    private var patientId: String? = ""

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_history)

        mDialog = SkyDialog(this)

        scan_barcode_surface.setOnClickListener {
            scanningView.startAnimation()
            cameraSource!!.start(scan_barcode_surface!!.holder)
        }
    }

    fun getHistoryFromServer(isPatient: Boolean) {
        mDialog.show()
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(DoctorServiceApi::class.java)
        patientInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(PatientServiceApi::class.java)

        val serverCall = if (isPatient)
            patientInterface.checkHistoryOfPatient(AppSharedPreference(this).getString(Constants.PREF_USER_ID))
        else
            requestInterface.checkHistoryOfPatient(patientId!!)

        serverCall.enqueue(object : Callback<HistoryModel> {
            override fun onFailure(call: Call<HistoryModel>, t: Throwable) {
                mDialog.dismiss()
                AppAlerts().showAlertMessage(
                    this@CheckHistoryActivity,
                    "Error",
                    "Something is Wrong!"
                )
            }

            override fun onResponse(call: Call<HistoryModel>, response: Response<HistoryModel>) {
                mDialog.dismiss()
                when (response.code()) {
                    200 -> {
                        val resData = response.body()!!

                        qr_code_relative_layout.visibility = View.GONE

                        val adapter = FragmentPagerAdapter(supportFragmentManager)
                        if (!resData.treaments.isNullOrEmpty())
                            adapter.addFragment(
                                TreatmentListFragment(resData.treaments),
                                "Treatments"
                            )
                        else
                            Toast.makeText(
                                this@CheckHistoryActivity,
                                "Treatment not found.",
                                Toast.LENGTH_SHORT
                            ).show()

                        if (!resData.reports.isNullOrEmpty())
                            adapter.addFragment(ReportListFragment(resData.reports), "Reports")
                        else
                            Toast.makeText(
                                this@CheckHistoryActivity,
                                "Treatment not found.",
                                Toast.LENGTH_SHORT
                            ).show()

                        view_pager.adapter = adapter
                        tab_layout.setupWithViewPager(view_pager)
                    }
                    else -> {
                        responseErrorHandlerOfCode400(
                            this@CheckHistoryActivity,
                            response.errorBody()!!.string()
                        )
                    }
                }
            }
        })
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
                            this@CheckHistoryActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(scan_barcode_surface!!.holder)
                    } else {
                        Toast.makeText(application, "Permission Required..", Toast.LENGTH_SHORT)
                            .show()
                        ActivityCompat.requestPermissions(
                            this@CheckHistoryActivity,
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
                    user_name_text.post {
                        user_name_text.text = Editable.Factory.getInstance()
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
                    this@CheckHistoryActivity,
                    "Please try again...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<User>, response: Response<User>) {
                mDialog.dismiss()
                when {
                    response.isSuccessful -> {
                        name_layout.visibility = View.VISIBLE
                        val responseData: User = response.body()!!
                        user_name_text.text = "${responseData.firstName} ${responseData.lastName}"
                        getHistoryFromServer(isPatient = false)
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
                            this@CheckHistoryActivity,
                            "Please try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (cameraSource != null)
            cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        if (AppSharedPreference(this).getString(Constants.PREF_USER_TYPE) == Constants.USER_PATIENT) {
            qr_code_relative_layout.visibility = View.GONE
            getHistoryFromServer(true)
        } else {
            initialiseDetectorsAndSources()
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        view_pager.currentItem = tab!!.position
    }
}
