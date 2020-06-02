package com.finalyearproject.medicare.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.managers.UserManagement
import kotlinx.android.synthetic.main.activity_doctor_home.text_hello_user_name
import kotlinx.android.synthetic.main.activity_patient_home.*

class PatientHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home)
        setSupportActionBar(toolbar_patient)

        initViews()
    }

    private fun initViews() {
        text_hello_user_name.text = "Hello, ${AppSharedPreference(this).getString("userName")}"

        medical_history_button.setOnClickListener {
            startActivity(Intent(this, CheckHistoryActivity::class.java))
        }

        doctor_list_button.setOnClickListener {
            startActivity(Intent(this, DoctorListActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
        inflater.inflate(R.menu.menu_patient_home, menu)
        return true
    }
}
