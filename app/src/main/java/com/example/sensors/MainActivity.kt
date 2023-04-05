package com.example.sensors

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var proximitySensor: Sensor? = null
    private lateinit var tvProximity: TextView
    private lateinit var toggleProximity: ToggleButton

    private var lightSensor: Sensor? = null
    private lateinit var tvLight: TextView
    private lateinit var toggleLight: ToggleButton

    private lateinit var btnOrient: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        btnOrient = findViewById(R.id.btn_orient)
        btnOrient.setOnClickListener {
            val intent = Intent(this, OrientationActivity::class.java)
            startActivity(intent)
        }

        tvProximity = findViewById(R.id.tv_proximity)
        tvLight = findViewById(R.id.tv_light)

        toggleProximity = findViewById(R.id.toggle_proximity)
        toggleProximity.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                proximitySensor?.also { proximity ->
                    sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
            else {
                sensorManager.unregisterListener(this, proximitySensor)
                tvProximity.text = "Disabled"
            }
        }

        toggleLight = findViewById(R.id.toggle_light)
        toggleLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            else {
                sensorManager.unregisterListener(this, lightSensor)
                tvLight.text = "Disabled"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            tvProximity.text = distance.toString()
        }
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]
            tvLight.text = light.toString()
        }
        // Do something with this sensor data.
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
    }
}