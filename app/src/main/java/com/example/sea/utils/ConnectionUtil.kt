package com.example.sea.utils

import android.content.Context
import android.net.ConnectivityManager

object ConnectionUtil {
    @JvmStatic fun checkNetwork(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
