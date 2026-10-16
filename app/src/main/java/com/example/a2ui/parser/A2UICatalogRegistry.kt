package com.example.a2ui.parser

import com.example.a2ui.model.CatalogComponentSpec
import com.example.a2ui.model.PropertySpec
import org.json.JSONArray
import org.json.JSONObject

/**
 * Registry defining all available components in the A2UI Catalog.
 * Used for validation, dynamic documentation, and generating the JSON Schema
 * used in system prompts for AI agents (Gemini / Koog).
 */
object A2UICatalogRegistry {

    val catalogUri: String = "https://a2ui.org/catalog/v1"

    val components: List<CatalogComponentSpec> = listOf(
        // Layouts
        CatalogComponentSpec(
            name = "Column",
            category = "Layout",
            description = "Arranges child components vertically with optional spacing, alignment, and card padding.",
            supportedProperties = listOf(
                PropertySpec("spacing", "number", "Vertical spacing between items in dp", defaultVal = "8"),
                PropertySpec("padding", "number", "Inner padding around the column in dp", defaultVal = "0"),
                PropertySpec("horizontalAlignment", "string", "Start, CenterHorizontally, or End", defaultVal = "Start"),
                PropertySpec("fillMaxWidth", "boolean", "Whether column expands to full width", defaultVal = "true")
            ),
            allowedChildren = true,
            exampleJson = """{"id": "root", "component": "Column", "children": ["header", "content"], "properties": {"spacing": 12}}"""
        ),
        CatalogComponentSpec(
            name = "Row",
            category = "Layout",
            description = "Arranges child components horizontally. Resolves card height alignment using IntrinsicSize/weights.",
            supportedProperties = listOf(
                PropertySpec("spacing", "number", "Horizontal spacing between items in dp", defaultVal = "8"),
                PropertySpec("padding", "number", "Inner padding around the row in dp", defaultVal = "0"),
                PropertySpec("verticalAlignment", "string", "Top, CenterVertically, or Bottom", defaultVal = "CenterVertically"),
                PropertySpec("fillMaxWidth", "boolean", "Whether row expands to full width", defaultVal = "true")
            ),
            allowedChildren = true,
            exampleJson = """{"id": "metrics_row", "component": "Row", "children": ["metric_1", "metric_2"], "properties": {"spacing": 8}}"""
        ),
        CatalogComponentSpec(
            name = "Card",
            category = "Layout",
            description = "Material 3 Card container for grouping related content with elevation, border, and background.",
            supportedProperties = listOf(
                PropertySpec("variant", "string", "filled, elevated, or outlined", defaultVal = "filled"),
                PropertySpec("padding", "number", "Inner padding in dp", defaultVal = "16"),
                PropertySpec("backgroundColor", "string", "Optional hex color or semantic token (e.g. #1E293B, primaryContainer)", defaultVal = null),
                PropertySpec("cornerRadius", "number", "Corner radius in dp", defaultVal = "16")
            ),
            allowedChildren = true,
            exampleJson = """{"id": "climate_card", "component": "Card", "children": ["card_content"], "properties": {"variant": "elevated", "padding": 16}}"""
        ),
        CatalogComponentSpec(
            name = "Box",
            category = "Layout",
            description = "A basic stacking layout container for layering components on top of each other.",
            supportedProperties = listOf(
                PropertySpec("contentAlignment", "string", "Center, TopStart, BottomEnd, etc.", defaultVal = "Center"),
                PropertySpec("fillMaxWidth", "boolean", "Whether box expands to full width", defaultVal = "true")
            ),
            allowedChildren = true,
            exampleJson = """{"id": "banner_box", "component": "Box", "children": ["bg_graphic", "overlay_text"]}"""
        ),

        // Display
        CatalogComponentSpec(
            name = "Text",
            category = "Display",
            description = "Renders text formatted with Material 3 typography scales.",
            supportedProperties = listOf(
                PropertySpec("text", "string", "The text content to display", required = true),
                PropertySpec("variant", "string", "displayLarge, headlineMedium, headlineSmall, titleLarge, titleMedium, bodyLarge, bodyMedium, labelMedium, labelSmall", defaultVal = "bodyMedium"),
                PropertySpec("color", "string", "Hex color string (e.g. #FFFFFF, #6366F1, primary, onSurfaceVariant)", defaultVal = null),
                PropertySpec("bold", "boolean", "Whether to render in bold font weight", defaultVal = "false"),
                PropertySpec("maxLines", "number", "Maximum lines before ellipsizing", defaultVal = "null"),
                PropertySpec("textAlign", "string", "Start, Center, End, Justify", defaultVal = "Start")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "title_text", "component": "Text", "properties": {"text": "Climate TRACE Emissions", "variant": "headlineSmall", "bold": true}}"""
        ),
        CatalogComponentSpec(
            name = "Icon",
            category = "Display",
            description = "Renders a Material Symbol or vector icon.",
            supportedProperties = listOf(
                PropertySpec("icon", "string", "Icon name (eco, energy, flight, check, warning, info, trending_up, trending_down, refresh, settings, cloud, water)", required = true),
                PropertySpec("tint", "string", "Hex color or semantic token for icon tint", defaultVal = null),
                PropertySpec("size", "number", "Icon size in dp", defaultVal = "24")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "eco_icon", "component": "Icon", "properties": {"icon": "eco", "tint": "#10B981", "size": 28}}"""
        ),
        CatalogComponentSpec(
            name = "Badge",
            category = "Display",
            description = "Status chip badge with color indicators (success, warning, error, info).",
            supportedProperties = listOf(
                PropertySpec("text", "string", "Badge label text", required = true),
                PropertySpec("status", "string", "success, warning, error, info, neutral", defaultVal = "neutral"),
                PropertySpec("icon", "string", "Optional leading icon", defaultVal = null)
            ),
            allowedChildren = false,
            exampleJson = """{"id": "status_badge", "component": "Badge", "properties": {"text": "-14.2% YoY", "status": "success", "icon": "trending_down"}}"""
        ),
        CatalogComponentSpec(
            name = "Divider",
            category = "Display",
            description = "Horizontal separator line.",
            supportedProperties = listOf(
                PropertySpec("thickness", "number", "Thickness in dp", defaultVal = "1"),
                PropertySpec("color", "string", "Hex color or outline token", defaultVal = null)
            ),
            allowedChildren = false,
            exampleJson = """{"id": "separator", "component": "Divider", "properties": {"thickness": 1}}"""
        ),
        CatalogComponentSpec(
            name = "Spacer",
            category = "Display",
            description = "Adds empty space between components.",
            supportedProperties = listOf(
                PropertySpec("height", "number", "Spacer height in dp", defaultVal = "16"),
                PropertySpec("width", "number", "Spacer width in dp", defaultVal = "0")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "gap", "component": "Spacer", "properties": {"height": 16}}"""
        ),

        // Controls & Input
        CatalogComponentSpec(
            name = "Button",
            category = "Input",
            description = "Interactive button that triggers an A2UIAction back to the agent.",
            supportedProperties = listOf(
                PropertySpec("text", "string", "Button label", required = true),
                PropertySpec("variant", "string", "filled, outlined, tonal, text, elevated", defaultVal = "filled"),
                PropertySpec("icon", "string", "Optional icon name", defaultVal = null),
                PropertySpec("enabled", "boolean", "Whether button is clickable", defaultVal = "true"),
                PropertySpec("fillMaxWidth", "boolean", "Expand full width", defaultVal = "false")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "btn_compare", "component": "Button", "properties": {"text": "Compare with France", "variant": "tonal", "icon": "refresh"}, "action": {"name": "compare_country", "parameters": {"code": "FR"}}}"""
        ),
        CatalogComponentSpec(
            name = "TextField",
            category = "Input",
            description = "Editable text field that updates the surface data model.",
            supportedProperties = listOf(
                PropertySpec("dataKey", "string", "State key in surface dataModel", required = true),
                PropertySpec("label", "string", "Field floating label", defaultVal = ""),
                PropertySpec("placeholder", "string", "Placeholder text", defaultVal = ""),
                PropertySpec("leadingIcon", "string", "Optional leading icon name", defaultVal = null),
                PropertySpec("singleLine", "boolean", "Single line input", defaultVal = "true")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "input_query", "component": "TextField", "properties": {"dataKey": "query", "label": "Search Country or Sector", "placeholder": "e.g. Germany Transport"}}"""
        ),
        CatalogComponentSpec(
            name = "Slider",
            category = "Input",
            description = "Continuous value slider bound to dataModel.",
            supportedProperties = listOf(
                PropertySpec("dataKey", "string", "State key in surface dataModel", required = true),
                PropertySpec("label", "string", "Label describing slider", defaultVal = ""),
                PropertySpec("min", "number", "Minimum value", defaultVal = "0"),
                PropertySpec("max", "number", "Maximum value", defaultVal = "100"),
                PropertySpec("steps", "number", "Discreet steps (0 for continuous)", defaultVal = "0"),
                PropertySpec("unit", "string", "Display unit suffix (e.g. °C, %, Mt)", defaultVal = "")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "temp_slider", "component": "Slider", "properties": {"dataKey": "temperature", "label": "Target Temp", "min": 16, "max": 28, "unit": "°C"}}"""
        ),
        CatalogComponentSpec(
            name = "Switch",
            category = "Input",
            description = "Toggle switch bound to dataModel.",
            supportedProperties = listOf(
                PropertySpec("dataKey", "string", "State key in surface dataModel", required = true),
                PropertySpec("label", "string", "Label text beside switch", required = true),
                PropertySpec("subtitle", "string", "Optional secondary text", defaultVal = null)
            ),
            allowedChildren = false,
            exampleJson = """{"id": "eco_toggle", "component": "Switch", "properties": {"dataKey": "eco_mode", "label": "Eco Optimization", "subtitle": "Reduce carbon footprint"}}"""
        ),

        // Custom Domain Components (John O'Reilly ClimateTrace & Advanced A2UI)
        CatalogComponentSpec(
            name = "EmissionsChart",
            category = "Custom",
            description = "Domain-specific ClimateTrace emissions chart with animated bars, sector breakdown, and Mt CO2e values.",
            supportedProperties = listOf(
                PropertySpec("title", "string", "Chart title header", required = true),
                PropertySpec("categories", "array", "List of sector names (e.g. ['Power', 'Manufacturing', 'Transport', 'Agriculture'])", required = true),
                PropertySpec("values", "array", "List of numeric values in Mt CO2e", required = true),
                PropertySpec("unit", "string", "Measurement unit", defaultVal = "Mt CO2e"),
                PropertySpec("color", "string", "Primary chart bar color", defaultVal = "#06B6D4")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "chart_emissions", "component": "EmissionsChart", "properties": {"title": "2024 Greenhouse Gas Emissions", "categories": ["Power", "Manufacturing", "Transport", "Agriculture"], "values": [212.5, 148.0, 114.2, 58.7], "unit": "Mt CO2e"}}"""
        ),
        CatalogComponentSpec(
            name = "MetricCard",
            category = "Custom",
            description = "High-impact KPI card with value, unit, subtitle, and delta trend percentage.",
            supportedProperties = listOf(
                PropertySpec("title", "string", "Metric name", required = true),
                PropertySpec("value", "string", "Main numeric or formatted value", required = true),
                PropertySpec("unit", "string", "Unit label (e.g. Mt, %, kWh)", defaultVal = ""),
                PropertySpec("delta", "string", "Change text (e.g. -8.4%)", defaultVal = null),
                PropertySpec("trend", "string", "up, down, or neutral", defaultVal = "neutral"),
                PropertySpec("icon", "string", "Header icon", defaultVal = null),
                PropertySpec("accentColor", "string", "Card accent color hex", defaultVal = null)
            ),
            allowedChildren = false,
            exampleJson = """{"id": "metric_total", "component": "MetricCard", "properties": {"title": "Total Emissions", "value": "674", "unit": "Mt CO2e", "delta": "-4.2% vs 2023", "trend": "down", "icon": "eco"}}"""
        ),
        CatalogComponentSpec(
            name = "ComparisonRow",
            category = "Custom",
            description = "Comparative visual bar between two metrics (e.g. Target vs Actual or Country A vs Country B).",
            supportedProperties = listOf(
                PropertySpec("labelA", "string", "First item label", required = true),
                PropertySpec("valueA", "number", "First item numeric value", required = true),
                PropertySpec("labelB", "string", "Second item label", required = true),
                PropertySpec("valueB", "number", "Second item numeric value", required = true),
                PropertySpec("unit", "string", "Unit suffix", defaultVal = "")
            ),
            allowedChildren = false,
            exampleJson = """{"id": "comp_row", "component": "ComparisonRow", "properties": {"labelA": "Germany", "valueA": 674, "labelB": "France", "valueB": 382, "unit": "Mt CO2e"}}"""
        )
    )

    fun getComponent(name: String): CatalogComponentSpec? {
        return components.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
