package com.example.farmconnect.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val emoji: String // Store emoji character instead of image resource
)