package com.finalyearproject.medicare.helpers

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.finalyearproject.medicare.models.ResponseModel
import com.google.gson.GsonBuilder


fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun responseErrorHandlerOfCode400(context: Context, errorBody: String) {
    val gson = GsonBuilder().create()
    val resData: ResponseModel =
        gson.fromJson(errorBody, ResponseModel::class.java)
    Toast.makeText(
        context,
        resData.message.toString(),
        Toast.LENGTH_SHORT
    ).show()
}