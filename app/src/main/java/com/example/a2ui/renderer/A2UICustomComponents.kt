package com.example.a2ui.renderer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2ui.model.A2UIComponent
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.SecondaryCyan

/**
 * Domain-specific EmissionsChart component as featured in John O'Reilly's A2UI Compose post.
 * Renders categorized greenhouse gas emissions with animated bars, sector breakdowns,
 * and Mt CO2e values.
 */
@Composable
fun RenderA2UIEmissionsChart(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val title = component.getStringProperty("title", "Emissions Breakdown")
    val unit = component.getStringProperty("unit", "Mt CO2e")
    val colorHex = component.properties["color"]?.toString()
    val baseColor = parseColorString(colorHex, SecondaryCyan)

    val rawCategories = component.getListProperty("categories")
    val rawValues = component.getListProperty("values")

    val categories = rawCategories.map { it.toString() }
    val values = rawValues.mapNotNull {
        when (it) {
            is Number -> it.toFloat()
            is String -> it.toFloatOrNull()
            else -> null
        }
    }

    val maxVal = values.maxOrNull()?.coerceAtLeast(1f) ?: 100f
    val totalVal = values.sum()

    // Palette for distinct sector bars
    val sectorColors = listOf(
        Color(0xFF06B6D4), // Cyan - Power
        Color(0xFF6366F1), // Indigo - Manufacturing
        Color(0xFFF59E0B), // Amber - Transport
        Color(0xFF10B981), // Emerald - Agriculture
        Color(0xFFEC4899), // Pink - Waste
        Color(0xFF8B5CF6)  // Violet - Other
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_emissions_chart_${component.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total: ${"%.1f".format(totalVal)} $unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = baseColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Climate TRACE",
                        color = baseColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Render each category bar
            categories.forEachIndexed { index, category ->
                val valItem = values.getOrNull(index) ?: 0f
                val targetFraction = (valItem / maxVal).coerceIn(0.05f, 1f)
                val percentage = if (totalVal > 0) (valItem / totalVal) * 100f else 0f
                val color = sectorColors.getOrElse(index % sectorColors.size) { baseColor }

                var animated by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { animated = true }
                val animFraction by animateFloatAsState(
                    targetValue = if (animated) targetFraction else 0f,
                    animationSpec = tween(durationMillis = 600 + index * 100),
                    label = "bar_anim_$index"
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${"%.1f".format(valItem)} $unit (${"%.0f".format(percentage)}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Progress Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animFraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(color.copy(alpha = 0.8f), color)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * MetricCard component for high-impact KPI presentation with trend indicators.
 */
@Composable
fun RenderA2UIMetricCard(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val title = component.getStringProperty("title", "Metric")
    val value = component.getStringProperty("value", "0")
    val unit = component.getStringProperty("unit", "")
    val delta = component.properties["delta"]?.toString()
    val trend = component.getStringProperty("trend", "neutral").lowercase()
    val iconName = component.properties["icon"]?.toString()
    val accentHex = component.properties["accentColor"]?.toString()

    val accentColor = parseColorString(accentHex, MaterialTheme.colorScheme.primary)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_metric_card_${component.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                if (!iconName.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = mapIconName(iconName),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            if (!delta.isNullOrBlank()) {
                val trendColor = when (trend) {
                    "down" -> AccentSuccess // For emissions, reduction is positive!
                    "up" -> AccentError
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when (trend) {
                        "down" -> Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = "Trending Down",
                            tint = trendColor,
                            modifier = Modifier.size(16.dp)
                        )
                        "up" -> Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Trending Up",
                            tint = trendColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = delta,
                        style = MaterialTheme.typography.labelSmall,
                        color = trendColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * ComparisonRow visual component for comparing two values.
 */
@Composable
fun RenderA2UIComparisonRow(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val labelA = component.getStringProperty("labelA", "Item A")
    val valueA = component.getFloatProperty("valueA", 50f)
    val labelB = component.getStringProperty("labelB", "Item B")
    val valueB = component.getFloatProperty("valueB", 50f)
    val unit = component.getStringProperty("unit", "")

    val total = (valueA + valueB).coerceAtLeast(1f)
    val ratioA = (valueA / total).coerceIn(0.1f, 0.9f)
    val ratioB = 1f - ratioA

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_comparison_${component.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$labelA: ${"%.0f".format(valueA)} $unit",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$labelB: ${"%.0f".format(valueB)} $unit",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SecondaryCyan
                )
            }

            // Dual colored ratio bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(ratioA)
                        .height(10.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Box(
                    modifier = Modifier
                        .weight(ratioB)
                        .height(10.dp)
                        .background(SecondaryCyan)
                )
            }
        }
    }
}
