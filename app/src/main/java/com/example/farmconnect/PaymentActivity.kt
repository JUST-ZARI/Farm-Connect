package com.example.farmconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.farmconnect.databinding.ActivityPaymentBinding
import com.google.android.material.card.MaterialCardView

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupToolbar()
    }

    private fun setupListeners() {
        // Helper: ensure only one radio is checked
        fun selectMethod(selected: RadioButton) {
            val radios = listOf(
                binding.mpesaRadio,
                binding.bankTransferRadio,
                binding.stripeRadio
            )

            radios.forEach { it.isChecked = (it == selected) }

            when (selected.id) {
                R.id.bankTransferRadio -> {
                    highlightSelectedCard(binding.cardBankTransfer)
                    showBankTransferDetails()
                }
                R.id.mpesaRadio -> {
                    highlightSelectedCard(binding.cardMpesa)
                    showMPesaDetails()
                }
                R.id.stripeRadio -> {
                    highlightSelectedCard(binding.cardStripe)
                    showStripeDetails()
                }
            }
        }

        // Make radios themselves clickable
        binding.mpesaRadio.setOnClickListener {
            selectMethod(binding.mpesaRadio)
        }

        binding.bankTransferRadio.setOnClickListener {
            selectMethod(binding.bankTransferRadio)
        }

        binding.stripeRadio.setOnClickListener {
            selectMethod(binding.stripeRadio)
        }

        // Make the whole cards clickable too
        binding.cardMpesa.setOnClickListener {
            selectMethod(binding.mpesaRadio)
        }

        binding.cardBankTransfer.setOnClickListener {
            selectMethod(binding.bankTransferRadio)
        }

        binding.cardStripe.setOnClickListener {
            selectMethod(binding.stripeRadio)
        }

        // Default selection
        selectMethod(binding.bankTransferRadio)

        // Confirm button
        binding.btnProcessPayment.setOnClickListener {
            processPayment()
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun showBankTransferDetails() {
        // Bank Transfer specific UI or logic
        binding.bankDetailsSection.visibility = View.VISIBLE
        binding.mpesaDetailsSection.visibility = View.GONE
        binding.stripeDetailsSection.visibility = View.GONE
    }

    private fun showMPesaDetails() {
        // Mpesa specific UI or logic
        binding.bankDetailsSection.visibility = View.GONE
        binding.mpesaDetailsSection.visibility = View.VISIBLE
        binding.stripeDetailsSection.visibility = View.GONE
        Toast.makeText(this, "M-Pesa payment selected", Toast.LENGTH_SHORT).show()
    }

    private fun showStripeDetails() {
        // Stripe specific UI or logic
        binding.bankDetailsSection.visibility = View.GONE
        binding.mpesaDetailsSection.visibility = View.GONE
        binding.stripeDetailsSection.visibility = View.VISIBLE
        Toast.makeText(this, "Stripe payment selected", Toast.LENGTH_SHORT).show()
    }

    private fun highlightSelectedCard(selectedCard: MaterialCardView) {
        // Reset all to grey
        binding.cardMpesa.strokeColor = getColor(R.color.border_light)
        binding.cardBankTransfer.strokeColor = getColor(R.color.border_light)
        binding.cardStripe.strokeColor = getColor(R.color.border_light)

        // Highlight selected card with blue stroke
        selectedCard.strokeColor = getColor(R.color.primary_blue)
    }

    private fun processPayment() {
        val selectedMethod = when {
            binding.mpesaRadio.isChecked -> "M-Pesa"
            binding.bankTransferRadio.isChecked -> "Bank Transfer"
            binding.stripeRadio.isChecked -> "Stripe"
            else -> "Unknown"
        }

        // Currently showing a confirmation message without full payment logic
        val message = when (selectedMethod) {
            "Bank Transfer" -> "Please transfer the amount to the provided bank details. We'll confirm your payment once received."
            "M-Pesa" -> "Redirecting to M-Pesa payment gateway..."
            "Stripe" -> "Redirecting to Stripe payment gateway..."
            else -> "Processing payment..."
        }

        AlertDialog.Builder(this)
            .setTitle("Payment Confirmation")
            .setMessage("$message\n\nSelected method: $selectedMethod")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                // Go back to MainActivity and open BuyerDashboardFragment
                val intent = Intent(this, BuyerDashboardActivity::class.java).apply {
                    // Clear intermediate activities (DeliveryDetails / PickupDetails / Payment)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("navigate_to", "buyer_dashboard")
                }
                startActivity(intent)
                finish() // close PaymentActivity
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}