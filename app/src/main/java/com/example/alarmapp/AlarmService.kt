package com.example.alarmapp

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.PriorityQueue


class AlarmService : Service() {

    var hour1 = -1
    var played1 = false
    var played2 = false
    var min1 = -1
    var hour2 = -1
    var min2 = -1

    @Volatile
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler

    private lateinit var ringtone: Ringtone

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Inside onCreate() of Service", Toast.LENGTH_SHORT).show()
        handlerThread = HandlerThread("AlarmThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, defaultRingtoneUri)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Inside onStartCommand() of Service", Toast.LENGTH_SHORT).show()

        if (intent != null){
            if (hour1==-1) {
                hour1 = intent.getIntExtra("Hour", -1)
                min1 = intent.getIntExtra("Minute", -1)
            }
            else {
                hour2 = intent.getIntExtra("Hour", -1)
                min2 = intent.getIntExtra("Minute", -1)
            }
        }
        val s = hour1.toString()+':'+min1.toString()+" - "+hour2.toString()+':'+min2.toString()
        Log.d("Alarm Setting", s)

        handler.post(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                if (true) {
                    if ((LocalDateTime.now().hour==hour1) and (LocalDateTime.now().minute==min1) and !played1) {
                        val s = hour1.toString()+':'+min1.toString()+'-'+hour2.toString()+':'+min2.toString()
                        Log.d("Alarming", s)
                        ringtone.play()
                        Thread.sleep(10000)
                        ringtone.stop()
                        played1=true
                    }

                    if ((LocalDateTime.now().hour==hour2) and (LocalDateTime.now().minute==min2) and !played2) {
                        val s = hour1.toString()+':'+min1.toString()+'-'+hour2.toString()+':'+min2.toString()
                        Log.d("Alarming", s)
                        ringtone.play()
                        Thread.sleep(10000)
                        ringtone.stop()
                        played2=true
                    }
                    handler.postDelayed(this, 10000)
                }
            }
        })

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null
    }

    override fun onDestroy() {
        handlerThread.quit()
        super.onDestroy()
        Toast.makeText(this, "Inside onDestroy() of Service", Toast.LENGTH_SHORT).show()
    }

}