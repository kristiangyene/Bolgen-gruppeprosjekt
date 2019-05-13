package com.example.sea.ui.main

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
import com.example.sea.R
import com.example.sea.ui.base.BasePresenter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.text.DecimalFormat

class MainPresenter(view: MainContract.View, private var activity: Activity, private var interactor: MainContract.Interactor) : MainContract.Presenter, BasePresenter(activity) {
    private var lastLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private var locationUpdateState = false
    private var locationStart = 0
    private var locationRequest: LocationRequest? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var view : MainContract.View? = view

    companion object {
        const val SMS_PERMISSION = 1
        const val LOCATION_PERMISSION = 2
        const val BOTH_PERMISSION = 3
        const val REQUEST_CHECK_SETTINGS = 4
    }

    // Ber brukeren å velge ce merke når appen startes for første gang
    override fun firstStart() {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        val measurements = arrayOf(activity.getString(R.string.ce_mark_a), activity.getString(R.string.ce_mark_b), activity.getString(R.string.ce_mark_c), activity.getString(R.string.ce_mark_d))
        var checkedItem: Int?

        builder.setTitle(R.string.navigation_drawer_ce_mark)

        builder.setSingleChoiceItems(measurements, 0) { dialog, _ ->
            interactor.setCeMark(measurements[(dialog as AlertDialog).listView.checkedItemPosition])

            checkedItem = (dialog).listView.checkedItemPosition
            if(checkedItem != null) {
                interactor.setFirstStart(false)
            }

            setupPreviewText()
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()

//      sjekker om appen har tillatelse til både lokasjon og sms
        if (!checkPermission("both")) {
            requestPermission("both")
        }
    }

    override fun checkFirstStart(): Boolean {
        return interactor.getFirstStart()
    }

    override fun sendSMS() {
        if (checkPermission("both")) {
            val smsManager = SmsManager.getDefault()
            val phoneNumber = "47327997"

            smsManager.sendTextMessage(phoneNumber, null, "${interactor.getLatitude()} , ${interactor.getLongitude()}", null, null)

            view!!.showMessage(activity.getString(R.string.navigation_drawer_coordinates_sent), Toast.LENGTH_LONG)
        }
        else {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto: 47327997")

                if(checkPermission("location")) {
                    putExtra("sms_body", "${interactor.getLatitude()} , ${interactor.getLongitude()}")
                }
            }

            view!!.showMessage(activity.getString(R.string.navigation_drawer_coordinates_not_sent), Toast.LENGTH_LONG)
            view!!.launchSMSApp(intent)
        }
    }

    override fun onDrawerCeClick(menuItem: MenuItem) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        val measurements = arrayOf(activity.getString(R.string.ce_mark_a), activity.getString(R.string.ce_mark_b), activity.getString(R.string.ce_mark_c), activity.getString(R.string.ce_mark_d))

        builder.setTitle(R.string.navigation_drawer_ce_mark)

        val position = measurements.indexOf(interactor.getCeMark())

        menuItem.isChecked = true
        builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
            interactor.setCeMark(measurements[(dialog as AlertDialog).listView.checkedItemPosition])
            view!!.updatePreviewTextView(measurements[(dialog).listView.checkedItemPosition].split(" ")[0], menuItem.itemId)
            menuItem.isChecked = false
            activity!!.recreate()
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
            menuItem.isChecked = false
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDrawerTemperatureClick(menuItem: MenuItem) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        val measurements = arrayOf("˚C", "˚F")

        builder.setTitle(R.string.navigation_drawer_temperature)

        val position = if (interactor.getTemperaturUnit() == null) {
            0
        }
        else {
            measurements.indexOf(interactor.getTemperaturUnit())
        }
        menuItem.isChecked = true
        builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
            interactor.setTemperaturUnit(measurements[(dialog as AlertDialog).listView.checkedItemPosition])
            view!!.updatePreviewTextView(measurements[(dialog).listView.checkedItemPosition], menuItem.itemId)
            menuItem.isChecked = false
            activity!!.recreate()
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
            menuItem.isChecked = false
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDrawerWindClick(menuItem: MenuItem) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        val measurements = arrayOf("mps", "km/t")

