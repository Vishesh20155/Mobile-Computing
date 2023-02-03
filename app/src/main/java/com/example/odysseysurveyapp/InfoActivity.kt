package com.example.odysseysurveyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val bundle = intent.extras
        if (bundle != null){
            val events: ArrayList<String> = bundle.getStringArrayList("Events") as ArrayList<String>
            val ratings: ArrayList<Int> = bundle.getIntegerArrayList("Ratings") as ArrayList<Int>
            var tv: TextView = findViewById(R.id.textView)
//            tv.text = bundle.getStringArrayList("Events")?.get(0) ?: "Hello"

            var forTextView: String = ""
            for (s in events){
                forTextView+=s
                forTextView+="\n"
            }

            tv.text = forTextView
        }
    }
}