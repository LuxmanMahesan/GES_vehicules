package com.ges.vehiclegate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VehicleRow(
    entry: VehicleEntry,
    onMarkExit: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val fmt = rememberDateFormatter()
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.plate, style = MaterialTheme.typography.titleMedium)
                Text(entry.companyName, style = MaterialTheme.typography.bodyMedium)
                Text("Destination: ${entry.destination.label}", style = MaterialTheme.typography.bodySmall)
                Text("Arrivée: ${fmt.format(Date(entry.arrivalAt))}", style = MaterialTheme.typography.bodySmall)
                if (!entry.driverPhone.isNullOrBlank()) {
                    Text("Tel: ${entry.driverPhone}", style = MaterialTheme.typography.bodySmall)
                }
                if (!entry.notes.isNullOrBlank()) {
                    Text("Note: ${entry.notes}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ✅ boutons en colonne
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onMarkExit(entry.id) }) {
                    Text("Sortie")
                }
                OutlinedButton(onClick = { onEdit(entry.id) }) {
                    Text("Éditer")
                }
            }
        }
    }
}

@Composable
private fun rememberDateFormatter(): SimpleDateFormat {
    // Simple, local
    return SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault())
}
