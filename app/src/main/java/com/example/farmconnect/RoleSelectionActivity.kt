package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.farmconnect.databinding.ActivityRoleSelectionBinding
import com.google.android.material.card.MaterialCardView

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding
    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Farmer card click
        binding.cardFarmer.setOnClickListener {
            selectRole("farmer", binding.cardFarmer)
        }

        // Buyer card click
        binding.cardBuyer.setOnClickListener {
            selectRole("buyer", binding.cardBuyer)
        }

        // Driver card click
        binding.cardDriver.setOnClickListener {
            selectRole("driver", binding.cardDriver)
        }

        // Get Started button click
        binding.btnGetStarted.setOnClickListener {
            navigateBasedOnRole()
        }
    }

    private fun selectRole(role: String, selectedCard: MaterialCardView) {
        // Reset all cards
        resetAllCards()

        // Set selected card style
        selectedCard.strokeColor = ContextCompat.getColor(this, R.color.card_selected_stroke)
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_selected_background))

        // Store selected role
        selectedRole = role

        // Enable Get Started button
        binding.btnGetStarted.isEnabled = true
    }

    private fun resetAllCards() {
        val cards = listOf(binding.cardFarmer, binding.cardBuyer, binding.cardDriver)

        cards.forEach { card ->
            card.strokeColor = ContextCompat.getColor(this, R.color.card_stroke)
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_background))
        }
    }

    private fun navigateBasedOnRole() {
        when (selectedRole) {
            "farmer" -> navigateToFarmerDashboard()
            "buyer" -> navigateToBuyerDashboard()
            "driver" -> navigateToDriverDashboard()
            else -> navigateToSignUp() // Fallback
        }
    }

    private fun navigateToFarmerDashboard() {
        val intent = Intent(this, FarmerDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToBuyerDashboard() {
        val intent = Intent(this, BuyerDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDriverDashboard() {
        // If you create a DriverDashboardActivity later, add it here
        // For now, redirect to sign up or show message
        Toast.makeText(this, "Driver dashboard coming soon!", Toast.LENGTH_SHORT).show()
        navigateToSignUp()
    }

    private fun navigateToSignUp() {
        selectedRole?.let { role ->
            val intent = Intent(this, SignUpActivity::class.java).apply {
                putExtra("SELECTED_ROLE", role)
            }
            startActivity(intent)
            finish()
        }
    }
}