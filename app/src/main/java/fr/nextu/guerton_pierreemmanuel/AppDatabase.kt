package fr.nextu.guerton_pierreemmanuel

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.nextu.guerton_pierreemmanuel.entity.Movie
import fr.nextu.guerton_pierreemmanuel.entity.MovieDAO

@Database(entities = [Movie::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDAO


    companion object {

        fun getInstance(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "cour_android2.db"
            ).build()
        }
    }
}



