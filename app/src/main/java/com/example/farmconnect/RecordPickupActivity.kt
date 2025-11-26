package com.example.farmconnect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityRecordPickupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class RecordPickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordPickupBinding
    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var isUpdating = false
    private var pickupId: String? = null

    // Image picker
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we are editing an existing pickup
        intent?.extras?.let { bundle ->
            isUpdating = true
            pickupId = bundle.getString("PICKUP_ID")
            binding.topAppBar.title = "Update Pickup"
            binding.btnRecordPickup.text = "Update Pickup"

            binding.etFarmerName.setText(bundle.getString("FARMER_NAME", ""))
            binding.etCrop.setText(bundle.getString("CROP", ""))
            binding.etWeight.setText(bundle.getString("WEIGHT", ""))
            binding.etComments.setText(bundle.getString("COMMENTS", ""))

            val condition = bundle.getString("CONDITION", "")
            when (condition) {
                "Perfect" -> binding.rbPerfect.isChecked = true
                "Good" -> binding.rbGood.isChecked = true
                "Fair" -> binding.rbFair.isChecked = true
                "Bad" -> binding.rbBad.isChecked = true
            }
        }

        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            showDiscardChangesDialog()
        }

        // Optional menu for delete; only visible when updating
        binding.topAppBar.inflateMenu(R.menu.record_pickup_menu)
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    showDeleteConfirmation()
                    true
                }

                else -> false
            }
        }

        if (!isUpdating) {
            binding.topAppBar.menu.findItem(R.id.action_delete)?.isVisible = false
        }
    }

    private fun setupClickListeners() {
        // Record / Update button
        binding.btnRecordPickup.setOnClickListener {
            if (validateForm()) {
                if (isUpdating) {
                    updatePickup()
                } else {
                    recordPickup()
                }
            }
        }

        // Photo upload row
        binding.uploadPhoto.setOnClickListener {
            openImagePicker()
        }
    }

    /* ------------ Form helpers ------------ */

    private fun openImagePicker() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }
        pickImage.launch(intent)
    }

    private fun getSelectedCondition(): String? {
        return when (binding.rgCondition.checkedRadioButtonId) {
            R.id.rbPerfect -> "Perfect"
            R.id.rbGood -> "Good"
            R.id.rbFair -> "Fair"
            R.id.rbBad -> "Bad"
            else -> null
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val condition = getSelectedCondition()

        // Clear previous errors
        binding.etFarmerName.error = null
        binding.etCrop.error = null
        binding.etWeight.error = null

        if (TextUtils.isEmpty(farmerName)) {
            binding.etFarmerName.error = "Farmer name is required"
            binding.etFarmerName.requestFocus()
            isValid = false
        }

        if (TextUtils.isEmpty(crop)) {
            binding.etCrop.error = "Crop name is required"
            if (isValid) binding.etCrop.requestFocus()
            isValid = false
        }

        if (TextUtils.isEmpty(weight)) {
            binding.etWeight.error = "Weight is required"
            if (isValid) binding.etWeight.requestFocus()
            isValid = false
        } else if (!weight.matches("\\d+(\\.\\d+)?".toRegex())) {
            binding.etWeight.error = "Please enter a valid weight"
            if (isValid) binding.etWeight.requestFocus()
            isValid = false
        }

        if (condition == null) {
            Snackbar.make(binding.root, "Please select condition", Snackbar.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun hasChanges(): Boolean {
        return binding.etFarmerName.text?.isNotEmpty() == true ||
                binding.etCrop.text?.isNotEmpty() == true ||
                binding.etWeight.text?.isNotEmpty() == true ||
                binding.etComments.text?.isNotEmpty() == true ||
                binding.rgCondition.checkedRadioButtonId != -1 ||
                selectedImageUri != null
    }

    /* ------------ Dialogs ------------ */

    private fun showDiscardChangesDialog() {
        if (hasChanges()) {
            AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Discard them?")
                .setPositiveButton("Discard") { _, _ -> finish() }
                .setNegativeButton("Keep Editing", null)
                .show()
        } else {
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Pickup")
            .setMessage("Are you sure you want to delete this pickup record?")
            .setPositiveButton("Delete") { _, _ -> deletePickup() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /* ------------ Firestore / Storage ------------ */

    private fun recordPickup() {
        showLoading(true)

        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim().toDouble()
        val condition = getSelectedCondition() ?: ""
        val comments = binding.etComments.text.toString().trim()

        fun saveToDb(imageUrl: String?) {
            val data = hashMapOf(
                "farmerName" to farmerName,
                "crop" to crop,
                "weight" to weight,
                "condition" to condition,
                "comments" to comments,
                "imageUrl" to imageUrl,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("pickups")
                .add(data)
                .addOnSuccessListener {
                    showLoading(false)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Error saving pickup: ${e.message}")
                }
        }

        if (selectedImageUri != null) {
            val fileName = "pickups/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)
            ref.putFile(selectedImageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Upload failed")
                    }
                    ref.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    saveToDb(uri.toString())
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Image upload failed: ${e.message}")
                }
        } else {
            saveToDb(null)
        }
    }

    private fun updatePickup() {
        val id = pickupId
        if (id == null) {
            showError("Error: Missing pickup ID")
            return
        }

        showLoading(true)

        val farmerName = binding.etFarmerName.text.toString().trim()
        val crop = binding.etCrop.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim().toDouble()
        val condition = getSelectedCondition() ?: ""
        val comments = binding.etComments.text.toString().trim()

        fun updateDb(imageUrl: String?) {
            val updates = hashMapOf<String, Any>(
                "farmerName" to farmerName,
                "crop" to crop,
                "weight" to weight,
                "condition" to condition,
                "comments" to comments
            )
            if (imageUrl != null) {
                updates["imageUrl"] = imageUrl
            }

            db.collection("pickups").document(id)
                .update(updates)
                .addOnSuccessListener {
                    showLoading(false)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Error updating pickup: ${e.message}")
                }
        }

        if (selectedImageUri != null) {
            val fileName = "pickups/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)
            ref.putFile(selectedImageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception ?: Exception("Upload failed")
                    }
                    ref.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    updateDb(uri.toString())
                }
                .addOnFailureListener { e ->
                    showLoading(false)
                    showError("Image upload failed: ${e.message}")
                }
        } else {
            updateDb(null)
        }
    }

    private fun deletePickup() {
        val id = pickupId
        if (id == null) {
            showError("Error: Missing pickup ID")
            return
        }

        showLoading(true)
        db.collection("pickups").document(id)
            .delete()
            .addOnSuccessListener {
                showLoading(false)
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showError("Error deleting pickup: ${e.message}")
            }
    }

    /* ------------ UI helpers ------------ */

    private fun showLoading(loading: Boolean) {
        binding.btnRecordPickup.isEnabled = !loading
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
