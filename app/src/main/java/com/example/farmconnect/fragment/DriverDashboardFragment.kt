package com.example.farmconnect.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmconnect.R
import com.example.farmconnect.adapter.PickupAdapter
import com.example.farmconnect.databinding.FragmentDriverDashboardBinding
import com.example.farmconnect.model.Pickup
import com.google.android.material.progressindicator.LinearProgressIndicator

class DriverDashboardFragment : Fragment() {

    private var _binding: FragmentDriverDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickupAdapter: PickupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDashboard()
        setupPickupsRecyclerView()
        setupClickListeners()
    }

    private fun setupDashboard() {
        // Set progress for monthly distance
        val progressDistance = view?.findViewById<LinearProgressIndicator>(R.id.progressDistance)
        progressDistance?.progress = 65 // 650km out of 1000km (65%)

        // You can load real data here from your backend
        loadDriverData()
    }

    private fun setupPickupsRecyclerView() {
        val pickups = getSamplePickups()

        pickupAdapter = PickupAdapter(
            pickups,
            onMapClick = { pickup ->
                showMapForPickup(pickup)
            },
            onRecordClick = { pickup ->
                recordPickupDetails(pickup)
            },
            onCancelClick = { pickup ->
                cancelPickup(pickup)
            },
            onPickupClick = { pickup ->
                handlePickupAction(pickup)
            }
        )

        binding.rvPickups.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pickupAdapter
        }
    }

    private fun loadDriverData() {
        // Example of loading data from backend
        // val driverStats = api.getDriverStats()
        // binding.textDeliveries.text = driverStats.deliveries.toString()
        // binding.textDistance.text = driverStats.distance.toString()
        // etc.
    }

    private fun setupClickListeners() {
        // Notification icon click
        binding.imageNotifications.setOnClickListener {
            showNotifications()
        }

        // Pricing tier clicks
        setupPricingTierClicks()
    }

    private fun setupPricingTierClicks() {
        // You can add click listeners for each pricing tier
        binding.cardTierA.setOnClickListener {
            showTierDetails("A", "Standard Tier - Basic delivery services")
        }
        binding.cardTierB.setOnClickListener {
            showTierDetails("B", "Premium Tier - Priority delivery with tracking")
        }
        binding.cardTierC.setOnClickListener {
            showTierDetails("C", "Economy Tier - Budget-friendly delivery options")
        }
    }

    // Pickup-related functions
    private fun showMapForPickup(pickup: Pickup) {
        Toast.makeText(requireContext(), "Opening map for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
        // Here you would integrate with Google Maps API
    }

    private fun recordPickupDetails(pickup: Pickup) {
        Toast.makeText(requireContext(), "Recording details for ${pickup.crop}", Toast.LENGTH_SHORT).show()
        // Here you would open a form to record pickup details
    }

    private fun cancelPickup(pickup: Pickup) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Cancel Pickup")
            .setMessage("Are you sure you want to cancel pickup from ${pickup.farmerName}?")
            .setPositiveButton("Yes, Cancel") { dialog, which ->
                // Update pickup status
                pickupAdapter.updatePickups(getSamplePickups().filter { it.id != pickup.id })
                Toast.makeText(requireContext(), "Pickup cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun handlePickupAction(pickup: Pickup) {
        when (pickup.status) {
            "scheduled" -> {
                Toast.makeText(requireContext(), "Starting pickup from ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
                // Update status to in_progress
            }
            "in_progress" -> {
                Toast.makeText(requireContext(), "Pickup completed for ${pickup.farmerName}", Toast.LENGTH_SHORT).show()
                // Update status to completed
            }
            else -> {
                Toast.makeText(requireContext(), "Pickup action for ${pickup.status}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSamplePickups(): List<Pickup> {
        return listOf(
            Pickup(
                "1",
                "Mwangi Kimani",
                "123 Green Valley Rd",
                "Organic Carrots",
                "250 kg",
                "scheduled"
            ),
            Pickup(
                "2",
                "Akiriyi Otieno",
                "456 Sunny Fields Ln",
                "Fresh Tomatoes",
                "400 kg",
                "scheduled"
            ),
            Pickup(
                "3",
                "Junna Omondi",
                "789 Orchard Way",
                "Sweet Potatoes",
                "300 kg",
                "scheduled"
            )
        )
    }

    private fun showNotifications() {
        Toast.makeText(requireContext(), "Notifications", Toast.LENGTH_SHORT).show()
        // Navigate to notifications activity
        // findNavController().navigate(R.id.action_driverDashboardFragment_to_notificationsFragment)
    }

    private fun showTierDetails(tier: String, details: String) {
        Toast.makeText(requireContext(), "Tier $tier: $details", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

