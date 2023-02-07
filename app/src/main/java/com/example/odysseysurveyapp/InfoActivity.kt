package com.example.odysseysurveyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val bundle = intent.extras
        if (bundle != null){
            val events: ArrayList<String> = bundle.getStringArrayList("Events") as ArrayList<String>
            val ratings: ArrayList<Int> = bundle.getIntegerArrayList("Ratings") as ArrayList<Int>
            var tv: TextView = findViewById(R.id.rating_textView)
//            tv.text = bundle.getStringArrayList("Events")?.get(0) ?: "Hello"

            if (events.size > 0) {
                var forTextView: String = ""
                for ((iterator, s) in events.withIndex()) {
                    forTextView += s
                    forTextView += " is "
                    forTextView += if (ratings[iterator] == 0) {
                        "unrated"
                    } else {
                        "rated ${ratings[iterator]}/5."
                    }

                    forTextView += "\n"

                }


                tv.text = forTextView
            }
        }
    }

    override fun onStart() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onStart", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onStart")
        super.onStart()
    }

    override fun onResume() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onResume", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onResume")
        super.onResume()
    }
    override fun onPause() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onPause", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onPause")
        super.onPause()
    }
    override fun onStop() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onStop", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onStop")
        super.onStop()
    }

    override fun onRestart() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onRestart", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onRestart")
        super.onRestart()
    }

    override fun onDestroy() {
        Toast.makeText(applicationContext, "State of activity: ${this.localClassName} is now onDestroy", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onDestroy")
        super.onDestroy()
    }
}