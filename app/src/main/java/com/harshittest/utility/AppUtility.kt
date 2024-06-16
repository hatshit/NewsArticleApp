package com.harshittest.utility

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.view.WindowManager



class AppUtility {
    companion object {

        var progressDialog: ProgressDialog? = null

        fun progressBarShow(context: Context?) {
           // progressDialog = ProgressDialog(context as Activity, R.style.progressbarstyle)
            progressDialog?.setMessage(
                "Loading" + "..."
            )
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog?.isIndeterminate = true
            progressDialog?.show()
        }


        fun progressBarDissMiss() {
            try {
                progressDialog?.dismiss()
                progressDialog?.setCancelable(false)
                progressDialog?.window
                    ?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isInternetConnected(context: Context?): Boolean {
            val cm = context
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


    }
}