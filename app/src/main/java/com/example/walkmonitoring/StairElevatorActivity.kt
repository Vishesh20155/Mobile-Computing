package com.example.walkmonitoring

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.sqrt

class StairElevatorActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var accelerometerSensor: Sensor? = null
    private var magnetometerSensor: Sensor? = null
    private lateinit var tvStair: TextView
    private lateinit var tvAccelerometer: TextView

    //    For Graph:
    private var graphSeries1: LineGraphSeries<DataPoint?>? = null
    private var graphDataPoints2: LineGraphSeries<DataPoint>? = null

    private var accReadings = 0.0
    private var totalAccValue = 0.0
    private var accAvg = 0.0

    private var g1XPrev = 0.0
    private var g2PrevX = 0.0

    private var lastMag: Double = 0.0
    private var avgMag = 0.0
    private var netMag = 0.0

    private val smoothingWindowSize = 3
    private val accWindowValues = FloatArray(smoothingWindowSize)
    private var currRunningIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stair_elevator)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        tvStair = findViewById(R.id.tv_is_stair)
        tvAccelerometer = findViewById(R.id.tv_acc_stair)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL)

        val graph = findViewById<GraphView>(R.id.graph_stairs)
        graphSeries1 = LineGraphSeries()
        graph.addSeries(graphSeries1)
        graph.title = "Accelerator Signal"
        graph.gridLabelRenderer.verticalAxisTitle = "Signal Value"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(60.0)

        val graph2 = findViewById<GraphView>(R.id.graph2_stairs)
        graphDataPoints2 = LineGraphSeries()
        graph2.title = "Smoothed Signal"
        graph2.addSeries(graphDataPoints2)
        graph2.gridLabelRenderer.verticalAxisTitle = "Signal Value"
        graph2.viewport.isXAxisBoundsManual = true
        graph2.viewport.setMinX(0.0)
        graph2.viewport.setMaxX(60.0)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
//                tvAccelerometer.text = x.toString()+"\n"+y.toString()+"\n"+z.toString()

                accReadings = event.values[2].toDouble()

                lastMag = accReadings.toDouble()

//                for (i in 0..2) {
//                    mRunningAccelTotal[i] = mRunningAccelTotal[i] - mAccelValueHistory[i][mCurReadIndex]
//                    mAccelValueHistory[i][mCurReadIndex] = mRawAccelValues[i]
//                    mRunningAccelTotal[i] = mRunningAccelTotal[i] + mAccelValueHistory[i][mCurReadIndex]
//                    mCurAccelAvg[i] = mRunningAccelTotal[i] / smoothingWindowSize
//                }
                totalAccValue = totalAccValue - accWindowValues[currRunningIdx]
                accWindowValues[currRunningIdx] = accReadings.toFloat()
                totalAccValue = totalAccValue + accWindowValues[currRunningIdx]
                accAvg = totalAccValue / smoothingWindowSize

                currRunningIdx++;
                if(currRunningIdx >= smoothingWindowSize){
                    currRunningIdx = 0;
                }

                avgMag = accAvg

                if (lastMag - avgMag > 0.2)
                    netMag = lastMag - avgMag; //removes gravity effect

//                netMag = lastMag - avgMag; //removes gravity effect

                //update graph data points

                //update graph data points
                g1XPrev += 1.0
                graphSeries1!!.appendData(DataPoint(g1XPrev, lastMag), true, 60)

                g2PrevX += 1.0
                graphDataPoints2!!.appendData(DataPoint(g2PrevX, netMag), true, 60)

                if(netMag > 3.5 && g2PrevX>20) {
                    tvStair.text = "Stairs"
                    Toast.makeText(applicationContext, "Stairs Detected", Toast.LENGTH_SHORT).show()
                }
//                peakDetection()
            }

            if(event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                val mag = sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2])
                tvAccelerometer.text = "\n $mag"
                if(mag < 22) {
                    Toast.makeText(applicationContext, "Lift Detected", Toast.LENGTH_SHORT).show()
                    tvStair.text = "Lift"
                }
                else {
                    tvStair.text = "Nothing"
                }
            }
        }
        else {
            tvAccelerometer.text = "Disabled"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}