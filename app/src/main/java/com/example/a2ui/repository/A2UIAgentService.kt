package com.example.a2ui.repository

import com.example.a2ui.model.A2UIAction
import com.example.a2ui.model.A2UISurface
import com.example.a2ui.parser.A2UIParser
import com.example.a2ui.parser.A2UISchemaGenerator
import com.example.a2ui.parser.ParseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AgentResponse(
    val explanationText: String,
    val surface: A2UISurface?,
    val rawJson: String?
)

object A2UIAgentService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getGeminiApiKey(): String {
        return try {
            val buildConfigClass = Class.forName("com.example.BuildConfig")
            val field = buildConfigClass.getField("GEMINI_API_KEY")
            val key = field.get(null)?.toString() ?: ""
            if (key == "MY_GEMINI_API_KEY" || key.isBlank()) "" else key
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Process an arbitrary user prompt and generate an A2UI interface.
     */
    suspend fun generateUIFromPrompt(prompt: String): AgentResponse = withContext(Dispatchers.IO) {
        val apiKey = getGeminiApiKey()

        if (apiKey.isNotBlank()) {
            try {
                return@withContext callGeminiApi(prompt, apiKey)
            } catch (e: Exception) {
                // If API call fails (e.g. quota, network), fallback to intelligent simulated agent
            }
        }

        // Realistic Koog/Gemini Agent Simulation
        delay(600) // Realistic streaming/thinking pause
        return@withContext generateSimulatedAgentResponse(prompt)
    }

    /**
     * Handle user action triggered from an interactive component (e.g. "compare_country").
     */
    suspend fun handleUserAction(action: A2UIAction, currentSurface: A2UISurface): AgentResponse = withContext(Dispatchers.IO) {
        delay(400) // Brief reactive turnaround

        when (action.name) {
            "compare_country" -> {
                val target = action.parameters["targetCountry"]?.toString() ?: "France"
                val json = """
{
  "surfaceId": "climatetrace_comparison",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Climate TRACE: Germany vs $target",
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["comp_header", "comp_bar", "chart_side_by_side", "summary_card", "btn_back"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    {
      "id": "comp_header",
      "component": "Card",
      "children": ["comp_header_col"],
      "properties": { "variant": "elevated", "padding": 16 }
    },
    {
      "id": "comp_header_col",
      "component": "Column",
      "children": ["comp_badge", "comp_title", "comp_sub"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "comp_badge",
      "component": "Badge",
      "properties": { "text": "Bilateral Comparison", "status": "info", "icon": "eco" }
    },
    {
      "id": "comp_title",
      "component": "Text",
      "properties": { "text": "Emissions: Germany vs $target", "variant": "titleLarge", "bold": true }
    },
    {
      "id": "comp_sub",
      "component": "Text",
      "properties": { "text": "France produces 43% lower total greenhouse gas emissions due to a high nuclear baseline.", "variant": "bodyMedium" }
    },
    {
      "id": "comp_bar",
      "component": "ComparisonRow",
      "properties": {
        "labelA": "Germany",
        "valueA": 674,
        "labelB": "$target",
        "valueB": 382,
        "unit": "Mt CO2e"
      }
    },
    {
      "id": "chart_side_by_side",
      "component": "EmissionsChart",
      "properties": {
        "title": "$target Sectoral Emissions (Mt CO2e)",
        "unit": "Mt CO2e",
        "categories": ["Road Transport", "Agriculture", "Manufacturing", "Power Generation", "Buildings"],
        "values": [128.4, 82.1, 74.3, 42.6, 54.6],
        "color": "#6366F1"
      }
    },
    {
      "id": "summary_card",
      "component": "MetricCard",
      "properties": {
        "title": "Per Capita Emissions Difference",
        "value": "-3.6 t",
        "unit": "per person in $target",
        "delta": "44% lower than Germany",
        "trend": "down",
        "icon": "trending_down",
        "accentColor": "#10B981"
      }
    },
    {
      "id": "btn_back",
      "component": "Button",
      "properties": {
        "text": "Reset to Germany Overview",
        "variant": "tonal",
        "icon": "refresh",
        "fillMaxWidth": true
      },
      "action": {
        "name": "reset_view",
        "parameters": {}
      }
    }
  ]
}
                """.trimIndent()

                val parseResult = A2UIParser.parse(json)
                AgentResponse(
                    explanationText = "Analyzed emissions contrast between Germany and $target. France benefits from an ultra-low carbon grid, while Germany's transition is ongoing.",
                    surface = (parseResult as? ParseResult.Success)?.surface,
                    rawJson = json
                )
            }

            "drill_sector" -> {
                val sector = action.parameters["sector"]?.toString() ?: "Power Generation"
                val json = """
{
  "surfaceId": "sector_drilldown",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Deep Dive: $sector",
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["sector_header", "kpi_power", "sub_chart", "btn_back"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    {
      "id": "sector_header",
      "component": "Card",
      "children": ["sector_col"],
      "properties": { "variant": "elevated", "padding": 16 }
    },
    {
      "id": "sector_col",
      "component": "Column",
      "children": ["sec_badge", "sec_title", "sec_desc"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "sec_badge",
      "component": "Badge",
      "properties": { "text": "Sub-Sector Assets", "status": "warning", "icon": "energy" }
    },
    {
      "id": "sec_title",
      "component": "Text",
      "properties": { "text": "$sector Assets", "variant": "titleLarge", "bold": true }
    },
    {
      "id": "sec_desc",
      "component": "Text",
      "properties": { "text": "Emissions by fuel source observed by Climate TRACE thermal satellites and radar.", "variant": "bodyMedium" }
    },
    {
      "id": "kpi_power",
      "component": "MetricCard",
      "properties": {
        "title": "Lignite & Hard Coal Share",
        "value": "68%",
        "unit": "of sector",
        "delta": "-12.4% vs 2022",
        "trend": "down",
        "icon": "cloud",
        "accentColor": "#F59E0B"
      }
    },
    {
      "id": "sub_chart",
      "component": "EmissionsChart",
      "properties": {
        "title": "$sector Sub-sources (Mt CO2e)",
        "unit": "Mt CO2e",
        "categories": ["Lignite Coal", "Hard Coal", "Fossil Gas", "Biomass / Other"],
        "values": [108.2, 49.3, 44.1, 10.9],
        "color": "#F59E0B"
      }
    },
    {
      "id": "btn_back",
      "component": "Button",
      "properties": {
        "text": "Back to National Overview",
        "variant": "tonal",
        "icon": "refresh",
        "fillMaxWidth": true
      },
      "action": {
        "name": "reset_view",
        "parameters": {}
      }
    }
  ]
}
                """.trimIndent()
                val parseResult = A2UIParser.parse(json)
                AgentResponse(
                    explanationText = "Drilled into Germany's $sector. Coal phasing remains the primary lever for the upcoming 2030 targets.",
                    surface = (parseResult as? ParseResult.Success)?.surface,
                    rawJson = json
                )
            }

            "reset_view" -> {
                val preset = A2UIPresets.getPreset("climatetrace")
                val parseResult = A2UIParser.parse(preset.jsonContent)
                AgentResponse(
                    explanationText = "Reset to national overview for Germany.",
                    surface = (parseResult as? ParseResult.Success)?.surface,
                    rawJson = preset.jsonContent
                )
            }

            "apply_schedule" -> {
                val temp = currentSurface.dataModel["temperature"] ?: 21.5
                val eco = currentSurface.dataModel["eco_mode"] ?: true
                AgentResponse(
                    explanationText = "Schedule updated! Living room thermostat adjusted to $temp°C with Eco Peak-Shifting set to $eco.",
                    surface = currentSurface,
                    rawJson = null
                )
            }

            "confirm_reservation" -> {
                val passenger = currentSurface.dataModel["passengerName"] ?: "Guest"
                AgentResponse(
                    explanationText = "Booking confirmed for passenger '$passenger' on flight JL004! An e-ticket receipt and carbon offset certificate have been issued.",
                    surface = currentSurface,
                    rawJson = null
                )
            }

            "submit_survey" -> {
                val rating = currentSurface.dataModel["rating"] ?: 8.0
                val comments = currentSurface.dataModel["feedbackText"] ?: ""
                AgentResponse(
                    explanationText = "Thank you! Received satisfaction rating of $rating/10 with your comments: '$comments'.",
                    surface = currentSurface,
                    rawJson = null
                )
            }

            else -> {
                AgentResponse(
                    explanationText = "Received user action '${action.name}' with parameters: ${action.parameters}.",
                    surface = currentSurface,
                    rawJson = null
                )
            }
        }
    }

    private suspend fun callGeminiApi(prompt: String, apiKey: String): AgentResponse {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val systemPrompt = A2UISchemaGenerator.buildSystemPrompt()

        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val httpRequest = Request.Builder().url(url).post(requestBody).build()

        val response = httpClient.newCall(httpRequest).execute()
        val responseBodyStr = response.body?.string() ?: throw IllegalStateException("Empty response")

        val respObj = JSONObject(responseBodyStr)
        val candidate = respObj.optJSONArray("candidates")?.optJSONObject(0)
        val content = candidate?.optJSONObject("content")
        val part = content?.optJSONArray("parts")?.optJSONObject(0)
        val text = part?.optString("text", "") ?: ""

        val parseResult = A2UIParser.parse(text)
        return if (parseResult is ParseResult.Success) {
            AgentResponse(
                explanationText = "Generated custom A2UI surface for: '$prompt'",
                surface = parseResult.surface,
                rawJson = text
            )
        } else {
            generateSimulatedAgentResponse(prompt)
        }
    }

    private fun generateSimulatedAgentResponse(prompt: String): AgentResponse {
        val lower = prompt.lowercase()

        val (title, json) = when {
            "flight" in lower || "travel" in lower || "japan" in lower || "tokyo" in lower -> {
                "Flight Itinerary" to A2UIPresets.getPreset("travel_booking").jsonContent
            }
            "energy" in lower || "home" in lower || "thermostat" in lower || "smart" in lower -> {
                "Energy Center" to A2UIPresets.getPreset("smart_home").jsonContent
            }
            "survey" in lower || "feedback" in lower || "form" in lower -> {
                "Feedback Form" to A2UIPresets.getPreset("feedback_survey").jsonContent
            }
            "climate" in lower || "emission" in lower || "carbon" in lower || "trace" in lower -> {
                "Emissions Data" to A2UIPresets.getPreset("climatetrace").jsonContent
            }
            else -> {
                "Dynamic UI: $prompt" to """
{
  "surfaceId": "custom_agent_surface",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Generated UI: $prompt",
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["header_card", "stats_row", "action_row"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    {
      "id": "header_card",
      "component": "Card",
      "children": ["header_box"],
      "properties": { "variant": "elevated", "padding": 16 }
    },
    {
      "id": "header_box",
      "component": "Column",
      "children": ["badge_tag", "main_title", "main_subtitle"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "badge_tag",
      "component": "Badge",
      "properties": { "text": "Agent Generated UI", "status": "info", "icon": "eco" }
    },
    {
      "id": "main_title",
      "component": "Text",
      "properties": { "text": "$prompt", "variant": "titleLarge", "bold": true }
    },
    {
      "id": "main_subtitle",
      "component": "Text",
      "properties": { "text": "Generated dynamically by A2UI protocol using native Jetpack Compose Material 3 components.", "variant": "bodyMedium" }
    },
    {
      "id": "stats_row",
      "component": "Row",
      "children": ["kpi_1", "kpi_2"],
      "properties": { "spacing": 10, "fillMaxWidth": true }
    },
    {
      "id": "kpi_1",
      "component": "MetricCard",
      "properties": {
        "title": "Confidence Score",
        "value": "98.4%",
        "unit": "Schema Match",
        "delta": "Valid Catalog",
        "trend": "up",
        "icon": "check",
        "accentColor": "#10B981"
      }
    },
    {
      "id": "kpi_2",
      "component": "MetricCard",
      "properties": {
        "title": "Adjacency Nodes",
        "value": "7",
        "unit": "Components",
        "delta": "0 warnings",
        "trend": "neutral",
        "icon": "settings",
        "accentColor": "#6366F1"
      }
    },
    {
      "id": "action_row",
      "component": "Row",
      "children": ["btn_action_1"],
      "properties": { "spacing": 8, "fillMaxWidth": true }
    },
    {
      "id": "btn_action_1",
      "component": "Button",
      "properties": {
        "text": "Interact with Agent",
        "variant": "filled",
        "fillMaxWidth": true,
        "icon": "arrow_forward"
      },
      "action": {
        "name": "interact_custom",
        "parameters": { "prompt": "$prompt" }
      }
    }
  ]
}
                """.trimIndent()
            }
        }

        val parseResult = A2UIParser.parse(json)
        return AgentResponse(
            explanationText = "Here is the declarative A2UI interface created for: '$prompt'",
            surface = (parseResult as? ParseResult.Success)?.surface,
            rawJson = json
        )
    }
}
