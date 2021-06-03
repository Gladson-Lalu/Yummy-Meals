package com.example.yummymeals.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room


class DBGetItems(val context: Context, val id: String) :
    AsyncTask<Void, Void, List<ItemsEntity>?>() {
    private val db = Room.databaseBuilder(context, ItemDatabase::class.java, "items-db")
        .build()

    override fun doInBackground(vararg p0: Void?): List<ItemsEntity>? {
        val itemsEntityList = (db.itemDao().getAllItemsByRestaurantId(id))
        db.close()
        return itemsEntityList
    }
}

