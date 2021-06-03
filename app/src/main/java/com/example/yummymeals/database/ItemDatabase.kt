package com.example.yummymeals.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemsEntity::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
