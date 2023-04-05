package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class OrientationActivity : AppCompatActivity(), SensorEventListener {

    private var isAligned = false
    private var azimuth = 0f
    private var targetAzimuth = 0f

    private lateinit var rotationMatrix: FloatArray
    private lateinit var orientation: FloatArray
//    private lateinit var compassView: ImageView
    private lateinit var tvGeoma: TextView

    private lateinit var sensorManager: SensorManager
    private var geomagSensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orientation)

        rotationMatrix = FloatArray(9)
        orientation = FloatArray(3)

        // Get references to the compass and feedback views
//        compassView = findViewById(R.id.compass_view)
        tvGeoma = findViewById(R.id.tv_geomag)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        geomagSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
//            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
//            SensorManager.getOrientation(rotationMatrix, orientation)
//
//            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
//            val declination = getMagneticDeclination()
//            targetAzimuth = azimuth + declination
//
////            compassView.rotation = -azimuth
//
//            // Check if the device is aligned with the magnetic north pole
//            if (!isAligned && abs(azimuth - targetAzimuth) < 5f) {
//                isAligned = true
//                feedbackView.text = "Success! Your phone is aligned with Earth's frame of reference."
//            } else if (!isAligned) {
//                // Calculate the rotation needed to align the device with the magnetic north pole
//                val diff = (targetAzimuth - azimuth + 360f) % 360f
//                val direction = if (diff > 180f) "right" else "left"
//                val angle = min(diff, 360f - diff)
//                val message = "Rotate $angle° $direction to align with Earth's frame of reference."
//                feedbackView.text = message
//            }

            val rotationVector = event.values
            tvGeoma.text = rotationVector.size.toString()
        }
    }



    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }

    override fun onResume() {
        super.onResume()

        geomagSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

}