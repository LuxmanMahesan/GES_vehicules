package com.ges.vehiclegate.domain.model

data class VehicleEntry(
    val id: Long = 0L,
    val plate: String,
    val companyName: String,
    val destination: Destination,
    val driverPhone: String?,
    val notes: String?,
    val arrivalAt: Long,  // epoch millis
    val exitAt: Long?     // null tant que pas sorti
)
