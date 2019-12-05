package com.finalyearproject.medicare.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.medicare.R
import com.finalyearproject.medicare.fragments.ChooseFragment

class AuthActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val fragmentTransaction = fragmentManager.beginTransaction()
        var chooseFragment = ChooseFragment()
        fragmentTransaction.add(R.id.auth_fragment, chooseFragment, chooseFragment.tag)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount == 0) {
            Log.d(TAG, "backStack is null")
            super.onBackPressed()
        } else {
            Log.d(TAG, "calling pop")
            fragmentManager.popBackStack()
        }
    }

    companion object {
        const val TAG: String = "AuthActivity"
    }
}
