package com.avis.app.ptalk.domain.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.avis.app.ptalk.domain.model.Device
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevice(): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE macAddress = :address LIMIT 1")
    suspend fun get(address: String): Device?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(device: Device)

    @Query("DELETE FROM devices WHERE macAddress = :address")
    suspend fun deleteByAddress(address: String)
}