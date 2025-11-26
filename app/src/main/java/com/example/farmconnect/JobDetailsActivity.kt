package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.farmconnect.databinding.ActivityJobDetailsBinding

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailsBinding

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
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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