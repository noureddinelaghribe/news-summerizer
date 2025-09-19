package com.noureddine.newssummerizer.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.noureddine.newssummerizer.Interface.ArticleDao
import com.noureddine.newssummerizer.model.ArticlePreProcessing
import com.noureddine.newssummerizer.model.ArticleAfterProcessing

@Database(entities = [ArticlePreProcessing::class,ArticleAfterProcessing::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database_1"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
