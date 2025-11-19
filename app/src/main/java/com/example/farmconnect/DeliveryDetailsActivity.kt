package com.example.farmconnect

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityDeliveryDetailsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton

class DeliveryDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDeliveryDetailsBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    // Sample locations (in a real app, get these from your data)
    private val farmerLocation = LatLng(-1.2921, 36.8219) // Nairobi coordinates
    private val buyerLocation = LatLng(-1.3036, 36.7741) // Another Nairobi location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMapView(savedInstanceState)
        setupDeliveryData()
        setupClickListeners()
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        // Initialize map view
        mapView = binding.root.findViewById(R.id.mapView) ?: return

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun setupDeliveryData() {
        // Set order information
        binding.tvOrderNumber.text = "FC-4321"
        binding.tvFarmerName.text = "Kamau Karluki"
        binding.tvBuyerName.text = "Achieng Markets"
        binding.tvETA.text = "45 min"

        // Set delivery items (already in XML, but you can make them dynamic)
    }

    private fun setupClickListeners() {
        // Current Location FAB
        val fabLocation = binding.root.findViewById<MaterialButton>(R.id.fabLocation)
        fabLocation?.setOnClickListener {
            centerMapOnCurrentLocation()
        }

        // Proceed to Payment Button
        binding.btnProceedToPayment.setOnClickListener {
            proceedToPayment()
        }

        // Delivery Location Input - simulate address search
        binding.etDeliveryLocation.setOnClickListener {
            showLocationSearch()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set up map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        // Add markers for farmer and buyer locations
        googleMap.addMarker(
            MarkerOptions()
                .position(farmerLocation)
                .title("Farmer: Kamau Karluki")
        )

        googleMap.addMarker(
            MarkerOptions()
                .position(buyerLocation)
                .title("Buyer: Achieng Markets")
        )

        // Draw route between locations
        drawRoute()

        // Move camera to show both locations
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
            .include(farmerLocation)
            .include(buyerLocation)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun drawRoute() {
        // In a real app, use Directions API to get actual route
        // For demo, draw a straight line
        val route = PolylineOptions()
            .add(farmerLocation, buyerLocation)
            .width(8f)
            .color(android.graphics.Color.parseColor("#2196F3"))

        googleMap.addPolyline(route)
    }

    private fun centerMapOnCurrentLocation() {
        // In a real app, get current location using FusedLocationProviderClient
        val centerLocation = LatLng(
            (farmerLocation.latitude + buyerLocation.latitude) / 2,
            (farmerLocation.longitude + buyerLocation.longitude) / 2
        )

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(centerLocation, 12f)
        )

        Toast.makeText(this, "Centered on route", Toast.LENGTH_SHORT).show()
    }

    private fun showLocationSearch() {
        Toast.makeText(this, "Search for delivery location", Toast.LENGTH_SHORT).show()
        // In real app, implement Places API for location search
    }

    private fun proceedToPayment() {
        val deliveryLocation = binding.etDeliveryLocation.text.toString().trim()

        if (deliveryLocation.isEmpty()) {
            binding.etDeliveryLocation.error = "Please enter delivery location"
            return
        }

        Toast.makeText(this, "Proceeding to payment for delivery", Toast.LENGTH_SHORT).show()

        // Navigate to Payment Activity
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("ORDER_TYPE", "DELIVERY")
            putExtra("ORDER_NUMBER", "FC-4321")
            putExtra("DELIVERY_LOCATION", deliveryLocation)
            putExtra("ESTIMATED_TIME", "45 min")
        }
        startActivity(intent)
    }

    // MapView lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}