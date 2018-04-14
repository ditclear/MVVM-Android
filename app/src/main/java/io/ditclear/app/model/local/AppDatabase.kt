package io.ditclear.app.model.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.ditclear.app.model.data.Article
import io.ditclear.app.model.local.dao.PaoDao

/**
 * 页面描述：AppDatabase
 *
 */
@Database(entities = arrayOf(Article::class),version = 1)
abstract class AppDatabase :RoomDatabase(){

    abstract fun paoDao(): PaoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "app.db")
                        .build()
    }

}