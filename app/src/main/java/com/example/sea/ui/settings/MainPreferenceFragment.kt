package com.example.sea.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.sea.R
import com.example.sea.ui.main.MainPresenter

@Suppress("DEPRECATION")
class MainPreferenceFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_main)

        // notification preference change listener
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)))

        // theme change listener
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.key_theme_mode)))

        // feedback preference click listener
        val myPref = findPreference(getString(R.string.key_send_feedback))
        myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsActivity.sendFeedback(activity)
            true
        }

        SettingsActivity.locationPreference = findPreference(resources.getString(R.string.key_settings_permissions_position)) as SwitchPreference
        SettingsActivity.locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        SettingsActivity.locationPreference.setOnPreferenceChangeListener { _, _ ->
            if(SettingsActivity.locationPreference.isChecked) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                context.startActivity(intent)
                SettingsActivity.locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
            else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MainPresenter.LOCATION_PERMISSION)
            }
            true
        }

        SettingsActivity.smsPreference = findPreference(resources.getString(R.string.key_settings_permissions_sms)) as SwitchPreference
        SettingsActivity.smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        SettingsActivity.smsPreference.setOnPreferenceChangeListener { _, _ ->
            if(SettingsActivity.smsPreference.isChecked) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                context.startActivity(intent)
                SettingsActivity.smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            }
            else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS),
                    MainPresenter.SMS_PERMISSION
                )
            }
            true
        }
    }


    override fun onResume() {
        SettingsActivity.smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        SettingsActivity.locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        super.onResume()
    }
}