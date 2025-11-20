package com.example.farmconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.farmconnect.adapter.ProductAdapter
import com.example.farmconnect.databinding.ActivityBuyerDashboardBinding
import com.example.farmconnect.model.Product

class BuyerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerDashboardBinding
    private lateinit var productAdapter: ProductAdapter
    private val cartItems = mutableListOf<Product>() // Track cart items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        val products = getSampleProducts()

        productAdapter = ProductAdapter(products) { product ->
            addToCart(product)
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(this@BuyerDashboardActivity, 2)
            adapter = productAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnFilter.setOnClickListener {
            Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSort.setOnClickListener {
            Toast.makeText(this, "Sort options coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home
                    true
                }

                R.id.navigation_cart -> {
                    navigateToCart()
                    true
                }

                R.id.navigation_profile -> {
                    Toast.makeText(this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun addToCart(product: Product) {
        // Add product to cart
        cartItems.add(product)
        Toast.makeText(this, "Added ${product.name} to cart!", Toast.LENGTH_SHORT).show()

        // Ask user if they want to go to cart
        android.app.AlertDialog.Builder(this)
            .setTitle("Added to Cart")
            .setMessage("${product.name} has been added to your cart. Do you want to view your cart?")
            .setPositiveButton("View Cart") { dialog, which ->
                navigateToCart()
            }
            .setNegativeButton("Continue Shopping") { dialog, which ->
                // User stays on the current page
            }
            .show()
    }

    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java).apply {
            // Pass the entire cart items list
            putExtra("CART_ITEMS", ArrayList(cartItems))
        }
        startActivity(intent)
    }

    private fun getSampleProducts(): List<Product> {
        return listOf(
            Product(
                id = "1",
                name = "Fresh Tomatoes",
                description = "Juicy, farm-fresh tomatoes",
                price = 2.50,
                quantity = "10",
                unit = "kg",
                emoji = "🍅",
                imageUrl = null,
                category = "Vegetables",
                owner = "Sunrise Farm"
            ),
            Product(
                id = "2",
                name = "Organic Carrots",
                description = "Sweet organic carrots",
                price = 1.80,
                quantity = "8",
                unit = "kg",
                emoji = "🥕",
                imageUrl = null,
                category = "Vegetables",
                owner = "Green Valley Farm"
            ),
            Product(
                id = "3",
                name = "Sweet Potatoes",
                description = "Sweet, starchy potatoes perfect for roasting",
                price = 3.00,
                quantity = "15",
                unit = "kg",
                emoji = "🍠",
                imageUrl = null,
                category = "Root Crops",
                owner = "Harvest Fields"
            ),
            Product(
                id = "4",
                name = "Green Bell Peppers",
                description = "Crisp and fresh bell peppers",
                price = 4.20,
                quantity = "12",
                unit = "kg",
                emoji = "🫑",
                imageUrl = null,
                category = "Vegetables",
                owner = "Garden Fresh Farm"
            ),
            Product(
                id = "5",
                name = "Farm Fresh Eggs",
                description = "Free-range farm eggs",
                price = 5.00,
                quantity = "30",
                unit = "dozen",
                emoji = "🥚",
                imageUrl = null,
                category = "Poultry",
                owner = "Happy Hens Farm"
            ),
            Product(
                id = "6",
                name = "Crisp Lettuce",
                description = "Fresh and crispy lettuce",
                price = 1.50,
                quantity = "20",
                unit = "head",
                emoji = "🥬",
                imageUrl = null,
                category = "Vegetables",
                owner = "Leafy Greens Farm"
            )
        )
    }
}