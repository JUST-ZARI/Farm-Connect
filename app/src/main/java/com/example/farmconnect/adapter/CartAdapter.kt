package com.example.farmconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmconnect.R
import com.example.farmconnect.model.CartItem
import com.bumptech.glide.Glide

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivCartProductImage)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val btnDecrease: TextView = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: TextView = itemView.findViewById(R.id.btnIncrease)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.tvProductName.text = cartItem.name
        holder.tvProductPrice.text = "KES ${cartItem.price}/${cartItem.unit}kg"
        holder.tvQuantity.text = cartItem.quantity.toString()
        holder.tvTotalPrice.text = "KES ${String.format("%.2f", cartItem.getTotalPrice())}"

        // Load image if available
        cartItem.imageUrl?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .centerCrop() // or .fitCenter() if you prefer no cropping
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(holder.ivProductImage)
        } ?: run {
            holder.ivProductImage.setImageResource(R.drawable.ic_image_placeholder)
        }

        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                onQuantityChanged(cartItem)
                notifyItemChanged(position)
            }
        }

        holder.btnIncrease.setOnClickListener {
            cartItem.quantity++
            onQuantityChanged(cartItem)
            notifyItemChanged(position)
        }

        holder.btnRemove.setOnClickListener {
            onItemRemoved(cartItem)
        }
    }



    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}