package com.example.a2ui.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.a2ui.model.A2UIAction
import com.example.a2ui.model.A2UIComponent
import com.example.a2ui.model.A2UISurface

/**
 * Main dynamic A2UI (Agent-to-UI) Compose Renderer.
 * Traverses the component adjacency list starting from the root component,
 * rendering native Jetpack Compose Material 3 components dynamically.
 */
@Composable
fun A2UIRenderer(
    surface: A2UISurface,
    onAction: (A2UIAction) -> Unit,
    onDataChange: (key: String, value: Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    val rootComponent = surface.rootComponent

    if (rootComponent == null) {
        EmptySurfacePlaceholder(surface.surfaceId)
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_surface_${surface.surfaceId}")
    ) {
        RenderComponentNode(
            componentId = surface.rootComponentId,
            surface = surface,
            visitedIds = emptySet(),
            onAction = onAction,
            onDataChange = onDataChange,
            modifier = Modifier
        )
    }
}

@Composable
fun RenderComponentNode(
    componentId: String,
    surface: A2UISurface,
    visitedIds: Set<String>,
    onAction: (A2UIAction) -> Unit,
    onDataChange: (key: String, value: Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cycle detection guard
    if (componentId in visitedIds) {
        CycleWarning(componentId)
        return
    }

    val component = surface.components[componentId]
    if (component == null) {
        MissingComponentWarning(componentId)
        return
    }

    val nextVisited = visitedIds + componentId

    // Helper to render all children in order
    val renderChildrenContent: @Composable () -> Unit = {
        for (childId in component.children) {
            val childComp = surface.components[childId]
            val childModifier = if (childComp?.weight != null && childComp.weight > 0f) {
                Modifier // Weights are applied by parent Row/Column directly if needed
            } else {
                Modifier
            }
            RenderComponentNode(
                componentId = childId,
                surface = surface,
                visitedIds = nextVisited,
                onAction = onAction,
                onDataChange = onDataChange,
                modifier = childModifier
            )
        }
    }

    // Dynamic Dispatcher across Catalog components
    when (component.component.lowercase().trim()) {
        // Layouts
        "column" -> RenderA2UIColumn(component, modifier, renderChildrenContent)
        "row" -> RenderA2UIRow(component, modifier, renderChildrenContent)
        "card" -> RenderA2UICard(component, modifier, renderChildrenContent)
        "box" -> RenderA2UIBox(component, modifier, renderChildrenContent)

        // Primitives & Display
        "text" -> RenderA2UIText(component, modifier)
        "icon" -> RenderA2UIIcon(component, modifier)
        "badge" -> RenderA2UIBadge(component, modifier)
        "divider" -> RenderA2UIDivider(component, modifier)
        "spacer" -> RenderA2UISpacer(component, modifier)

        // Controls & Input
        "button" -> RenderA2UIButton(component, modifier, onAction)
        "textfield" -> {
            val dataKey = component.getStringProperty("dataKey", component.id)
            val currentVal = surface.dataModel[dataKey]?.toString() ?: ""
            RenderA2UITextField(
                component = component,
                currentValue = currentVal,
                onValueChange = { onDataChange(dataKey, it) },
                modifier = modifier
            )
        }
        "slider" -> {
            val dataKey = component.getStringProperty("dataKey", component.id)
            val currentVal = when (val v = surface.dataModel[dataKey]) {
                is Number -> v.toFloat()
                is String -> v.toFloatOrNull() ?: 50f
                else -> 50f
            }
            RenderA2UISlider(
                component = component,
                currentValue = currentVal,
                onValueChange = { onDataChange(dataKey, it) },
                modifier = modifier
            )
        }
        "switch" -> {
            val dataKey = component.getStringProperty("dataKey", component.id)
            val currentVal = when (val v = surface.dataModel[dataKey]) {
                is Boolean -> v
                is String -> v.toBooleanStrictOrNull() ?: false
                else -> false
            }
            RenderA2UISwitch(
                component = component,
                checked = currentVal,
                onCheckedChange = { onDataChange(dataKey, it) },
                modifier = modifier
            )
        }

        // Custom Domain Components (ClimateTrace & Analytics)
        "emissionschart", "barchart", "chart" -> RenderA2UIEmissionsChart(component, modifier)
        "metriccard", "kpicard" -> RenderA2UIMetricCard(component, modifier)
        "comparisonrow" -> RenderA2UIComparisonRow(component, modifier)

        // Fallback for unknown catalog components
        else -> UnknownComponentCard(component, modifier)
    }
}

@Composable
private fun EmptySurfacePlaceholder(surfaceId: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("a2ui_empty_surface")
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "Empty Surface ($surfaceId)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "No 'root' component found in this A2UI adjacency list.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CycleWarning(componentId: String) {
    Text(
        text = "⚠ Cyclic reference detected at id='$componentId'",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
private fun MissingComponentWarning(componentId: String) {
    Text(
        text = "⚠ Missing child component with id='$componentId'",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
private fun UnknownComponentCard(component: A2UIComponent, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = "Unknown Catalog Component: '${component.component}'",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "ID: ${component.id} • Available in Catalog? Check Catalog Specs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
