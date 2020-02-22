package com.finalyearproject.medicare.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.ReportAdapter
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.helpers.responseErrorHandlerOfCode400
import com.finalyearproject.medicare.managers.UploadManagement
import com.finalyearproject.medicare.managers.UserManagement
import com.finalyearproject.medicare.models.ReportModel
import com.finalyearproject.medicare.models.ResponseModel
import com.finalyearproject.medicare.retrofit.LabServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_lab_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


class LabHomeActivity : AppCompatActivity(), ReportAdapter.ReportCallbackInterface {

    private lateinit var requestInterface: LabServiceApi
    private lateinit var mDialog: AppProgressDialog
    lateinit var addReportData: JsonObject

    private lateinit var currentPhotoPath: String //save the photo path for upload report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_home)

        mDialog = AppProgressDialog(this)
        mDialog.show()
        getPendingReports(Constants.STATUS_PENDING)
    }

    //Get List of pending report
    private fun getPendingReports(status: String) {
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(LabServiceApi::class.java)
        val serverCall = requestInterface.getReports(status)
        serverCall.enqueue(object : Callback<ArrayList<ReportModel>> {
            override fun onFailure(call: Call<ArrayList<ReportModel>>, t: Throwable) {
                Log.d("CHECK", "Fail $t")
                mDialog.dismiss()
            }

            override fun onResponse(
                call: Call<ArrayList<ReportModel>>,
                response: Response<ArrayList<ReportModel>>
            ) {
                if (response.code() == 200) {
                    mDialog.dismiss()
                    val reports: ArrayList<ReportModel> = response.body()!!

                    pending_report_recycler.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@LabHomeActivity)
                        adapter = ReportAdapter(this@LabHomeActivity, null, reports)
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_pending_reports -> {
                mDialog.show()
                getPendingReports(Constants.STATUS_PENDING)
                true
            }
            R.id.action_done_reports -> {
                mDialog.show()
                getPendingReports(Constants.STATUS_DONE)
                true
            }
            R.id.action_logout -> {
                UserManagement.userLogOut(this)
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_lab_home, menu)
        return true
    }

    //Called when the recycler tile was clicked
    override fun onAddReportClick(requestData: JsonObject) {
        addReportData = requestData

        Toast.makeText(
            this,
            "Please take a photo for upload report.",
            Toast.LENGTH_SHORT
        ).show()

        /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 100)*/

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 100)
                }
            }
        }

    }

    //Upload file in server when photo was taken.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Log.d("PHOTO", currentPhotoPath)
            mDialog.show()
            UploadManagement
                .uploadImageFromUri(
                    Uri.fromFile(File(currentPhotoPath)),
                    UploadManagement.UPLOAD_PATH_REPORT
                )
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    mDialog.message("Upload is ${round(progress)}% done.")
                }
                .addOnSuccessListener {
                    mDialog.dismiss()
                    val result = it.metadata!!.reference!!.downloadUrl
                    result.addOnSuccessListener { uri ->
                        val reportLink = uri.toString()
                        addReportData.addProperty("reportLink", reportLink)
                        Log.d(AuthActivity.TAG, "Success $reportLink")
                        addReportToServer()
                    }
                }
                .addOnFailureListener {
                    mDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Report upload task failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.printStackTrace()
                }
        }
    }

    //Create Image file for save the photo
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun addReportToServer() {
        requestInterface =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(LabServiceApi::class.java)

        mDialog.show()
        val serverCall = requestInterface.addReport(addReportData)
        serverCall.enqueue(object : Callback<ResponseModel> {
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                mDialog.dismiss()
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                mDialog.dismiss()
                if (response.code() == 200) {
                    Toast.makeText(
                        this@LabHomeActivity,
                        (response.body() as ResponseModel).message.toString(),
                        Toast.LENGTH_LONG
                    ).show()

                    //For update report list
                    getPendingReports(Constants.STATUS_PENDING)
                } else if (response.code() == 401) {
                    responseErrorHandlerOfCode400(
                        this@LabHomeActivity,
                        response.errorBody()!!.string()
                    )
                }
            }
        })
    }
}
