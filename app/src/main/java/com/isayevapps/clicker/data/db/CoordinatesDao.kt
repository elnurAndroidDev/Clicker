package com.isayevapps.clicker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinatesDao {
    @Query("SELECT * FROM coordinates WHERE deviceId = :deviceId")
    fun getAllByDeviceIdFlow(deviceId: Int): Flow<List<CoordinateEntity>>

    @Query("SELECT * FROM coordinates WHERE deviceId = :deviceId")
    suspend fun getAllByDeviceId(deviceId: Int): List<CoordinateEntity>

    @Query("SELECT * FROM coordinates WHERE id = :id")
    suspend fun get(id: Int): CoordinateEntity

    @Update
    suspend fun update(coordinate: CoordinateEntity)

    @Insert
    suspend fun insert(coordinate: CoordinateEntity)

    @Query("DELETE FROM coordinates WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM coordinates WHERE deviceId = :deviceId")
    suspend fun deleteAllByDeviceId(deviceId: Int)

}