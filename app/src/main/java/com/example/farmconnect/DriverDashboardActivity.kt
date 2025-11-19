package com.example.farmconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmconnect.adapter.PickupAdapter
import com.example.farmconnect.databinding.ActivityDriverDashboardBinding
import com.example.farmconnect.model.Pickup
import com.google.android.material.progressindicator.LinearProgressIndicator

class DriverDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverDashboardBinding
    private lateinit var pickupAdapter: PickupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDashboard()
        setupPickupsRecyclerView()
        setupClickListeners()
    }

    private fun setupDashboard() {
        // Set progress for monthly distance
        val progressDistance = findViewById<LinearProgressIndicator>(R.id.progressDistance)
        progressDistance.progress = 65 // 650km out of 1000km (65%)

        // You can load real data here from your backend
        loadDriverData()
    }

    private fun setupPickupsRecyclerView() {
        val pickups = getSamplePickups()

        pickupAdapter = PickupAdapter(
            pickups,
            onMapClick = { pickup ->
                showMapForPickup(pickup)
            },
            onRecordClick = { pickup ->
                recordPickupDetails(pickup)
            },
            onCancelClick = { pickup ->
                cancelPickup(pickup)
            },
            onPickupClick = { pickup ->
                handlePickupAction(pickup)
            }
        )

        binding.rvPickups.apply {
            layoutManager = LinearLayoutManager(this@DriverDashboardActivity)
            adapter = pickupAdapter
        }
    }

    private fun loadDriverData() {
        // Example of loading data from backend
        // val driverStats = api.getDriverStats()
        // binding.textDeliveries.text = driverStats.deliveries.toString()
        // binding.textDistance.text = driverStats.distance.toString()
        // etc.
    }

    private fun setupClickListeners() {
        // Notification icon click
        binding.imageNotifications.setOnClickListener {
            showNotifications()
        }

        // Pricing tier clicks
        setupPricingTierClicks()
    }

    private fun setupPricingTierClicks() {
        // You can add click listeners for each pricing tier
        binding.cardTierA.setOnClickListener {
            showTierDetails("A", "Standard Tier - Basic delivery services")
        }
        binding.cardTierB.setOnClickListener {
            showTierDetails("B", "Premium Tier - Priority delivery with tracking")
        }
        binding.cardTierC.setOnClickListener {
            showTierDetails("C", "Economy Tier - Budget-friendly delivery options")
        }
    }

    // Pickup-related functions
    private fun showMapForPickup(pickup: Pickup) {
        Toast.makeText(this, "Opening map for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
        // Here you would integrate with Google Maps API
    }

    private fun recordPickupDetails(pickup: Pickup) {
        Toast.makeText(this, "Recording details for ${pickup.crop}", Toast.LENGTH_SHORT).show()
        // Here you would open a form to record pickup details
    }

    private fun cancelPickup(pickup: Pickup) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Cancel Pickup")
            .setMessage("Are you sure you want to cancel pickup from ${pickup.farmerName}?")
            .setPositiveButton("Yes, Cancel") { dialog, which ->
                // Update pickup status
                pickupAdapter.updatePickups(getSamplePickups().filter { it.id != pickup.id })
                Toast.makeText(this, "Pickup cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun handlePickupAction(pickup: Pickup) {
        when (pickup.status) {
            "scheduled" -> {
                Toast.makeText(this, "Starting pickup from ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
                // Update status to in_progress
            }
            "in_progress" -> {
                Toast.makeText(this, "Pickup completed for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
                // Update status to completed
            }
            else -> {
                Toast.makeText(this, "Pickup action for ${pickup.status}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSamplePickups(): List<Pickup> {
        return listOf(
            Pickup(
                "1",
                "Mwangi Kimani",
                "123 Green Valley Rd",
                "Organic Carrots",
                "250 kg",
                "scheduled"
            ),
            Pickup(
                "2",
                "Akiriyi Otieno",
                "456 Sunny Fields Ln",
                "Fresh Tomatoes",
                "400 kg",
                "scheduled"
            ),
            Pickup(
                "3",
                "Junna Omondi",
                "789 Orchard Way",
                "Sweet Potatoes",
                "300 kg",
                "scheduled"
            )
        )
    }

    private fun showNotifications() {
        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        // Navigate to notifications activity
        // val intent = Intent(this, NotificationsActivity::class.java)
        // startActivity(intent)
    }

    private fun showTierDetails(tier: String, details: String) {
        Toast.makeText(this, "Tier $tier: $details", Toast.LENGTH_SHORT).show()
    }
}