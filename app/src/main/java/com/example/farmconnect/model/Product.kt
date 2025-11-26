package com.example.farmconnect.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize


@IgnoreExtraProperties
@Parcelize
data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var unit: String = "",
    var imageUrl: String? = null,
    var category: String = "",
    var owner: String = "",
    var farmerId: String = ""
) : Parcelable