package com.example.farmconnect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val buyerId: String,
    val buyerName: String,
    val farmerId: String,
    val farmerName: String,
    val items: List<CartItem>,
    val orderType: OrderType, // DELIVERY or PICKUP
    val deliveryLocation: String? = null, // Required for DELIVERY
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val status: OrderStatus, // PENDING, ACCEPTED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: String? = null
) : Parcelable {
    enum class OrderType {
        DELIVERY, PICKUP
    }
    
    enum class OrderStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}

