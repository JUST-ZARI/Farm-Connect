package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileName: TextView
    private lateinit var paymentMethodOption: LinearLayout
    private lateinit var changePasswordOption: LinearLayout
    private lateinit var notificationOption: LinearLayout
    private lateinit var privacyOption: LinearLayout
    private lateinit var logoutOption: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupClickListeners()
        loadUserData()
    }

    private fun initializeViews() {
        profileName = findViewById(R.id.profileName)
        paymentMethodOption = findViewById(R.id.paymentMethodOption)
        changePasswordOption = findViewById(R.id.changePasswordOption)
        notificationOption = findViewById(R.id.notificationOption)
        privacyOption = findViewById(R.id.privacyOption)
        logoutOption = findViewById(R.id.logoutOption)
    }

    private fun setupClickListeners() {
        // Payment Method Option
        paymentMethodOption.setOnClickListener {
            navigateToPaymentMethod()
        }

        // Change Password Option
        changePasswordOption.setOnClickListener {
            showChangePasswordDialog()
        }

        // Notification Settings
        notificationOption.setOnClickListener {
            navigateToNotificationSettings()
        }

        // Privacy Settings
        privacyOption.setOnClickListener {
            navigateToPrivacySettings()
        }

        // Logout Option
        logoutOption.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserData() {
        // Here you would typically load user data from your data source
        // For now, we'll use the hardcoded name from the design
        val userName = "Alex Mull"
        profileName.text = userName

        // You can load more user data here like:
        // - Profile picture
        // - Email
        // - Other user information
    }

    private fun navigateToPaymentMethod() {
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)

        // Optional: Add animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

        val currentPassword = dialogView.findViewById<EditText>(R.id.currentPassword)
        val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)
        val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { dialog, _ ->
                val currentPass = currentPassword.text.toString()
                val newPass = newPassword.text.toString()
                val confirmPass = confirmPassword.text.toString()

                if (validatePasswordChange(currentPass, newPass, confirmPass)) {
                    changePassword(currentPass, newPass)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun validatePasswordChange(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        if (currentPassword.isEmpty()) {
            showToast("Please enter current password")
            return false
        }

        if (newPassword.isEmpty()) {
            showToast("Please enter new password")
            return false
        }

        if (newPassword.length < 6) {
            showToast("New password must be at least 6 characters")
            return false
        }

        if (newPassword != confirmPassword) {
            showToast("New passwords don't match")
            return false
        }

        // Add more validation as needed
        return true
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        // Implement your password change logic here
        // This would typically involve API calls to your backend

        showToast("Password changed successfully")

        // You might want to log the user out after password change
        // or just return to the previous screen
    }

    private fun navigateToNotificationSettings() {
        // Navigate to Notification Settings Activity
        val intent = Intent(this, NotificationSettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToPrivacySettings() {
        // Navigate to Privacy Settings Activity
        val intent = Intent(this, PrivacySettingsActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        // Implement your logout logic here
        // This would typically involve:
        // - Clearing user session
        // - Clearing cached data
        // - Navigating to login screen

        showToast("Logged out successfully")

        // Example: Navigate to Login Activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Method to update profile name if needed
    fun updateProfileName(newName: String) {
        profileName.text = newName
    }

    // Method to handle profile picture change
    fun changeProfilePicture() {
        // Implement profile picture change logic
        // This could open camera or gallery to select new picture
    }
}