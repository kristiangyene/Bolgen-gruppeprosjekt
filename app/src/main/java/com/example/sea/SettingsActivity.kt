@file:Suppress("DEPRECATION")

package com.example.sea

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.preference.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
import org.jetbrains.anko.act
import java.util.*

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load settings fragment
        fragmentManager.beginTransaction().replace(android.R.id.content, MainPreferenceFragment()).commit()
    }

    class MainPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_main)

            // notification preference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)))

            // theme change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_theme_mode)))

            // feedback preference click listener
            val myPref = findPreference(getString(R.string.key_send_feedback))
            myPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                sendFeedback(activity)
                true
            }

            locationPreference = findPreference(resources.getString(R.string.key_settings_permissions_position)) as SwitchPreference
            locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            locationPreference.setOnPreferenceChangeListener { _, _ ->
                if(locationPreference.isChecked) {
                    val intent = Intent()
                    intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                    locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                }
                else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MainActivity.LOCATION_PERMISSION)
                }
                true
            }

            smsPreference = findPreference(resources.getString(R.string.key_settings_permissions_sms)) as SwitchPreference
            smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            smsPreference.setOnPreferenceChangeListener { _, _ ->
                if(smsPreference.isChecked) {
                    val intent = Intent()
                    intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                    smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                }
                else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), MainActivity.SMS_PERMISSION)
                }
                true
            }
        }


        override fun onResume() {
            smsPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            locationPreference.isChecked = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            super.onResume()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.LOCATION_PERMISSION -> {
                locationPreference.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            MainActivity.SMS_PERMISSION -> {
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
        private val TAG = SettingsActivity::class.java.simpleName
        private lateinit var smsPreference : SwitchPreference
        private lateinit var locationPreference: SwitchPreference

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val stringValue = newValue.toString()

                if (preference is ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    val index = preference.findIndexOfValue(stringValue)

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                        if (index >= 0) {
                            preference.entries[index]
                        }
                        else {
                            null
                        }
                    )
                }
                else if (preference is RingtonePreference) {
                    // For ringtone preferences, look up the correct display value
                    // using RingtoneManager.
                    if (TextUtils.isEmpty(stringValue)) {
                        // Empty values correspond to 'silent' (no ringtone).
                        preference.setSummary(R.string.settings_ringtone_silent)

                    }
                    else {
                        val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue)
                        )

                        if (ringtone == null) {
                            // Clear the summary if there was a lookup error.
                            preference.setSummary(R.string.summary_choose_ringtone)
                        }
                        else {
                            // Set the summary to reflect the new ringtone display
                            // name.
                            val name = ringtone.getTitle(preference.getContext())
                            preference.setSummary(name)
                        }
                    }

                }
                else if (preference is EditTextPreference) {
                    if (preference.getKey() == "key_gallery_name") {
                        // update the changed gallery name to summary filed
                        preference.setSummary(stringValue)
                    }
                }
                else {
                    preference.summary = stringValue
                }
                true
            }

        /**
         * Email client intent to send support mail
         * Appends the necessary device information to email body
         * useful when providing support
         */
        fun sendFeedback(context: Context) {
            var body: String? = null
            try {
                body = context.packageManager.getPackageInfo(context.packageName, 0).versionName
                body =
                    "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                            Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                            "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER
            } catch (e: PackageManager.NameNotFoundException) {
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact@androidhive.info"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app")
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)))
        }
    }
}