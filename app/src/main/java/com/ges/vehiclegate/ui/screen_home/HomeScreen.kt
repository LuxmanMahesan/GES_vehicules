package com.ges.vehiclegate.ui.screen_home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddVehicle: () -> Unit,
    onSeeToday: () -> Unit,
    viewModel: HomeViewModel = HomeViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("VehicleGate - Accueil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onAddVehicle) {
                Text("Ajouter un véhicule")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = onSeeToday) {
                Text("Véhicules du jour")
            }
        }
    }
}
