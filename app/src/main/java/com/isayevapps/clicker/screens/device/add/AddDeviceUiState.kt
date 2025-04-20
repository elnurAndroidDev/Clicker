package com.isayevapps.clicker.screens.device.add

data class AddDeviceUiState(
    val deviceName: String = "",
    val invalidUrlErrorText: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)
