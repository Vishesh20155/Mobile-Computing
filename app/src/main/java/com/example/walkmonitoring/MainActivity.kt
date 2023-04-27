package com.example.walkmonitoring

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.pow
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var btnNextAct: Button

    private var accelerometerSensor: Sensor? = null
    private lateinit var tvAccelerometer: TextView
    private lateinit var toggleAccelerometer: ToggleButton

    //    For Graph:
    private var mSeries1: LineGraphSeries<DataPoint?>? = null
    private var mSeries2: LineGraphSeries<DataPoint>? = null

    private val mRawAccelValues = FloatArray(3)
    private val mRunningAccelTotal = FloatArray(3)
    private val mCurAccelAvg = FloatArray(3)
    var mSensorCount = 0

    private var mGraph1LastXValue = 0.0
    private var mGraph2LastXValue = 0.0

    private var lastMag: Double = 0.0
    private var avgMag = 0.0
    private var netMag = 0.0

    private val smoothingWindowSize = 20
    private val mAccelValueHistory = Array(3) {
        FloatArray(
            smoothingWindowSize
        )
    }
    private var mCurReadIndex = 0

    //peak detection variables
    private var lastXPoint = 1.0
    var stepThreshold = 1.0
    var noiseThreshold = 2.0
    private val windowSize = 10

    var mStepCounter = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        toggleAccelerometer = findViewById(R.id.tb_accelerometer)
        tvAccelerometer = findViewById(R.id.tv_accelerometer)

        toggleAccelerometer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            else {
                sensorManager.unregisterListener(this, accelerometerSensor)
                tvAccelerometer.text = "Disabled"
            }
        }

        val graph = findViewById<GraphView>(R.id.graph)
        mSeries1 = LineGraphSeries()
        graph.addSeries(mSeries1)
        graph.title = "Accelerator Signal"
        graph.gridLabelRenderer.verticalAxisTitle = "Signal Value"
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(60.0)

        val graph2 = findViewById<GraphView>(R.id.graph2)
        mSeries2 = LineGraphSeries()
        graph2.title = "Smoothed Signal"
        graph2.addSeries(mSeries2)
        graph2.gridLabelRenderer.verticalAxisTitle = "Signal Value"
        graph2.viewport.isXAxisBoundsManual = true
        graph2.viewport.setMinX(0.0)
        graph2.viewport.setMaxX(60.0)

        btnNextAct = findViewById(R.id.btn_stair_activity)
        btnNextAct.setOnClickListener {
            val intent = Intent(this, StairElevatorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                tvAccelerometer.text = x.toString()+"\n"+y.toString()+"\n"+z.toString()

                mRawAccelValues[0] = event.values[0];
                mRawAccelValues[1] = event.values[1];
                mRawAccelValues[2] = event.values[2];

                lastMag = sqrt(
                    mRawAccelValues[0].pow(2) + mRawAccelValues[1].pow(2) + mRawAccelValues[2].pow(
                        2
                    )
                ).toDouble();

                for (i in 0..2) {
                    mRunningAccelTotal[i] =
                        mRunningAccelTotal[i] - mAccelValueHistory[i][mCurReadIndex]
                    mAccelValueHistory[i][mCurReadIndex] = mRawAccelValues[i]
                    mRunningAccelTotal[i] =
                        mRunningAccelTotal[i] + mAccelValueHistory[i][mCurReadIndex]
                    mCurAccelAvg[i] = mRunningAccelTotal[i] / smoothingWindowSize
                }
                mCurReadIndex++;
                if(mCurReadIndex >= smoothingWindowSize){
                    mCurReadIndex = 0;
                }

                avgMag = sqrt(
                    mCurAccelAvg[0].pow(2) + mCurAccelAvg[1].pow(2) + mCurAccelAvg[2].pow(2)
                ).toDouble()

                netMag = lastMag - avgMag; //removes gravity effect

                //update graph data points

                //update graph data points
                mGraph1LastXValue += 1.0
                mSeries1!!.appendData(DataPoint(mGraph1LastXValue, lastMag), true, 60)

                mGraph2LastXValue += 1.0
                mSeries2!!.appendData(DataPoint(mGraph2LastXValue, netMag), true, 60)

                peakDetection()

                val tvSteps = findViewById<TextView>(R.id.tv_num_steps)
                tvSteps.text = mStepCounter.toString()
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
                    mStepCounter += 1
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }
}