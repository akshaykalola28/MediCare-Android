package com.finalyearproject.medicare.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.google.android.material.snackbar.Snackbar

class AppAlerts {

    fun showSnackBar(container: View, msg: String) {
        Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBarWithAction(
        container: View,
        msg: String,
        btnTitle: String,
        onClickListener: View.OnClickListener
    ) {
        val snackbar = Snackbar.make(container, msg, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(btnTitle, onClickListener)
        snackbar.show()
    }

    fun showAlertWithAction(
        mContext: Context,
        title: String,
        msg: String,
        actionString: String,
        negativeString: String,
        clickListener: DialogInterface.OnClickListener,
        isSetNegativeButton: Boolean
    ) {
        val builder = AlertDialog.Builder(mContext)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(actionString, clickListener)
        if (isSetNegativeButton)
            builder.setNegativeButton(negativeString, null)
        builder.create().show()
    }

    fun showAlertMessage(mContext: Context, title: String, msg: String) {
        val ac: Activity = mContext as Activity
        if (!ac.isFinishing && !ac.isDestroyed) {
            val builder = AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
            builder.create().show()
        }
    }
}