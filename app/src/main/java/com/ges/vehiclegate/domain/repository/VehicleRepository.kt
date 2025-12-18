package com.ges.vehiclegate.domain.repository

import com.ges.vehiclegate.domain.model.VehicleEntry
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun observeOnSiteVehicles(): Flow<List<VehicleEntry>>
    fun observeTodayVehicles(todayStart: Long, tomorrowStart: Long): Flow<List<VehicleEntry>>

    suspend fun add(entry: VehicleEntry): Long
    suspend fun markExit(id: Long, exitAt: Long)
    suspend fun getById(id: Long): VehicleEntry?     // ✅
    suspend fun update(entry: VehicleEntry)

    suspend fun restoreOnSite(id: Long)   // ✅

    suspend fun archiverJournee()


}
