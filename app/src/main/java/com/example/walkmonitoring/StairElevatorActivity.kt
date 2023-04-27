package com.example.walkmonitoring

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ToggleButton
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.pow
import kotlin.math.sqrt

class StairElevatorActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var accelerometerSensor: Sensor? = null
    private lateinit var tvStair: TextView
    private lateinit var tvAccelerometer: TextView

    //    For Graph:
    private var mSeries1: LineGraphSeries<DataPoint?>? = null
    private var mSeries2: LineGraphSeries<DataPoint>? = null

    private var mRawAccelValues = 0.0
    private var mRunningAccelTotal = 0.0
    private var mCurAccelAvg = 0.0

    private var mGraph1LastXValue = 0.0
    private var mGraph2LastXValue = 0.0

    private var lastMag: Double = 0.0
    private var avgMag = 0.0
    private var netMag = 0.0

    private val smoothingWindowSize = 20
    private val mAccelValueHistory = FloatArray(smoothingWindowSize)
    private var mCurReadIndex = 0

    //peak detection variables
    private var lastXPoint = 1.0
    var stepThreshold = 1.3
    var noiseThreshold = 2.0
    private val windowSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stair_elevator)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        tvStair = findViewById(R.id.tv_is_stair)
        tvAccelerometer = findViewById(R.id.tv_acc_stair)
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)

        val graph = findViewById<GraphView>(R.id.graph_stairs)
        mSeries1 = LineGraphSeries()
        graph.addSeries(mSeries1)
        graph.title = "Accelerator Signal"
        graph.gridLabelRenderer.verticalAxisTitle = "Signal Value"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(60.0)

        val graph2 = findViewById<GraphView>(R.id.graph2_stairs)
        mSeries2 = LineGraphSeries()
        graph2.title = "Smoothed Signal"
        graph2.addSeries(mSeries2)
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
                tvAccelerometer.text = x.toString()+"\n"+y.toString()+"\n"+z.toString()

                mRawAccelValues = event.values[2].toDouble()

                lastMag = mRawAccelValues.toDouble()

//                for (i in 0..2) {
//                    mRunningAccelTotal[i] = mRunningAccelTotal[i] - mAccelValueHistory[i][mCurReadIndex]
//                    mAccelValueHistory[i][mCurReadIndex] = mRawAccelValues[i]
//                    mRunningAccelTotal[i] = mRunningAccelTotal[i] + mAccelValueHistory[i][mCurReadIndex]
//                    mCurAccelAvg[i] = mRunningAccelTotal[i] / smoothingWindowSize
//                }
                mRunningAccelTotal = mRunningAccelTotal - mAccelValueHistory[mCurReadIndex]
                mAccelValueHistory[mCurReadIndex] = mRawAccelValues.toFloat()
                mRunningAccelTotal = mRunningAccelTotal + mAccelValueHistory[mCurReadIndex]
                mCurAccelAvg = mRunningAccelTotal / smoothingWindowSize

                mCurReadIndex++;
                if(mCurReadIndex >= smoothingWindowSize){
                    mCurReadIndex = 0;
                }

                avgMag = mCurAccelAvg

                netMag = lastMag - avgMag; //removes gravity effect

                //update graph data points

                //update graph data points
                mGraph1LastXValue += 1.0
                mSeries1!!.appendData(DataPoint(mGraph1LastXValue, lastMag), true, 60)

                mGraph2LastXValue += 1.0
                mSeries2!!.appendData(DataPoint(mGraph2LastXValue, netMag), true, 60)

                peakDetection()
            }
        }
        else {
            tvAccelerometer.text = "Disabled"
        }
    }

    private fun peakDetection() {

        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer, Mladenov et al.
            *Threshold, stepThreshold was derived by observing people's step graph
            * ASSUMPTIONS:
            * Phone is held vertically in portrait orientation for better results
         */
        val highestValX = mSeries2!!.highestValueX
        if (highestValX - lastXPoint < windowSize) {
            return
        }
        val valuesInWindow = mSeries2!!.getValues(lastXPoint, highestValX)
        lastXPoint = highestValX
        var forwardSlope = 0.0
        var downwardSlope = 0.0
        val dataPointList: MutableList<DataPoint> = ArrayList()
        valuesInWindow.forEachRemaining { e: DataPoint ->
            dataPointList.add(
                e
            )
        } //This requires API 24 or higher
        for (i in dataPointList.indices) {
            if (i == 0) continue else if (i < dataPointList.size - 1) {
                forwardSlope = dataPointList[i + 1].y - dataPointList[i].y
                downwardSlope = dataPointList[i].y - dataPointList[i - 1].y
                if (forwardSlope < 0 && downwardSlope > 0 && dataPointList[i].y > stepThreshold && dataPointList[i].y < noiseThreshold) {
                    tvStair.text = "Climbing"
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }
}