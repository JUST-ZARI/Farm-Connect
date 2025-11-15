package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityAssignedPickupsBinding

class AssignedPickupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignedPickupsBinding

    // Sample data for pickups
    private val pickups = listOf(
        Pickup(
            farmerName = "Mwangi Kimani",
            location = "123 Green Valley Rd",
            crop = "Organic Carrots",
            weight = "250 kg",
            latitude = -1.2921,
            longitude = 36.8219
        ),
        Pickup(
            farmerName = "Alanyi Otteno",
            location = "456 Sunny Fields Ln",
            crop = "Fresh Tomatoes",
            weight = "400 kg",
            latitude = -1.3036,
            longitude = 36.7741
        ),
        Pickup(
            farmerName = "Juma Onondi",
            location = "789 Orchuri Way",
            crop = "Sweet Potatoes",
            weight = "300 kg",
            latitude = -1.2689,
            longitude = 36.8033
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignedPickupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPickupData()
        setupClickListeners()
    }

    private fun setupPickupData() {
        // Set data for first pickup
        binding.tvFarmer1.text = pickups[0].farmerName
        binding.tvLocation1.text = pickups[0].location
        binding.tvCrop1.text = pickups[0].crop
        binding.tvWeight1.text = pickups[0].weight

        // Set data for second pickup
        binding.tvFarmer2.text = pickups[1].farmerName
        binding.tvLocation2.text = pickups[1].location
        binding.tvCrop2.text = pickups[1].crop
        binding.tvWeight2.text = pickups[1].weight

        // Set data for third pickup
        binding.tvFarmer3.text = pickups[2].farmerName
        binding.tvLocation3.text = pickups[2].location
        binding.tvCrop3.text = pickups[2].crop
        binding.tvWeight3.text = pickups[2].weight
    }

    private fun setupClickListeners() {
        // Pickup 1 buttons
        binding.btnMap1.setOnClickListener { openMap(pickups[0]) }
        binding.btnComplete1.setOnClickListener { completePickup(pickups[0]) }
        binding.btnCancel1.setOnClickListener { cancelPickup(pickups[0]) }

        // Pickup 2 buttons
        binding.btnMap2.setOnClickListener { openMap(pickups[1]) }
        binding.btnComplete2.setOnClickListener { completePickup(pickups[1]) }
        binding.btnCancel2.setOnClickListener { cancelPickup(pickups[1]) }

        // Pickup 3 buttons
        binding.btnMap3.setOnClickListener { openMap(pickups[2]) }
        binding.btnComplete3.setOnClickListener { completePickup(pickups[2]) }
        binding.btnCancel3.setOnClickListener { cancelPickup(pickups[2]) }
    }

    private fun openMap(pickup: Pickup) {
        // Open location in Google Maps
        val gmmIntentUri = Uri.parse("geo:${pickup.latitude},${pickup.longitude}?q=${pickup.location}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback: Show coordinates in toast
            Toast.makeText(this, "Location: ${pickup.location}", Toast.LENGTH_LONG).show()
        }
    }

    private fun completePickup(pickup: Pickup) {
        // Navigate to Record Pickup activity
        val intent = Intent(this, RecordPickupActivity::class.java).apply {
            putExtra("FARMER_NAME", pickup.farmerName)
            putExtra("CROP", pickup.crop)
            putExtra("WEIGHT", pickup.weight)
            putExtra("LOCATION", pickup.location)
        }
        startActivity(intent)

        Toast.makeText(this, "Completing pickup for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
    }

    private fun cancelPickup(pickup: Pickup) {
        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cancel Pickup")
            .setMessage("Are you sure you want to cancel pickup from ${pickup.farmerName}?")
            .setPositiveButton("Yes, Cancel") { dialog, which ->
                // Remove pickup from list (in real app, call API)
                Toast.makeText(this, "Pickup cancelled for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
                // Here you would update the UI to remove the cancelled pickup
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Data class for pickup information
    data class Pickup(
        val farmerName: String,
        val location: String,
        val crop: String,
        val weight: String,
        val latitude: Double,
        val longitude: Double
    )
}