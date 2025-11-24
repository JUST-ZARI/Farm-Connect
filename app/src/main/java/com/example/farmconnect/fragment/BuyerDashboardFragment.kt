package com.example.farmconnect.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.farmconnect.adapter.ProductAdapter
import com.example.farmconnect.databinding.FragmentBuyerDashboardBinding
import com.example.farmconnect.model.Product

class BuyerDashboardFragment : Fragment() {

    private var _binding: FragmentBuyerDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter
    private val cartItems = mutableListOf<Product>() // Track cart items

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            addToCart(product)
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
        
        // Load products from Firestore
        loadProductsFromFirestore()
    }
    
    private fun loadProductsFromFirestore() {
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.documents.mapNotNull { doc ->
                    try {
                        Product(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            quantity = doc.getString("quantity") ?: "0",
                            unit = doc.getString("unit") ?: "",
                            imageUrl = doc.getString("imageUrl"),
                            category = doc.getString("category") ?: "",
                            owner = doc.getString("owner") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                productAdapter.updateProducts(products)
            }
            .addOnFailureListener { e ->
                // Fallback to sample products if Firestore fails
                productAdapter.updateProducts(getSampleProducts())
                android.util.Log.e("BuyerDashboard", "Error loading products: ${e.message}")
            }
    }

    private fun setupClickListeners() {
        binding.btnFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Filter options coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSort.setOnClickListener {
            Toast.makeText(requireContext(), "Sort options coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToCart(product: Product) {
        // Add product to cart
        cartItems.add(product)
        Toast.makeText(requireContext(), "Added ${product.name} to cart!", Toast.LENGTH_SHORT).show()

        // Ask user if they want to go to cart
        android.app.AlertDialog.Builder(requireContext())
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
        // Navigate to cart fragment using Navigation Component
        val bundle = Bundle().apply {
            putParcelableArrayList("CART_ITEMS", ArrayList(cartItems))
        }
        findNavController().navigate(
            com.example.farmconnect.R.id.action_buyerDashboardFragment_to_cartFragment,
            bundle
        )
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
                imageUrl = null,
                category = "Vegetables",
                owner = "Leafy Greens Farm"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

