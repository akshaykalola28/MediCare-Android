package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.adapters.DoctorAdapter
import com.akshaykalola.skydialog.SkyDialog
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.models.User
import com.finalyearproject.medicare.retrofit.PatientServiceApi
import com.finalyearproject.medicare.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.activity_doctor_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorListActivity : AppCompatActivity() {

    private val doctorAdapter = DoctorAdapter()
    private lateinit var mDialog: SkyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        mDialog = SkyDialog(this)
        doctor_list_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DoctorListActivity)
            adapter = doctorAdapter
        }
        generateDoctorList()
    }

    private fun generateDoctorList() {
        mDialog.show()
        val service =
            ServiceBuilder.getClient(AppSharedPreference(this).getString(Constants.PREF_API_TOKEN))
                .create(PatientServiceApi::class.java)
        val call = service.getDoctorList()
        call.enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                mDialog.dismiss()
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            Log.d("DEBUG: ", "Called. ${it.size}")
                            doctorAdapter.doctorList.submitList(it)
                        }
                    }
                }
                mDialog.dismiss()
            }
        })
    }
}