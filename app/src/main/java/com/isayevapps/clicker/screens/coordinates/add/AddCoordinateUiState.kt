package com.isayevapps.clicker.screens.coordinates.add

import com.isayevapps.clicker.screens.coordinates.Coordinate

data class AddCoordinateUiState(
    val deviceId: Int = 0,
    val coordinates: List<Coordinate> = emptyList(),
    val x: Int = 0,
    val y: Int = 0,
    val step: Int = 10,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val showTimeDialog: Boolean = false,
    val isLoading: Boolean = false
)
