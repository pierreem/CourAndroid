package fr.nextu.guerton_pierreemmanuel

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity2 : AppCompatActivity() {

    lateinit var json: TextView
    lateinit var job: Job
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
    }

    override fun onStart() {
        super.onStart()
        getPictureList()
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }


   fun getPictureList() {
       job = CoroutineScope(Dispatchers.IO).launch {
           val ret = requestPictureList()

           withContext(Dispatchers.Main) {
               json.text = ret
           }
       }
   }

    fun requestPictureList(): String {
        val client = OkHttpClient()


        val request: Request = Request.Builder()
            .url("https://api.betaseries.com/movies/list")
            .get()
            .addHeader("X-BetaSeries-Key", getString(R.string.betaseries_api_key))
            .build()

        val response: Response = client.newCall(request).execute()

        return response.body?.string() ?: ""
    }
}