package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityRecordPickupBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecordPickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordPickupBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupToolbar()
    }

    private fun setupClickListeners() {
        // Record Pickup Button Click
        binding.btnRecordPickup.setOnClickListener {
            if (validateForm()) {
                recordPickup()
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateForm(): Boolean {
        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val conditionSelected = binding.rgCondition.checkedRadioButtonId != -1

        // Clear previous errors
        binding.etFarmerName.error = null
        binding.etCrop.error = null
        binding.etWeight.error = null

        if (farmerName.isEmpty()) {
            binding.etFarmerName.error = "Please enter farmer's name"
            return false
        }

        if (crop.isEmpty()) {
            binding.etCrop.error = "Please enter crop type"
            return false
        }

        if (weight.isEmpty()) {
            binding.etWeight.error = "Please enter weight"
            return false
        }

        if (!conditionSelected) {
            Toast.makeText(this, "Please select crop condition", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun recordPickup() {
        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val comments = binding.etComments.text.toString().trim()
    }
}


