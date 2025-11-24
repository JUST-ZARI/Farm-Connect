package com.example.farmconnect

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.farmconnect.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login Button Click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }

        // Forgot Password Click
        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        // Sign Up Click
        binding.tvSignUp.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.emailInputLayout.error = "Email is required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInputLayout.error = "Please enter a valid email address"
                false
            }
            password.isEmpty() -> {
                binding.passwordInputLayout.error = "Password is required"
                false
            }
            password.length < 6 -> {
                binding.passwordInputLayout.error = "Password must be at least 6 characters"
                false
            }
            else -> {
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                true
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        // Show loading state
        binding.btnLogin.text = "Logging in..."
        binding.btnLogin.isEnabled = false

        // Sign in with Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    user?.let {
                        // Get user role from Firestore and navigate accordingly
                        getUserRoleAndNavigate(it.uid)
                    }
                } else {
                    // Login failed
                    binding.btnLogin.text = "Login"
                    binding.btnLogin.isEnabled = true

                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_USER_NOT_FOUND" -> {
                                binding.emailInputLayout.error = "No account found with this email"
                                Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_LONG).show()
                            }
                            "ERROR_WRONG_PASSWORD" -> {
                                binding.passwordInputLayout.error = "Incorrect password"
                                Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_INVALID_EMAIL" -> {
                                binding.emailInputLayout.error = "Invalid email address"
                                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_USER_DISABLED" -> {
                                Toast.makeText(this, "This account has been disabled.", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun getUserRoleAndNavigate(uid: String) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: "user"
                
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                
                // Navigate to appropriate main activity based on role
                // Each main activity hosts fragments via Navigation Component
                when (role.lowercase()) {
                    "farmer" -> {
                        val intent = Intent(this, FarmerDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    "buyer" -> {
                        val intent = Intent(this, BuyerDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    "driver" -> {
                        val intent = Intent(this, DriverDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        // Default navigation - fallback to buyer dashboard
                        Toast.makeText(this, "Unknown role. Redirecting to buyer dashboard.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, BuyerDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener {
                // If role fetch fails, show error and don't navigate
                Toast.makeText(this, "Failed to load user profile. Please try again.", Toast.LENGTH_LONG).show()
                binding.btnLogin.text = "Login"
                binding.btnLogin.isEnabled = true
            }
    }

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset Password")
        builder.setMessage("Enter your email address and we'll send you a link to reset your password.")
        
        val input = TextInputEditText(this)
        input.hint = "Enter your email"
        input.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        
        // Set layout parameters for the input field
        val layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(64, 16, 64, 16)
        input.layoutParams = layoutParams
        
        val container = android.widget.LinearLayout(this)
        container.orientation = android.widget.LinearLayout.VERTICAL
        container.addView(input)
        
        builder.setView(container)
        
        builder.setPositiveButton("Send") { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        
        builder.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_LONG).show()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_USER_NOT_FOUND" -> {
                                Toast.makeText(this, "No account found with this email address.", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this, "Failed to send reset email: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        // Optional: finish() if you don't want users to come back to login
    }
}