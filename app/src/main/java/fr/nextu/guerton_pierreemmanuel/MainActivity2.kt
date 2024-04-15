package fr.nextu.guerton_pierreemmanuel

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.google.gson.Gson
import fr.nextu.guerton_pierreemmanuel.entity.Movie
import fr.nextu.guerton_pierreemmanuel.entity.Movies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity2 : AppCompatActivity() {

    lateinit var json: TextView
    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.back).setOnClickListener {
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        json = findViewById(R.id.json)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "movies.db"
        ).build()
    }

    override fun onStart() {
        super.onStart()
        updateViewFromDB()
        requestMoviesList()
    }

    override fun onStop() {
        super.onStop()
    }

    fun updateViewFromDB() {
        CoroutineScope(Dispatchers.IO).launch {
            val flow = db.movieDao().getFlowData()
            flow.collect{
                Log.e("MainActivity2", "Flow data: $it")
            }
        }
    }

    fun requestMoviesList() = CoroutineScope(Dispatchers.IO).async {
        val client = OkHttpClient()


        val request: Request = Request.Builder()
            .url("https://api.betaseries.com/movies/list")
            .get()
            .addHeader("X-BetaSeries-Key", getString(R.string.betaseries_api_key))
            .build()

        val response: Response = client.newCall(request).execute()
        moviesFromJson(response.body?.string() ?: "")
    }

    fun moviesFromJson(json: String) {
        val gson = Gson()
        val om = gson.fromJson(json,  Movies::class.java)
        db.movieDao().insertAll(*om.movies.toTypedArray())
    }
}