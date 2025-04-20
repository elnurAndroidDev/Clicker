package com.isayevapps.clicker.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.isayevapps.clicker.screens.device.Device

@Entity(tableName = "devices", indices = [Index(value = ["name"], unique = true)])
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
) {
    fun toDevice(): Device {
        return Device(id, name)
    }
}
