package com.example.a2ui.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.a2ui.repository.A2UIPresets
import com.example.a2ui.viewmodel.A2UITab
import com.example.a2ui.viewmodel.A2UIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A2UIMainScreen(
    viewModel: A2UIViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showPresetsMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Back handling: pop back to Renderer if on another tab
    BackHandler(enabled = uiState.currentTab != A2UITab.RENDERER) {
        viewModel.selectTab(A2UITab.RENDERER)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "A2UI Compose",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }

                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About A2UI"
                        )
                    }

                    Box {
                        IconButton(onClick = { showPresetsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Presets Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = showPresetsMenu,
                            onDismissRequest = { showPresetsMenu = false }
                        ) {
                            Text(
                                text = "Load A2UI Preset",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            A2UIPresets.presets.forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset.name) },
                                    onClick = {
                                        viewModel.loadPreset(preset)
                                        showPresetsMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("a2ui_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == A2UITab.RENDERER,
                    onClick = { viewModel.selectTab(A2UITab.RENDERER) },
                    icon = { Icon(Icons.Default.Devices, contentDescription = "Renderer") },
                    label = { Text("Renderer") },
                    modifier = Modifier.testTag("nav_renderer")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == A2UITab.JSON_EDITOR,
                    onClick = { viewModel.selectTab(A2UITab.JSON_EDITOR) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Editor") },
                    label = { Text("JSON Editor") },
                    modifier = Modifier.testTag("nav_editor")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == A2UITab.AGENT_STUDIO,
                    onClick = { viewModel.selectTab(A2UITab.AGENT_STUDIO) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Agent Studio") },
                    label = { Text("Agent Studio") },
                    modifier = Modifier.testTag("nav_agent")
                )
                NavigationBarItem(
                    selected = uiState.currentTab == A2UITab.CATALOG_SCHEMA,
                    onClick = { viewModel.selectTab(A2UITab.CATALOG_SCHEMA) },
                    icon = { Icon(Icons.Default.Extension, contentDescription = "Catalog") },
                    label = { Text("Catalog") },
                    modifier = Modifier.testTag("nav_catalog")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                A2UITab.RENDERER -> {
                    A2UIRendererTab(
                        currentSurface = uiState.currentSurface,
                        selectedPreset = uiState.selectedPreset,
                        eventLogs = uiState.eventLogs,
                        parseWarnings = uiState.parseWarnings,
                        onSelectPreset = { viewModel.loadPreset(it) },
                        onAction = { viewModel.handleUserAction(it) },
                        onDataChange = { key, value -> viewModel.onDataModelChange(key, value) },
                        onSwitchToEditor = { viewModel.selectTab(A2UITab.JSON_EDITOR) },
                        onClearLogs = { viewModel.clearLogs() }
                    )
                }
                A2UITab.JSON_EDITOR -> {
                    A2UIEditorTab(
                        rawJson = uiState.rawJson,
                        parseError = uiState.parseError,
                        selectedPreset = uiState.selectedPreset,
                        onJsonChange = { viewModel.updateRawJson(it) },
                        onApply = {
                            viewModel.applyEditedJson()
                            viewModel.selectTab(A2UITab.RENDERER)
                        },
                        onFormat = { viewModel.formatJson() },
                        onResetPreset = { viewModel.loadPreset(uiState.selectedPreset) }
                    )
                }
                A2UITab.AGENT_STUDIO -> {
                    A2UIAgentTab(
                        chatMessages = uiState.chatMessages,
                        isGenerating = uiState.isGenerating,
                        onSendPrompt = { viewModel.sendAgentPrompt(it) },
                        onAction = { viewModel.handleUserAction(it) },
                        onDataChange = { key, value -> viewModel.onDataModelChange(key, value) },
                        onInspectInEditor = { viewModel.selectTab(A2UITab.JSON_EDITOR) },
                        onOpenInRenderer = { viewModel.selectTab(A2UITab.RENDERER) }
                    )
                }
                A2UITab.CATALOG_SCHEMA -> {
                    A2UICatalogTab(
                        onLoadExampleToEditor = {
                            viewModel.updateRawJson(it)
                            viewModel.selectTab(A2UITab.JSON_EDITOR)
                        }
                    )
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About A2UI Compose") },
            text = {
                Text(
                    text = "A2UI (Agent-to-UI) is a declarative UI protocol designed by Google and explored by John O'Reilly in ClimateTraceKMP.\n\n" +
                            "• AI agents describe user interfaces in JSON using component catalogs.\n" +
                            "• Adjacency list representation enables streamable, secure UI without executing remote code.\n" +
                            "• Built with clean MVVM architecture in Kotlin & Jetpack Compose."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
