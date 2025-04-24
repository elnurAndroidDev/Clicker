package com.isayevapps.clicker.utils

import kotlin.math.floor

fun timeStrToInt(time: String): Int {
    val (hours, minutes, seconds, millis) = time.split(":", ".").map { it.toInt() }
    return hours * 3600000 + minutes * 60000 + seconds * 1000 + millis
}

fun timeIntToStr(milliseconds: Int): String {
    val hours = floor((milliseconds / 3600000.0)).toInt()
    val minutes = floor(((milliseconds % 3600000) / 60000.0)).toInt()
    val seconds = floor(((milliseconds % 60000) / 1000.0)).toInt()
    val millis = milliseconds % 1000
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
}