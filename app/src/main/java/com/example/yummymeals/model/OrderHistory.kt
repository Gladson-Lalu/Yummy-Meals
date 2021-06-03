package com.example.yummymeals.model

data class OrderHistory(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val date: String,
    val foodItems: ArrayList<MenuItem>
)