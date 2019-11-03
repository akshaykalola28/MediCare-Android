package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.fragments.ChooseFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        var fragmentTransaction = supportFragmentManager.beginTransaction()
        var chooseFragment = ChooseFragment()
        fragmentTransaction.add(R.id.auth_fragment, chooseFragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
