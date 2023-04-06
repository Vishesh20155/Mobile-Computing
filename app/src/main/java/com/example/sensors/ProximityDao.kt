package com.example.sensors

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ProximityDao {
    @Insert
    fun insert(proximitySensorData: ProximitySensorData)
}