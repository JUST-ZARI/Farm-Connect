package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.farmconnect.databinding.ActivityBuyerMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup bottom navigation with Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        // Handle navigation request from PaymentActivity (if any)
        handleNavigationIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleNavigationIntent(intent)
        }
    }

    private fun handleNavigationIntent(intent: Intent) {
        val destination = intent.getStringExtra("navigate_to") ?: return

        when (destination) {
            "buyer_dashboard" -> {
                // navigate to buyer dashboard fragment
                navController.navigate(R.id.navigation_home)
            }
        }
    }
}