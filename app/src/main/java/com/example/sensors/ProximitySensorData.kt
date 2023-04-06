package com.example.sensors

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProximitySensorData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "values") val value: Float
)
