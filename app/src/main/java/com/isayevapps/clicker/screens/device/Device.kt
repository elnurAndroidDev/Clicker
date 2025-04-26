package com.isayevapps.clicker.screens.device

import com.isayevapps.clicker.data.db.DeviceEntity

data class Device(
    val id: Int,
    val name: String,
    val ip: String
) {
    fun toDeviceEntity() = DeviceEntity(id, name, ip)
}
