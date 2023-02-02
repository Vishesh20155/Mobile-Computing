package com.example.odysseysurveyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Code to add drop down
//        val roleOptions = resources.getStringArray(R.array.role_options)
//        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, roleOptions)
//        val autoCompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
//        autoCompleteTV.setAdapter(arrayAdapter)

        val spinner: Spinner = findViewById(R.id.role_spinner)
        ArrayAdapter.createFromResource(this, R.array.role_options, android.R.layout.simple_spinner_item).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    fun nextButtonFunction(view: View) {
        Toast.makeText(applicationContext,"this is toast message", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, EventsActivity::class.java)
        startActivity(intent)
    }
}