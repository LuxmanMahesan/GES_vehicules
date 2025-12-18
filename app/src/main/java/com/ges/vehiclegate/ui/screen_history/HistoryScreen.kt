package com.ges.vehiclegate.ui.screen_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.util.pdf.PdfOpener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dir = context.getExternalFilesDir(null)

    val fichiers = remember {
        dir?.listFiles()
            ?.filter { it.extension == "pdf" }
            ?.sortedByDescending { it.lastModified() }
            ?.take(30)
            ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique PDF") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(fichiers) { file ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            PdfOpener.ouvrir(context, file)
                        }
                ) {
                    Text(
                        file.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
