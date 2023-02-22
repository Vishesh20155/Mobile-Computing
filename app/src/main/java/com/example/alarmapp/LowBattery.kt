package com.example.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class LowBattery : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val threshold = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = threshold * 100 / scale.toFloat()

            if (batteryPct < 20) {
                // The battery level is below 20%, stop the service
                context?.stopService(Intent(context, AlarmService::class.java))
            }
        }
    }

}