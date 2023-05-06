package com.example.walkmonitoring

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var srfView: SurfaceView
    private lateinit var srfHolder: SurfaceHolder
    private lateinit var btnStep: Button

    var simulatedSteps = 0
    private val paint = Paint()
    private val walkingCoordinatesX = mutableListOf<Float>()
    private val walkingCoordinatesY = mutableListOf<Float>()


//    For direction
    var initialized = false
    var initialAngle = 0.0
    var currAngle = 0.0
    private val accVec = FloatArray(3)
    private var magnetometerSensor: Sensor? = null
    private lateinit var tvMagnetometer: TextView
    private lateinit var tvDirection: TextView


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

        btnStep = findViewById(R.id.btn_step_simulator)
        srfView = findViewById(R.id.surface_view_trajectoory)
        srfHolder = srfView.holder

        toggleAccelerometer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
                sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
//                initialized = true
            }
            else {
                sensorManager.unregisterListener(this, accelerometerSensor)
                sensorManager.unregisterListener(this, magnetometerSensor)
                tvAccelerometer.text = "Disabled"
                tvMagnetometer.text = "Disabled"
                tvDirection.text = "Disabled"
                initialized = false
            }
        }

        mSeries1 = LineGraphSeries()

//        val graph2 = findViewById<GraphView>(R.id.graph2)
        mSeries2 = LineGraphSeries()
//        graph2.title = "Smoothed Signal"
//        graph2.addSeries(mSeries2)
//        graph2.gridLabelRenderer.verticalAxisTitle = "Signal Value"
//        graph2.viewport.isXAxisBoundsManual = true
//        graph2.viewport.setMinX(0.0)
//        graph2.viewport.setMaxX(60.0)

        btnNextAct = findViewById(R.id.btn_stair_activity)
        btnNextAct.setOnClickListener {
            val intent = Intent(this, StairElevatorActivity::class.java)
            startActivity(intent)
        }

        btnStep.setOnClickListener {

            val canvas = srfHolder.lockCanvas()
//            for (i in 0..simulatedSteps) {
//                canvas.drawPoint(500f + 0 * i, 1000f - 30 * i, paint)
//            }
//            simulatedSteps++

            for (i in 0 until walkingCoordinatesX.size) {
                canvas.drawPoint(walkingCoordinatesX[i], walkingCoordinatesY[i], paint)
            }
            srfHolder.unlockCanvasAndPost(canvas)
        }

        paint.color = Color.CYAN
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 15F

//        Code for Direction Part:
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accVec[0] = 0.0f
        accVec[1] = 0.0f
        accVec[2] = 9.8f
        tvMagnetometer = findViewById(R.id.tv_magnetometer)
        tvDirection = findViewById(R.id.tv_direction)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                accVec[0] = x
                accVec[1] = y
                accVec[2] = z
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

            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                val rotationMatrix = FloatArray(9)
//                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getRotationMatrix(rotationMatrix, null, accVec, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                val rotAngleZ = orientation[0] * 180 / Math.PI

                if (!initialized) {
                    initialAngle = rotAngleZ
                    initialized = true
                }
                currAngle = rotAngleZ
                tvMagnetometer.text = (currAngle-initialAngle).toString()
                getDirectionFromDegrees(rotAngleZ)
            }
        }
        else {
            tvAccelerometer.text = "Disabled"
            tvMagnetometer.text = "Disabled"
            tvDirection.text = "Disabled"
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
                    walkingCoordinatesX.add(500f+0*mStepCounter)
                    walkingCoordinatesY.add(1000f-30f*mStepCounter)
//                    val canvas = srfHolder.lockCanvas()
//                    for (i in 0..mStepCounter.roundToInt()){
//                        canvas.drawPoint(500f+0*i,
//                            1000f-30f*i, paint)
//                    }
//                    srfHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun getDirectionFromDegrees(x: Double) {
        if(x <= 22.5 && x >= -22.5){
            tvDirection.text = "North"
        }
        else if(x < -22.5 && x >= -67.5){
            tvDirection.text = "North-West"
        }
        else if(x < -67.5 && x >= -112.5){
            tvDirection.text = "West"
        }
        else if(x < -112.5 && x >= -157.5){
            tvDirection.text = "South-West"
        }
        else if(x < -157.5 || x >= 157.5){
            tvDirection.text = "South"
        }
        else if(x < 157.5 && x >= 112.5){
            tvDirection.text = "South-East"
        }
        else if(x < 112.5 && x >= 67.5){
            tvDirection.text = "East"
        }
        else if(x < 67.5 && x > 22.5){
            tvDirection.text = "North-East"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("Accuracy changed")
    }
}