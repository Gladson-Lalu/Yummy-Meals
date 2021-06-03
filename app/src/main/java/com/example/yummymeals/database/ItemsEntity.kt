package com.example.yummymeals.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ItemMenu")
data class ItemsEntity(
    @PrimaryKey val FoodId: String,
    @ColumnInfo(name = "FoodName") val FoodName: String,
    @ColumnInfo(name = "FoodCost") val FoodCost: String,
    @ColumnInfo(name = "RestaurantId") val RestaurantId: String
)