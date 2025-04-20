package com.isayevapps.clicker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DeviceEntity::class, CoordinateEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun coordinateDao(): CoordinatesDao
}