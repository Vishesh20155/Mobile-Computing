package com.example.sensors

import androidx.room.Dao
import androidx.room.Insert


@Dao
interface GeomagneticDao {
    @Insert
    fun insert(geomagneticSensorData: GeomagneticSensorData)
}