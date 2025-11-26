package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityPickupDetailsBinding

class PickupDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickupDetailsBinding

    // Sample pickup data
    private val pickup = Pickup(
        farmerName = "Mwangi Kimani",
        farmerPhone = "+254 712 345678",
        farmAddress = "Y23 Farm Road, Rural Town",
        locationNote = "Shave to Power",
        items = listOf(
            OrderItem("Fresh Tomatoes", 50, 30.0, 1500.0),
            OrderItem("Organic Lettuce", 20, 40.0, 800.0),
            OrderItem("Sweet Potatoes", 30, 40.0, 1200.0)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPickupData()
        setupClickListeners()
    }

    private fun setupPickupData() {
        // Set farmer information
        binding.tvFarmerName.text = pickup.farmerName
        binding.tvFarmerPhone.text = pickup.farmerPhone

        // Set farm location
        binding.tvFarmAddress.text = pickup.farmAddress


        // Calculate and set totals
        val totalWeight = pickup.items.sumOf { it.weight }
        val totalPrice = pickup.items.sumOf { it.totalPrice }

        binding.tvTotalWeight.text = "${totalWeight} kg"
        binding.tvTotalPrice.text = "Ksh ${String.format("%,.0f", totalPrice)}"

        // In a real app, you would dynamically create the order items
        // For now, they're hardcoded in the XML layout
    }

    private fun setupClickListeners() {
        // Phone number click - make call
        binding.tvFarmerPhone.setOnClickListener {
            makePhoneCall(pickup.farmerPhone)
        }

        // Address click - open maps
        binding.tvFarmAddress.setOnClickListener {
            openMap(pickup.farmAddress)
        }

        // Proceed to Payment button
        binding.btnProceedToPayment.setOnClickListener {
            proceedToPayment()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNumber.filter { it.isDigit() }}")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Cannot make phone call", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMap(address: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Location: $address", Toast.LENGTH_LONG).show()
        }
    }

    private fun proceedToPayment() {
        Toast.makeText(this, "Proceeding to payment...", Toast.LENGTH_SHORT).show()

        // Navigate to Payment Activity
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("TOTAL_AMOUNT", pickup.items.sumOf { it.totalPrice })
            putExtra("FARMER_NAME", pickup.farmerName)
            putExtra("ITEMS_COUNT", pickup.items.size)
        }
        startActivity(intent)
    }

    // Data classes
    data class Pickup(
        val farmerName: String,
        val farmerPhone: String,
        val farmAddress: String,
        val locationNote: String,
        val items: List<OrderItem>
    )

    data class OrderItem(
        val name: String,
        val weight: Int,
        val pricePerKg: Double,
        val totalPrice: Double
    )
}