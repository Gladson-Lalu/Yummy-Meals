package com.example.yummymeals.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummymeals.R
import com.example.yummymeals.model.OrderHistory
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(val context: Context, private val orderList: ArrayList<OrderHistory>) :
    RecyclerView.Adapter<HistoryAdapter.OrderHistoryHolder>() {
    class OrderHistoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtHistoryRestaurantName)
        val txtOrderData: TextView = view.findViewById(R.id.txtHistoryDate)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerOrderHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row_order_history, parent, false)
        return OrderHistoryHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryHolder, position: Int) {

        val order = orderList[position]
        val date = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.ENGLISH
        ).format(SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH).parse(order.date)).toString()
        holder.txtRestaurantName.text = order.restaurantName
        holder.txtOrderData.text = date
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = CartItemAdapter(context, order.foodItems)
    }
}