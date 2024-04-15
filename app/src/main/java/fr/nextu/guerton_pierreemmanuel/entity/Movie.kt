package fr.nextu.guerton_pierreemmanuel.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movie")
data class Movie(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String
)

data class Movies(val movies: List<Movie>)