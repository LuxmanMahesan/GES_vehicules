package com.ges.vehiclegate.ui.screen_add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onBack: () -> Unit,
    viewModel: AddVehicleViewModel = AddVehicleViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un véhicule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Écran d'ajout (vide pour l’instant)")
        }
    }
}
