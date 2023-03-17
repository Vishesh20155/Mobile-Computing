package com.example.dictionaryapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MainActivityTAG"
class MainActivity : AppCompatActivity() {
    private lateinit var etWord: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etWord = findViewById(R.id.et_search_word)
        btnSearch = findViewById(R.id.btn_search)
        tvText = findViewById(R.id.tv_text)

        btnSearch.setOnClickListener {
            val word = etWord.text.toString()
            val wordUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word
            val connMngr: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connMngr.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                CallAPI().execute(wordUrl)
            }
        }
    }

    private inner class CallAPI(): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            val url = URL(p0[0])
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.connect()
            val response = conn.responseCode
            lateinit var jsonArray: JSONArray
            if (response == HttpURLConnection.HTTP_OK) {
                val inpStream = conn.inputStream
                val reader = BufferedReader(InputStreamReader(inpStream))
                jsonArray = JSONArray(reader.readLine())
            }
            println(jsonArray)
            Log.e("JSON Array", jsonArray.toString())
            return jsonArray.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val jsonArray = JSONArray(result)
            val jsonObj = jsonArray.getJSONObject(0)
            Log.e("JSON Object", jsonObj.toString())
//            tvText.text = jsonObj.toString()

//            val wordObj = Gson().fromJson(result, WordDetails::class.java)
//            Log.e("Word Details", wordObj.toString())
            val intent = Intent(this@MainActivity, ResultsActivity::class.java)
            intent.putExtra("APIResult", result)
            startActivity(intent)
        }

    }
}