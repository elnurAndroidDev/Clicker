package com.isayevapps.clicker.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.isayevapps.clicker.screens.coordinates.Coordinate

@Entity(tableName = "coordinates", indices = [Index(value = ["index"], unique = true)])
data class CoordinateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val index: Int,
    val deviceId: Int,
    val x: Int,
    val y: Int,
    val clicksCount: Int,
    val time: Int,
    val keyDownTime: Int,
    val intervalTime: Int,
) {
    fun toCoordinate(): Coordinate {
        return Coordinate(id, index, deviceId, x, y, clicksCount, time, keyDownTime, intervalTime)
    }
}
