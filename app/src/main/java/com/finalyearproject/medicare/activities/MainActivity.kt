package com.finalyearproject.medicare.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            if (AppSharedPreference(this).getString(Constants.PREF_USER_ID) != "") {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }
}
