package com.example.dropspot.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dropspot.data.dao.SpotDao
import com.example.dropspot.data.model.Spot

@Database(entities = [Spot::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun spotDao(): SpotDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "spot_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
