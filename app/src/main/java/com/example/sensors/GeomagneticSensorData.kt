package com.example.sensors

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GeomagneticSensorData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "z_orientation") val z_orient: Float,
    @ColumnInfo(name = "x_orientation") val x_orient: Float,
    @ColumnInfo(name = "y_orientation") val y_orient: Float,
    @ColumnInfo(name = "rot_scalar") val rot_scalar: Float,
    @ColumnInfo(name = "correctness") val correctness: Float,
    @ColumnInfo(name = "timestamp") val time: Long
)
