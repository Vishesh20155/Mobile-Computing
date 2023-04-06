package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlin.math.abs

class OrientationActivity : AppCompatActivity(), SensorEventListener {

    private var isAligned = false
    private var isRegistered: Boolean = false

//    private lateinit var compassView: ImageView
    private lateinit var tvGeoma: TextView
    private lateinit var tvSuccess: TextView

    private lateinit var sensorManager: SensorManager
    private var geomagSensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orientation)

//        rotationMatrix = FloatArray(9)
//        orientation = FloatArray(3)

        // Get references to the compass and feedback views
//        compassView = findViewById(R.id.compass_view)
        tvGeoma = findViewById(R.id.tv_geomag)
        tvSuccess = findViewById(R.id.tv_success)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        geomagSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            val rotationVector = event.values

            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)
            SensorManager.getOrientation(rotationMatrix, orientation)

            val z_orientation = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val x_orientation = Math.toDegrees(orientation[1].toDouble()).toFloat()
            val y_orientation = Math.toDegrees(orientation[2].toDouble()).toFloat()

//            tvGeoma.text = rotationVector.size.toString() +
//                    "\n" + azimuthDegrees +
//                    "\n" + pitchDegrees +
//                    "\n" + rollDegrees +
//                    "\n" + rotationVector[3] +
//                    "\n" + rotationVector[4]

            if (abs(z_orientation) <= 2) {
                var direction = "left"
                if(z_orientation<0) direction = "right"
                tvGeoma.text = "Rotate the phone around z-axis towards $direction by ${abs(z_orientation)} deg"
            }
            else if (abs(x_orientation) <= 2) {

            }
            else if (abs(y_orientation) <= 2) {

            }
            else {
                tvSuccess.visibility = View.VISIBLE
                tvGeoma.visibility = View.GONE
                sensorManager.unregisterListener(this)
                isRegistered = false
            }
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
        isRegistered = true
    }

    override fun onPause() {
        super.onPause()
        if (isRegistered)
            sensorManager.unregisterListener(this)
    }

}