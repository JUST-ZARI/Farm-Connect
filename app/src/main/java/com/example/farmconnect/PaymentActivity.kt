package com.example.farmconnect

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class PaymentActivity : AppCompatActivity() {

    private lateinit var paymentMethodGroup: RadioGroup
    private lateinit var bankDetailsSection: LinearLayout
    private lateinit var mpassRadio: RadioButton
    private lateinit var bankTransferRadio: RadioButton
    private lateinit var stripeRadio: RadioButton
    private lateinit var confirmPaymentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup)
        bankDetailsSection = findViewById(R.id.bankDetailsSection)
        mpassRadio = findViewById(R.id.mpassRadio)
        bankTransferRadio = findViewById(R.id.bankTransferRadio)
        stripeRadio = findViewById(R.id.stripeRadio)
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton)

        // Set bank transfer as default selected
        bankTransferRadio.isChecked = true
    }

    private fun setupListeners() {
        paymentMethodGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.bankTransferRadio -> {
                    showBankTransferDetails()
                }
                R.id.mpassRadio -> {
                    showMPassDetails()
                }
                R.id.stripeRadio -> {
                    showStripeDetails()
                }
            }
        }

        confirmPaymentButton.setOnClickListener {
            processPayment()
        }
    }

    private fun showBankTransferDetails() {
        bankDetailsSection.visibility = LinearLayout.VISIBLE
        // You can add specific logic for bank transfer here
    }

    private fun showMPassDetails() {
        bankDetailsSection.visibility = LinearLayout.GONE
        // Show M-Pass specific UI or logic
        Toast.makeText(this, "M-Pass payment selected", Toast.LENGTH_SHORT).show()
    }

    private fun showStripeDetails() {
        bankDetailsSection.visibility = LinearLayout.GONE
        // Show Stripe specific UI or logic
        Toast.makeText(this, "Stripe payment selected", Toast.LENGTH_SHORT).show()
    }

    private fun processPayment() {
        val selectedMethod = when (paymentMethodGroup.checkedRadioButtonId) {
            R.id.mpassRadio -> "M-Pass"
            R.id.bankTransferRadio -> "Bank Transfer"
            R.id.stripeRadio -> "Stripe"
            else -> "Unknown"
        }

        // Here you would implement the actual payment processing logic
        // For now, just show a confirmation message
        val message = when (selectedMethod) {
            "Bank Transfer" -> "Please transfer the amount to the provided bank details. We'll confirm your payment once received."
            "M-Pass" -> "Redirecting to M-Pass payment gateway..."
            "Stripe" -> "Redirecting to Stripe payment gateway..."
            else -> "Processing payment..."
        }

        AlertDialog.Builder(this)
            .setTitle("Payment Confirmation")
            .setMessage("$message\n\nSelected method: $selectedMethod")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate to success screen or previous activity
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Method to dynamically add payment items to the table
    fun addPaymentItem(name: String, cost: String, profile: String) {
        val paymentTable = findViewById<LinearLayout>(R.id.paymentTable)

        val rowLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(48, 32, 48, 32) // Convert dp to pixels
            background = getDrawable(android.R.attr.selectableItemBackground)
        }

        val nameTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = name
            textSize = 14f
            setTextColor(getColor(android.R.color.black))
        }

        val costTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = cost
            textSize = 14f
            setTextColor(getColor(android.R.color.black))
            gravity = Gravity.CENTER
        }

        val profileTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = profile
            textSize = 14f
            setTextColor(getColor(android.R.color.black))
            gravity = Gravity.END
        }

        rowLayout.addView(nameTextView)
        rowLayout.addView(costTextView)
        rowLayout.addView(profileTextView)

        paymentTable.addView(rowLayout)
    }
}