package com.example.odysseysurveyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
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

        Toast.makeText(this, "State of activity: ${this.localClassName} is now onCreate", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onCreate")

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

        if (savedInstanceState!=null){
//            Toast.makeText(this, "Restoring", Toast.LENGTH_SHORT).show()
            val check1 = savedInstanceState.getBoolean("check1")
            val check2 = savedInstanceState.getBoolean("check2")
            val check3 = savedInstanceState.getBoolean("check3")
            val check4 = savedInstanceState.getBoolean("check4")
            val check5 = savedInstanceState.getBoolean("check5")

            if(check1){ rating1.visibility=View.VISIBLE }
            if(check2){ rating2.visibility=View.VISIBLE }
            if(check3){ rating3.visibility=View.VISIBLE }
            if(check4){ rating4.visibility=View.VISIBLE }
            if(check5){ rating5.visibility=View.VISIBLE }
        }

        checkbox1.setOnClickListener {
            showHideRating(rating1, checkbox1)
        }

        checkbox2.setOnClickListener { showHideRating(rating2, checkbox2) }

        checkBox3.setOnClickListener { showHideRating(rating3, checkBox3) }

        checkBox4.setOnClickListener { showHideRating(rating4, checkBox4) }

        checkBox5.setOnClickListener { showHideRating(rating5, checkBox5) }

        val clearButton:Button = findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
//            Toast.makeText(applicationContext, "Clicked Clear", Toast.LENGTH_SHORT).show()
            clearUtil(checkbox1, rating1)
            clearUtil(checkbox2, rating2)
            clearUtil(checkBox3, rating3)
            clearUtil(checkBox4, rating4)
            clearUtil(checkBox5, rating5)
        }

        val submitButton:Button = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
//            Toast.makeText(applicationContext, "Clicked Submit", Toast.LENGTH_SHORT).show()
            val events = ArrayList<String>()
            val ratings = ArrayList<Int>()

            prepareArrayList(checkbox1, rating1, events, ratings)
            prepareArrayList(checkbox2, rating2, events, ratings)
            prepareArrayList(checkBox3, rating3, events, ratings)
            prepareArrayList(checkBox4, rating4, events, ratings)
            prepareArrayList(checkBox5, rating5, events, ratings)

            val bundleIncoming = intent.extras
            val bundle:Bundle = Bundle()

            if(bundleIncoming!=null){
                bundle.putString("Name", bundleIncoming.getString("Name"))
                bundle.putString("Role", bundleIncoming.getString("Role"))
            }

            bundle.putStringArrayList("Events", events)
            bundle.putIntegerArrayList("Ratings", ratings)

            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("check1", checkbox1.isChecked)
        outState.putBoolean("check2", checkbox2.isChecked)
        outState.putBoolean("check3", checkBox3.isChecked)
        outState.putBoolean("check4", checkBox4.isChecked)
        outState.putBoolean("check5", checkBox5.isChecked)
//        Toast.makeText(this, "Saving Instance", Toast.LENGTH_SHORT).show()
    }

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
        println("Here In Events")
        Toast.makeText(this, "State of activity: $localClassName is now onDestroy", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onDestroy")
        super.onDestroy()
    }

}