package com.ges.vehiclegate.ui.screen_today

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ges.vehiclegate.domain.usecase.GetTodayVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import com.ges.vehiclegate.domain.usecase.FinishDayUseCase
import com.ges.vehiclegate.util.DateTimeProvider
import com.ges.vehiclegate.util.pdf.PdfReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class TodayViewModel(
    private val getTodayVehicles: GetTodayVehiclesUseCase,
    private val restoreVehicleOnSite: RestoreVehicleOnSiteUseCase,
    private val finishDayUseCase: FinishDayUseCase,
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

    fun tousSortis(): Boolean =
        _uiState.value.vehicles.all { it.exitAt != null }

    fun finirJournee(
        context: Context,
        onPdfGenere: (File) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val vehicules = _uiState.value.vehicles

                val pdf = PdfReportGenerator()
                    .generer(context, vehicules)

                finishDayUseCase()

                onPdfGenere(pdf)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erreur fin de journée") }
            }
        }
    }
}
