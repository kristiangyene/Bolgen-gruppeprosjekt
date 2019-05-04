package com.example.sea.ui.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.sea.ui.main.MainPresenter

open class BasePresenter(private var context : Context) : BaseContract.Presenter {

    override fun checkPermission(permissionOption: String): Boolean {
        return when (permissionOption) {
            "sms" -> {
                ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            }
            "location" -> {
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun requestPermission(permissionOption: String) {
        when (permissionOption) {
            "sms" -> {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SEND_SMS), MainPresenter.SMS_PERMISSION)
            }
            "location" -> {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MainPresenter.LOCATION_PERMISSION)
            }
            else -> {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_COARSE_LOCATION), MainPresenter.BOTH_PERMISSION)
            }
        }
    }

}