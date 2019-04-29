package com.example.sea

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.location.Location
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.ebanx.swipebtn.SwipeButton
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_menu_items.*
import kotlinx.android.synthetic.main.view_pager.*
import java.text.DecimalFormat

// TODO: appen vil kræsje hvis man bruker andre språk. Endre sharedpreference keysa

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private var locationUpdateState = false
    private var locationStart = 0
    private var locationRequest: LocationRequest? = null

    // Referanse til presenter
    //private val presenter = MainPresenter(this, MainInteractor())

    companion object {
        const val SMS_PERMISSION = 1
        const val LOCATION_PERMISSION = 2
        private const val BOTH_PERMISSION = 3
        private const val REQUEST_CHECK_SETTINGS = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        sharedPreferences = this.getSharedPreferences(fileName, Context.MODE_PRIVATE)

        //sjekker om appen startes for første gang
        if (sharedPreferences.getBoolean("firstTime", true)) {
            firstStart()
            createLocationRequest()
            sharedPreferences.edit().putBoolean("firstTime", false).apply()
        }
        else {
            createLocationRequest()
        }

        drawerLayout = findViewById(R.id.drawer)
        val checkedItems = booleanArrayOf(false, false, false, false, false, false, false)
        // håndterer klikk på itemene i navigation draweren
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            dialog(menuItem, checkedItems)
            true
        }

        for (i in 0..4) {
            updateTextViewStart(i)
        }

        // lager drawer icon til navigation draweren. Åpner navigation draweren når man trykker på iconet.
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.drawer)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // View Pager tillater brukeren å sveipe mellom fragmenter
        // Oppretter en adapter som vet hvilken fragment som skal vises på hver side
        val adapter = PagerAdapter(supportFragmentManager, this)
        viewpager.adapter = adapter

        // Kobler sammen tab-en med view pageren. Tab-en vil oppdateres når brukeren sveiper, og når den blir klikket på.
        // Tab-ene får også riktig tittel når metoden onPageTitle() kalles
        tabs.setupWithViewPager(viewpager)


        val sosButton = findViewById<SwipeButton>(R.id.swipe_btn)
        sosButton.setOnActiveListener {
            if (checkPermission("sms")) {
                val smsManager = SmsManager.getDefault()
                val phoneNumber = "46954940"
                if(checkPermission("sms")) {
                    smsManager.sendTextMessage(phoneNumber, null, "${lastLocation.latitude} , ${lastLocation.longitude}", null, null)
                }
                else {
                    smsManager.sendTextMessage(phoneNumber, null, "Koordinater: ", null, null)
                }
                Toast.makeText(this@MainActivity, "Tekstmelding sendt til 46954940", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@MainActivity, "Har ikke tilatelse til å sende melding!", Toast.LENGTH_LONG).show()

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto: 46954940")
                    if(checkPermission("location")) {
                        putExtra("sms_body", "${lastLocation.latitude} , ${lastLocation.longitude}")
                    }
                }

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            sosButton.toggleState()
        }
    }

    // Sjekker om appen har tillatelse til å sende sms og finne enhetens lokasjon
    private fun checkPermission(permissionOption: String): Boolean {
        return when (permissionOption) {
            "sms" -> {
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            }
            "location" -> {
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    // Viser en pop-up dialog som ber brukeren om å gi appen tillatelse til å sende sms og hente enhetens lokasjon
    private fun requestPermission(permissionOption: String) {
        when (permissionOption) {
            "sms" -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION)
            }
            "location" -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION)
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_COARSE_LOCATION), BOTH_PERMISSION)
            }
        }
    }

    // Viser en toast melding hvis brukeren velger ikke å gi appen tillatelse
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@MainActivity, "Appen funker ikke uten posisjon tilgang", Toast.LENGTH_LONG).show()
                }
            }
            BOTH_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@MainActivity, "Appen funker ikke uten posisjon tilgang", Toast.LENGTH_LONG).show()
                }
                else if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@MainActivity, "Du kan endre tillatelsene i innstillinger", Toast.LENGTH_LONG).show()
                }
                else if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest()
                }
            }
            SMS_PERMISSION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this@MainActivity, "Du kan endre tillatelsene i innstillinger", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Lager en request for å hente enhetens posisjon hvis appen ikke klarer å hente siste registrerte posisjon
    private fun createLocationRequest() {
        // lager en request, hvor den gir nøyaktig plassering, men samtidig ved å ikke bruke veldig mye strøm, og bruker som regel 300 ms på å motta posisjonoppdateringer
        // interval angir hastigheten i millisekunder der appen foretrekker å motta posisjonsoppdateringer
        locationRequest = LocationRequest.create()?.apply {
            interval = 300
            fastestInterval = 200
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
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
                    exception.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                }
                catch (sendEx: IntentSender.SendIntentException) {}
            }
        }
    }

    // Henter enhetens posisjon enten ved å hente siste registrerte posisjon i enheten eller ved å requeste en location update
    private fun getLocation(locationRequest: LocationRequest) {
        val format = DecimalFormat("#.###")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkPermission("location")) {
            // henter siste registrerte posisjon i enheten, posisjonen kan være null for ulike grunner, når bruker skrur av posisjon innstillingen
            // sletter cache, eller at enheten aldri registrerte en posisjon. Retunerer null ganske sjeldent
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationUpdateState = false
                    lastLocation = location
                    locationStart = 1
                    supportActionBar?.title = "${format.format(lastLocation.latitude)}, ${format.format(lastLocation.longitude)}"
                    sharedPreferences.edit().putFloat("lat", lastLocation.latitude.toFloat()).apply()
                    sharedPreferences.edit().putFloat("long", lastLocation.longitude.toFloat()).apply()
                }
                else {
                    // Hvis enheten ikke finner siste posisjon, så opprettes en ny klient og ber om plasseringsoppdateringer
                    locationUpdateState = true

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)
                            lastLocation = p0.lastLocation
                            supportActionBar?.title = "${format.format(lastLocation.latitude)}, ${format.format(lastLocation.longitude)}"
                            sharedPreferences.edit().putFloat("lat", lastLocation.latitude.toFloat()).apply()
                            sharedPreferences.edit().putFloat("long", lastLocation.longitude.toFloat()).apply()
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                createLocationRequest()
            }
        }
    }

    private fun stopUpdate() {
        if (locationUpdateState) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            locationUpdateState = false
            locationStart = 1
        }
    }

    override fun onPause() {
        super.onPause()
        stopUpdate()
    }

    // Oppdaterer posisjon
    override fun onResume() {
        super.onResume()
        if (locationStart != 0) {
            getLocation(locationRequest!!)
        }
    }

    // Oppdaterer previewen i navigation draweren når appen åpnes for første gang eller når all storagen i appen har blitt slettet
    private fun updateTextViewStart(position: Int) {
        val inflaterLayout = layoutInflater.inflate(R.layout.navigation_menu_items, root_nav_preview, false)

        when (position) {
            0 -> {
                val ceMarkTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
                if (ceMarkText == null) {
                    ceMarkTextView.text = "A"
                }
                else {
                    ceMarkTextView.text = ceMarkText.split(" ")[0]
                }
                nav_view.menu.findItem(R.id.ce).actionView = inflaterLayout
            }
            1 -> {
                val temperatureTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val temperatureText = sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null)
                if (temperatureText == null) {
                    temperatureTextView.text = "˚C"
                }
                else {
                    temperatureTextView.text = temperatureText
                }
                nav_view.menu.findItem(R.id.temperature).actionView = inflaterLayout
            }
            2 -> {
                val windTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val windText = sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null)
                if (windText == null) {
                    windTextView.text = getString(R.string.navigation_drawer_wind_base)
                }
                else {
                    windTextView.text = windText
                }
                nav_view.menu.findItem(R.id.wind).actionView = inflaterLayout
            }
            3 -> {
                val visibilityTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val visibilityText = sharedPreferences.getString(getString(R.string.navigation_drawer_visibility), null)
                if (visibilityText == null) {
                    visibilityTextView.text = getString(R.string.navigation_drawer_visibility_base)
                }
                else {
                    visibilityTextView.text = visibilityText
                }
                nav_view.menu.findItem(R.id.visibility).actionView = inflaterLayout
            }
            else -> {
                val pressureTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val pressureText = sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null)
                if (pressureText == null) {
                    pressureTextView.text = getString(R.string.navigation_drawer_pressure_base)
                }
                else {
                    pressureTextView.text = pressureText
                }
                nav_view.menu.findItem(R.id.pressure).actionView = inflaterLayout
            }
        }
    }

    // Oppdaterer previewen i navigation draweren når man endrer måleenhet
    private fun updateTextView(position: Int) {
        val inflaterLayout = layoutInflater.inflate(R.layout.navigation_menu_items, root_nav_preview, false)

        when (position) {
            R.id.ce -> {
                val ceMarkTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
                if (ceMarkText != null) ceMarkTextView.text = ceMarkText.split(" ")[0]
                nav_view.menu.findItem(R.id.ce).actionView = inflaterLayout
            }
            R.id.temperature -> {
                val temperatureTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val temperatureText = sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null)
                temperatureTextView.text = temperatureText
                nav_view.menu.findItem(R.id.temperature).actionView = inflaterLayout
            }
            R.id.wind -> {
                val windTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val windText = sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null)
                windTextView.text = windText
                nav_view.menu.findItem(R.id.wind).actionView = inflaterLayout
            }
            R.id.visibility -> {
                val visibilityTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val visibilityText = sharedPreferences.getString(getString(R.string.navigation_drawer_visibility), null)
                visibilityTextView.text = visibilityText
                nav_view.menu.findItem(R.id.visibility).actionView = inflaterLayout
            }
            R.id.pressure -> {
                val pressureTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val pressureText = sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null)
                pressureTextView.text = pressureText
                nav_view.menu.findItem(R.id.pressure).actionView = inflaterLayout
            }
        }
    }

    // Lukker navigation draweren hvis den er åpen og man trykker på back knappen, ellers funker back knappen som vanlig.
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    // viser en alert dialog når brukeren trykker på en av itemene i navigation draweren
    private fun dialog(menuItem: MenuItem, checkedItems: BooleanArray) {
        // Viser SettingsActivity hvis brukeren trykker på settings i navigation draweren
        if(menuItem.itemId == R.id.settings) {
            menuItem.isChecked = false
            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }

        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.ce -> {
                builder.setTitle(R.string.navigation_drawer_ce_mark)
                val measurements = arrayOf(getString(R.string.ce_mark_a), getString(R.string.ce_mark_b), getString(R.string.ce_mark_c), getString(R.string.ce_mark_d))
                val position: Int?

                position = measurements.indexOf(sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null))

                builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
                    sharedPreferences.edit().putString(getString(R.string.navigation_drawer_ce_mark), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
                    menuItem.isChecked = false
                    updateTextView(menuItem.itemId)
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.temperature -> {
                builder.setTitle(R.string.navigation_drawer_temperature)
                val measurements = arrayOf("˚C", "˚F")
                val position: Int?

                position = if (sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null) == null) {
                    0
                }
                else {
                    measurements.indexOf(sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null))
                }

                builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
                    sharedPreferences.edit().putString(getString(R.string.navigation_drawer_temperature), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
                    menuItem.isChecked = false
                    updateTextView(menuItem.itemId)
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.wind -> {
                builder.setTitle(R.string.navigation_drawer_wind_speed)
                val measurements = arrayOf("Km/h", "Mph", "Mps")
                val position: Int

                position = if (sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null) == null) {
                    0
                }
                else {
                    measurements.indexOf(sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null))
                }

                builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
                    sharedPreferences.edit().putString(getString(R.string.navigation_drawer_wind_speed), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
                    menuItem.isChecked = false
                    updateTextView(menuItem.itemId)
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.visibility -> {
                builder.setTitle(R.string.navigation_drawer_visibility)
                val measurements = arrayOf("Km", "Miles")
                val position: Int

                position = if (sharedPreferences.getString(getString(R.string.navigation_drawer_visibility), null) == null) {
                    0
                }
                else {
                    measurements.indexOf(sharedPreferences.getString(getString(R.string.navigation_drawer_visibility), null))
                }

                builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
                    sharedPreferences.edit().putString(getString(R.string.navigation_drawer_visibility), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
                    menuItem.isChecked = false
                    updateTextView(menuItem.itemId)
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.pressure -> {
                builder.setTitle(R.string.navigation_drawer_pressure)
                val measurements = arrayOf("HPa", "Mb", "bar", "mmHg")
                val position: Int?

                position = if (sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null) == null) {
                        0
                    }
                    else {
                        measurements.indexOf(sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null))
                    }

                builder.setSingleChoiceItems(measurements, position) { dialog, _ ->
                    sharedPreferences.edit().putString(getString(R.string.navigation_drawer_pressure), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
                    menuItem.isChecked = false
                    updateTextView(menuItem.itemId)
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.preferences -> {
                builder.setTitle(getString(R.string.navigation_drawer_weatherpreferences))
                val parameters = arrayOf(
                    getString(R.string.navigation_drawer_tide),
                    getString(R.string.navigation_drawer_temperature2),
                    getString(R.string.navigation_drawer_weather),
                    getString(R.string.navigation_drawer_fog),
                    getString(R.string.navigation_drawer_humidity),
                    getString(R.string.navigation_drawer_cloudiness),
                    getString(R.string.navigation_drawer_pressure2)
                )

                for (item in 0 until parameters.size) {
                    if (sharedPreferences.getBoolean(parameters[item], false)) {
                        checkedItems[item] = true
                    }
                }

                builder.setMultiChoiceItems(parameters, checkedItems) { _, which, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(parameters[which], true).apply()
                    } else {
                        sharedPreferences.edit().putBoolean(parameters[which], false).apply()
                    }
                }

                builder.setPositiveButton(R.string.navigation_drawer_ok) { _, _ ->
                    // Legger til widgets for valgte parametre
                    menuItem.isChecked = false
                    recreate()
                }
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    // Ber brukeren å velge ce merke når appen startes for første gang
    private fun firstStart(): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.navigation_drawer_ce_mark)

        var itemChecked: Int? = null
        val measurements = arrayOf(getString(R.string.ce_mark_a), getString(R.string.ce_mark_b), getString(R.string.ce_mark_c), getString(R.string.ce_mark_d))

        builder.setSingleChoiceItems(measurements, 0) { dialog, _ ->
            sharedPreferences.edit().putString(getString(R.string.navigation_drawer_ce_mark), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
            updateTextViewStart(0)
            dialog.dismiss()
            itemChecked = (dialog).listView.checkedItemPosition
        }

        val mDialog = builder.create()
        mDialog.setCancelable(false)
        mDialog.show()

        // sjekker om appen har tillatelse til både lokasjon og sms
        if (!checkPermission("both")) {
            requestPermission("both")
        }

        return itemChecked != null
    }
}

