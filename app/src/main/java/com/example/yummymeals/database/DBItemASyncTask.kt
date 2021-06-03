package com.example.yummymeals.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBItemASyncTask(
    val context: Context,
    private val entity: ItemsEntity? = null,
    private val mode: Int,
    val restaurantId: String? = null
) :
    AsyncTask<Void, Void, Boolean>() {
    private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "items-db")
        .build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        if (restaurantId != null) {
            return when (mode) {
                1 -> {//check by id
                    val restaurantItems =
                        db.itemDao().getAllItemsByRestaurantId(restaurantId)
                    db.close()
                    restaurantItems?.isNotEmpty() ?: false
                }
                else -> false
            }
        }
        if (entity != null) {
            return when (mode) {
                2 -> {//save items
                    db.itemDao().insertItem(entity)
                    db.close()
                    true
                }
                3 -> {//remove items
                    db.itemDao().deleteItem(entity)
                    db.close()
                    true
                }

                else -> false
            }
        }

        return when (mode) {
            4 -> {
                db.itemDao().deleteAllItems()
                db.close()
                return true
            }
            else -> false
        }
    }
}

