package com.example.farmconnect.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.farmconnect.databinding.FragmentJobDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior

class JobDetailsFragment : Fragment() {

    private var _binding: FragmentJobDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        loadJobDetails()
    }

    private fun setupClickListeners() {
        // Add click listener to the map placeholder to start navigation
        // Or you can add a floating action button
    }

    private fun loadJobDetails() {
        // Here you would load actual job data from your data source
        // For now, we'll use the data from the design
        val distance = "15.2 km"
        val estimatedTime = "135 min"

        binding.distanceText.text = distance
        binding.timeText.text = estimatedTime

        // You can load more job details here:
        // - Pickup locations
        // - Customer information
        // - Route details
        // - etc.
    }

    private fun showStartNavigationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(com.example.farmconnect.R.layout.bottom_sheet_navigation, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val slideText = bottomSheetView.findViewById<android.widget.TextView>(com.example.farmconnect.R.id.slideText)

        bottomSheetDialog.show()

        // Make bottom sheet full expanded
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun startNavigation() {
        // Implement navigation logic here
        // This could:
        // 1. Open Google Maps or other navigation app
        // 2. Start in-app navigation
        // 3. Update job status

        showToast("Navigation started!")

        // Example: Open Google Maps
        try {
            val uri = "google.navigation:q=Farmer+Kamau's+Farm"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Google Maps not installed")
            // Fallback to web version or other navigation app
            val uri = "https://maps.google.com/?q=Farmer+Kamau's+Farm"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        // Update job status in your backend
        updateJobStatus("in_progress")
    }

    private fun updateJobStatus(status: String) {
        // Implement API call to update job status
        // This would typically involve calling your backend API
        // For now, we'll just show a toast
        when (status) {
            "in_progress" -> showToast("Job status updated to: In Progress")
            "completed" -> showToast("Job status updated to: Completed")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Method to update job details dynamically
    fun updateJobDetails(distance: String, time: String, destination: String) {
        binding.distanceText.text = distance
        binding.timeText.text = time
        // Update destination text view if you have one
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

