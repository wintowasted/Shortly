package com.example.shortly

import android.app.AlertDialog

class LoadingDialog(val activity:MainActivity) {
    private lateinit var isdialog:AlertDialog

    fun startLoading(){

        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog,null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }

    fun isDismiss(){
        isdialog.dismiss()
    }
}