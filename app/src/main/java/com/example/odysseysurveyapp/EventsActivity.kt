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

    private lateinit var checkbox1:CheckBox
    private lateinit var rating1:RatingBar

    lateinit var checkbox2:CheckBox
    private lateinit var rating2:RatingBar

    private lateinit var checkBox3:CheckBox
    private lateinit var rating3:RatingBar

    private lateinit var checkBox4:CheckBox
    private lateinit var rating4:RatingBar

    private lateinit var checkBox5:CheckBox
    private lateinit var rating5:RatingBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        Toast.makeText(this, "Entered onCreate for Events", Toast.LENGTH_SHORT).show()

        checkbox1 = findViewById(R.id.music_checkBox)
        rating1 = findViewById(R.id.music_ratingBar)

        checkbox2 = findViewById(R.id.dance_checkBox)
        rating2 = findViewById(R.id.dance_ratingBar)

        checkBox3 = findViewById(R.id.play_checkBox)
        rating3 = findViewById(R.id.play_ratingBar)

        checkBox4 = findViewById(R.id.fashion_checkBox)
        rating4 = findViewById(R.id.fashion_ratingBar)

        checkBox5 = findViewById(R.id.food_checkBox)
        rating5 = findViewById(R.id.food_ratingBar)

        checkbox1.setOnClickListener {
            showHideRating(rating1, checkbox1)
        }

        checkbox2.setOnClickListener { showHideRating(rating2, checkbox2) }

        checkBox3.setOnClickListener { showHideRating(rating3, checkBox3) }

        checkBox4.setOnClickListener { showHideRating(rating4, checkBox4) }

        checkBox5.setOnClickListener { showHideRating(rating5, checkBox5) }

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

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putFloat("val", rating1.rating)
//        outState.putFloat("val", rating2.rating)
//        outState.putFloat("val", rating3.rating)
//        outState.putFloat("val", rating4.rating)
//        outState.putFloat("val", rating5.rating)
//    }

    private fun showHideRating(ratingBar: RatingBar, checkbox: CheckBox) {
        ratingBar.visibility = if (checkbox.isChecked){
            View.VISIBLE
        } else{
            View.INVISIBLE
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
            ratings.add(ratingBar.rating.toInt())
        }
    }

}