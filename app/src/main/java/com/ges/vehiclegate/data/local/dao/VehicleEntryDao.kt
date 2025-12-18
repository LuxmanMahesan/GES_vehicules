package com.ges.vehiclegate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ges.vehiclegate.data.local.entity.VehicleEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleEntryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: VehicleEntryEntity): Long

    @Update
    suspend fun update(entry: VehicleEntryEntity)

    @Query("SELECT * FROM vehicle_entries WHERE exitAt IS NULL ORDER BY arrivalAt DESC")
    fun observeOnSiteVehicles(): Flow<List<VehicleEntryEntity>>

    @Query("""
        SELECT * FROM vehicle_entries
        WHERE arrivalAt BETWEEN :startInclusive AND :endExclusive
        ORDER BY arrivalAt DESC
    """)
    fun observeVehiclesInRange(
        startInclusive: Long,
        endExclusive: Long
    ): Flow<List<VehicleEntryEntity>>

    @Query("SELECT * FROM vehicle_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VehicleEntryEntity?

    @Query("UPDATE vehicle_entries SET exitAt = :exitAt WHERE id = :id")
    suspend fun markExit(id: Long, exitAt: Long)

    @Query("UPDATE vehicle_entries SET exitAt = NULL WHERE id = :id")
    suspend fun restoreOnSite(id: Long)

}
