package com.example.yummymeals.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {

    @Insert
    fun insertItem(itemsEntity: ItemsEntity)

    @Delete
    fun deleteItem(itemsEntity: ItemsEntity)

    @Query(value = "SELECT * FROM ItemMenu WHERE RestaurantId = :restaurantId")
    fun getAllItemsByRestaurantId(restaurantId: String): List<ItemsEntity>?

    @Query("DELETE FROM ItemMenu")
    fun deleteAllItems()
}
