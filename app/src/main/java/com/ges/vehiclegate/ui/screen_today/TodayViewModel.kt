package com.ges.vehiclegate.ui.screen_today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.usecase.GetTodayVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import com.ges.vehiclegate.util.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodayViewModel(
    private val getTodayVehicles: GetTodayVehiclesUseCase,
    private val restoreVehicleOnSite: RestoreVehicleOnSiteUseCase,  // ✅
    private val dateTimeProvider: DateTimeProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState

    init {
        val start = dateTimeProvider.startOfTodayMillis()
        val end = dateTimeProvider.startOfTomorrowMillis()

        viewModelScope.launch {
            getTodayVehicles(start, end).collect { list ->
                _uiState.update {
                    it.copy(
                        vehicles = list,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun restore(id: Long) {
        viewModelScope.launch {
            try {
                restoreVehicleOnSite(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Erreur rétablir") }
            }
        }
    }
}
