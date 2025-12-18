package com.ges.vehiclegate.ui.screen_add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.model.VehicleEntry
import com.ges.vehiclegate.domain.usecase.AddVehicleEntryUseCase
import com.ges.vehiclegate.util.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


import com.ges.vehiclegate.feature_ocr.ocr.PlateOcrService
import com.ges.vehiclegate.feature_ocr.ocr.PlateTextParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class AddVehicleViewModel(
    private val addVehicleEntry: AddVehicleEntryUseCase,
    private val dateTimeProvider: DateTimeProvider,
    private val ocrService: PlateOcrService = PlateOcrService()

) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState

    fun onPlateChange(v: String) = _uiState.update { it.copy(plate = v, error = null) }
    fun onCompanyChange(v: String) = _uiState.update { it.copy(companyName = v, error = null) }
    fun onDestinationChange(v: com.ges.vehiclegate.domain.model.Destination) =
        _uiState.update { it.copy(destination = v, error = null) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(driverPhone = v) }
    fun onNotesChange(v: String) = _uiState.update { it.copy(notes = v) }

    fun onPhotoCaptured(path: String) {
        _uiState.update { it.copy(photoPath = path, error = null) }
        runOcrFromPhoto(path)
    }

    override fun onCleared() {
        super.onCleared()
        ocrService.close()
    }



    fun save(onSuccess: () -> Unit) {
        val s = _uiState.value

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
                val now = dateTimeProvider.nowMillis()
                val entry = VehicleEntry(
                    plate = plate,
                    companyName = company,
                    destination = s.destination,
                    driverPhone = s.driverPhone.trim().ifBlank { null },
                    notes = s.notes.trim().ifBlank { null },
                    arrivalAt = now,
                    exitAt = null,
                    photoPath = s.photoPath
                )
                addVehicleEntry(entry)
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Erreur inconnue") }
            }
        }
    }

    fun runOcrFromPhoto(path: String) {
        _uiState.update { it.copy(isOcrRunning = true, ocrInfo = "OCR en cours...", error = null) }

        viewModelScope.launch {
            try {
                val rawText = withContext(Dispatchers.Default) {
                    // reconnaissance ML Kit (suspend) + parsing
                    ocrService.recognizeTextFromFile(path)
                }

                val plate = PlateTextParser.extractBestPlate(rawText)

                _uiState.update {
                    it.copy(
                        isOcrRunning = false,
                        plate = plate ?: it.plate, // si OCR échoue, on ne détruit pas la saisie
                        ocrInfo = if (plate != null) "Plaque détectée: $plate" else "Plaque non détectée, saisie manuelle."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isOcrRunning = false,
                        ocrInfo = null,
                        error = "OCR erreur: ${e.message ?: "inconnue"}"
                    )
                }
            }
        }
    }

}
