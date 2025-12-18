package com.ges.vehiclegate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodayVehicleRow(
    entry: VehicleEntry,
    onRestore: (Long) -> Unit,          // ✅
    modifier: Modifier = Modifier
) {
    val fmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.plate, style = MaterialTheme.typography.titleMedium)
                val status = if (entry.exitAt == null) "SUR SITE" else "SORTI"
                Text(status, style = MaterialTheme.typography.labelLarge)
            }

            Text(entry.companyName, style = MaterialTheme.typography.bodyMedium)
            Text("Destination: ${entry.destination.label}", style = MaterialTheme.typography.bodySmall)

            Text("Arrivée: ${fmt.format(Date(entry.arrivalAt))}", style = MaterialTheme.typography.bodySmall)
            Text(
                "Sortie: ${entry.exitAt?.let { fmt.format(Date(it)) } ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )

            if (!entry.driverPhone.isNullOrBlank()) {
                Text("Tel: ${entry.driverPhone}", style = MaterialTheme.typography.bodySmall)
            }
            if (!entry.notes.isNullOrBlank()) {
                Text("Note: ${entry.notes}", style = MaterialTheme.typography.bodySmall)
            }

            // ✅ bouton rétablir uniquement si sorti
            if (entry.exitAt != null) {
                Spacer(Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { onRestore(entry.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Rétablir")
                }
            }
        }
    }
}
