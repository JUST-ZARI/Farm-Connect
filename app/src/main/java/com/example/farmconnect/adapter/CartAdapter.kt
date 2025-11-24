package com.example.farmconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmconnect.R
import com.example.farmconnect.model.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductEmoji: TextView = itemView.findViewById(R.id.tvProductEmoji)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
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
        holder.tvProductPrice.text = "$${cartItem.price} / ${cartItem.unit}"
        holder.tvQuantity.text = cartItem.quantity.toString()
        holder.tvTotalPrice.text = "$${String.format("%.2f", cartItem.getTotalPrice())}"

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