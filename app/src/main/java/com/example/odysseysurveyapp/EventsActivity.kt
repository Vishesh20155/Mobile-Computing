package com.example.odysseysurveyapp

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
    }

    private fun showHide(view:View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }

    fun submitButtonFunction(view: View) {
        Toast.makeText(applicationContext,"this is toast message",Toast.LENGTH_SHORT).show()
    }

}