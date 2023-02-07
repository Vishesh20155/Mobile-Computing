package com.example.odysseysurveyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "State of activity: ${this.localClassName} is now onCreate", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onCreate")
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
//        Toast.makeText(applicationContext,"this is toast message", Toast.LENGTH_SHORT).show()
        val nameText:EditText = findViewById(R.id.name_editText)
        val spinner:Spinner = findViewById(R.id.role_spinner)
        if (nameText.text.toString().isEmpty()){
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
        }
        else {
            val intent = Intent(this, EventsActivity::class.java)
            val bundleOutput = Bundle()
            bundleOutput.putString("Name", nameText.text.toString())
            bundleOutput.putString("Role", spinner.selectedItem.toString())
            intent.putExtras(bundleOutput)
            startActivity(intent)
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

    override fun onDestroy() {
        println("Here In main")
        Toast.makeText(applicationContext, "State of activity: ${this.localClassName} is now onDestroy", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Toast.makeText(this, "State of activity: ${this.localClassName} is now onRestart", Toast.LENGTH_SHORT).show()
        Log.i("Tagger", "State of activity: ${this.localClassName} is now onRestart")
        super.onRestart()
    }

}