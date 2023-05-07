package com.example.walkmonitoring

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var srfView: SurfaceView
    private lateinit var srfHolder: SurfaceHolder
    private lateinit var btnStep: Button

    private val paint = Paint()
    private val walkingCoordinatesX = mutableListOf<Float>()
    private val walkingCoordinatesY = mutableListOf<Float>()

//    For distance and displacement
    private lateinit var tvDistance: TextView
    private lateinit var tvDisplacement: TextView
    private lateinit var tbMale: ToggleButton
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    var strideLength = 74.7f
    var displacementX = 0.0
    var displacementY = 0.0



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

    private var graphVals1: LineGraphSeries<DataPoint?>? = null
    private var graphSeries2: LineGraphSeries<DataPoint>? = null

    private val currAccVals = FloatArray(3)
    private val netAccelMovingVals = FloatArray(3)
    private val accAvgVals = FloatArray(3)

    private var graph1PrevX = 0.0
    private var graph2PrevX = 0.0

    private var lastMag: Double = 0.0
    private var avgMag = 0.0
    private var netMag = 0.0

    private val smoothingWindowSize = 20
    private val accPrevVals = Array(3) {
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

    var stepCounterVal = 0f

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
                var multiplier = 0.415f
                multiplier = if (!tbMale.isChecked) {
                    0.415f
                } else {
                    0.413f
                }
                strideLength = etHeight.text.toString().toFloat() * multiplier

                Toast.makeText(applicationContext, "Stride Length = $strideLength", Toast.LENGTH_SHORT).show()

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

        graphVals1 = LineGraphSeries()

        graphSeries2 = LineGraphSeries()

        btnNextAct = findViewById(R.id.btn_stair_activity)
        btnNextAct.setOnClickListener {
            val intent = Intent(this, StairElevatorActivity::class.java)
            startActivity(intent)
        }

        tvDistance = findViewById(R.id.tv_distance)
        tvDisplacement = findViewById(R.id.tv_displacement)
        etHeight = findViewById(R.id.et_inp_height)
        etWeight = findViewById(R.id.et_inp_weight)
        tbMale = findViewById(R.id.tb_male)


        btnStep.setOnClickListener {
            val canvas = srfHolder.lockCanvas()
//            for (i in 0..simulatedSteps) {
//                canvas.drawPoint(500f + 0 * i, 1000f - 30 * i, paint)
//            }
//            simulatedSteps++

            for (i in 0 until walkingCoordinatesX.size) {
//                canvas.drawPoint(walkingCoordinatesX[i], walkingCoordinatesY[i], paint)
                if(i==0) {
                    canvas.drawLine(500f, 1000f, walkingCoordinatesX[i], walkingCoordinatesY[i], paint)
                }
                else {
                    canvas.drawLine(walkingCoordinatesX[i-1], walkingCoordinatesY[i-1], walkingCoordinatesX[i], walkingCoordinatesY[i], paint)
                }
            }
            srfHolder.unlockCanvasAndPost(canvas)
            tvDisplacement.text = (sqrt(displacementX*displacementX + displacementY*displacementY)).toString()
            Toast.makeText(applicationContext, "X: $displacementX Y:$displacementY", Toast.LENGTH_SHORT).show()
            tvDistance.text = (stepCounterVal.toFloat() * strideLength).toString()
        }

        paint.color = Color.CYAN
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 5F

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

                currAccVals[0] = event.values[0];
                currAccVals[1] = event.values[1];
                currAccVals[2] = event.values[2];

                lastMag = sqrt(
                    currAccVals[0].pow(2) + currAccVals[1].pow(2) + currAccVals[2].pow(
                        2
                    )
                ).toDouble();

                for (i in 0..2) {
                    netAccelMovingVals[i] =
                        netAccelMovingVals[i] - accPrevVals[i][mCurReadIndex]
                    accPrevVals[i][mCurReadIndex] = currAccVals[i]
                    netAccelMovingVals[i] =
                        netAccelMovingVals[i] + accPrevVals[i][mCurReadIndex]
                    accAvgVals[i] = netAccelMovingVals[i] / smoothingWindowSize
                }
                mCurReadIndex++;
                if(mCurReadIndex >= smoothingWindowSize){
                    mCurReadIndex = 0;
                }

                avgMag = sqrt(
                    accAvgVals[0].pow(2) + accAvgVals[1].pow(2) + accAvgVals[2].pow(2)
                ).toDouble()

                netMag = lastMag - avgMag; //removes gravity effect

                //update graph data points

                //update graph data points
                graph1PrevX += 1.0
                graphVals1!!.appendData(DataPoint(graph1PrevX, lastMag), true, 60)

                graph2PrevX += 1.0
                graphSeries2!!.appendData(DataPoint(graph2PrevX, netMag), true, 60)

                detectStep()

                val tvSteps = findViewById<TextView>(R.id.tv_num_steps)
                tvSteps.text = stepCounterVal.toString()
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

    private fun detectStep() {

        val highestValX = graphSeries2!!.highestValueX
        if (highestValX - lastXPoint < windowSize) {
            return
        }
        val valuesInWindow = graphSeries2!!.getValues(lastXPoint, highestValX)
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
                    stepCounterVal += 1
                    walkingCoordinatesX.add((500f+30*sin((currAngle-initialAngle)*(Math.PI/180))*stepCounterVal).toFloat())
                    walkingCoordinatesY.add((1000f-30*cos((currAngle-initialAngle)*(Math.PI/180))*stepCounterVal).toFloat())

                    displacementY += strideLength*cos((currAngle-initialAngle)*(Math.PI/180))
                    displacementX += strideLength*sin((currAngle-initialAngle)*(Math.PI/180))
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

// References:
// https://github.com/isibord/StepTrackerAndroid/blob/355df91edd759b674764f5a94c1162105cf663d9/app/src/main/java/edu/uw/daisyi/steptracker/DebugActivity.java#L157
// https://www.verywellfit.com/set-pedometer-better-accuracy-3432895