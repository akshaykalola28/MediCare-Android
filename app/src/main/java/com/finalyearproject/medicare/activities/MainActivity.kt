package com.finalyearproject.medicare.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants
import com.finalyearproject.medicare.managers.UserManagement


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1000
        )
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
                        continueToApp()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(
                            application,
                            "Permission Denied. You might get some trouble while using the application.",
                            Toast.LENGTH_SHORT
                        ).show()
                        continueToApp()
                    }
                }
            }
        }
    }

    private fun continueToApp() {
        Handler().postDelayed({
            if (AppSharedPreference(this).getString(Constants.PREF_USER_ID) != "") {
                UserManagement.openHomeActivity(
                    this,
                    AppSharedPreference(this).getString(Constants.PREF_USER_TYPE)
                )
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}
