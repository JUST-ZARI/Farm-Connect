package com.example.farmconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.farmconnect.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                binding.emailInputLayout.error = "Email or username is required"
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

        // Simulate API call - Replace with your actual authentication logic
        android.os.Handler().postDelayed({
            // Reset button state
            binding.btnLogin.text = "Login"
            binding.btnLogin.isEnabled = true

            // For demo purposes - In real app, check against your backend
            if (email.isNotEmpty() && password.length >= 6) {
                // Successful login
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                // Navigate to buyer dashboard instead of main activity
                val intent = Intent(this, BuyerDashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }, 1500)
    }

    private fun showForgotPasswordDialog() {
        Toast.makeText(this, "Forgot Password feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        // Optional: finish() if you don't want users to come back to login
    }
}