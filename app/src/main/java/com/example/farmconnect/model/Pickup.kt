package com.example.farmconnect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pickup(
    val id: String,
    val farmerName: String,
    val location: String,
    val crop: String,
    val weight: String,
    val status: String // "scheduled", "in_progress", "completed", "cancelled"
) : Parcelable