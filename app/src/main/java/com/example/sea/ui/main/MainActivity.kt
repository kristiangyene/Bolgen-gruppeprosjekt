package com.example.sea.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.TextView
import android.widget.Toast
import com.ebanx.swipebtn.SwipeButton
import com.example.sea.R
import com.example.sea.ui.hourly.HourlyFragment
import com.example.sea.ui.now.NowFragment
import com.example.sea.ui.settings.SettingsActivity
import com.example.sea.ui.weekly.WeeklyFragment
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_menu_items.*
import kotlinx.android.synthetic.main.view_pager.*
import kotlin.NullPointerException

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var drawerLayout: DrawerLayout
    private val fileName = "com.example.sea"
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        presenter = MainPresenter(this, this, MainInteractor(this, fileName))

        //sjekker om appen startes for første gang
        if(presenter.checkFirstStart()) {
            presenter.firstStart()
        }

        presenter.createLocationRequest()
        setupDrawer()
        setupNavigationMenu()
        setupViewPagerAndTabs()

        val sosButton = findViewById<SwipeButton>(R.id.swipe_btn)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        sosButton.setOnActiveListener {
            sosDialog(sosButton, builder)
        }
    }

    override fun sosDialog(sosButton: SwipeButton, builder: AlertDialog.Builder){
        builder.setTitle(R.string.navigation_drawer_emergency)
        builder.setMessage(R.string.navigation_drawer_emergency_text)

        builder.setPositiveButton(R.string.navigation_drawer_ok) { _, _ ->
            presenter.sendSMS()
            sosButton.toggleState()
        }
        builder.setNegativeButton(R.string.navigation_drawer_cancel) { dialog, _ ->
            sosButton.toggleState()
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }



    override fun updateFragmentNow() {
        val tabs = findViewById<TabLayout>(R.id.tabs)
        if(tabs.getTabAt(0)!!.isSelected || tabs.getTabAt(1)!!.isSelected) {
            supportFragmentManager.beginTransaction().replace(R.id.now_fragment, NowFragment()).commit()
        }
    }

    override fun updateFragmentHour(){
        val tabs = findViewById<TabLayout>(R.id.tabs)
        if(tabs.getTabAt(0)!!.isSelected || tabs.getTabAt(1)!!.isSelected || tabs.getTabAt(2)!!.isSelected) {
            supportFragmentManager.beginTransaction().replace(R.id.recyclerview1, HourlyFragment()).commit()
        }
    }

    override fun updateFragmentWeek(){
        val tabs = findViewById<TabLayout>(R.id.tabs)
        if(tabs.getTabAt(1)!!.isSelected || tabs.getTabAt(2)!!.isSelected || tabs.getTabAt(3)!!.isSelected) {
            supportFragmentManager.beginTransaction().replace(R.id.recyclerview2, WeeklyFragment()).commit()
        }
    }

    private fun setupViewPagerAndTabs() {
        // View Pager tillater brukeren å sveipe mellom fragmenter
        adapter = PagerAdapter(supportFragmentManager, this)
        viewpager.adapter = adapter

        // Kobler sammen tab-en med view pageren. Tab-en vil oppdateres når brukeren sveiper, og når den blir klikket på.
        tabs.setupWithViewPager(viewpager)
    }

    override fun setupDrawer() {
        // lager drawer icon til navigation draweren. Åpner navigation draweren når man trykker på iconet.
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.drawer)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun setupNavigationMenu() {
        drawerLayout = findViewById(R.id.drawer)
        // håndterer klikk på itemene i navigation draweren
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId) {
                R.id.ce ->  {
                    presenter.onDrawerCeClick(menuItem)
                }
                R.id.temperature -> {
                    presenter.onDrawerTemperatureClick(menuItem)
                }
                R.id.wind ->  {
                    presenter.onDrawerWindClick(menuItem)
                }
                R.id.pressure -> {
                    presenter.onDrawerPressureClick(menuItem)
                }
                R.id.preferences -> {
                    presenter.onDrawerPreferencesClick(menuItem)
                }
                R.id.settings -> {
                    presenter.onDrawerSettingsClick()
                }
            }
            true
        }
        presenter.setupPreviewText()
    }

    override fun updatePreviewTextView(text: String, itemId : Int) {
        val inflaterLayout = layoutInflater.inflate(R.layout.navigation_menu_items, root_nav_preview, false)

        val textView = inflaterLayout.findViewById<TextView>(R.id.navigation_drawer_preview)
        textView.text = text
        nav_view.menu.findItem(itemId).actionView = inflaterLayout
    }

    override fun launchSMSApp(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun loadSettingsScreen() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun updateTitle(text : String) {
        supportActionBar?.title = text
    }

    override fun showLocationSettingsMessage(exception: ResolvableApiException, checkValue : Int) {
        exception.startResolutionForResult(this, checkValue)
    }

    override fun showMessage(text: String, length : Int) {
        Toast.makeText(this, text, length).show()
    }

    override fun getActivity(): Activity = this

    // Viser en toast melding hvis brukeren velger ikke å gi appen tillatelse
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
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

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

