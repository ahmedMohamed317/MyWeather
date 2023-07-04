package com.example.myweather

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = arrayOf( WeatherResponse::class), version = 1)
@TypeConverters(TypeConverter::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDAO() : WeatherDAO

    companion object {
        private var Instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {

            return Instance ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "weather db"
                ).build()
                Instance = instance
                return instance
            }
        }
    }
}