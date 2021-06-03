package com.example.yummymeals.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val restaurant_id: String,
    @ColumnInfo(name = "restaurant_name") val restaurant_name: String,
    @ColumnInfo(name = "restaurant_rating") val restaurant_rating: String,
    @ColumnInfo(name = "restaurant_imgUrl") val restaurant_imgUrl: String,
    @ColumnInfo(name = "restaurant_price") val restaurant_Price: String
) : Serializable