        builder.setTitle(R.string.navigation_drawer_wind_speed)

        val position = if (interactor.getWindUnit() == null) {
            0
        }
        else {
            measurements.indexOf(interactor.getWindUnit())
        }

        menuItem.isChecked = true
        builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
            interactor.setWindUnit(measurements[(dialog as AlertDialog).listView.checkedItemPosition])
            view!!.updatePreviewTextView(measurements[(dialog).listView.checkedItemPosition], menuItem.itemId)
            menuItem.isChecked = false
            activity!!.recreate()
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
            menuItem.isChecked = false
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDrawerPressureClick(menuItem: MenuItem) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
        val measurements = arrayOf("hPa", "mb", "bar", "mmHg")

        builder.setTitle(R.string.navigation_drawer_pressure)

        val position = if (interactor.getPressureUnit() == null) {
            0
        }
        else {
            measurements.indexOf(interactor.getPressureUnit())
        }

        menuItem.isChecked = true
        builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
            interactor.setPressureUnit(measurements[(dialog as AlertDialog).listView.checkedItemPosition])
            view!!.updatePreviewTextView(measurements[(dialog).listView.checkedItemPosition], menuItem.itemId)
            activity!!.recreate()
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
            menuItem.isChecked = false
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDrawerPreferencesClick(menuItem: MenuItem) {
        val checkedItems = booleanArrayOf(false, false, false, false, false, false, false)
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)

        builder.setTitle(activity.getString(R.string.navigation_drawer_weatherpreferences))
        val parameters = arrayOf(
            activity.getString(R.string.navigation_drawer_tide),
            activity.getString(R.string.navigation_drawer_temperature2),
            activity.getString(R.string.navigation_drawer_weather),
            activity.getString(R.string.navigation_drawer_fog),
            activity.getString(R.string.navigation_drawer_humidity),
            activity.getString(R.string.navigation_drawer_cloudiness),
            activity.getString(R.string.navigation_drawer_pressure2)
        )

        for (item in 0 until parameters.size) {
            if (interactor.getWeatherPreference(parameters[item])) {
                checkedItems[item] = true
            }
        }

        menuItem.isChecked = true
        builder.setMultiChoiceItems(parameters, checkedItems) { _, which, isChecked ->
            if (isChecked) {
                interactor.setWeatherPreference(parameters[which], true)
            }
            else {
                interactor.setWeatherPreference(parameters[which], false)
            }
        }

        builder.setPositiveButton(R.string.navigation_drawer_ok) { _, _ ->
            menuItem.isChecked = false
            view!!.updateFragmentNow()
        }

