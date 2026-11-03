package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GameSessionEntity::class, RoundEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DoppelkopfDatabase : RoomDatabase() {
    abstract fun dao(): DoppelkopfDao

    companion object {
        @Volatile
        private var INSTANCE: DoppelkopfDatabase? = null

        fun getInstance(context: Context): DoppelkopfDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoppelkopfDatabase::class.java,
                    "doppelkopf.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
