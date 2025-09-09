package com.example.phonedebatehub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//This is the Database

@Database(
    entities = [DebateEntity::class, MessageEntity::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debateDao(): DebateDao
    abstract fun messageDao(): MessageDao


    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app-db"
            )
                .fallbackToDestructiveMigration()   // keeps dev simple
                .build()
        }
    }
}