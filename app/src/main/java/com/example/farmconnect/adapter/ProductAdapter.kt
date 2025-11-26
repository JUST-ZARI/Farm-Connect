package com.example.farmconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmconnect.R
import com.example.farmconnect.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit,
    private val onAddToCartClick: ((Product) -> Unit)? = null,
    private val showAddToCart: Boolean = false,
    private val isBuyer: Boolean
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(
            product = product,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            onAddToCartClick = onAddToCartClick,
            showAddToCart = showAddToCart,
            isBuyer = isBuyer
        )
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val btnEdit: View = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: View = itemView.findViewById(R.id.btnDelete)
        private val btnAddToCart: View = itemView.findViewById(R.id.btnAddToCart)

        fun bind(
            product: Product,
            onEditClick: (Product) -> Unit,
            onDeleteClick: (Product) -> Unit,
            onAddToCartClick: ((Product) -> Unit)?,
            showAddToCart: Boolean,
            isBuyer: Boolean
        ) {
            tvProductName.text = product.name
            tvProductPrice.text = "Ksh ${"%.2f".format(product.price)}/${product.unit}kg · Qty: ${product.quantity}"

            // Load product image if available
            product.imageUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivProductImage)
            } ?: run {
                ivProductImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Role-based UI
            if (isBuyer) {
                // Buyer: no edit/delete, only Add to Cart
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE

                if (showAddToCart && onAddToCartClick != null) {
                    btnAddToCart.visibility = View.VISIBLE
                    btnAddToCart.setOnClickListener { onAddToCartClick(product) }
                } else {
                    btnAddToCart.visibility = View.GONE
                }
            } else {
                // Farmer: show edit/delete, hide Add to Cart
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE
                btnAddToCart.visibility = View.GONE

                btnEdit.setOnClickListener { onEditClick(product) }
                btnDelete.setOnClickListener { onDeleteClick(product) }
            }
        }
    }

}