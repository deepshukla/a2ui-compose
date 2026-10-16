package com.example.a2ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2ui.model.A2UIAction
import com.example.a2ui.model.A2UIActionEvent
import com.example.a2ui.model.A2UISurface
import com.example.a2ui.model.AgentChatMessage
import com.example.a2ui.parser.A2UIParser
import com.example.a2ui.parser.ParseResult
import com.example.a2ui.repository.A2UIAgentService
import com.example.a2ui.repository.A2UIPreset
import com.example.a2ui.repository.A2UIPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class A2UITab {
    RENDERER,
    JSON_EDITOR,
    AGENT_STUDIO,
    CATALOG_SCHEMA
}

data class A2UIState(
    val currentTab: A2UITab = A2UITab.RENDERER,
    val selectedPreset: A2UIPreset = A2UIPresets.presets.first(),
    val currentSurface: A2UISurface? = null,
    val rawJson: String = "",
    val parseError: String? = null,
    val parseWarnings: List<String> = emptyList(),
    val isGenerating: Boolean = false,
    val eventLogs: List<A2UIActionEvent> = emptyList(),
    val chatMessages: List<AgentChatMessage> = emptyList(),
    val isDarkTheme: Boolean = true
)

class A2UIViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(A2UIState())
    val uiState: StateFlow<A2UIState> = _uiState.asStateFlow()

    init {
        // Load initial ClimateTrace preset
        loadPreset(A2UIPresets.presets.first())

        // Initial welcome message from Koog Agent
        _uiState.update { state ->
            state.copy(
                chatMessages = listOf(
                    AgentChatMessage(
                        isUser = false,
                        text = "Hello! I am your A2UI Agent. I describe native user interfaces as JSON payloads that Compose renders using your catalog components. You can prompt me to generate or customize any interface, or interact with buttons in the Renderer tab!",
                        surfacePayload = null,
                        rawJson = null
                    )
                )
            )
        }
    }

    fun selectTab(tab: A2UITab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun loadPreset(preset: A2UIPreset) {
        val parseResult = A2UIParser.parse(preset.jsonContent)
        when (parseResult) {
            is ParseResult.Success -> {
                _uiState.update {
                    it.copy(
                        selectedPreset = preset,
                        currentSurface = parseResult.surface,
                        rawJson = A2UIParser.formatJson(preset.jsonContent),
                        parseError = null,
                        parseWarnings = parseResult.warnings
                    )
                }
            }
            is ParseResult.Error -> {
                _uiState.update {
                    it.copy(
                        selectedPreset = preset,
                        rawJson = preset.jsonContent,
                        parseError = parseResult.message,
                        parseWarnings = emptyList()
                    )
                }
            }
        }
    }

    fun updateRawJson(newJson: String) {
        _uiState.update { it.copy(rawJson = newJson) }
    }

    fun applyEditedJson() {
        val json = _uiState.value.rawJson
        val parseResult = A2UIParser.parse(json)
        when (parseResult) {
            is ParseResult.Success -> {
                _uiState.update {
                    it.copy(
                        currentSurface = parseResult.surface,
                        parseError = null,
                        parseWarnings = parseResult.warnings
                    )
                }
            }
            is ParseResult.Error -> {
                _uiState.update {
                    it.copy(parseError = parseResult.message)
                }
            }
        }
    }

    fun formatJson() {
        val formatted = A2UIParser.formatJson(_uiState.value.rawJson)
        _uiState.update { it.copy(rawJson = formatted) }
    }

    fun onDataModelChange(key: String, value: Any?) {
        val current = _uiState.value.currentSurface ?: return
        val updatedModel = HashMap(current.dataModel).apply {
            put(key, value)
        }
        val updatedSurface = current.copy(dataModel = updatedModel)
        _uiState.update { it.copy(currentSurface = updatedSurface) }
    }

    fun handleUserAction(action: A2UIAction) {
        val current = _uiState.value.currentSurface ?: return

        val newEvent = A2UIActionEvent(
            surfaceId = current.surfaceId,
            actionName = action.name,
            parameters = action.parameters,
            sourceComponentId = action.parameters["componentId"]?.toString() ?: "action"
        )

        _uiState.update {
            it.copy(
                eventLogs = listOf(newEvent) + it.eventLogs.take(19)
            )
        }

        // Inform Agent in multi-turn loop
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            val response = A2UIAgentService.handleUserAction(action, current)

            if (response.surface != null) {
                _uiState.update {
                    it.copy(
                        currentSurface = response.surface,
                        rawJson = response.rawJson ?: it.rawJson,
                        parseError = null
                    )
                }
            }

            // Also post message into chat loop
            _uiState.update { state ->
                val newChat = state.chatMessages + listOf(
                    AgentChatMessage(
                        isUser = true,
                        text = "Triggered action: '${action.name}' with params: ${action.parameters}"
                    ),
                    AgentChatMessage(
                        isUser = false,
                        text = response.explanationText,
                        surfacePayload = response.surface,
                        rawJson = response.rawJson
                    )
                )
                state.copy(
                    chatMessages = newChat,
                    isGenerating = false
                )
            }
        }
    }

    fun sendAgentPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = AgentChatMessage(isUser = true, text = prompt)
        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                isGenerating = true
            )
        }

        viewModelScope.launch {
            val response = A2UIAgentService.generateUIFromPrompt(prompt)

            if (response.surface != null) {
                _uiState.update {
                    it.copy(
                        currentSurface = response.surface,
                        rawJson = response.rawJson ?: it.rawJson,
                        parseError = null
                    )
                }
            }

            val agentMsg = AgentChatMessage(
                isUser = false,
                text = response.explanationText,
                surfacePayload = response.surface,
                rawJson = response.rawJson
            )

            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + agentMsg,
                    isGenerating = false
                )
            }
        }
    }

    fun clearLogs() {
        _uiState.update { it.copy(eventLogs = emptyList()) }
    }
}
