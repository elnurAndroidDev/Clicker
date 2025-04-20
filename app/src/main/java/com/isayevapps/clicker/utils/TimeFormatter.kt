package com.isayevapps.clicker.utils

fun timeStrToInt(time: String): Int {
    val (hours, minutes, seconds) = time.split(":").map { it.toInt() }
    return hours * 3600 + minutes * 60 + seconds
}

fun timeIntToStr(time: Int): String {
    return String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60)
}