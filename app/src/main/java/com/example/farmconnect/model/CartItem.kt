package com.example.farmconnect.model

data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val unit: String,
    val emoji: String,
    var quantity: Int
) {
    fun getTotalPrice(): Double {
        return price * quantity
    }
}