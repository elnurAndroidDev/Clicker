package com.isayevapps.clicker.screens.coordinates.list

import com.isayevapps.clicker.screens.coordinates.Coordinate

data class CoordinateListUiState(
    val coordinates: List<Coordinate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)