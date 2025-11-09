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
                    Toast.makeText(this, "Cart feature coming soon!", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Added ${product.name} to cart!", Toast.LENGTH_SHORT).show()
        // Here you would add the product to the cart in your data model
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