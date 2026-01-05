package com.ykt.musicplayer.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var themeExpanded by remember { mutableStateOf(false) }
    var qualityExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                SettingsSectionTitle("Playback")
                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Rounded.Repeat,
                        title = "Default Looping",
                        subtitle = "Repeat single song by default",
                        trailing = {
                            Switch(
                                checked = settings.looping,
                                onCheckedChange = { viewModel.toggleLooping() }
                            )
                        }
                    )
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Rounded.AutoMode,
                        title = "Auto Play",
                        subtitle = "Start playing next song automatically",
                        trailing = {
                            Switch(
                                checked = settings.autoplay,
                                onCheckedChange = { viewModel.toggleAutoPlay() }
                            )
                        }
                    )
                    SettingsDivider()
                    Box {
                        SettingsItem(
                            icon = Icons.Rounded.HighQuality,
                            title = "Audio Quality",
                            subtitle = settings.audioQuality.replaceFirstChar { it.uppercase() },
                            onClick = { qualityExpanded = true }
                        )
                        DropdownMenu(
                            expanded = qualityExpanded,
                            onDismissRequest = { qualityExpanded = false }
                        ) {
                            listOf("low", "normal", "high").forEach { quality ->
                                DropdownMenuItem(
                                    text = { Text(quality.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.setAudioQuality(quality)
                                        qualityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                SettingsSectionTitle("Appearance")
                SettingsGroup {
                    Box {
                        SettingsItem(
                            icon = Icons.Rounded.Palette,
                            title = "Theme",
                            subtitle = settings.theme.replaceFirstChar { it.uppercase() },
                            onClick = { themeExpanded = true }
                        )
                        DropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false }
                        ) {
                            listOf("system", "light", "dark").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.setTheme(option)
                                        themeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Rounded.ColorLens,
                        title = "Dynamic Colors",
                        subtitle = "Use wallpaper-based colors (Android 12+)",
                        trailing = {
                            Switch(
                                checked = settings.dynamicColors,
                                onCheckedChange = { viewModel.toggleDynamicColors() }
                            )
                        }
                    )
                }
            }

            item {
                SettingsSectionTitle("Display")
                SettingsGroup {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(16.dp))
                            val timeoutText = if (settings.screenTimeoutMs == 0L) "Never" else "${(settings.screenTimeoutMs / 1000)}s"
                            Text("Idle Screen Timeout: $timeoutText", style = MaterialTheme.typography.bodyLarge)
                        }
                        Slider(
                            value = if (settings.screenTimeoutMs == 0L) 11000f else settings.screenTimeoutMs.toFloat(),
                            onValueChange = {
                                val newValue = if (it > 10500f) 0L else it.toLong()
                                viewModel.updateScreenTimeout(newValue)
                            },
                            valueRange = 2000f..11000f,
                            steps = 8,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Rounded.Lyrics,
                        title = "Show Lyrics",
                        subtitle = "Display lyrics on player screen if available",
                        trailing = {
                            Switch(
                                checked = settings.showLyrics,
                                onCheckedChange = { viewModel.toggleShowLyrics() }
                            )
                        }
                    )
                }
            }

            item {
                SettingsSectionTitle("About")
                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Rounded.Info,
                        title = "Version",
                        subtitle = "1.0.0 (Beta)",
                        onClick = {}
                    )
                    SettingsDivider()
                    SettingsItem(
                        icon = Icons.Rounded.Feedback,
                        title = "Help & Feedback",
                        subtitle = "Report issues or request features",
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                trailing()
            }
        }
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}
