package com.isayevapps.clicker.screens.coordinates

data class Coordinate(
    val id: Int,
    val index: Int,
    val deviceId: Int,
    val name: String,
    val x: Int,
    val y: Int,
    val clicksCount: Int,
    val time: Int,
    val keyDownTime: Int,
    val intervalTime: Int
)
