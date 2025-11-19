package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get the selected role from RoleSelectionActivity
        selectedRole = intent.getStringExtra("SELECTED_ROLE")

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            if (validateForm()) {
                performSignUp()
            }
        }

        binding.tvSignIn.setOnClickListener {
            // Navigate to Sign In activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        // Clear previous errors
        binding.etFullName.error = null
        binding.etEmail.error = null
        binding.etPhone.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Please enter your full name"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Please enter your email address"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email address"
            isValid = false
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Please enter your phone number"
            isValid = false
        } else if (phone.length < 10) {
            binding.etPhone.error = "Please enter a valid phone number"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Please create a password"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    private fun performSignUp() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val role = selectedRole ?: "user" // default to "user" if no role selected

        // Show loading state
        binding.btnSignUp.isEnabled = false
        binding.btnSignUp.text = "Creating account..."

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User account created successfully
                    val user = auth.currentUser
                    user?.let {
                        // Save additional user data to Firestore
                        saveUserToFirestore(it.uid, fullName, email, phone, role)
                    }
                } else {
                    // Sign up failed
                    binding.btnSignUp.isEnabled = true
                    binding.btnSignUp.text = "Sign Up"
                    
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                binding.etEmail.error = "This email is already registered"
                                Toast.makeText(this, "Email already exists. Please use a different email or login.", Toast.LENGTH_LONG).show()
                            }
                            "ERROR_WEAK_PASSWORD" -> {
                                binding.etPassword.error = "Password is too weak"
                                Toast.makeText(this, "Password is too weak. Please use a stronger password.", Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_INVALID_EMAIL" -> {
                                binding.etEmail.error = "Invalid email address"
                                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Sign up failed: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun saveUserToFirestore(uid: String, fullName: String, email: String, phone: String, role: String) {
        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "role" to role,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                // User data saved successfully
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                
                // Navigate to appropriate dashboard based on role
                when (role) {
                    "buyer" -> {
                        val intent = Intent(this, BuyerDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    // Add other role-specific activities here when available
                    else -> {
                        // Default navigation - you can customize this
                        val intent = Intent(this, BuyerDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            .addOnFailureListener { e ->
                // User created but data save failed
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.text = "Sign Up"
                Toast.makeText(this, "Account created but failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}