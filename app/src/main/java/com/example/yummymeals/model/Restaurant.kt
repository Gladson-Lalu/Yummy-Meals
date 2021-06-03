package com.example.yummymeals.model

data class Restaurant(
    val restaurantName: String,
    val restaurantPrice: String,
    val restaurantId: String,
    val restaurantRating: String,
    val imageUrl: String,
    val isFav: Boolean
)