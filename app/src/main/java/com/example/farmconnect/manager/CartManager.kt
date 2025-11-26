package com.example.farmconnect.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.farmconnect.model.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private const val CART_PREFS = "cart_prefs"
    private const val CART_ITEMS_KEY = "cart_items"
    
    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE)
    }
    
    fun addToCart(item: CartItem) {
        val cartItems = getCartItems().toMutableList()
        val existingItem = cartItems.find { it.productId == item.productId }
        
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            cartItems.add(item)
        }
        
        saveCartItems(cartItems)
    }
    
    fun getCartItems(): List<CartItem> {
        val json = prefs?.getString(CART_ITEMS_KEY, "[]") ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }
    
    fun removeFromCart(item: CartItem) {
        val cartItems = getCartItems().toMutableList()
        cartItems.removeAll { it.productId == item.productId }
        saveCartItems(cartItems)
    }
    
    fun updateCartItem(updatedItem: CartItem) {
        val cartItems = getCartItems().toMutableList()
        val index = cartItems.indexOfFirst { it.productId == updatedItem.productId }
        if (index != -1) {
            cartItems[index] = updatedItem
            saveCartItems(cartItems)
        }
    }
    
    fun clearCart() {
        prefs?.edit()?.remove(CART_ITEMS_KEY)?.apply()
    }
    
    private fun saveCartItems(items: List<CartItem>) {
        val json = Gson().toJson(items)
        prefs?.edit()?.putString(CART_ITEMS_KEY, json)?.apply()
    }
}