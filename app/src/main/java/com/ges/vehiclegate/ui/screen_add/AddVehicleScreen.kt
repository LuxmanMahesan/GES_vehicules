package com.ges.vehiclegate.ui.screen_add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.usecase.AddVehicleEntryUseCase
import com.ges.vehiclegate.ui.components.DestinationDropdown
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ges.vehiclegate.feature_ocr.camera.CameraCapture





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AppModule.provideVehicleRepository(context) }
    val dateTimeProvider = remember { AppModule.provideDateTimeProvider() }

    val viewModel = remember {
        AddVehicleViewModel(
            addVehicleEntry = AddVehicleEntryUseCase(repo),
            dateTimeProvider = dateTimeProvider
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    var showCamera by remember { mutableStateOf(false) }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) showCamera = true
            else viewModel.onPlateChange(uiState.plate) // noop, juste pour éviter warning; sinon set error si tu veux
        }

    if (showCamera) {
        CameraCapture(
            onClose = { showCamera = false },
            onImageCaptured = { path ->
                viewModel.onPhotoCaptured(path)
                showCamera = false
            },
            onError = { t ->
                showCamera = false
            },
            onPlateDetected = { plateDetected ->
                viewModel.onPlateChange(plateDetected)
            }
        )

        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un véhicule") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.photoPath == null) "Prendre une photo" else "Reprendre une photo")
            }



            // ✅ Aperçu (si photo)
            uiState.photoPath?.let { path ->
                val bmp = remember(path) { BitmapFactory.decodeFile(path) }
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Photo véhicule",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }

            if (uiState.isOcrRunning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.ocrInfo?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }



            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = uiState.plate,
                onValueChange = viewModel::onPlateChange,
                label = { Text("Plaque ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = viewModel::onCompanyChange,
                label = { Text("Nom de la société") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DestinationDropdown(
                value = uiState.destination,
                onValueChange = viewModel::onDestinationChange,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.driverPhone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Téléphone chauffeur (optionnel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (optionnel)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )

            Button(
                onClick = { viewModel.save(onSuccess = onBack) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isSaving) "Enregistrement..." else "Valider l'arrivée")
            }
        }
    }
}
