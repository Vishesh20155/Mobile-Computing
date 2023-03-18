package com.example.dictionaryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import org.json.JSONArray

class ResultsActivity : AppCompatActivity() {
    val wordDetailsList: MutableList<WordDetails> = mutableListOf()
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
        val meaningsJsonArray = JSONArray(meanings)

        for (i in 0 until meaningsJsonArray.length()) {
            val meaningsJSON = meaningsJsonArray.getJSONObject(i)

            val wordDetails = WordDetails()
            wordDetails.partOfSpeech = meaningsJSON.getString("partOfSpeech")

            val definitions = meaningsJSON.getJSONArray("definitions")
            for (j in 0 until definitions.length()) {
                val definitionContents = DefinitionContents()
                val defObject = definitions.getJSONObject(j)
                definitionContents.definition = defObject.getString("definition")
                if (!defObject.isNull("example")) {
                    definitionContents.example = defObject.getString("example")
                }
                val defSynonyms = defObject.getJSONArray("synonyms")
                for (k in 0 until defSynonyms.length()) {
                    val s1 = defSynonyms.getString(k)
                    definitionContents.synonyms.add(s1)
                }
                val defAntonyms = defObject.getJSONArray("antonyms")
                for (k in 0 until defAntonyms.length()) {
                    val s1 = defAntonyms.getString(k)
                    definitionContents.antonyms.add(s1)
                }
                wordDetails.definitions.add(definitionContents)
            }

            val synonyms = meaningsJSON.getJSONArray("synonyms")
            for (j in 0 until synonyms.length()) {
                val s = synonyms.getString(j)
                wordDetails.synonyms.add(s)
            }

            val antonyms = meaningsJSON.getJSONArray("antonyms")
            for (j in 0 until antonyms.length()) {
                val s = antonyms.getString(j)
                wordDetails.antonyms.add(s)
            }
            Log.e("WordDetails $i", wordDetails.toString())

            wordDetailsList.add(wordDetails)
        }


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