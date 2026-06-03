package com.avis.app.ptalk.domain.data.local.repo

import com.avis.app.ptalk.domain.data.local.dao.DeviceDao
import com.avis.app.ptalk.domain.model.Device
import kotlinx.coroutines.flow.Flow
import org.thingai.base.log.ILog
import javax.inject.Inject

class DeviceRepository @Inject constructor (private val dao: DeviceDao) {
    private val TAG = "DeviceRepository"

    fun devices(): Flow<List<Device>> = dao.getAllDevice()
    suspend fun upsert(device: Device) {
        ILog.d(TAG, "upsert", device.toString())
        try {
            dao.upsert(device)
        } catch (e: Exception) {
            ILog.e(TAG, "upsert", e.message)
            e.printStackTrace()
        }
    }
    suspend fun get(address: String) = dao.get(address)
    suspend fun delete(address: String) = dao.deleteByAddress(address)
}