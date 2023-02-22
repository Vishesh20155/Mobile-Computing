package com.example.alarmapp

import PhoneStateReceiver
import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.time.LocalDateTime


class AlarmService : Service() {


    var hour1 = -1
    var played1 = false
    var played2 = false
    var min1 = -1
    var hour2 = -1
    var min2 = -1

    // Registers for Broadcast Receivers
    private var receiver: BroadcastReceiver? = null
    private var chargingReceiver: BroadcastReceiver? = null
    private var lowBatteryReceiver: BroadcastReceiver? = null

    @Volatile
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler

    private lateinit var ringtone: Ringtone

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Inside onCreate() of Service", Toast.LENGTH_SHORT).show()
        handlerThread = HandlerThread("AlarmThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, defaultRingtoneUri)

        // For Broadcast receiver
        receiver = PhoneStateReceiver()
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(receiver, intentFilter)

        chargingReceiver = Charging()
        val chargingIntentFilter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        registerReceiver(chargingReceiver, chargingIntentFilter)

        lowBatteryReceiver = LowBattery()
        val lowBatteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(lowBatteryReceiver, lowBatteryIntentFilter)
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
        ringtone.stop()
        hour1 = -1
        played1 = true
        played2 = true
        min1 = -1
        hour2 = -1
        min2 = -1
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(chargingReceiver)
        unregisterReceiver(lowBatteryReceiver)
        Toast.makeText(this, "Inside onDestroy() of Service", Toast.LENGTH_SHORT).show()
    }

}

