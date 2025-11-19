package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityFarmersMarketBinding

class FarmersMarketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFarmersMarketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFarmersMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        loadMarketData()
    }

    private fun setupClickListeners() {
        // Add Product Button Click
        binding.btnAddProduct.setOnClickListener {
            navigateToAddProduct()
        }

        // Global Images Checkbox
        binding.cbGlobalImages.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Global images enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMarketData() {
        // Here you would load real data from your backend/database
        // For now, we're using static data from the layout
    }

    private fun navigateToAddProduct() {
        Toast.makeText(this, "Navigate to Add Product screen", Toast.LENGTH_SHORT).show()
        // Uncomment when you create AddProductActivity
        // val intent = Intent(this, AddProductActivity::class.java)
        // startActivity(intent)
    }
}