package fr.nextu.guerton_pierreemmanuel

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.nextu.guerton_pierreemmanuel.entity.Movie
import fr.nextu.guerton_pierreemmanuel.entity.MovieDAO

@Database(entities = [Movie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDAO
}



