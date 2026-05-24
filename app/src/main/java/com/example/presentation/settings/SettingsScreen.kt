package com.example.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val aiEnabled by viewModel.aiEnhancementEnabled.collectAsState()
    val hwActive by viewModel.hardwareAccelerationEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            SettingSwitchRow(
                title = "AI Audio Enhancement",
                subtitle = "Uses local ML model to reduce background noise",
                checked = aiEnabled,
                onCheckedChange = { viewModel.toggleAiEnhancement(it) }
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            SettingSwitchRow(
                title = "Hardware Acceleration",
                subtitle = "Improves video decoding performance",
                checked = hwActive,
                onCheckedChange = { viewModel.toggleHardwareAcceleration(it) }
            )
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
