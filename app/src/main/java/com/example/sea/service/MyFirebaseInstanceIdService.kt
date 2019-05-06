@file:Suppress("DEPRECATION")

package com.example.sea.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {

    val TAG = "PushNotifService"
    lateinit var name: String

    override fun onTokenRefresh() {
        // henter id
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Token perangkat ini: $token")

    }

}
