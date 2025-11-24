package com.example.farmconnect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val unit: String,
    var quantity: Int
) : Parcelable {

    fun getTotalPrice(): Double {
        return price * quantity
    }
}
