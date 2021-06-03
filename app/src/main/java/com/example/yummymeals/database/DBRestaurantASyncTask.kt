package com.example.yummymeals.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBRestaurantASyncTask(
    val context: Context,
    private val entity: RestaurantEntity,
    private val mode: Int
) :
    AsyncTask<Void, Void, Boolean>() {
    private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
        .build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        return when (mode) {
            1 -> {//check by id
                val restaurant: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(entity.restaurant_id)
                db.close()
                (restaurant != null)
            }
            2 -> {//save
                db.restaurantDao().insertRestaurant(entity)
                db.close()
                true
            }
            3 -> {
                db.restaurantDao().deleteRestaurant(entity)
                db.close()
                true
            }

            else -> false
        }
    }
}

