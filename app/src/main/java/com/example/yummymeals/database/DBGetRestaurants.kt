package com.example.yummymeals.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBGetRestaurants(val context: Context) :
    AsyncTask<Void, Void, List<RestaurantEntity>?>() {
    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
        .build()

    override fun doInBackground(vararg p0: Void?): List<RestaurantEntity>? {
        val restaurantEntityList = db.restaurantDao().getAllRestaurants()
        db.close()
        return restaurantEntityList
    }
}