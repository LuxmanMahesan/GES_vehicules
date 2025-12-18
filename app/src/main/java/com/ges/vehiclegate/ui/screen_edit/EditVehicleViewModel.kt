package com.ges.vehiclegate.ui.screen_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.usecase.GetVehicleByIdUseCase
import com.ges.vehiclegate.domain.usecase.UpdateVehicleEntryUseCase
import com.ges.vehiclegate.ui.screen_add.AddVehicleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditVehicleViewModel(
    private val vehicleId: Long,
    private val getVehicleById: GetVehicleByIdUseCase,
    private val updateVehicleEntry: UpdateVehicleEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState(isSaving = true))
    val uiState: StateFlow<AddVehicleUiState> = _uiState

    private var original: VehicleEntry? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val entry = getVehicleById(vehicleId)
                if (entry == null) {
                    _uiState.update { it.copy(isSaving = false, error = "Véhicule introuvable") }
                    return@launch
                }

                original = entry

                _uiState.update {
                    it.copy(
                        plate = entry.plate,
                        companyName = entry.companyName,
                        destination = entry.destination,
                        driverPhone = entry.driverPhone.orEmpty(),
                        notes = entry.notes.orEmpty(),
                        photoPath = entry.photoPath,
                        isSaving = false,
                        error = null,
                        isOcrRunning = false,
                        ocrInfo = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erreur chargement véhicule"
                    )
                }
            }
        }
    }

    fun onPlateChange(v: String) = _uiState.update { it.copy(plate = v, error = null) }
    fun onCompanyChange(v: String) = _uiState.update { it.copy(companyName = v, error = null) }
    fun onDestinationChange(v: com.ges.vehiclegate.domain.model.Destination) =
        _uiState.update { it.copy(destination = v, error = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(driverPhone = v) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }

    fun save(onSuccess: () -> Unit) {
        val s = _uiState.value
        val base = original

        if (base == null) {
            _uiState.update { it.copy(error = "Impossible d'éditer : données non chargées") }
            return
        }

        val plate = s.plate.trim()
        val company = s.companyName.trim()

        if (plate.isBlank()) {
            _uiState.update { it.copy(error = "Plaque obligatoire") }
            return
        }
        if (company.isBlank()) {
            _uiState.update { it.copy(error = "Nom de société obligatoire") }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val updated = base.copy(
                    plate = plate,
                    companyName = company,
                    destination = s.destination,
                    driverPhone = s.driverPhone.trim().ifBlank { null },
                    notes = s.notes.trim().ifBlank { null }
                    // arrivalAt / exitAt / photoPath conservés
                )

                updateVehicleEntry(updated)

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erreur modification"
                    )
                }
            }
        }
    }
}
