package com.example.farmconnect.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.farmconnect.R
import com.example.farmconnect.databinding.FragmentAssignedPickupsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AssignedPickupsFragment : Fragment() {

    private var _binding: FragmentAssignedPickupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignedPickupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupToolbar()

    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            // Navigate back when back button is clicked
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        // Map Destination buttons
        binding.btnMap1.setOnClickListener { onMapDestinationClicked(1) }
        binding.btnMap2?.setOnClickListener { onMapDestinationClicked(2) }
        binding.btnMap3?.setOnClickListener { onMapDestinationClicked(3) }

        // Record Pickup buttons
        binding.btnRecord1.setOnClickListener { onRecordPickupClicked(1) }
        binding.btnRecord2?.setOnClickListener { onRecordPickupClicked(2) }
        binding.btnRecord3?.setOnClickListener { onRecordPickupClicked(3) }
    }

    private fun onMapDestinationClicked(pickupId: Int) {
        // Navigate to JobDetailsActivity
        findNavController().navigate(R.id.action_assignedPickupsFragment_to_jobDetailsActivity)
    }

    private fun onRecordPickupClicked(pickupId: Int) {
        // Navigate to RecordPickupActivity
        findNavController().navigate(R.id.action_assignedPickupsFragment_to_recordPickupActivity)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}