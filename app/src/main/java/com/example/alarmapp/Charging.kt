package com.example.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Charging : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
            context?.stopService(Intent(context, AlarmService::class.java))
        }
    }
}