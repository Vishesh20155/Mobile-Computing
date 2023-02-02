package com.example.odysseysurveyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
    }

    fun submitButtonFunction(view: View) {
        Toast.makeText(applicationContext,"this is toast message",Toast.LENGTH_SHORT).show()
    }

}