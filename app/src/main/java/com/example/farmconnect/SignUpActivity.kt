package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class SignUpActivity : AppCompatActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: MaterialButton
    private lateinit var tvSignIn: TextView
    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Get the selected role from RoleSelectionActivity
        selectedRole = intent.getStringExtra("SELECTED_ROLE")

        // You can use the selected role to customize the signup form
        // For example, show a message or pre-fill certain fields
        if (selectedRole != null) {
            // Optional: Show which role is being signed up for
            Toast.makeText(this, "Signing up as $selectedRole", Toast.LENGTH_SHORT).show()
        }

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvSignIn = findViewById(R.id.tvSignIn)
    }

    private fun setupClickListeners() {
        btnSignUp.setOnClickListener {
            if (validateForm()) {
                performSignUp()
            }
        }

        tvSignIn.setOnClickListener {
            // Navigate to Sign In activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Clear previous errors
        etFullName.error = null
        etEmail.error = null
        etPhone.error = null
        etPassword.error = null
        etConfirmPassword.error = null

        if (fullName.isEmpty()) {
            etFullName.error = "Please enter your full name"
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "Please enter your email address"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email address"
            return false
        }

        if (phone.isEmpty()) {
            etPhone.error = "Please enter your phone number"
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Please create a password"
            return false
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun performSignUp() {
        val fullName = etFullName.text.toString().trim()

        // Include the selected role in the signup process
        val role = selectedRole ?: "user" // default to "user" if no role selected

        Toast.makeText(this, "Account created successfully for $fullName as $role!", Toast.LENGTH_SHORT).show()

        // Here you would typically make API call to register user
        // You can send the role to your backend along with other user data
        // For example: api.registerUser(fullName, email, phone, password, role)

        // After successful registration, you might want to navigate to the main activity
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)
        // finish()
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}