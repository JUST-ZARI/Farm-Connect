package com.example.farmconnect.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.farmconnect.LoginActivity
import com.example.farmconnect.PaymentActivity
import com.example.farmconnect.R
import com.example.farmconnect.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

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
        val user = Firebase.auth.currentUser ?: return

        val userId = user.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("fullName") ?: "Unnamed User"
                    binding.profileName.text = name
                }
            }
            .addOnFailureListener {
                binding.profileName.text = "Error loading name"
            }
    }

    private fun navigateToPaymentMethod() {
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        startActivity(intent)
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
        val user = Firebase.auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
        
        // Re-authenticate the user
        user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Update password
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        showToast("Password updated successfully")
                    } else {
                        showToast("Failed to update password: ${updateTask.exception?.message}")
                    }
                }
            } else {
                showToast("Authentication failed: ${authTask.exception?.message}")
            }
        } ?: showToast("User not found. Please log in again.")
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        
        val currentPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.currentPasswordLayout)
        val newPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.newPasswordLayout)
        val confirmPasswordLayout = dialogView.findViewById<TextInputLayout>(R.id.confirmPasswordLayout)
        val passwordMatchText = dialogView.findViewById<TextView>(R.id.passwordMatchText)
        
        val currentPassword = dialogView.findViewById<EditText>(R.id.currentPassword)
        val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)
        val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)
        
        // Real-time password validation
        newPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordStrength(s.toString(), newPasswordLayout)
                validatePasswordMatch(newPassword.text.toString(), confirmPassword.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordMatch(newPassword.text.toString(), s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change", null) // Set to null to override default button behavior
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.setOnShowListener {
            val positiveButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val currentPass = currentPassword.text.toString()
                val newPass = newPassword.text.toString()
                val confirmPass = confirmPassword.text.toString()
                
                if (validatePasswordChange(currentPass, newPass, confirmPass)) {
                    changePassword(currentPass, newPass)
                    dialog.dismiss()
                }
            }
        }
        
        dialog.show()
    }

    private fun validatePasswordStrength(password: String, passwordLayout: TextInputLayout) {
        val strength = when {
            password.isEmpty() -> {
                passwordLayout.helperText = "At least 8 characters"
                passwordLayout.setHelperTextColor(
                    ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.colorTextSecondary)
                ))
                0
            }
            password.length < 8 -> {
                passwordLayout.helperText = "Too short"
                passwordLayout.setHelperTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.colorError)
                ))
                1
            }
            !password.matches(Regex(".*[A-Z].*")) || 
            !password.matches(Regex(".*[a-z].*")) || 
            !password.matches(Regex(".*\\d.*")) -> {
                passwordLayout.helperText = "Include letters and numbers"
                passwordLayout.setHelperTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.colorError)
                ))
                2
            }
            else -> {
                passwordLayout.helperText = "Strong password"
                passwordLayout.setHelperTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.colorSuccess)
                ))
                3
            }
        }
        
        // Update password strength indicator
        // You can add a visual strength meter here if needed
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String) {
        val passwordMatchText = view?.findViewById<TextView>(R.id.passwordMatchText) ?: return
        
        if (confirmPassword.isEmpty()) {
            passwordMatchText.visibility = View.GONE
            return
        }
        
        if (password == confirmPassword) {
            passwordMatchText.text = "Passwords match"
            passwordMatchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSuccess))
            passwordMatchText.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle),
                null, null, null
            )
        } else {
            passwordMatchText.text = "Passwords don't match"
            passwordMatchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorError))
            passwordMatchText.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_error),
                null, null, null
            )
        }
        passwordMatchText.visibility = View.VISIBLE
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

