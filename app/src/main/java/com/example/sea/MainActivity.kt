package com.example.sea

import android.content.Intent
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
import kotlinx.android.synthetic.main.view_pager.*
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val checkedItemPosition = mutableListOf(0, 0, 0, 0, 0)
        drawerLayout = findViewById(R.id.drawer)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if(menuItem.itemId in arrayOf(R.id.temperature, R.id.wind, R.id.visibility, R.id.pressure, R.id.ce)) {
                dialog(menuItem, checkedItemPosition)
            }
            true
        }

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.drawer)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager = findViewById<ViewPager>(R.id.viewpager)

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = PagerAdapter(supportFragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Find the tab layout that shows the tabs
        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager)
    }

    fun startMap(view : View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun dialog(menuItem: MenuItem, checkedItemPosition : MutableList<Int>) {
        val builder = AlertDialog.Builder(this)
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.ce -> {
                builder.setTitle(R.string.navigation_drawer_ce_mark)
                val measurements = arrayOf("A", "B", "C", "D")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[0]) { dialog, _ ->
                    checkedItemPosition[0] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
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
            }
            R.id.wind -> {
                builder.setTitle(R.string.navigation_drawer_wind)
                val measurements = arrayOf("Km/h", "Mph", "Mps")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[2]) { dialog, _ ->
                    checkedItemPosition[2] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
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
            }
            R.id.pressure -> {
                builder.setTitle(R.string.navigation_drawer_pressure)
                val measurements = arrayOf("HPa", "Mb", "bar", "mmHg")
                builder.setSingleChoiceItems(measurements, checkedItemPosition[4]) { dialog, _ ->
                    checkedItemPosition[4] = (dialog as AlertDialog).listView.checkedItemPosition
                    menuItem.isChecked = false
                    dialog.dismiss()
                }
            }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            menuItem.isChecked = false
        }
        builder.show()
    }
}
