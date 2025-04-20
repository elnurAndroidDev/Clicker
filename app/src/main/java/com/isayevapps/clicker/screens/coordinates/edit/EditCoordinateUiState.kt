package com.isayevapps.clicker.screens.coordinates.edit

data class EditCoordinateUiState(
    val deviceId: Int = 0,
    val id: Int = 0,
    val index: Int = 0,
    val name: String = "",
    val time: String = "00:00:00",
    val x: Int = 0,
    val y: Int = 0,
    val step: Int = 10,
    val clicksCount: Int = 1,
    val keyDownTime: Int = 100,
    val intervalTime: Int = 100,
    val error: String? = null
)
