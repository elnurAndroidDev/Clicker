package com.isayevapps.clicker.screens.device.add

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

sealed class AddDeviceEvent {
    data class OnDeviceNameChange(val deviceName: TextFieldValue) : AddDeviceEvent()
    data class OnDeviceIpChange(val deviceIp: TextFieldValue) : AddDeviceEvent()
    data class AddDevice(val onSuccess: () -> Unit) : AddDeviceEvent()
    object HideErrorDialog : AddDeviceEvent()
}

data class AddDeviceUiState(
    val deviceName: TextFieldValue = TextFieldValue("", selection = TextRange(0)),
    val deviceIp: TextFieldValue = TextFieldValue("", selection = TextRange(0)),
    val invalidUrlErrorText: String? = null,
    val invalidIPErrorText: String? = null,
    val isAddEnabled: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)
