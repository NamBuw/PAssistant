package com.avis.app.ptalk.domain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.avis.app.ptalk.domain.data.local.dao.DeviceDao
import com.avis.app.ptalk.domain.model.Device

@Database(
    entities = [Device::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}