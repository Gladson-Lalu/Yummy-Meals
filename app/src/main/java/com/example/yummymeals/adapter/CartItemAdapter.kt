package com.example.yummymeals.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yummymeals.R
import com.example.yummymeals.model.MenuItem

class CartItemAdapter(
    val context: Context,
    private val cartItemList: ArrayList<MenuItem>
) : RecyclerView.Adapter<CartItemAdapter.CartItemHolder>() {

    class CartItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtCartItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtCartItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row_cart_items, parent, false)
        return CartItemHolder(view)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
        val cartItem = cartItemList[position]
        holder.txtItemName.text = cartItem.itemName
        holder.txtItemPrice.text = context.getString(R.string.itemRupees, cartItem.itemPrice)
    }
}