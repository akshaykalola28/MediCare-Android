package com.finalyearproject.medicare.helpers

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreference(mContext: Context) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = mContext.getSharedPreferences("default", 0)
    }

    fun saveString(key: String, value: String): Boolean {
        return try {
            val editor = sharedPreferences!!.edit()
            editor.putString(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getString(key: String): String {
        return sharedPreferences!!.getString(key, "")!!
    }
}