package com.example.farmconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.farmconnect.databinding.ActivityFarmerDashboardBinding

class FarmerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // You can add click listeners and data loading here
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Add functionality for order items, etc.
        // For example, you can make orders clickable to view details
    }
}