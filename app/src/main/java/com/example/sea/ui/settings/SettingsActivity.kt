@file:Suppress("DEPRECATION")

package com.example.sea.ui.settings

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.content.pm.PackageManager
import android.preference.*
import com.example.sea.R
import com.example.sea.ui.main.MainPresenter

class SettingsActivity : AppCompatPreferenceActivity() {
    var context : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        fragmentManager.beginTransaction().replace(android.R.id.content, MainPreferenceFragment()).commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainPresenter.LOCATION_PERMISSION -> {
                locationPreference.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            MainPresenter.SMS_PERMISSION -> {
                smsPreference.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        lateinit var smsPreference : SwitchPreference
        lateinit var locationPreference: SwitchPreference
        var number : Int? = null

        fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener =
                sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }

        private val sBindPreferenceSummaryToValueListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val stringValue = newValue.toString()

                if (preference is ListPreference) {
                    val index = preference.findIndexOfValue(stringValue)

                    preference.setSummary(
                        if (index >= 0) {
                            preference.entries[index]
                        }
                        else {
                            null
                        }
                    )

                    if(preference.key == "key_network_calls") {
                        if(index >= 0) {
                            number = index
                        }
                    }
                }
                else if (preference is RingtonePreference) {
                    if (TextUtils.isEmpty(stringValue)) {
                        preference.setSummary(R.string.settings_ringtone_silent)

                    }
                    else {
                        val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue)
                        )

                        if (ringtone == null) {
                            preference.setSummary(R.string.summary_choose_ringtone)
                        }
                        else {
                            val name = ringtone.getTitle(preference.getContext())
                            preference.setSummary(name)
                        }
                    }

                }
                else if (preference is EditTextPreference) {
                    if (preference.getKey() == "key_gallery_name") {
                        preference.setSummary(stringValue)
                    }
                }
                else {
                    preference.summary = stringValue
                }
                true
            }

        fun sendFeedback(context: Context) {
            var body: String? = null
            try {
                body = context.packageManager.getPackageInfo(context.packageName, 0).versionName
                body =
                    "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                            Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                            "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER
            }
            catch (e: PackageManager.NameNotFoundException) {}

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("eirikgs@student.matnat.uio.no"))
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_message))
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)))
        }
    }
}