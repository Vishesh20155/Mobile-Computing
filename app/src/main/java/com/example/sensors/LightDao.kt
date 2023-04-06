package com.example.sensors

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface LightDao {
    @Insert
    fun insert(lightSensorData: LightSensorData)
}