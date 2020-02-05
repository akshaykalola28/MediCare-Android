package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.ReportAdapter
import com.finalyearproject.medicare.helpers.AppProgressDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.ReportModel
import com.finalyearproject.medicare.retrofit.LabServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.activity_lab_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LabHomeActivity : AppCompatActivity() {

    private lateinit var requestInterface: LabServiceApi
    private lateinit var mDialog: AppProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_home)

        mDialog = AppProgressDialog(this)
        mDialog.show()
        getPendingReports(Constants.STATUS_PENDING)
    }

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
}
