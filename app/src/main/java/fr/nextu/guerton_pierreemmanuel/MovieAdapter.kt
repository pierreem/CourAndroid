package fr.nextu.guerton_pierreemmanuel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.nextu.guerton_pierreemmanuel.MovieAdapter.MovieViewHolder
import fr.nextu.guerton_pierreemmanuel.entity.Movie

class MovieAdapter(private val dataSet: List<Movie>) : RecyclerView.Adapter<MovieViewHolder>() {
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        init {

            textView = view.findViewById(R.id.title_tv)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.movie_item, viewGroup, false)

        return MovieViewHolder(view)

    }

    override fun getItemCount()= dataSet.size


    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.textView.text = dataSet[position].title
    }

}