package com.ges.vehiclegate.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehicle_entries",
    indices = [
        Index(value = ["arrivalAt"]),
        Index(value = ["exitAt"]),
        Index(value = ["plate"])
    ]
)
data class VehicleEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val plate: String,
    val companyName: String,

    /**
     * On stocke la destination sous forme String (label) pour rester simple.
     * (On mappera vers enum Destination côté domain)
     */
    val destinationLabel: String,

    val driverPhone: String?,
    val notes: String?,

    val arrivalAt: Long,
    val exitAt: Long?
)
