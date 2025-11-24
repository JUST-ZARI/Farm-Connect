package com.example.farmconnect

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityDeliveryDetailsBinding
import com.example.farmconnect.model.CartItem
import com.example.farmconnect.model.Order
import com.example.farmconnect.model.Product
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeliveryDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDeliveryDetailsBinding
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private var cartItems: List<CartItem> = emptyList()
    private var subtotal: Double = 0.0
    private var deliveryFee: Double = 8.0
    private var total: Double = 0.0

    // Sample locations (in a real app, get these from your data)
    private val farmerLocation = LatLng(-1.2921, 36.8219) // Nairobi coordinates
    private val buyerLocation = LatLng(-1.3036, 36.7741) // Another Nairobi location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get cart items from intent
        receiveCartItems()
        
        setupMapView(savedInstanceState)
        setupDeliveryData()
        setupClickListeners()
    }
    
    private fun receiveCartItems() {
        val products = intent.getParcelableArrayListExtra<Product>("CART_ITEMS")
        if (products != null) {
            // Convert Products to CartItems
            cartItems = products.map { product ->
                CartItem(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    unit = product.unit,
                    quantity = product.quantity.toIntOrNull() ?: 1
                )
            }
            subtotal = intent.getDoubleExtra("SUBTOTAL", cartItems.sumOf { it.getTotalPrice() })
            deliveryFee = intent.getDoubleExtra("DELIVERY_FEE", 8.0)
            total = intent.getDoubleExtra("TOTAL", subtotal + deliveryFee)
        }
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        // MapView is optional - if it exists in layout, initialize it
        try {
            val mapViewId = resources.getIdentifier("mapView", "id", packageName)
            if (mapViewId != 0) {
                mapView = findViewById(mapViewId)
                mapView?.onCreate(savedInstanceState)
                mapView?.getMapAsync(this)
            }
        } catch (e: Exception) {
            // MapView not in layout, that's okay
        }
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
        /*val fabLocation = binding.root.findViewById<MaterialButton>(R.id.fabLocation)
        fabLocation?.setOnClickListener {
            centerMapOnCurrentLocation()
        }*/

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
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isCompassEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        // Add markers for farmer and buyer locations
        googleMap?.addMarker(
            MarkerOptions()
                .position(farmerLocation)
                .title("Farmer Location")
        )

        googleMap?.addMarker(
            MarkerOptions()
                .position(buyerLocation)
                .title("Buyer Location")
        )

        // Draw route between locations
        drawRoute()

        // Move camera to show both locations
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
            .include(farmerLocation)
            .include(buyerLocation)
            .build()

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun drawRoute() {
        // In a real app, use Directions API to get actual route
        // For demo, draw a straight line
        googleMap?.let { map ->
            val route = PolylineOptions()
                .add(farmerLocation, buyerLocation)
                .width(8f)
                .color(android.graphics.Color.parseColor("#2196F3"))

            map.addPolyline(route)
        }
    }

    private fun centerMapOnCurrentLocation() {
        // In a real app, get current location using FusedLocationProviderClient
        val centerLocation = LatLng(
            (farmerLocation.latitude + buyerLocation.latitude) / 2,
            (farmerLocation.longitude + buyerLocation.longitude) / 2
        )

        googleMap?.animateCamera(
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

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get user and farmer information
        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { buyerDoc ->
                val buyerName = buyerDoc.getString("fullName") ?: "Unknown Buyer"
                
                // Get farmer ID from first product (assuming all products are from same farmer)
                // In a real app, you'd need to handle multiple farmers
                val firstProduct = cartItems.firstOrNull()
                if (firstProduct != null) {
                    // Find farmer from product
                    firestore.collection("products")
                        .document(firstProduct.productId)
                        .get()
                        .addOnSuccessListener { productDoc ->
                            val farmerId = productDoc.getString("farmerId") ?: ""
                            val farmerName = productDoc.getString("owner") ?: "Unknown Farmer"
                            
                            // Create order in Firestore
                            createOrder(user.uid, buyerName, farmerId, farmerName, deliveryLocation)
                        }
                } else {
                    Toast.makeText(this, "Error: No products in cart", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun createOrder(
        buyerId: String,
        buyerName: String,
        farmerId: String,
        farmerName: String,
        deliveryLocation: String
    ) {
        val orderData = hashMapOf(
            "buyerId" to buyerId,
            "buyerName" to buyerName,
            "farmerId" to farmerId,
            "farmerName" to farmerName,
            "orderType" to Order.OrderType.DELIVERY.name,
            "deliveryLocation" to deliveryLocation,
            "subtotal" to subtotal,
            "deliveryFee" to deliveryFee,
            "total" to total,
            "status" to Order.OrderStatus.PENDING.name,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "estimatedDeliveryTime" to "45 min",
            "items" to cartItems.map { item ->
                hashMapOf(
                    "productId" to item.productId,
                    "name" to item.name,
                    "price" to item.price,
                    "unit" to item.unit,
                    "quantity" to item.quantity
                )
            }
        )

        firestore.collection("orders")
            .add(orderData)
            .addOnSuccessListener { documentReference ->
                val orderId = documentReference.id
                
                // Navigate to Payment Activity
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra("ORDER_ID", orderId)
                    putExtra("ORDER_TYPE", "DELIVERY")
                    putExtra("ORDER_NUMBER", "FC-${orderId.take(6)}")
                    putExtra("DELIVERY_LOCATION", deliveryLocation)
                    putExtra("ESTIMATED_TIME", "45 min")
                    putExtra("TOTAL_AMOUNT", total)
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // MapView lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}