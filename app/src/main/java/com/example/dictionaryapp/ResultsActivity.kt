package com.example.dictionaryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import org.json.JSONArray

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val result = intent.getStringExtra("APIResult")
        val resultJson = JSONArray(result).getJSONObject(0)
        val tv = findViewById<TextView>(R.id.tv_res)
        tv.text = resultJson.getString("word")

        // Converting the API data to data class
//        val meanings: MutableList<WordDetails> = resultJson.get("meanings") as MutableList<WordDetails>
        val meanings = resultJson.get("meanings").toString()
        val meaningsJSON = Gson().toJson(JSONArray(meanings).getJSONObject(0))
        Log.e("Meanings", meanings)
        Log.e("Meanings in JSON", meaningsJSON.toString())

        // Starting the fragment to show the List of words as recycler view:
        val bundle = Bundle()
        bundle.putString("Word", resultJson.getString("word"))

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val posListFragment = PoSListFragment()
        posListFragment.arguments = bundle
        fragmentTransaction.add(R.id.list_frag_container, posListFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
}