package com.example.farmconnect.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: String,
    val unit: String,
    val emoji: String,
    val imageUrl: String? = null,
    val category: String,
    val owner: String
) : Parcelable