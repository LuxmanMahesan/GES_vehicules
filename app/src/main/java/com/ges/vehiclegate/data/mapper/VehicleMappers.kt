package com.ges.vehiclegate.data.mapper

import com.ges.vehiclegate.data.local.entity.VehicleEntryEntity
import com.ges.vehiclegate.domain.model.Destination
import com.ges.vehiclegate.domain.model.VehicleEntry

fun VehicleEntryEntity.toDomain(): VehicleEntry =
    VehicleEntry(
        id = id,
        plate = plate,
        companyName = companyName,
        destination = Destination.fromLabel(destinationLabel),
        driverPhone = driverPhone,
        notes = notes,
        arrivalAt = arrivalAt,
        exitAt = exitAt,
        photoPath = photoPath
    )

fun VehicleEntry.toEntity(): VehicleEntryEntity =
    VehicleEntryEntity(
        id = id,
        plate = plate,
        companyName = companyName,
        destinationLabel = destination.label,
        driverPhone = driverPhone,
        notes = notes,
        arrivalAt = arrivalAt,
        exitAt = exitAt,
        photoPath = photoPath
    )
