package com.example.farmconnect.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmconnect.adapter.OrderAdapter
import com.example.farmconnect.databinding.FragmentFarmerDashboardBinding
import com.example.farmconnect.model.CartItem
import com.example.farmconnect.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FarmerDashboardFragment : Fragment() {

    private var _binding: FragmentFarmerDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var orderAdapter: OrderAdapter
    private var ordersListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            orders = mutableListOf(),
            onAcceptClick = { order -> acceptOrder(order) },
            onRejectClick = { order -> rejectOrder(order) }
        )

        // Assuming you have a RecyclerView in the layout - you may need to add it
        // For now, we'll work with the existing layout structure
    }

    private fun loadOrders() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please login", Toast.LENGTH_SHORT).show()
            return
        }

        // Listen for orders where this farmer is the recipient
        ordersListener = firestore.collection("orders")
            .whereEqualTo("farmerId", user.uid)
            .whereIn("status", listOf("PENDING", "ACCEPTED", "IN_PROGRESS"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading orders: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val buyerId = doc.getString("buyerId") ?: return@mapNotNull null
                        val buyerName = doc.getString("buyerName") ?: "Unknown Buyer"
                        val farmerId = doc.getString("farmerId") ?: return@mapNotNull null
                        val farmerName = doc.getString("farmerName") ?: "Unknown Farmer"
                        val orderType = Order.OrderType.valueOf(doc.getString("orderType") ?: "DELIVERY")
                        val status = Order.OrderStatus.valueOf(doc.getString("status") ?: "PENDING")
                        val subtotal = doc.getDouble("subtotal") ?: 0.0
                        val deliveryFee = doc.getDouble("deliveryFee") ?: 0.0
                        val total = doc.getDouble("total") ?: 0.0
                        val deliveryLocation = doc.getString("deliveryLocation")
                        
                        // Get items
                        val items = (doc.get("items") as? List<Map<String, Any>>)
                            ?.map { itemMap ->
                                CartItem(
                                    productId = itemMap["productId"] as? String ?: "",
                                    name = itemMap["name"] as? String ?: "",
                                    price = (itemMap["price"] as? Number ?: 0.0).toDouble(),
                                    unit = itemMap["unit"] as? String ?: "",
                                    quantity = ((itemMap["quantity"] as? Long) ?: 0L).toInt()
                                )
                            }
                            ?: emptyList()
                        
                        Order(
                            id = doc.id,
                            buyerId = buyerId,
                            buyerName = buyerName,
                            farmerId = farmerId,
                            farmerName = farmerName,
                            items = items,
                            orderType = orderType,
                            deliveryLocation = deliveryLocation,
                            subtotal = subtotal,
                            deliveryFee = deliveryFee,
                            total = total,
                            status = status
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                orderAdapter.updateOrders(orders)
            }
    }

    private fun acceptOrder(order: Order) {
        firestore.collection("orders")
            .document(order.id)
            .update("status", Order.OrderStatus.ACCEPTED.name)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Order accepted!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to accept order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun rejectOrder(order: Order) {
        firestore.collection("orders")
            .document(order.id)
            .update("status", Order.OrderStatus.REJECTED.name)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Order rejected", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to reject order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ordersListener?.remove()
        _binding = null
    }
}

