package com.ges.vehiclegate.ui.screen_today

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.usecase.GetTodayVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import com.ges.vehiclegate.domain.usecase.FinishDayUseCase
import com.ges.vehiclegate.ui.components.TodayVehicleRow
import com.ges.vehiclegate.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val repo = remember { AppModule.provideVehicleRepository(context) }
    val dateTimeProvider = remember { AppModule.provideDateTimeProvider() }

    val viewModel = remember {
        TodayViewModel(
            getTodayVehicles = GetTodayVehiclesUseCase(repo),
            restoreVehicleOnSite = RestoreVehicleOnSiteUseCase(repo),
            finishDayUseCase = FinishDayUseCase(repo),
            dateTimeProvider = dateTimeProvider
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Véhicules du jour") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
        ) {

            OutlinedButton(
                onClick = { navController.navigate(Routes.HISTORY) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Historique PDF")
            }

            Spacer(Modifier.height(12.dp))

            if (viewModel.tousSortis() && uiState.vehicles.isNotEmpty()) {
                Button(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finir la journée")
                }
                Spacer(Modifier.height(12.dp))
            }

            Text(
                text = "Total: ${uiState.vehicles.size}",
                style = MaterialTheme.typography.titleMedium
            )

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
                            onRestore = { id -> viewModel.restore(id) }
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false

                    viewModel.finirJournee(context) { pdfFile ->

                        Toast.makeText(context, "PDF créé: ${pdfFile.path}", Toast.LENGTH_LONG).show()

                        val uri = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".provider",
                            pdfFile
                        )

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(uri, "application/pdf")
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Aucune application PDF trouvée", Toast.LENGTH_LONG).show()
                        }
                    }

                }) { Text("Confirmer") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Annuler")
                }
            },
            title = { Text("Fin de journée") },
            text = { Text("Générer PDF et réinitialiser la journée ?") }
        )
    }
}
