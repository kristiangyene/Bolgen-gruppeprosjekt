package com.example.sea

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.view_pager.*
import kotlinx.android.synthetic.main.navigation_menu_items.*

// TODO: appen vil kræsje hvis man bruker andre språk. Endre fra keysa
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private val fileName = "com.example.sea"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sharedPreferences = this.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        //sjekker om den har blitt kjørt før
        if (sharedPreferences.getBoolean("firstTime", true)) {
            firstStart()
            sharedPreferences.edit().putBoolean("firstTime", false).apply()
        }

        drawerLayout = findViewById(R.id.drawer)
        val checkedItems = booleanArrayOf(false, false, false, false, false, false)
        // håndterer klikk på itemene i navigation drawerenx
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            dialog(menuItem, checkedItems)
            true
        }

        for(i in 1 .. 4) {
            updateTextViewStart(i)
        }

        // lager drawer icon til navigation draweren. Åpner navigation draweren når man trykker på iconet.
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.drawer)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // View Pager tillater brukeren å sveipe mellom fragmenter
        // Oppretter en adapter som vet hvilken fragment som skal vises på hver side
        val adapter = PagerAdapter(supportFragmentManager)
        viewpager.adapter = adapter

        // Kobler sammen tab-en med view pageren. Tab-en vil oppdateres når brukeren sveiper, og når den blir klikket på.
        // Tab-ene får også riktig tittel når metoden onPageTitle() kalles
        tabs.setupWithViewPager(viewpager)
        DataAPI().fetchData(this)
    }

    // oppdaterer previewen i navigation draweren først man starter appen
    private fun updateTextViewStart(position: Int) {
        val inflaterLayout = layoutInflater.inflate(R.layout.navigation_menu_items, root_nav_preview, false)

        when(position) {
            0 ->  {
                val ceMarkTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
                if(ceMarkText == null) {
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
                if(temperatureText == null) {
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
                if(windText == null) {
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
                if(visibilityText == null) {
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
                if(pressureText == null) {
                    pressureTextView.text = getString(R.string.navigation_drawer_pressure_base)
                }
                else {
                    pressureTextView.text = pressureText
                }
                nav_view.menu.findItem(R.id.pressure).actionView = inflaterLayout
            }
        }
    }

    // oppdaterer previewen i navigation draweren når man endrer måleenhet
    private fun updateTextView(position : Int) {
        val inflaterLayout = layoutInflater.inflate(R.layout.navigation_menu_items, root_nav_preview, false)

        when(position) {
            R.id.ce -> {
                val ceMarkTextView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
                val ceMarkText = sharedPreferences.getString(getString(R.string.navigation_drawer_ce_mark), null)
                if(ceMarkText != null) ceMarkTextView.text = ceMarkText.split(" ")[0]
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

    fun startMap(view : View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    // lukker navigation draweren hvis den er åpen og man trykker på back knappen, ellers funker back knappen som vanlig.
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    // lager alert dialoger for alle itemene i navigation draweren
    private fun dialog(menuItem: MenuItem, checkedItems : BooleanArray) {
        val builder = AlertDialog.Builder(this)
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.ce -> {
                builder.setTitle(R.string.navigation_drawer_ce_mark)
                // Midlertidlig løsning på beskrivelse av CE - merking
                val A = "A - Havgående båter skal tåle en vindstyrke på mer enn 20,8 sekundmeter og en bølgehøyde på mer enn fire meter."
                val B = "B - Båter til bruk utenfor kysten skal tåle til og med 20,7 sekundmeter og en bølgehøyde til fire meter."
                val C = "C - Båter nær kysten skal tåle til og med 13,8 sekundmeter og bølger til og med to meter"
                val D = "D - Båter i beskyttet farvann tåler mindre enn 7,7 sekundmeter i vindstyrke og til og med 0,3 meter i bølgehøyde."
                val measurements = arrayOf(A, B, C, D)
                val position : Int?

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
                val position : Int?

                position = if(sharedPreferences.getString(getString(R.string.navigation_drawer_temperature), null) == null) {
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
                val position : Int

                position = if(sharedPreferences.getString(getString(R.string.navigation_drawer_wind_speed), null) == null) {
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
                val position : Int

                position = if(sharedPreferences.getString(getString(R.string.navigation_drawer_visibility), null) == null) {
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
                val position : Int?

                position = if(sharedPreferences.getString(getString(R.string.navigation_drawer_pressure), null) == null) {
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
            R.id.værpreferanser -> {
                builder.setTitle(getString(R.string.navigation_drawer_weatherpreferences))
                val parameters = arrayOf(getString(R.string.navigation_drawer_tide), getString(R.string.navigation_drawer_temperature_2), getString(R.string.navigation_drawer_weather), getString(R.string.navigation_drawer_fog), getString(R.string.navigation_drawer_humidity), getString(R.string.navigation_drawer_cloudiness))

                for(item in 0 until parameters.size) {
                    if(sharedPreferences.getBoolean(parameters[item], false)) {
                        checkedItems[item] = true
                    }
                }

                builder.setMultiChoiceItems(parameters, checkedItems) {_, which, isChecked ->
                    if (isChecked) {
                        sharedPreferences.edit().putBoolean(parameters[which], true).apply()
                    }
                    else {
                        sharedPreferences.edit().putBoolean(parameters[which], false).apply()
                    }
                }

                builder.setPositiveButton(R.string.navigation_drawer_ok) {_, _ ->
                    // Legger til widgets for valgte parametre
                    menuItem.isChecked = false
                }
            }
        }
        builder.setCancelable(false)
        builder.show()
    }
    fun firstStart() {
        //velger CE merke
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.navigation_drawer_ce_mark)
        val A = "A - Havgående båter skal tåle en vindstyrke på mer enn 20,8 sekundmeter og en bølgehøyde på mer enn fire meter."
        val B = "B - Båter til bruk utenfor kysten skal tåle til og med 20,7 sekundmeter og en bølgehøyde til fire meter."
        val C = "C - Båter nær kysten skal tåle til og med 13,8 sekundmeter og bølger til og med to meter"
        val D = "D - Båter i beskyttet farvann tåler mindre enn 7,7 sekundmeter i vindstyrke og til og med 0,3 meter i bølgehøyde."
        val measurements = arrayOf(A, B, C, D)
        builder.setSingleChoiceItems(measurements, 0) { dialog, _ ->
            sharedPreferences.edit().putString(getString(R.string.navigation_drawer_ce_mark), measurements[(dialog as AlertDialog).listView.checkedItemPosition]).apply()
            updateTextViewStart(0)
            dialog.dismiss()
        }
        val mDialog = builder.create()
        mDialog.setCancelable(false)
        mDialog.show()
    }
}
