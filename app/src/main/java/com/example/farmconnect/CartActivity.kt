package com.example.farmconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmconnect.adapter.CartAdapter
import com.example.farmconnect.databinding.ActivityCartBinding
import com.example.farmconnect.model.CartItem
import com.example.farmconnect.model.Product

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive cart items from BuyerDashboard
        receiveCartItems()
        setupRecyclerView()
        setupClickListeners()
        updateOrderSummary()
    }

    private fun receiveCartItems() {
        val passedProducts = intent.getParcelableArrayListExtra<Product>("CART_ITEMS")
        passedProducts?.forEach { product ->
            // Convert Product to CartItem with quantity 1
            val existingItem = cartItems.find { it.productId == product.id }
            if (existingItem != null) {
                existingItem.quantity++
            } else {
                cartItems.add(
                    CartItem(
                        productId = product.id,
                        name = product.name,
                        price = product.price,
                        unit = product.unit,
                        emoji = product.emoji,
                        quantity = 1
                    )
                )
            }
        }
    }

    // ... rest of your CartActivity code remains the same
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChanged = { updateOrderSummary() },
            onItemRemoved = { item ->
                cartItems.remove(item)
                updateOrderSummary()
                Toast.makeText(this, "Removed ${item.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        // Delivery option selection
        binding.cardDelivery.setOnClickListener {
            selectDeliveryOption(true)
        }

        binding.cardPickup.setOnClickListener {
            selectDeliveryOption(false)
        }

        // Checkout button
        binding.btnCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                // Here you would navigate to order confirmation
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectDeliveryOption(isDelivery: Boolean) {
        if (isDelivery) {
            binding.cardDelivery.setCardBackgroundColor(getColor(R.color.delivery_option_selected))
            binding.cardPickup.setCardBackgroundColor(getColor(R.color.delivery_option_unselected))
            updateOrderSummary()
        } else {
            binding.cardDelivery.setCardBackgroundColor(getColor(R.color.delivery_option_unselected))
            binding.cardPickup.setCardBackgroundColor(getColor(R.color.delivery_option_selected))
            updateOrderSummary()
        }
    }

    private fun updateOrderSummary() {
        val subtotal = cartItems.sumOf { it.getTotalPrice() }
        val deliveryFee = if (binding.cardDelivery.cardBackgroundColor.defaultColor == getColor(R.color.delivery_option_selected)) {
            8.00 // Delivery fee
        } else {
            0.00 // Pickup is free
        }
        val total = subtotal + deliveryFee

        binding.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"
        binding.tvDeliveryFee.text = "$${String.format("%.2f", deliveryFee)}"
        binding.tvTotal.text = "$${String.format("%.2f", total)}"

        cartAdapter.updateCartItems(cartItems)
    }
}