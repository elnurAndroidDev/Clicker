package com.isayevapps.clicker.screens.device.add

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

data class AddDeviceUiState(
    val deviceName: TextFieldValue = TextFieldValue("", selection = TextRange(0)),
    val invalidUrlErrorText: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)
