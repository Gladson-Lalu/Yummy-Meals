package com.example.yummymeals.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.yummymeals.R
import com.example.yummymeals.database.DBItemASyncTask
import com.example.yummymeals.fragment.*
import com.google.android.material.navigation.NavigationView

class HomeDashboardActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var txtName: TextView
    private lateinit var txtNumber: TextView
    private lateinit var preferences: SharedPreferences
    private var previousMenuItem: MenuItem? = null
    private var mBackPressed: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txtHeaderName)
        txtNumber = navigationView.getHeaderView(0).findViewById(R.id.txtHeaderNumber)
        preferences = getSharedPreferences(getString(R.string.preferenceFile), Context.MODE_PRIVATE)

        val name = preferences.getString("name", "Name")
        if (name != null) txtName.text = name.capitalize()
        val number = preferences.getString("number", "+9188888888")
        if (number != null) txtNumber.text = StringBuilder(number).insert(0, "+91-")

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        openHomeFragment()
        val actionBarDrawerToggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.homeDashboard -> {
                    openHomeFragment()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HistoryFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.favRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FaqsFragment()).commit()
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    drawerLayout.closeDrawers()
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Confirmation")
                    alertDialog.setMessage("Are you sure want to logout?")
                    alertDialog.setPositiveButton("yes") { _, _ ->
                        val dataPreferences = getSharedPreferences(
                            getString(R.string.preferenceFile),
                            Context.MODE_PRIVATE
                        )
                        dataPreferences.edit().clear().apply()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    alertDialog.setNegativeButton("No") { _, _ ->
                    }
                    alertDialog.create().show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() =
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            is HomeFragment -> {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Tap again to exit app", Toast.LENGTH_SHORT).show()
                    mBackPressed = System.currentTimeMillis()
                }
            }
            is ProfileFragment, is HistoryFragment, is FaqsFragment, is FavouritesFragment -> {
                openHomeFragment()
            }
            else -> super.onBackPressed()
        }

    private fun openHomeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()
        navigationView.setCheckedItem(R.id.homeDashboard)
    }

}
