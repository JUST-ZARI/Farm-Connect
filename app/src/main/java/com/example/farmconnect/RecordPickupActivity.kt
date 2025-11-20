package com.example.farmconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityRecordPickupBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecordPickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordPickupBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        // Record Pickup Button Click
        binding.btnRecordPickup.setOnClickListener {
            if (validateForm()) {
                recordPickup()
            }
        }

        // Photo Upload Card Click
        binding.cbUploadPhoto.setOnClickListener {
            openImagePicker()
        }

        // Also make the entire card clickable for photo upload
        /*binding.photoUploadCard.setOnClickListener {
            binding.cbUploadPhoto.isChecked = !binding.cbUploadPhoto.isChecked
            if (binding.cbUploadPhoto.isChecked) {
                openImagePicker()
            } else {
                selectedImageUri = null
            }
        }*/
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to Home
                    val intent = Intent(this, DriverDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_jules -> {
                    // Navigate to Jules/Profile
                    Toast.makeText(this, "Jules/Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profits -> {
                    // Navigate to Profits
                    Toast.makeText(this, "Profits", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Set the current item as selected
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun validateForm(): Boolean {
        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val conditionSelected = binding.rgCondition.checkedRadioButtonId != -1

        // Clear previous errors
        binding.etFarmerName.error = null
        binding.etCrop.error = null
        binding.etWeight.error = null

        if (farmerName.isEmpty()) {
            binding.etFarmerName.error = "Please enter farmer's name"
            return false
        }

        if (crop.isEmpty()) {
            binding.etCrop.error = "Please enter crop type"
            return false
        }

        if (weight.isEmpty()) {
            binding.etWeight.error = "Please enter weight"
            return false
        }

        if (!conditionSelected) {
            Toast.makeText(this, "Please select crop condition", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun recordPickup() {
        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val comments = binding.etComments.text.toString().trim()

        // Get selected condition
        val condition = when (binding.rgCondition.checkedRadioButtonId) {
            R.id.rbPerfect -> "Perfect"
            R.id.rbGood -> "Good"
            R.id.rbFair -> "Fair"
            R.id.rbBad -> "Bad"
            else -> "Not specified"
        }

        // Get photo upload status
        val photoUploaded = binding.cbUploadPhoto.isChecked

        // Here you would typically:
        // 1. Upload the image to your server (if selected)
        // 2. Send the pickup data to your backend API
        // 3. Show success message and navigate

        val message = "Pickup recorded:\n" +
                "Farmer: $farmerName\n" +
                "Crop: $crop\n" +
                "Weight: $weight kg\n" +
                "Condition: $condition\n" +
                "Photo: ${if (photoUploaded) "Uploaded" else "Not uploaded"}\n" +
                "Comments: ${if (comments.isNotEmpty()) comments else "None"}"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        // Clear form after successful submission
        clearForm()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.cbUploadPhoto.isChecked = true
                Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        binding.etFarmerName.text?.clear()
        binding.etCrop.text?.clear()
        binding.etWeight.text?.clear()
        binding.etComments.text?.clear()
        binding.rgCondition.clearCheck()
        binding.cbUploadPhoto.isChecked = false
        selectedImageUri = null
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }
}