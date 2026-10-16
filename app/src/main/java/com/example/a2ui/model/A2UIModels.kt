package com.example.a2ui.model

/**
 * Representation of an individual component in the A2UI (Agent-to-UI) protocol.
 * Following Google's A2UI declarative specification, components are organized in an
 * adjacency list where parent components reference children by unique IDs.
 */
data class A2UIComponent(
    val id: String,
    val component: String,
    val children: List<String> = emptyList(),
    val properties: Map<String, Any?> = emptyMap(),
    val action: A2UIAction? = null,
    val weight: Float? = null
) {
    fun getStringProperty(key: String, default: String = ""): String {
        return properties[key]?.toString() ?: default
    }

    fun getBooleanProperty(key: String, default: Boolean = false): Boolean {
        return when (val v = properties[key]) {
            is Boolean -> v
            is String -> v.toBooleanStrictOrNull() ?: default
            else -> default
        }
    }

    fun getFloatProperty(key: String, default: Float = 0f): Float {
        return when (val v = properties[key]) {
            is Number -> v.toFloat()
            is String -> v.toFloatOrNull() ?: default
            else -> default
        }
    }

    fun getIntProperty(key: String, default: Int = 0): Int {
        return when (val v = properties[key]) {
            is Number -> v.toInt()
            is String -> v.toIntOrNull() ?: default
            else -> default
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getListProperty(key: String): List<Any?> {
        return (properties[key] as? List<*>) ?: emptyList<Any?>()
    }

    @Suppress("UNCHECKED_CAST")
    fun getMapProperty(key: String): Map<String, Any?> {
        return (properties[key] as? Map<String, Any?>) ?: emptyMap()
    }
}

/**
 * Represents a user action event triggered from an interactive component (e.g. Button tap, Switch toggle).
 * Sent back to the Agent in the multi-turn loop.
 */
data class A2UIAction(
    val name: String,
    val parameters: Map<String, Any?> = emptyMap()
)

/**
 * An A2UI surface representing the complete declarative UI state.
 */
data class A2UISurface(
    val surfaceId: String = "main",
    val catalog: String = "https://a2ui.org/catalog/v1",
    val title: String = "Agent UI Surface",
    val components: Map<String, A2UIComponent> = emptyMap(),
    val dataModel: MutableMap<String, Any?> = mutableMapOf(),
    val rootComponentId: String = "root"
) {
    val rootComponent: A2UIComponent?
        get() = components[rootComponentId]
}

/**
 * Event log entry recorded when a user interacts with an A2UI component.
 */
data class A2UIActionEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val surfaceId: String,
    val actionName: String,
    val parameters: Map<String, Any?>,
    val sourceComponentId: String
)

/**
 * A message in the Agent-User chat loop.
 */
data class AgentChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val surfacePayload: A2UISurface? = null,
    val rawJson: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Definition of a component in the Catalog for schema generation and documentation.
 */
data class CatalogComponentSpec(
    val name: String,
    val category: String, // "Layout", "Display", "Input", "Custom"
    val description: String,
    val supportedProperties: List<PropertySpec>,
    val allowedChildren: Boolean = false,
    val exampleJson: String
)

data class PropertySpec(
    val name: String,
    val type: String, // "string", "number", "boolean", "array", "object"
    val description: String,
    val required: Boolean = false,
    val defaultVal: String? = null
)
