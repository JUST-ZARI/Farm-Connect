package com.example.farmconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.farmconnect.databinding.ActivityBuyerMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup bottom navigation with Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)
    }
}