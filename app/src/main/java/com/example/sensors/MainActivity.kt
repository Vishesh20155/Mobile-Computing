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
import androidx.lifecycle.ViewModel
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var proximitySensor: Sensor? = null
    private lateinit var tvProximity: TextView
    private lateinit var toggleProximity: ToggleButton

    private var lightSensor: Sensor? = null
    private lateinit var tvLight: TextView
    private lateinit var toggleLight: ToggleButton

    private var geoMagRotVecSensor: Sensor? = null
    private lateinit var tvGeoMagRotVec0: TextView
    private lateinit var tvGeoMagRotVec1: TextView
    private lateinit var tvGeoMagRotVec2: TextView
    private lateinit var tvGeoMagRotVec3: TextView
    private lateinit var toggleGeoMatRotVec: ToggleButton

    private lateinit var btnOrient: Button

    private lateinit var db: SensorDatabase

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        geoMagRotVecSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)

        btnOrient = findViewById(R.id.btn_orient)
        btnOrient.setOnClickListener {
            val intent = Intent(this, OrientationActivity::class.java)
            startActivity(intent)
        }

        tvProximity = findViewById(R.id.tv_proximity)
        tvLight = findViewById(R.id.tv_light)
        tvGeoMagRotVec0 = findViewById(R.id.tv_geomagnetic_0)
        tvGeoMagRotVec1 = findViewById(R.id.tv_geomagnetic_1)
        tvGeoMagRotVec2 = findViewById(R.id.tv_geomagnetic_2)
        tvGeoMagRotVec3 = findViewById(R.id.tv_geomagnetic_3)

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

        toggleGeoMatRotVec = findViewById(R.id.toggle_geomagnetic)
        toggleGeoMatRotVec.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                sensorManager.registerListener(this, geoMagRotVecSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            else {
                sensorManager.unregisterListener(this, geoMagRotVecSensor)
                tvGeoMagRotVec0.text = "Disabled"
                tvGeoMagRotVec1.text = "Disabled"
                tvGeoMagRotVec2.text = "Disabled"
                tvGeoMagRotVec3.text = "Disabled"
            }
        }

        db = Room.databaseBuilder(
            applicationContext,
            SensorDatabase::class.java,
            "Sensor-db"
        ).build()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            tvProximity.text = distance.toString()

            val proximityDao = db.proximityDao()
//            proximityDao.insert(ProximitySensorData(0, distance))
            CoroutineScope(Dispatchers.IO).launch {
                proximityDao.insert(ProximitySensorData(0, distance))
            }
        }
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]
            tvLight.text = light.toString()
            val lightDao = db.lightDao()
            CoroutineScope(Dispatchers.IO).launch {
                lightDao.insert(LightSensorData(0, light))
            }
        }

        if (event.sensor.type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            val geomag = event.values
//            tvGeoMagRotVec0.text = geomag[0].toString()
            tvGeoMagRotVec1.text = geomag[1].toString()
//            tvGeoMagRotVec2.text = geomag[2].toString()
//            tvGeoMagRotVec3.text = geomag[3].toString()
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