package com.example.farmconnect.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmconnect.DeliveryDetailsActivity
import com.example.farmconnect.R
import com.example.farmconnect.adapter.CartAdapter
import com.example.farmconnect.databinding.FragmentCartBinding
import com.example.farmconnect.model.CartItem
import com.example.farmconnect.model.Order
import com.example.farmconnect.model.Product

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Receive cart items from arguments
        receiveCartItems()
        setupRecyclerView()
        setupClickListeners()
        updateOrderSummary()
    }

    private fun receiveCartItems() {
        val passedProducts = arguments?.getParcelableArrayList<Product>("CART_ITEMS")
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
                        quantity = 1
                    )
                )
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChanged = { updateOrderSummary() },
            onItemRemoved = { item ->
                cartItems.remove(item)
                updateOrderSummary()
                Toast.makeText(requireContext(), "Removed ${item.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
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
                proceedToCheckout()
            } else {
                Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectDeliveryOption(isDelivery: Boolean) {
        if (isDelivery) {
            binding.cardDelivery.setCardBackgroundColor(requireContext().getColor(R.color.delivery_option_selected))
            binding.cardPickup.setCardBackgroundColor(requireContext().getColor(R.color.delivery_option_unselected))
            updateOrderSummary()
        } else {
            binding.cardDelivery.setCardBackgroundColor(requireContext().getColor(R.color.delivery_option_unselected))
            binding.cardPickup.setCardBackgroundColor(requireContext().getColor(R.color.delivery_option_selected))
            updateOrderSummary()
        }
    }

    private fun updateOrderSummary() {
        val subtotal = cartItems.sumOf { it.getTotalPrice() }
        val deliveryFee = if (binding.cardDelivery.cardBackgroundColor.defaultColor == requireContext().getColor(R.color.delivery_option_selected)) {
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

    private fun proceedToCheckout() {
        val isDeliverySelected = binding.cardDelivery.cardBackgroundColor.defaultColor == 
            requireContext().getColor(R.color.delivery_option_selected)
        
        // Create the cart items list to pass to the next activity
        val cartProducts = ArrayList(cartItems.map { 
            Product(
                id = it.productId,
                name = it.name,
                description = "",
                price = it.price,
                quantity = it.quantity.toString(),
                unit = it.unit,
                imageUrl = null,
                category = "",
                owner = ""
            )
        })
        
        // Create bundle with cart data
        val bundle = Bundle().apply {
            putParcelableArrayList("CART_ITEMS", cartProducts)
            putDouble("SUBTOTAL", cartItems.sumOf { it.getTotalPrice() })
            putDouble("DELIVERY_FEE", if (isDeliverySelected) 8.00 else 0.00)
            putDouble("TOTAL", cartItems.sumOf { it.getTotalPrice() } + if (isDeliverySelected) 8.00 else 0.00)
        }
        
        // Navigate based on selection
        if (isDeliverySelected) {
            // Navigate to Delivery Details
            findNavController().navigate(R.id.action_cartFragment_to_deliveryDetailsActivity, bundle)
        } else {
            // Navigate to Pickup Details
            findNavController().navigate(R.id.action_cartFragment_to_pickupDetailsActivity, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

