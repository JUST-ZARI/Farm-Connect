package com.example.farmconnect.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.farmconnect.LoginActivity
import com.example.farmconnect.PaymentActivity
import com.example.farmconnect.R
import com.example.farmconnect.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        loadUserData()
    }

    private fun setupClickListeners() {
        // Payment Method Option
        binding.paymentMethodOption.setOnClickListener {
            navigateToPaymentMethod()
        }

        // Change Password Option
        binding.changePasswordOption.setOnClickListener {
            showChangePasswordDialog()
        }

        // Logout Option
        binding.logoutOption.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserData() {
        // Here you would typically load user data from your data source
        // For now, we'll use the hardcoded name from the design
        val userName = "Alex Mull"
        binding.profileName.text = userName

        // You can load more user data here like:
        // - Profile picture
        // - Email
        // - Other user information
    }

    private fun navigateToPaymentMethod() {
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        startActivity(intent)
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

        val currentPassword = dialogView.findViewById<EditText>(R.id.currentPassword)
        val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)
        val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)

        AlertDialog.Builder(requireContext())
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

        return true
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        // Implement your password change logic here
        // This would typically involve API calls to your backend

        showToast("Password changed successfully")
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
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

        // Navigate to Login Activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Method to update profile name if needed
    fun updateProfileName(newName: String) {
        binding.profileName.text = newName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

