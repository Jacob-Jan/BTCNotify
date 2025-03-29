package com.bitcoin.btcnotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val homeBtn = findViewById<android.widget.ImageButton>(R.id.navHome)
        val infoBtn = findViewById<android.widget.ImageButton>(R.id.navInfo)
        val settingsBtn = findViewById<android.widget.ImageButton>(R.id.navSettings)

        homeBtn.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
        }

        infoBtn.setOnClickListener {
            if (navController.currentDestination?.id != R.id.infoFragment) {
                navController.navigate(R.id.infoFragment)
            }
        }

        settingsBtn.setOnClickListener {
            if (navController.currentDestination?.id != R.id.settingsFragment) {
                navController.navigate(R.id.settingsFragment)
            }
        }

        // Highlight active tab
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val defaultColor = android.graphics.Color.parseColor("#67867D")
            val activeColor = android.graphics.Color.parseColor("#395B50")

            homeBtn.setColorFilter(defaultColor)
            infoBtn.setColorFilter(defaultColor)
            settingsBtn.setColorFilter(defaultColor)

            when (destination.id) {
                R.id.homeFragment -> homeBtn.setColorFilter(activeColor)
                R.id.infoFragment -> infoBtn.setColorFilter(activeColor)
                R.id.settingsFragment -> settingsBtn.setColorFilter(activeColor)
            }
        }

        createNotificationChannel() // Ensure the notification channel is created
        checkNotificationPermission() // Check and request notification permission
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun createNotificationChannel() {
        val name = "Price Alerts"
        val descriptionText = "Notifications for significant Bitcoin price changes"
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH
        val channel = android.app.NotificationChannel("PRICE_ALERT_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: android.app.NotificationManager =
            getSystemService(android.app.NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }
}