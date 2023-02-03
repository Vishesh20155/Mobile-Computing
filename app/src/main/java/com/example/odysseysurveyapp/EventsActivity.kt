package com.example.odysseysurveyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.Toast

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val checkbox1:CheckBox = findViewById(R.id.music_checkBox)
        val rating1:RatingBar = findViewById(R.id.music_ratingBar)

        val checkbox2:CheckBox = findViewById(R.id.dance_checkBox)
        val rating2:RatingBar = findViewById(R.id.dance_ratingBar)

        val checkBox3:CheckBox = findViewById(R.id.play_checkBox)
        val rating3:RatingBar = findViewById(R.id.play_ratingBar)

        val checkBox4:CheckBox = findViewById(R.id.fashion_checkBox)
        val rating4:RatingBar = findViewById(R.id.fashion_ratingBar)

        val checkBox5:CheckBox = findViewById(R.id.food_checkBox)
        val rating5:RatingBar = findViewById(R.id.food_ratingBar)

        checkbox1.setOnClickListener {
            showHide(rating1)
        }

        checkbox2.setOnClickListener { showHide(rating2) }

        checkBox3.setOnClickListener { showHide(rating3) }

        checkBox4.setOnClickListener { showHide(rating4) }

        checkBox5.setOnClickListener { showHide(rating5) }

        val clearButton:Button = findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            Toast.makeText(applicationContext, "Clicked Clear", Toast.LENGTH_SHORT).show()
            clearUtil(checkbox1, rating1)
            clearUtil(checkbox2, rating2)
            clearUtil(checkBox3, rating3)
            clearUtil(checkBox4, rating4)
            clearUtil(checkBox5, rating5)
        }

        val submitButton:Button = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            Toast.makeText(applicationContext, "Clicked Submit", Toast.LENGTH_SHORT).show()
            val events = ArrayList<String>()
            val ratings = ArrayList<Int>()

            prepareArrayList(checkbox1, rating1, events, ratings)
            prepareArrayList(checkbox2, rating2, events, ratings)
            prepareArrayList(checkBox3, rating3, events, ratings)
            prepareArrayList(checkBox4, rating4, events, ratings)
            prepareArrayList(checkBox5, rating5, events, ratings)

            val bundle:Bundle = Bundle()

            bundle.putStringArrayList("Events", events)
            bundle.putIntegerArrayList("Ratings", ratings)

            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun showHide(view:View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }

    private fun clearUtil(checkbox:CheckBox, ratingBar: RatingBar){
        ratingBar.rating = 0F
        ratingBar.visibility = RatingBar.INVISIBLE
        checkbox.isChecked = false
    }

    private fun prepareArrayList(checkbox: CheckBox, ratingBar: RatingBar, events:ArrayList<String>, ratings:java.util.ArrayList<Int>){
        if(checkbox.isChecked){
            events.add(checkbox.text.toString())
            ratings.add(ratingBar.numStars)
        }
    }

}