package com.isayevapps.clicker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isayevapps.clicker.screens.coordinates.Coordinate

@Entity(tableName = "coordinates")
data class CoordinateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val index: Int,
    val deviceId: Int,
    val name: String,
    val x: Int,
    val y: Int,
    val clicksCount: Int,
    val time: Int,
    val keyDownTime: Int,
    val intervalTime: Int,
) {
    fun toCoordinate(): Coordinate {
        return Coordinate(id, index, deviceId, name, x, y, clicksCount, time, keyDownTime, intervalTime)
    }
}
