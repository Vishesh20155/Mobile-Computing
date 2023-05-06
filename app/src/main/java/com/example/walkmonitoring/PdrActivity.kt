package com.example.walkmonitoring

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import javax.net.ssl.SNIServerName
import kotlin.math.roundToInt

class PdrActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var magnetometerSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null

    val accVec = FloatArray(3)

    private val orientation = FloatArray(3)

    private lateinit var tvDirection: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdr)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)


//        Initialize UI elements here:
        tvDirection = findViewById(R.id.tv_direction)

        accVec[0] = 0.0F
        accVec[1] = 0.0F
        accVec[2] = 9.8F
    }

//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor == null) {
//            tvDirection.text = "Sensor Not found"
//            return
//        }
//
//        when (event.sensor.type) {
//            Sensor.TYPE_ACCELEROMETER -> System.arraycopy(event.values, 0, orientation, 0, 3)
//            Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, orientation, 3, 3)
//        }
//
//        val rotationMatrix = FloatArray(9)
//        val success = SensorManager.getRotationMatrix(rotationMatrix, null, orientation, orientation.copyOfRange(3, 6))
//
//        if (success) {
//            val orientationValues = FloatArray(3)
//            SensorManager.getOrientation(rotationMatrix, orientationValues)
//
//            val azimuth = orientationValues[0] * 180 / Math.PI
//            val currentDirection = getDirectionFromDegrees(azimuth.roundToInt())
//        }
//    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        when(event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accVec[0] = event.values[0]
                accVec[1] = event.values[1]
                accVec[2] = event.values[2]
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {

                val rotationMatrix = FloatArray(9)
//                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getRotationMatrix(rotationMatrix, null, accVec, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val rotAngleZ = orientation[0] * 180 / Math.PI
                getDirectionFromDegrees(rotAngleZ)
            }
        }
    }

    private fun getDirectionFromDegrees(x: Double) {
        if(x <= 22.5 && x >= -22.5){
            tvDirection.text = "Direction: North --> $x"
        }
        else if(x < -22.5 && x >= -67.5){
            tvDirection.text = "Direction: North-West --> $x"
        }
        else if(x < -67.5 && x >= -112.5){
            tvDirection.text = "Direction: West --> $x"
        }
        else if(x < -112.5 && x >= -157.5){
            tvDirection.text = "Direction: South-West --> $x"
        }
        else if(x < -157.5 || x >= 157.5){
            tvDirection.text = "Direction: South --> $x"
        }
        else if(x < 157.5 && x >= 112.5){
            tvDirection.text = "Direction: South-East --> $x"
        }
        else if(x < 112.5 && x >= 67.5){
            tvDirection.text = "Direction: East --> $x"
        }
        else if(x < 67.5 && x > 22.5){
            tvDirection.text = "Direction: North-East --> $x"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

}