package fr.nextu.guerton_pierreemmanuel.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {
    @Query("SELECT * FROM movie")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Movie>

    @Query("SELECT * FROM movie WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): Movie

    @Insert
    fun insertAll(vararg movies: Movie)

    @Delete
    fun delete(movie: Movie)

    @Query("SELECT * FROM movie")
    fun getFlowData(): Flow<List<Movie>>
}
