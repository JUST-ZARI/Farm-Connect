package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var distanceText: TextView
    private lateinit var timeText: TextView
    private lateinit var startNavigationButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        initializeViews()
        setupClickListeners()
        setupBottomNavigation()
        loadJobDetails()
    }

    private fun initializeViews() {
        distanceText = findViewById(R.id.distanceText)
        timeText = findViewById(R.id.timeText)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // You can add a floating action button for start navigation
        // or use the bottom sheet approach as shown below
    }

    private fun setupClickListeners() {
        // Add click listener to the map placeholder to start navigation
        /*val mapCard = findViewById<CardView>(R.id.mapCard)
        mapCard?.setOnClickListener {
            showStartNavigationBottomSheet()
        }*/

        // Or you can add a floating action button
        // startNavigationButton.setOnClickListener { showStartNavigationBottomSheet() }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_completed -> {
                    // Navigate to Completed Jobs
                    showToast("Completed Jobs")
                    true
                }
                R.id.navigation_home -> {
                    // Navigate to Home
                    finish()
                    true
                }
                R.id.navigation_jobs -> {
                    // Already on Jobs, do nothing or refresh
                    showToast("Jobs")
                    true
                }
                R.id.navigation_profile -> {
                    // Navigate to Profile
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Set jobs as selected
        bottomNavigation.selectedItemId = R.id.navigation_jobs
    }

    private fun loadJobDetails() {
        // Here you would load actual job data from your data source
        // For now, we'll use the data from the design
        val distance = "15.2 km"
        val estimatedTime = "135 min"

        distanceText.text = distance
        timeText.text = estimatedTime

        // You can load more job details here:
        // - Pickup locations
        // - Customer information
        // - Route details
        // - etc.
    }

    private fun showStartNavigationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_navigation, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        //val slideLayout = bottomSheetView.findViewById<LinearLayout>(R.id.slideLayout)
        val slideText = bottomSheetView.findViewById<TextView>(R.id.slideText)

        // Set up slide gesture
        //setupSlideGesture(slideLayout, slideText, bottomSheetDialog)

        bottomSheetDialog.show()

        // Make bottom sheet full expanded
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /*private fun setupSlideGesture(slideLayout: LinearLayout, slideText: TextView, dialog: BottomSheetDialog) {
        var isSliding = false
        var startX = 0f

        slideLayout.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    isSliding = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isSliding) {
                        val slideAmount = event.x - startX
                        if (slideAmount > 100) { // Threshold for successful slide
                            slideText.text = "Starting navigation..."
                            v.alpha = 0.7f
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isSliding) {
                        val slideAmount = event.x - startX
                        if (slideAmount > 100) { // Successful slide
                            startNavigation()
                            dialog.dismiss()
                        } else {
                            // Reset
                            slideText.text = "Slide to navigate"
                            v.alpha = 1.0f
                        }
                        isSliding = false
                    }
                    true
                }
                else -> false
            }
        }
    }*/

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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Method to update job details dynamically
    fun updateJobDetails(distance: String, time: String, destination: String) {
        distanceText.text = distance
        timeText.text = time
        // Update destination text view if you have one
    }

    // Handle back button press
   /* override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }*/
}