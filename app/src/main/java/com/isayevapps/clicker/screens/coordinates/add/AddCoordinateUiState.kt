package com.isayevapps.clicker.screens.coordinates.add

import androidx.compose.ui.text.input.TextFieldValue
import com.isayevapps.clicker.screens.coordinates.Coordinate

sealed class AddCoordinateEvent {
    data class OnTimeChange(val hours: Int, val minutes: Int, val seconds: Int, val millis: Int) : AddCoordinateEvent()
    data class OnClicksCountPlus(val id: Int) : AddCoordinateEvent()
    data class OnClicksCountMinus(val id: Int) : AddCoordinateEvent()
    data class OnKeyDownTimeChange(val id: Int, val time: TextFieldValue) : AddCoordinateEvent()
    data class OnIntervalChange(val id: Int, val time: TextFieldValue) : AddCoordinateEvent()
    object OnDecreaseX : AddCoordinateEvent()
    object OnIncreaseX : AddCoordinateEvent()
    object OnDecreaseY : AddCoordinateEvent()
    object OnIncreaseY : AddCoordinateEvent()
    object OnAddClick : AddCoordinateEvent()
    data class OnStepChange(val step: Int) : AddCoordinateEvent()
    data class OnDeleteClick(val id: Int) : AddCoordinateEvent()
    data class OnTimeClick(val id: Int, val time: Int) : AddCoordinateEvent()
    object OnHideDeleteDialog : AddCoordinateEvent()
    object OnHideTimeDialog : AddCoordinateEvent()
    object OnHideErrorDialog : AddCoordinateEvent()
    object OnDelete : AddCoordinateEvent()
}


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
