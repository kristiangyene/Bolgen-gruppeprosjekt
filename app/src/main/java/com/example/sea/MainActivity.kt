package com.example.sea

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.view_pager.*

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val checkedItemPosition = mutableListOf(0, 0, 0, 0, 0)
        drawerLayout = findViewById(R.id.drawer)
        val checkedItems = booleanArrayOf(false, false, false, false, false, false)
        // håndterer klikk på itemene i navigation draweren
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            dialog(menuItem, checkedItemPosition, checkedItems)

            true
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
    private fun dialog(menuItem: MenuItem, checkedItemPosition : MutableList<Int>, checkedItems : BooleanArray) {
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
                builder.setSingleChoiceItems(measurements, checkedItemPosition[0]) { dialog, _ ->
                    checkedItemPosition[0] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }

                builder.setNegativeButton("Avbryt") { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.temperature -> {
                builder.setTitle(R.string.navigation_drawer_temperature)
                val measurements = arrayOf("˚C", "˚F")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[1]) { dialog, _ ->
                    checkedItemPosition[1] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }

                builder.setNegativeButton("Avbryt") { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.wind -> {
                builder.setTitle(R.string.navigation_drawer_wind)
                val measurements = arrayOf("Km/h", "Mph", "Mps")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[2]) { dialog, _ ->
                    checkedItemPosition[2] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }

                builder.setNegativeButton("Avbryt") { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.visibility -> {
                builder.setTitle(R.string.navigation_drawer_visibility)
                val measurements = arrayOf("Km", "Miles")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[3]) { dialog, _ ->
                    checkedItemPosition[3] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }

                builder.setNegativeButton("Avbryt") { _, _ ->
                    menuItem.isChecked = false
                }
            }
            R.id.pressure -> {
                builder.setTitle(R.string.navigation_drawer_pressure)
                val measurements = arrayOf("HPa", "Mb", "bar", "mmHg")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[4]) { dialog, _ ->
                    checkedItemPosition[4] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }
                builder.setNegativeButton(R.string.navigation_drawer_cancel) { _, _ ->
                    menuItem.isChecked = false
                }
            }

            R.id.værpreferanser -> {
                builder.setTitle(getString(R.string.navigation_drawer_weatherpreferences))
                val selectedItemsindexList = ArrayList<Int>()
                val parameters = arrayOf(getString(R.string.navigation_drawer_tide), getString(R.string.navigation_drawer_wind), getString(R.string.navigation_drawer_rain), getString(R.string.navigation_drawer_fog), getString(R.string.navigation_drawer_humidity), getString(R.string.navigation_drawer_cloudiness))

                builder.setMultiChoiceItems(parameters, checkedItems) {_, which, isChecked ->
                    if (isChecked) {
                        selectedItemsindexList.add(which)
                    }
                    else {
                        selectedItemsindexList.remove(Integer.valueOf(which))
                    }

                }

                builder.setPositiveButton(R.string.navigation_drawer_ok) {_, _ ->
                    // Legger til widgets for valgte parametre
                    menuItem.isChecked = false
                }

            }
        }


        builder.show()
    }
}
