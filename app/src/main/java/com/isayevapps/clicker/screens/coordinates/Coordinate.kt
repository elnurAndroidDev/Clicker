package com.isayevapps.clicker.screens.coordinates

import com.isayevapps.clicker.data.db.CoordinateEntity

data class Coordinate(
    val id: Int = 0,
    val index: Int,
    val deviceId: Int,
    val x: Int,
    val y: Int,
    val clicksCount: Int,
    val time: Int,
    val keyDownTime: Int,
    val intervalTime: Int
) {
    fun toCoordinateEntity(): CoordinateEntity {
        return CoordinateEntity(
            id = id,
            index = index,
            deviceId = deviceId,
            x = x,
            y = y,
            clicksCount = clicksCount,
            time = time,
            keyDownTime = keyDownTime,
            intervalTime = intervalTime
        )

    }
}
