package com.example.a2ui.parser

import org.json.JSONArray
import org.json.JSONObject

/**
 * Generates JSON Schema specification from the Catalog.
 * Enables AI Agents (such as Gemini or Koog) to understand the catalog components,
 * their properties, required fields, and the adjacency list format.
 */
object A2UISchemaGenerator {

    fun generateJsonSchema(): String {
        val root = JSONObject()
        root.put("\$schema", "http://json-schema.org/draft-07/schema#")
        root.put("title", "A2UI Component Catalog Schema")
        root.put("description", "Agent-to-UI declarative protocol schema for dynamic Jetpack Compose rendering")
        root.put("type", "object")

        val rootProps = JSONObject()

        // Surface properties
        val surfaceId = JSONObject().apply {
            put("type", "string")
            put("description", "Unique identifier for this surface")
        }
        rootProps.put("surfaceId", surfaceId)

        val catalog = JSONObject().apply {
            put("type", "string")
            put("default", A2UICatalogRegistry.catalogUri)
        }
        rootProps.put("catalog", catalog)

        val title = JSONObject().apply {
            put("type", "string")
            put("description", "User-facing title of the rendered surface")
        }
        rootProps.put("title", title)

        // Components Array schema
        val componentsArray = JSONObject().apply {
            put("type", "array")
            put("description", "Flat adjacency list of UI components. One component MUST have id='root'.")
            val itemsObj = JSONObject().apply {
                put("type", "object")
                val itemProps = JSONObject().apply {
                    put("id", JSONObject().apply {
                        put("type", "string")
                        put("description", "Unique component identifier (e.g. 'root', 'card_1')")
                    })
                    put("component", JSONObject().apply {
                        put("type", "string")
                        put("enum", JSONArray(A2UICatalogRegistry.components.map { it.name }))
                        put("description", "The catalog component type to render")
                    })
                    put("children", JSONObject().apply {
                        put("type", "array")
                        put("items", JSONObject().apply { put("type", "string") })
                        put("description", "List of child component IDs in order of display")
                    })
                    put("properties", JSONObject().apply {
                        put("type", "object")
                        put("description", "Key-value attributes specific to this component")
                    })
                    put("action", JSONObject().apply {
                        put("type", "object")
                        val actProps = JSONObject().apply {
                            put("name", JSONObject().apply { put("type", "string") })
                            put("parameters", JSONObject().apply { put("type", "object") })
                        }
                        put("properties", actProps)
                        put("required", JSONArray(listOf("name")))
                    })
                }
                put("properties", itemProps)
                put("required", JSONArray(listOf("id", "component")))
            }
            put("items", itemsObj)
        }
        rootProps.put("components", componentsArray)

        root.put("properties", rootProps)
        root.put("required", JSONArray(listOf("components")))

        return root.toString(2)
    }

    /**
     * Builds the system instruction prompt snippet that informs an LLM
     * how to construct A2UI JSON responses.
     */
    fun buildSystemPrompt(): String {
        return """
You are an AI Agent with an Agent-to-UI (A2UI) interface.
When generating a user interface, you MUST output a single valid JSON object following the A2UI protocol.
Structure:
{
  "surfaceId": "main",
  "catalog": "${A2UICatalogRegistry.catalogUri}",
  "title": "<Display Title>",
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["header_card", "content_area"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    ...
  ]
}

Available Components:
${A2UICatalogRegistry.components.joinToString("\n") { "- ${it.name} (${it.category}): ${it.description}" }}

Rules:
1. Always include a root component with id="root".
2. Use an adjacency list: parent components define child IDs in their "children" array.
3. Interactive buttons include "action": {"name": "<action_id>", "parameters": { ... }}.
4. Output ONLY the JSON block.
        """.trimIndent()
    }
}
