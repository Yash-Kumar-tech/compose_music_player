package com.ykt.musicplayer.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.ykt.musicplayer.ui.player.PlayerViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    playerViewModel: PlayerViewModel,
    navController: NavController
) {
    val settings by viewModel.settings.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    val options = listOf("system", "light", "dark")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Default Looping", modifier = Modifier.weight(1f))
                Switch(
                    checked = settings.looping,
                    onCheckedChange = { viewModel.toggleLooping() }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Auto Play", modifier = Modifier.weight(1f))
                Switch(
                    checked = settings.autoplay,
                    onCheckedChange = { viewModel.toggleAutoPlay() }
                )
            }

            Text("Theme", style = MaterialTheme.typography.titleMedium)

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Theme: ${settings.theme}")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                viewModel.setTheme(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text("Screen", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            
            Column {
                Text("Idle Screen Timeout: ${(settings.screenTimeoutMs / 1000)}s")
                Slider(
                    value = settings.screenTimeoutMs.toFloat(),
                    onValueChange = { viewModel.updateScreenTimeout(it.toLong()) },
                    valueRange = 2000f..10000f,
                    steps = 7, // 2, 3, 4, 5, 6, 7, 8, 9, 10
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
