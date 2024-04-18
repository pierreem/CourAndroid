package fr.nextu.guerton_pierreemmanuel

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
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
import okhttp3.internal.notify

class MainActivity2 : AppCompatActivity() {
    val db: AppDatabase by lazy {
        AppDatabase.getInstance(applicationContext)
    }
    lateinit var movies_recycler: RecyclerView
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

        movies_recycler = findViewById<RecyclerView>(R.id.movies_recylcer).apply {
            adapter = MovieAdapter(emptyList())
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity2)
        }



        createNotificationChannel()


    }

    override fun onStart() {
        super.onStart()
        updateViewFromDB()
        requestMoviesList(::moviesFromJson)
    }

    override fun onStop() {
        super.onStop()
    }

    fun updateViewFromDB() {
        CoroutineScope(Dispatchers.IO).launch {
            val flow = db.movieDao().getFlowData()
            flow.collect{
                CoroutineScope(Dispatchers.Main).launch {
                    movies_recycler.adapter = MovieAdapter(it)
                }
            }
        }
    }


    fun requestMoviesList(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch{
            val client = OkHttpClient()


            val request: Request = Request.Builder()
                .url("https://api.betaseries.com/movies/list")
                .get()
                .addHeader("X-BetaSeries-Key", getString(R.string.betaseries_api_key))
                .build()

            val response: Response = client.newCall(request).execute()
            //notifyNewData(response)

            callback(response.body?.string() ?: "")
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun notifyNewData(response: Response) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.movies_updated_title))
            .setContentText(response.body?.string() ?: "")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val requestPermissionLauncher =
            this.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    NotificationManagerCompat.from(this).notify(1, builder.build())
                }
            }

        when {
            ContextCompat.checkSelfPermission(this,POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                NotificationManagerCompat.from(this).notify(1, builder.build())
            }
            else -> {
                //requestPermissions(arrayOf(POST_NOTIFICATIONS), 1)
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }


        /*with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity2,
                    POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {

                    } else {
                        Log.d("MainActivity", "Permission denied")
                    }
                }.launch(POST_NOTIFICATIONS)
                //requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                return@with
            }
            notify(1, builder.build())
        }*/
    }

    fun moviesFromJson(json: String) {
        val gson = Gson()
        val om = gson.fromJson(json,  Movies::class.java)
        Log.d("tag", "moviesFromJson: ${om.movies.size}")
        db.movieDao().insertAll(*om.movies.toTypedArray())
    }


    companion object {
        const val CHANNEL_ID = "fr_nextu_guerton_pierreemmanuel_channel_notification"
    }
}