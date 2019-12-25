package com.finalyearproject.medicare.helpers

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finalyearproject.medicare.R
import kotlinx.android.synthetic.main.progress_dialog.view.*

class AppProgressDialog(mContext: Context) {

    var view: View? = null
    var rootView: ViewGroup? = null

    init {
        rootView = ((mContext as Activity).window.decorView.rootView!! as ViewGroup)
        view = LayoutInflater.from(mContext)
            .inflate(R.layout.progress_dialog, rootView, false)

        view!!.mainLoadingView.setOnClickListener {
            //For disable outside views
        }
    }

    fun show(message: String = "Please Wait...") {
        rootView!!.removeView(view)
        view!!.messageTextView.text = message
        rootView!!.addView(view)
    }

    fun dismiss() {
        rootView!!.removeView(view)
    }
}