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
            Product("1", "Fresh Tomatoes", 2.50, "kg", "🍅"),
            Product("2", "Organic Carrots", 1.80, "kg", "🥕"),
            Product("3", "Sweet Potatoes", 3.00, "kg", "🍠"),
            Product("4", "Green Bell Peppers", 4.20, "kg", "🫑"),
            Product("5", "Farm Fresh Eggs", 5.00, "dozen", "🥚"),
            Product("6", "Crisp Lettuce", 1.50, "head", "🥬")
        )
    }
}