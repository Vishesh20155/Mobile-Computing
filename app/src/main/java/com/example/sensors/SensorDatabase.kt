package com.example.sensors

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProximitySensorData::class, LightSensorData::class, GeomagneticSensorData::class], version = 2)
abstract class SensorDatabase: RoomDatabase() {
    abstract fun proximityDao(): ProximityDao
    abstract fun lightDao(): LightDao
    abstract fun geomagneticDao(): GeomagneticDao
}