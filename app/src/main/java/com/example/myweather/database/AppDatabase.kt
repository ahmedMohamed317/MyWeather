package com.example.myweather.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweather.model.AlertPojo
import com.example.myweather.model.WeatherResponse


@Database(entities = arrayOf( WeatherResponse::class, AlertPojo::class), version = 3)
@TypeConverters(TypeConverter::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDAO() : WeatherDAO

    companion object {
        private var Instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {

            return Instance ?: synchronized(this)
            {
                try{
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "weather db"
                ).fallbackToDestructiveMigration().build()

                Instance = instance
                return instance
                }
                catch (e : Exception){
                    Log.d("inAppDatabaseCatch" , e.message.toString())

                }
                return getInstance(context)
            }
        }
    }
}