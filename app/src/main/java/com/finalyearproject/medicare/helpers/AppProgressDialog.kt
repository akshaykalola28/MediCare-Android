package com.finalyearproject.medicare.helpers

import android.app.ProgressDialog
import android.content.Context

class AppProgressDialog(mContext: Context) {

    val mProgressDialog = ProgressDialog(mContext)

    fun showDialog(message: String) {
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setMessage(message)
        mProgressDialog.show()
    }

    fun dismissDialog() {
        mProgressDialog.dismiss()
    }
}