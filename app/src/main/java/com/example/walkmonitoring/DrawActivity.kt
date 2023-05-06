package com.example.walkmonitoring

import android.graphics.*
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class DrawActivity : AppCompatActivity() {
    private lateinit var btnDraw: Button
    private lateinit var srfView: SurfaceView
    private lateinit var srfHolder: SurfaceHolder
    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw)

        btnDraw = findViewById(R.id.btn_draw)
        srfView = findViewById(R.id.srf_view)
        srfHolder = srfView.holder

        btnDraw.setOnClickListener {
            Toast.makeText(this, "Clicked Button ${num.toString()}", Toast.LENGTH_SHORT).show()
            val bitmap = Bitmap.createBitmap(srfView.width, srfView.height, Bitmap.Config.ARGB_8888)
            val paint = Paint()
            paint.color = Color.WHITE
            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 15F

            var canvas = Canvas(bitmap)

//            canvas.drawCircle(bitmap.height.toFloat()/2, bitmap.width.toFloat()/2, bitmap.height.toFloat()/5, paint)
//            canvas.drawCircle(bitmap.height.toFloat()/2, bitmap.width.toFloat()/2+bitmap.height.toFloat()/5, bitmap.height.toFloat()/5, paint)
//            imgTrajectoryBG.setImageBitmap(bitmap)


            canvas = srfHolder.lockCanvas()
            if(num%2 == 0)
                canvas.drawCircle(bitmap.height.toFloat()/2, bitmap.width.toFloat()/2+bitmap.height.toFloat()/5, bitmap.height.toFloat()/5, paint)
            else {
                for (i in 0..num)
                    canvas.drawPoint(200f+20*i, 200f+20*i, paint)
            }
            srfHolder.unlockCanvasAndPost(canvas)
            num++
        }
    }
}