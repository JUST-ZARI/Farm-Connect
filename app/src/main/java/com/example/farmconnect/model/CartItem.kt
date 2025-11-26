package com.example.farmconnect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    var quantity: Int = 1,
    val imageUrl: String? = null,
    val farmerId: String = ""
) : Parcelable {

    fun getTotalPrice(): Double {
        return price * quantity
    }
}
