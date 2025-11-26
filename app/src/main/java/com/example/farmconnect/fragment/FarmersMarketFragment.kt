package com.example.farmconnect.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.farmconnect.R
import com.example.farmconnect.databinding.FragmentFarmersMarketBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.permissionx.guolindev.PermissionX
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executors

class FarmersMarketFragment : Fragment() {

    private var _binding: FragmentFarmersMarketBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private var selectedImageUri: Uri? = null

    private val CLOUD_NAME = "dldrdsvqp"
    private val UPLOAD_PRESET = "android_unsigned"
    
    // Activity result launcher for image selection
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showSelectedImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFarmersMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupClickListeners()
        loadMarketData()
        setupToolbar()
    }

    private fun setupClickListeners() {
        // Save Product Button
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveProduct()
            }
        }

        // Cancel Button - Clear form
        binding.btnCancel.setOnClickListener {
            clearForm()
        }

        // Photo Upload Button
        binding.btnUploadPhotos.setOnClickListener {
            checkStoragePermission()
        }
        
        // Remove Image Button
        binding.btnRemoveImage.setOnClickListener {
            removeSelectedImage()
        }

        // Global Images Checkbox
        binding.cbGlobalImages.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Global images enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            // Navigate back when back button is clicked
            findNavController().navigateUp()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()
        val quantityText = binding.etQuantity.text.toString().trim()
        val priceText = binding.etPrice.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Product name is required"
            isValid = false
        } else {
            binding.etName.error = null
        }

        if (unit.isEmpty()) {
            binding.etUnit.error = "Unit is required"
            isValid = false
        } else {
            binding.etUnit.error = null
        }

        if (quantityText.isEmpty()) {
            binding.etQuantity.error = "Quantity is required"
            isValid = false
        } else {
            try {
                val quantity = quantityText.toInt()
                if (quantity <= 0) {
                    binding.etQuantity.error = "Quantity must be greater than 0"
                    isValid = false
                } else {
                    binding.etQuantity.error = null
                }
            } catch (e: NumberFormatException) {
                binding.etQuantity.error = "Invalid quantity"
                isValid = false
            }
        }

        if (priceText.isEmpty()) {
            binding.etPrice.error = "Price is required"
            isValid = false
        } else {
            try {
                // Extract number from price text (e.g., "KES 120" -> 120.0)
                val priceValue = priceText.replace(Regex("[^0-9.]"), "").toDouble()
                if (priceValue <= 0) {
                    binding.etPrice.error = "Price must be greater than 0"
                    isValid = false
                } else {
                    binding.etPrice.error = null
                }
            } catch (e: Exception) {
                binding.etPrice.error = "Invalid price format"
                isValid = false
            }
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            isValid = false
        } else {
            binding.etDescription.error = null
        }

        return isValid
    }

    private fun saveProduct() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please login to add products", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Saving..."

        // Get user's name from Firestore
        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val farmerName = document.getString("fullName") ?: "Unknown Farmer"
                
                // Get form values
                val name = binding.etName.text.toString().trim()
                val unit = binding.etUnit.text.toString().trim()
                val quantity = binding.etQuantity.text.toString().trim()
                val priceText = binding.etPrice.text.toString().trim()
                val description = binding.etDescription.text.toString().trim()
                
                // Extract price value (remove "KES" and other text)
                val price = priceText.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

                
                // Determine category (simple mapping based on name)
                val category = getCategoryForProduct(name)

                // Function to save product data with image URL
                fun saveProductData(imageUrl: String? = null) {
                    val productData = hashMapOf(
                        "name" to name,
                        "description" to description,
                        "price" to price,
                        "quantity" to quantity,
                        "unit" to unit,
                        "category" to category,
                        "owner" to farmerName,
                        "farmerId" to user.uid,
                        "createdAt" to com.google.firebase.Timestamp.now(),
                        "imageUrl" to imageUrl,
                        "useGlobalImages" to binding.cbGlobalImages.isChecked
                    )

                    // Add product to Firestore
                    firestore.collection("products")
                        .add(productData)
                        .addOnSuccessListener { documentReference ->
                            binding.btnSave.isEnabled = true
                            binding.btnSave.text = "Save Product"
                            Toast.makeText(requireContext(), "Product added successfully!", Toast.LENGTH_SHORT).show()
                            clearForm()
                            loadMarketData() // Reload products list
                        }
                        .addOnFailureListener { e ->
                            binding.btnSave.isEnabled = true
                            binding.btnSave.text = "Save Product"
                            Toast.makeText(requireContext(), "Failed to add product: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }

                // If an image is selected, upload it first
                if (selectedImageUri != null) {
                    uploadImageToCloudinary(selectedImageUri!!) { imageUrl ->
                        if (imageUrl != null) {
                            saveProductData(imageUrl)
                        } else {
                            binding.btnSave.isEnabled = true
                            binding.btnSave.text = "Save Product"
                        }
                    }
                } else {
                    saveProductData()
                }
            }
            .addOnFailureListener {
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Save Product"
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToCloudinary(
        fileUri: Uri,
        callback: (String?) -> Unit
    ) {
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Uploading image..."

        val appContext = requireContext().applicationContext
        val contentResolver = appContext.contentResolver

        val executor = Executors.newSingleThreadExecutor()
        val mainHandler = Handler(Looper.getMainLooper())

        executor.execute {
            try {
                val inputStream = contentResolver.openInputStream(fileUri)
                    ?: throw IOException("Cannot open image")
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "image.jpg",
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    throw IOException("Cloudinary error: ${response.code} ${response.message}")
                }

                val bodyString = response.body?.string() ?: throw IOException("Empty response body")
                val json = JSONObject(bodyString)
                val secureUrl = json.getString("secure_url")

                mainHandler.post {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Save Product"
                    callback(secureUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Save Product"
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    callback(null)
                }
            }
        }
    }

    private fun getEmojiForProduct(name: String): String {
        val lowerName = name.lowercase()
        return when {
            lowerName.contains("tomato") -> "🍅"
            lowerName.contains("carrot") -> "🥕"
            lowerName.contains("potato") -> "🥔"
            lowerName.contains("corn") -> "🌽"
            lowerName.contains("lettuce") -> "🥬"
            lowerName.contains("cucumber") -> "🥒"
            lowerName.contains("pepper") -> "🫑"
            lowerName.contains("eggplant") -> "🍆"
            lowerName.contains("broccoli") -> "🥦"
            lowerName.contains("onion") -> "🧅"
            lowerName.contains("apple") -> "🍎"
            lowerName.contains("banana") -> "🍌"
            lowerName.contains("orange") -> "🍊"
            lowerName.contains("grape") -> "🍇"
            lowerName.contains("strawberry") -> "🍓"
            lowerName.contains("egg") -> "🥚"
            lowerName.contains("milk") -> "🥛"
            lowerName.contains("cheese") -> "🧀"
            lowerName.contains("honey") -> "🍯"
            else -> "🌾" // Default emoji
        }
    }

    private fun getCategoryForProduct(name: String): String {
        val lowerName = name.lowercase()
        return when {
            lowerName.contains("tomato") || lowerName.contains("carrot") || 
            lowerName.contains("lettuce") || lowerName.contains("cucumber") ||
            lowerName.contains("pepper") || lowerName.contains("broccoli") ||
            lowerName.contains("onion") -> "Vegetables"
            lowerName.contains("apple") || lowerName.contains("banana") ||
            lowerName.contains("orange") || lowerName.contains("grape") ||
            lowerName.contains("strawberry") -> "Fruits"
            lowerName.contains("potato") || lowerName.contains("corn") -> "Root Crops"
            lowerName.contains("egg") || lowerName.contains("milk") ||
            lowerName.contains("cheese") -> "Poultry"
            else -> "Other"
        }
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etUnit.text?.clear()
        binding.etQuantity.text?.clear()
        binding.etPrice.text?.clear()
        binding.etDescription.text?.clear()
        binding.cbGlobalImages.isChecked = false
        removeSelectedImage()
    }
    
    private fun removeSelectedImage() {
        selectedImageUri = null
        binding.ivProductImage.visibility = View.GONE
        binding.btnRemoveImage.visibility = View.GONE
        binding.btnUploadPhotos.visibility = View.VISIBLE
    }
    
    private fun showSelectedImage(uri: Uri) {
        binding.ivProductImage.visibility = View.VISIBLE
        binding.btnRemoveImage.visibility = View.VISIBLE
        binding.btnUploadPhotos.visibility = View.GONE
        
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.ivProductImage)
    }
    
    private fun checkStoragePermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        PermissionX.init(this)
            .permissions(permission)   // or multiple: .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, ...)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "We need storage permission to select an image for your product",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    // Permission granted, open image picker
                    getContent.launch("image/*")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permission denied. Cannot select image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun getFileExtension(uri: Uri): String {
        var extension = ""

        requireContext().contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    val name = cursor.getString(nameIndex)
                    extension = name.substringAfterLast('.', "jpg")
                }
            }

        return if (extension.isBlank()) "jpg" else extension
    }

    private fun loadMarketData() {
        // Load farmer's products from Firestore
        val user = auth.currentUser
        if (user == null) return

        // TODO: Load and display farmer's products in the "My Products in Market" section
        // You can use a RecyclerView or update the static product cards
        firestore.collection("products")
            .whereEqualTo("farmerId", user.uid)
            .get()
            .addOnSuccessListener { documents ->
                // Update UI with farmer's products
                // For now, the layout has static product cards
                // You might want to replace them with a RecyclerView
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FarmersMarket", "Error loading products: ${e.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

