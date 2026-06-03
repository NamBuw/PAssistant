package com.ctslab.app.pconnect.domain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ctslab.app.pconnect.domain.data.local.dao.DeviceDao
import com.ctslab.app.pconnect.domain.model.Device

@Database(
    entities = [Device::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}