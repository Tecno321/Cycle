package com.example.cycle.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class AppBD : RoomDatabase() {
    abstract fun UsuarioDao(): UsuarioDao

    companion object{
        @Volatile
        private var INSTANCE: AppBD? = null

        fun getDatabase(context: Context): AppBD{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, AppBD::class.java,
                    "cycle_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}