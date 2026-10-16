package com.example.a2ui.repository

data class A2UIPreset(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val jsonContent: String
)

object A2UIPresets {

    val presets: List<A2UIPreset> = listOf(
        A2UIPreset(
            id = "climatetrace",
            name = "Climate TRACE Emissions",
            description = "John O'Reilly's sample: GHG emissions chart, KPI metrics, country comparison, and action buttons.",
            icon = "eco",
            jsonContent = """
{
  "surfaceId": "climatetrace_summary",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Climate TRACE Emissions Analysis",
  "dataModel": {
    "selectedCountry": "Germany",
    "year": 2024
  },
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["header_card", "metrics_row", "chart_emissions", "comparison_card", "action_bar"],
      "properties": {
        "spacing": 14,
        "padding": 0,
        "fillMaxWidth": true
      }
    },
    {
      "id": "header_card",
      "component": "Card",
      "children": ["header_content"],
      "properties": {
        "variant": "elevated",
        "padding": 16
      }
    },
    {
      "id": "header_content",
      "component": "Column",
      "children": ["header_badge_row", "header_title", "header_sub"],
      "properties": {
        "spacing": 6
      }
    },
    {
      "id": "header_badge_row",
      "component": "Row",
      "children": ["climate_badge", "country_badge"],
      "properties": {
        "spacing": 8
      }
    },
    {
      "id": "climate_badge",
      "component": "Badge",
      "properties": {
        "text": "Climate TRACE v3.2",
        "status": "info",
        "icon": "eco"
      }
    },
    {
      "id": "country_badge",
      "component": "Badge",
      "properties": {
        "text": "Germany (DEU)",
        "status": "neutral"
      }
    },
    {
      "id": "header_title",
      "component": "Text",
      "properties": {
        "text": "National Greenhouse Gas Inventory",
        "variant": "titleLarge",
        "bold": true
      }
    },
    {
      "id": "header_sub",
      "component": "Text",
      "properties": {
        "text": "Asset-level and satellite observed emissions across key economic sectors.",
        "variant": "bodyMedium"
      }
    },
    {
      "id": "metrics_row",
      "component": "Row",
      "children": ["metric_total", "metric_intensity"],
      "properties": {
        "spacing": 10,
        "fillMaxWidth": true
      }
    },
    {
      "id": "metric_total",
      "component": "MetricCard",
      "properties": {
        "title": "Total Net Emissions",
        "value": "674.2",
        "unit": "Mt CO2e",
        "delta": "-4.8% vs 2023",
        "trend": "down",
        "icon": "eco",
        "accentColor": "#06B6D4"
      }
    },
    {
      "id": "metric_intensity",
      "component": "MetricCard",
      "properties": {
        "title": "Per Capita",
        "value": "8.1",
        "unit": "t / person",
        "delta": "-3.1% YoY",
        "trend": "down",
        "icon": "trending_down",
        "accentColor": "#6366F1"
      }
    },
    {
      "id": "chart_emissions",
      "component": "EmissionsChart",
      "properties": {
        "title": "Sectoral Breakdown (2024)",
        "unit": "Mt CO2e",
        "categories": ["Power Generation", "Manufacturing", "Road Transport", "Agriculture", "Fossil Fuel Ops"],
        "values": [212.5, 148.0, 114.2, 58.7, 34.1],
        "color": "#06B6D4"
      }
    },
    {
      "id": "comparison_card",
      "component": "ComparisonRow",
      "properties": {
        "labelA": "Germany",
        "valueA": 674,
        "labelB": "France",
        "valueB": 382,
        "unit": "Mt CO2e"
      }
    },
    {
      "id": "action_bar",
      "component": "Row",
      "children": ["btn_compare", "btn_sectors"],
      "properties": {
        "spacing": 10,
        "fillMaxWidth": true
      }
    },
    {
      "id": "btn_compare",
      "component": "Button",
      "properties": {
        "text": "Compare France",
        "variant": "tonal",
        "icon": "refresh",
        "fillMaxWidth": true
      },
      "action": {
        "name": "compare_country",
        "parameters": {
          "targetCountry": "France",
          "targetCode": "FR"
        }
      }
    },
    {
      "id": "btn_sectors",
      "component": "Button",
      "properties": {
        "text": "Drill Power Sector",
        "variant": "filled",
        "icon": "energy",
        "fillMaxWidth": true
      },
      "action": {
        "name": "drill_sector",
        "parameters": {
          "sector": "Power Generation"
        }
      }
    }
  ]
}
            """.trimIndent()
        ),

        A2UIPreset(
            id = "smart_home",
            name = "Smart Energy Optimizer",
            description = "Interactive climate control with slider, eco switch, and real-time efficiency KPI.",
            icon = "energy",
            jsonContent = """
{
  "surfaceId": "smart_home_optimizer",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Eco Thermostat & Energy",
  "dataModel": {
    "temperature": 21.5,
    "eco_mode": true,
    "device_label": "Living Room Hub"
  },
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["status_card", "controls_card", "kpi_row", "btn_apply"],
      "properties": {
        "spacing": 12,
        "fillMaxWidth": true
      }
    },
    {
      "id": "status_card",
      "component": "Card",
      "children": ["status_content"],
      "properties": {
        "variant": "elevated",
        "padding": 16
      }
    },
    {
      "id": "status_content",
      "component": "Column",
      "children": ["badge_row", "system_title", "system_desc"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "badge_row",
      "component": "Row",
      "children": ["eco_badge"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "eco_badge",
      "component": "Badge",
      "properties": {
        "text": "Eco-Optimizer Active",
        "status": "success",
        "icon": "eco"
      }
    },
    {
      "id": "system_title",
      "component": "Text",
      "properties": {
        "text": "Home Climate Center",
        "variant": "titleLarge",
        "bold": true
      }
    },
    {
      "id": "system_desc",
      "component": "Text",
      "properties": {
        "text": "AI dynamic scheduling is saving approx. 2.4 kWh today.",
        "variant": "bodyMedium"
      }
    },
    {
      "id": "controls_card",
      "component": "Card",
      "children": ["controls_content"],
      "properties": {
        "variant": "outlined",
        "padding": 16
      }
    },
    {
      "id": "controls_content",
      "component": "Column",
      "children": ["temp_slider", "spacer_1", "eco_switch"],
      "properties": { "spacing": 8 }
    },
    {
      "id": "temp_slider",
      "component": "Slider",
      "properties": {
        "dataKey": "temperature",
        "label": "Desired Temperature",
        "min": 16,
        "max": 28,
        "unit": "°C",
        "steps": 24
      }
    },
    {
      "id": "spacer_1",
      "component": "Spacer",
      "properties": { "height": 8 }
    },
    {
      "id": "eco_switch",
      "component": "Switch",
      "properties": {
        "dataKey": "eco_mode",
        "label": "Peak-Hour Shifting",
        "subtitle": "Pre-heat during low grid carbon intensity"
      }
    },
    {
      "id": "kpi_row",
      "component": "Row",
      "children": ["metric_savings", "metric_grid"],
      "properties": {
        "spacing": 10,
        "fillMaxWidth": true
      }
    },
    {
      "id": "metric_savings",
      "component": "MetricCard",
      "properties": {
        "title": "Estimated Cost Savings",
        "value": "$28.40",
        "unit": "/ mo",
        "delta": "+18% efficiency",
        "trend": "up",
        "icon": "energy"
      }
    },
    {
      "id": "metric_grid",
      "component": "MetricCard",
      "properties": {
        "title": "Grid Carbon Index",
        "value": "184",
        "unit": "g CO2/kWh",
        "delta": "Clean Window",
        "trend": "down",
        "icon": "cloud",
        "accentColor": "#10B981"
      }
    },
    {
      "id": "btn_apply",
      "component": "Button",
      "properties": {
        "text": "Save & Apply Schedule",
        "variant": "filled",
        "fillMaxWidth": true,
        "icon": "check"
      },
      "action": {
        "name": "apply_schedule",
        "parameters": {
          "mode": "eco_optimized"
        }
      }
    }
  ]
}
            """.trimIndent()
        ),

        A2UIPreset(
            id = "travel_booking",
            name = "Travel Concierge",
            description = "Flight itinerary with passenger input field, travel metrics, and confirmation button.",
            icon = "flight",
            jsonContent = """
{
  "surfaceId": "travel_summary",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "AI Travel Concierge",
  "dataModel": {
    "passengerName": "Alex Chen",
    "seatPreference": "Window"
  },
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["flight_card", "passenger_card", "carbon_card", "book_btn"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    {
      "id": "flight_card",
      "component": "Card",
      "children": ["flight_details"],
      "properties": { "variant": "elevated", "padding": 16 }
    },
    {
      "id": "flight_details",
      "component": "Column",
      "children": ["flight_header", "route_row", "time_sub"],
      "properties": { "spacing": 8 }
    },
    {
      "id": "flight_header",
      "component": "Row",
      "children": ["airline_badge", "flight_num"],
      "properties": { "spacing": 8 }
    },
    {
      "id": "airline_badge",
      "component": "Badge",
      "properties": {
        "text": "Direct Flight",
        "status": "info",
        "icon": "flight"
      }
    },
    {
      "id": "flight_num",
      "component": "Text",
      "properties": {
        "text": "JL 004 • Boeing 787-9",
        "variant": "labelMedium"
      }
    },
    {
      "id": "route_row",
      "component": "Row",
      "children": ["city_from", "route_arrow", "city_to"],
      "properties": { "spacing": 12 }
    },
    {
      "id": "city_from",
      "component": "Text",
      "properties": {
        "text": "SFO",
        "variant": "headlineMedium",
        "bold": true
      }
    },
    {
      "id": "route_arrow",
      "component": "Icon",
      "properties": {
        "icon": "arrow_forward",
        "size": 28
      }
    },
    {
      "id": "city_to",
      "component": "Text",
      "properties": {
        "text": "HND (Tokyo)",
        "variant": "headlineMedium",
        "bold": true
      }
    },
    {
      "id": "time_sub",
      "component": "Text",
      "properties": {
        "text": "Departure: Oct 20 • 11:45 AM (11h 15m nonstop)",
        "variant": "bodySmall"
      }
    },
    {
      "id": "passenger_card",
      "component": "Card",
      "children": ["passenger_fields"],
      "properties": { "variant": "outlined", "padding": 16 }
    },
    {
      "id": "passenger_fields",
      "component": "Column",
      "children": ["input_passenger"],
      "properties": { "spacing": 8 }
    },
    {
      "id": "input_passenger",
      "component": "TextField",
      "properties": {
        "dataKey": "passengerName",
        "label": "Passenger Full Name",
        "placeholder": "Enter name as on passport"
      }
    },
    {
      "id": "carbon_card",
      "component": "ComparisonRow",
      "properties": {
        "labelA": "This Route",
        "valueA": 480,
        "labelB": "Industry Average",
        "valueB": 690,
        "unit": "kg CO2e"
      }
    },
    {
      "id": "book_btn",
      "component": "Button",
      "properties": {
        "text": "Confirm & Reserve Seat ($840)",
        "variant": "filled",
        "fillMaxWidth": true,
        "icon": "check"
      },
      "action": {
        "name": "confirm_reservation",
        "parameters": {
          "flight": "JL004",
          "from": "SFO",
          "to": "HND"
        }
      }
    }
  ]
}
            """.trimIndent()
        ),

        A2UIPreset(
            id = "feedback_survey",
            name = "Interactive Form & Survey",
            description = "Form with text inputs, sliders, switches, and submission actions.",
            icon = "check",
            jsonContent = """
{
  "surfaceId": "feedback_form",
  "catalog": "https://a2ui.org/catalog/v1",
  "title": "Product Satisfaction Survey",
  "dataModel": {
    "rating": 8.0,
    "feedbackText": "The dynamic A2UI component generation is lightning fast!",
    "wantsFollowUp": true
  },
  "components": [
    {
      "id": "root",
      "component": "Column",
      "children": ["survey_card", "inputs_card", "btn_submit"],
      "properties": { "spacing": 12, "fillMaxWidth": true }
    },
    {
      "id": "survey_card",
      "component": "Card",
      "children": ["survey_header"],
      "properties": { "variant": "elevated", "padding": 16 }
    },
    {
      "id": "survey_header",
      "component": "Column",
      "children": ["survey_badge", "survey_title", "survey_desc"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "survey_badge",
      "component": "Badge",
      "properties": {
        "text": "Quick 1-min Survey",
        "status": "info"
      }
    },
    {
      "id": "survey_title",
      "component": "Text",
      "properties": {
        "text": "How is your A2UI Experience?",
        "variant": "titleMedium",
        "bold": true
      }
    },
    {
      "id": "survey_desc",
      "component": "Text",
      "properties": {
        "text": "Help us improve dynamic component rendering in Jetpack Compose.",
        "variant": "bodyMedium"
      }
    },
    {
      "id": "inputs_card",
      "component": "Card",
      "children": ["inputs_list"],
      "properties": { "variant": "outlined", "padding": 16 }
    },
    {
      "id": "inputs_list",
      "component": "Column",
      "children": ["rating_slider", "spacer_2", "input_comments", "spacer_3", "followup_switch"],
      "properties": { "spacing": 6 }
    },
    {
      "id": "rating_slider",
      "component": "Slider",
      "properties": {
        "dataKey": "rating",
        "label": "Overall Satisfaction (1-10)",
        "min": 1,
        "max": 10,
        "steps": 9
      }
    },
    {
      "id": "spacer_2",
      "component": "Spacer",
      "properties": { "height": 8 }
    },
    {
      "id": "input_comments",
      "component": "TextField",
      "properties": {
        "dataKey": "feedbackText",
        "label": "What do you like most?",
        "placeholder": "Type your thoughts..."
      }
    },
    {
      "id": "spacer_3",
      "component": "Spacer",
      "properties": { "height": 8 }
    },
    {
      "id": "followup_switch",
      "component": "Switch",
      "properties": {
        "dataKey": "wantsFollowUp",
        "label": "Join beta tester group",
        "subtitle": "Get early access to custom catalog components"
      }
    },
    {
      "id": "btn_submit",
      "component": "Button",
      "properties": {
        "text": "Submit Feedback",
        "variant": "filled",
        "fillMaxWidth": true,
        "icon": "check"
      },
      "action": {
        "name": "submit_survey",
        "parameters": {
          "source": "a2ui_studio"
        }
      }
    }
  ]
}
            """.trimIndent()
        )
    )

    fun getPreset(id: String): A2UIPreset {
        return presets.firstOrNull { it.id == id } ?: presets.first()
    }
}
