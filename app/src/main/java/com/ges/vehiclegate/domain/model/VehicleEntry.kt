package com.ges.vehiclegate.domain.model

data class VehicleEntry(
    val id: Long = 0L,
    val plate: String,
    val companyName: String,
    val destination: Destination,
    val driverPhone: String?,
    val notes: String?,
    val arrivalAt: Long,
    val exitAt: Long?,
    val photoPath: String? // ✅ nouveau
)
