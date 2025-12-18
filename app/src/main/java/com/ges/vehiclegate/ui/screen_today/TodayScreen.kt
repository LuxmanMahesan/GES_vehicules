package com.ges.vehiclegate.ui.screen_today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.usecase.GetTodayVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import com.ges.vehiclegate.ui.components.TodayVehicleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AppModule.provideVehicleRepository(context) }
    val dateTimeProvider = remember { AppModule.provideDateTimeProvider() }

    val viewModel = remember {
        TodayViewModel(
            getTodayVehicles = GetTodayVehiclesUseCase(repo),
            restoreVehicleOnSite = RestoreVehicleOnSiteUseCase(repo),
            dateTimeProvider = dateTimeProvider
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Véhicules du jour") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Total: ${uiState.vehicles.size}",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (uiState.vehicles.isEmpty()) {
                Text("Aucun véhicule enregistré aujourd’hui.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.vehicles, key = { it.id }) { entry ->
                        TodayVehicleRow(
                            entry = entry,
                            onRestore = { id -> viewModel.restore(id) }   // ✅
                        )
                    }
                }
            }
        }
    }
}
