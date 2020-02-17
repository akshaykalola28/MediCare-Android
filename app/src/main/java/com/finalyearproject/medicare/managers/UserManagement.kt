package com.finalyearproject.medicare.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.finalyearproject.medicare.activities.AuthActivity
import com.finalyearproject.medicare.activities.DoctorHomeActivity
import com.finalyearproject.medicare.activities.LabHomeActivity
import com.finalyearproject.medicare.activities.PatientHomeActivity
import com.finalyearproject.medicare.helpers.AppSharedPreference
import com.finalyearproject.medicare.helpers.Constants

object UserManagement {

    fun openHomeActivity(context: Context, user_type: String) {
        when (user_type) {
            Constants.USER_DOCTOR -> {
                context.startActivity(Intent(context, DoctorHomeActivity::class.java))
                (context as Activity).finish()
            }
            Constants.USER_LABORATORY -> {
                context.startActivity(Intent(context, LabHomeActivity::class.java))
                (context as Activity).finish()
            }
            Constants.USER_PATIENT -> {
                context.startActivity(Intent(context, PatientHomeActivity::class.java))
                (context as Activity).finish()
            }
            else -> {
                Toast.makeText(
                    context,
                    "Wait For your module Update.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun userLogOut(context: Context) {
        AppSharedPreference(context).clearSharedPref(AppSharedPreference.DEFAULT_SHARED_PREF)
        context.startActivity(Intent(context, AuthActivity::class.java))
        (context as Activity).finish()
    }
}