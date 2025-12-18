package com.ges.vehiclegate.domain.usecase

import com.ges.vehiclegate.domain.repository.VehicleRepository

class MarkVehicleExitUseCase(
    private val repo: VehicleRepository
) {
    suspend operator fun invoke(id: Long, exitAt: Long) = repo.markExit(id, exitAt)
}
