package com.ges.vehiclegate.ui.screen_add

import com.ges.vehiclegate.domain.model.Destination

data class AddVehicleUiState(
    val plate: String = "",
    val companyName: String = "",
    val destination: Destination = Destination.AUTRE,
    val driverPhone: String = "",
    val notes: String = "",
    val photoPath: String? = null,

    val isOcrRunning: Boolean = false,     // ✅
    val ocrInfo: String? = null,           // ✅ (petit message debug/UX)
    val isSaving: Boolean = false,
    val error: String? = null
)
