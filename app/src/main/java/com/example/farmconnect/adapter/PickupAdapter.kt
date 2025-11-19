package com.example.farmconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.farmconnect.R
import com.example.farmconnect.model.Pickup
import com.google.android.material.button.MaterialButton

class PickupAdapter(
    private var pickups: List<Pickup>,
    private val onMapClick: (Pickup) -> Unit,
    private val onRecordClick: (Pickup) -> Unit,
    private val onCancelClick: (Pickup) -> Unit,
    private val onPickupClick: (Pickup) -> Unit
) : RecyclerView.Adapter<PickupAdapter.PickupViewHolder>() {

    class PickupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFarmerName: TextView = itemView.findViewById(R.id.tvFarmerName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvCrop: TextView = itemView.findViewById(R.id.tvCrop)
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnMap: MaterialButton = itemView.findViewById(R.id.btnMap)
        val btnRecord: MaterialButton = itemView.findViewById(R.id.btnRecord)
        val btnCancel: MaterialButton = itemView.findViewById(R.id.btnCancel)
        val btnPickup: MaterialButton = itemView.findViewById(R.id.btnPickup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pickup, parent, false)
        return PickupViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickupViewHolder, position: Int) {
        val pickup = pickups[position]

        holder.tvFarmerName.text = pickup.farmerName
        holder.tvLocation.text = pickup.location
        holder.tvCrop.text = pickup.crop
        holder.tvWeight.text = pickup.weight
        holder.tvStatus.text = pickup.status.replace("_", " ").capitalize()

        // Set status color
        val statusColor = when (pickup.status) {
            "scheduled" -> R.color.pickup_status_scheduled
            "in_progress" -> R.color.driver_primary
            "completed" -> R.color.pickup_status_completed
            "cancelled" -> R.color.pickup_status_cancelled
            else -> R.color.pickup_status_scheduled
        }
        holder.tvStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, statusColor))

        // Update pickup button text based on status
        holder.btnPickup.text = when (pickup.status) {
            "scheduled" -> "🚚 Start Pickup"
            "in_progress" -> "📦 Complete Pickup"
            else -> "🚚 Pickup"
        }

        holder.btnMap.setOnClickListener { onMapClick(pickup) }
        holder.btnRecord.setOnClickListener { onRecordClick(pickup) }
        holder.btnCancel.setOnClickListener { onCancelClick(pickup) }
        holder.btnPickup.setOnClickListener { onPickupClick(pickup) }
    }

    override fun getItemCount(): Int = pickups.size

    fun updatePickups(newPickups: List<Pickup>) {
        pickups = newPickups
        notifyDataSetChanged()
    }
}