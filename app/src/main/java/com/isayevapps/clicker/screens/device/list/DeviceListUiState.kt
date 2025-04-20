package com.isayevapps.clicker.screens.device.list

import com.isayevapps.clicker.screens.device.Device

data class DeviceListUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)