        builder.setCancelable(false)
        builder.show()
    }

    override fun onDrawerSettingsClick() {
        view!!.loadSettingsScreen()
    }

    override fun setupPreviewText() {
        val units = arrayOf(interactor.getCeMark(), interactor.getTemperaturUnit(), interactor.getWindUnit(), interactor.getVisibilityUnit(), interactor.getPressureUnit())
        val startUnits = arrayOf("A", "C", activity.getString(R.string.navigation_drawer_wind_base), activity.getString(R.string.navigation_drawer_pressure_base))
        val startId = arrayOf(R.id.ce, R.id.temperature, R.id.wind, R.id.pressure)

        for(i in 0 until startUnits.size) {
            if(i == 0) {
                if(units[i] != null) {
                    view!!.updatePreviewTextView(units[i]!!.split(" ")[0], startId[i])
                }
                else {
                    view!!.updatePreviewTextView(startUnits[i].split(" ")[0], startId[i])
                }
            }
            else {
                if(units[i] != null) {
                    view!!.updatePreviewTextView(units[i]!!, startId[i])
                }
                else {
                    view!!.updatePreviewTextView(startUnits[i], startId[i])
                }
            }
        }
    }

    // Viser en toast melding hvis brukeren velger ikke å gi appen tillatelse
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    view!!.showMessage(activity.getString(R.string.navigation_drawer_location_permission), Toast.LENGTH_LONG)
                }
            }
            BOTH_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    view!!.showMessage(activity.getString(R.string.navigation_drawer_location_permission), Toast.LENGTH_LONG)
                }
                else if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    view!!.showMessage(activity.getString(R.string.navigation_drawer_change_permission), Toast.LENGTH_LONG)
                }
                else if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest()
                }
            }
            SMS_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    view!!.showMessage(activity.getString(R.string.navigation_drawer_change_permission), Toast.LENGTH_LONG)
                }
            }
        }
    }

    // Lager en request for å hente enhetens posisjon hvis appen ikke klarer å hente siste registrerte posisjon
    override fun createLocationRequest() {
        // lager en request, hvor den gir nøyaktig plassering, men samtidig ved å ikke bruke veldig mye strøm, og bruker som regel 300 ms på å motta posisjonoppdateringer
        // interval angir hastigheten i millisekunder der appen foretrekker å motta posisjonsoppdateringer
        locationRequest = LocationRequest.create()?.apply {
            interval = 300
            fastestInterval = 200
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        // Kalle på API og hente brukerens posisjon bare hvis vi har både tillatelse til å hente enhetens posisjon og at lokasjon instillingen er på,
        // Lokasjon instillingen er på og appen har tillatelse til å hente enhetens posisjon
        task.addOnSuccessListener {
            getLocation(locationRequest!!)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Lokasjon innstillingene er ikke tilfredsstilt
                try {
                    // viser en dialog som ber brukeren å skru på lokasjon innstillingen
                    view!!.showLocationSettingsMessage(exception, REQUEST_CHECK_SETTINGS)
                }
                catch (sendEx: IntentSender.SendIntentException) {}
            }
        }
    }

    // Henter enhetens posisjon enten ved å hente siste registrerte posisjon i enheten eller ved å requeste en location update
    override fun getLocation(locationRequest: LocationRequest) {
        val format = DecimalFormat("#.###")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (checkPermission("location")) {
            // henter siste registrerte posisjon i enheten, posisjonen kan være null for ulike grunner, når bruker skrur av posisjon innstillingen
            // sletter cache, eller at enheten aldri registrerte en posisjon. Retunerer null ganske sjeldent
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationUpdateState = false
                    lastLocation = location
                    locationStart = 1

                    view!!.updateTitle("${format.format(lastLocation!!.latitude)}, ${format.format(lastLocation!!.longitude)}")
                    interactor.setLatitude(lastLocation!!.latitude.toFloat())
                    interactor.setLongitude(lastLocation!!.longitude.toFloat())
                }
                else {
                    // Hvis enheten ikke finner siste posisjon, så opprettes en ny klient og ber om plasseringsoppdateringer
                    locationUpdateState = true

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)
                            lastLocation = p0.lastLocation

                            view!!.updateTitle("${format.format(lastLocation!!.latitude)}, ${format.format(lastLocation!!.longitude)}")
                            interactor.setLatitude(lastLocation!!.latitude.toFloat())
                            interactor.setLongitude(lastLocation!!.longitude.toFloat())
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }
            }
        }
    }

    override fun stopUpdate() {
        if (locationUpdateState) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            locationUpdateState = false
            locationStart = 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                createLocationRequest()
            }
        }
    }

    override fun onPause() {
        stopUpdate()
    }

    override fun onResume() {
        if (locationStart != 0) {
            getLocation(locationRequest!!)
        }
    }

    override fun onDestroy() {
        view = null
    }
}