package com.finalyearproject.medicare.helpers

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreference(private val mContext: Context) {

    companion object {
        const val DEFAULT_SHARED_PREF: String = "default"
    }

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = mContext.getSharedPreferences(DEFAULT_SHARED_PREF, Context.MODE_PRIVATE)
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

    fun clearSharedPref(tag: String) {
        val sharedPrefs = mContext.getSharedPreferences(tag, Context.MODE_PRIVATE)
        val editor = sharedPrefs!!.edit()
        editor.clear()
        editor.apply()
    }
}