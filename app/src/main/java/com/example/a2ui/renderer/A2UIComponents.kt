package com.example.a2ui.renderer

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2ui.model.A2UIAction
import com.example.a2ui.model.A2UIComponent
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentInfo
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning

/**
 * Utility to parse color hex strings or semantic tokens into Compose Color.
 */
fun parseColorString(colorStr: String?, defaultColor: Color): Color {
    if (colorStr.isNullOrBlank()) return defaultColor
    val trimmed = colorStr.trim().lowercase()

    return when (trimmed) {
        "primary" -> Color(0xFF6366F1)
        "secondary" -> Color(0xFF06B6D4)
        "tertiary" -> Color(0xFF8B5CF6)
        "success" -> AccentSuccess
        "warning" -> AccentWarning
        "error" -> AccentError
        "info" -> AccentInfo
        "onsurface" -> Color(0xFF0F172A)
        "onsurfacevariant" -> Color(0xFF64748B)
        "white" -> Color.White
        "black" -> Color.Black
        else -> {
            try {
                if (trimmed.startsWith("#")) {
                    val hex = trimmed.removePrefix("#")
                    if (hex.length == 6) {
                        Color(android.graphics.Color.parseColor("#$hex"))
                    } else if (hex.length == 8) {
                        Color(android.graphics.Color.parseColor("#$hex"))
                    } else {
                        defaultColor
                    }
                } else {
                    defaultColor
                }
            } catch (e: Exception) {
                defaultColor
            }
        }
    }
}

/**
 * Maps a string icon name to a Material ImageVector.
 */
fun mapIconName(name: String?): ImageVector {
    return when (name?.lowercase()?.trim()) {
        "eco", "nature", "leaf" -> Icons.Default.Eco
        "energy", "bolt", "electric", "lightbulb" -> Icons.Default.Lightbulb
        "flight", "airplane", "plane" -> Icons.Default.Flight
        "check", "check_circle", "done" -> Icons.Default.Check
        "warning", "alert" -> Icons.Default.Warning
        "info", "help" -> Icons.Default.Info
        "trending_up", "trend_up" -> Icons.AutoMirrored.Filled.TrendingUp
        "trending_down", "trend_down" -> Icons.AutoMirrored.Filled.TrendingDown
        "refresh", "sync" -> Icons.Default.Refresh
        "settings", "tune" -> Icons.Default.Settings
        "cloud", "weather" -> Icons.Default.Cloud
        "water", "drop" -> Icons.Default.WaterDrop
        "search" -> Icons.Default.Search
        "arrow_forward", "forward" -> Icons.AutoMirrored.Filled.ArrowForward
        else -> Icons.Default.Info
    }
}

// ------------------------------------------------------------------------
// Standard Layout Composables
// ------------------------------------------------------------------------

@Composable
fun RenderA2UIColumn(
    component: A2UIComponent,
    modifier: Modifier = Modifier,
    renderChildren: @Composable () -> Unit
) {
    val spacing = component.getIntProperty("spacing", 8).dp
    val padding = component.getIntProperty("padding", 0).dp
    val fillMaxWidth = component.getBooleanProperty("fillMaxWidth", true)
    val alignmentStr = component.getStringProperty("horizontalAlignment", "Start")

    val alignment = when (alignmentStr.lowercase()) {
        "center", "centerhorizontally" -> Alignment.CenterHorizontally
        "end" -> Alignment.End
        else -> Alignment.Start
    }

    Column(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .padding(padding)
            .testTag("a2ui_column_${component.id}"),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = alignment
    ) {
        renderChildren()
    }
}

@Composable
fun RenderA2UIRow(
    component: A2UIComponent,
    modifier: Modifier = Modifier,
    renderChildren: @Composable () -> Unit
) {
    val spacing = component.getIntProperty("spacing", 8).dp
    val padding = component.getIntProperty("padding", 0).dp
    val fillMaxWidth = component.getBooleanProperty("fillMaxWidth", true)
    val alignmentStr = component.getStringProperty("verticalAlignment", "CenterVertically")

    val verticalAlignment = when (alignmentStr.lowercase()) {
        "top" -> Alignment.Top
        "bottom" -> Alignment.Bottom
        else -> Alignment.CenterVertically
    }

    // Use IntrinsicSize.Min on height to solve card height alignment issues highlighted in John O'Reilly's post
    Row(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(IntrinsicSize.Min)
            .padding(padding)
            .testTag("a2ui_row_${component.id}"),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = verticalAlignment
    ) {
        renderChildren()
    }
}

@Composable
fun RenderA2UICard(
    component: A2UIComponent,
    modifier: Modifier = Modifier,
    renderChildren: @Composable () -> Unit
) {
    val variant = component.getStringProperty("variant", "filled").lowercase()
    val padding = component.getIntProperty("padding", 16).dp
    val cornerRadius = component.getIntProperty("cornerRadius", 16).dp
    val bgHex = component.properties["backgroundColor"]?.toString()
    val shape = RoundedCornerShape(cornerRadius)

    when (variant) {
        "elevated" -> {
            ElevatedCard(
                shape = shape,
                modifier = modifier
                    .fillMaxWidth()
                    .testTag("a2ui_card_${component.id}"),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (bgHex != null) parseColorString(bgHex, MaterialTheme.colorScheme.surfaceContainerHighest) else MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(padding)) {
                    renderChildren()
                }
            }
        }
        "outlined" -> {
            OutlinedCard(
                shape = shape,
                modifier = modifier
                    .fillMaxWidth()
                    .testTag("a2ui_card_${component.id}"),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(padding)) {
                    renderChildren()
                }
            }
        }
        else -> {
            Card(
                shape = shape,
                modifier = modifier
                    .fillMaxWidth()
                    .testTag("a2ui_card_${component.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (bgHex != null) parseColorString(bgHex, MaterialTheme.colorScheme.surfaceVariant) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(padding)) {
                    renderChildren()
                }
            }
        }
    }
}

@Composable
fun RenderA2UIBox(
    component: A2UIComponent,
    modifier: Modifier = Modifier,
    renderChildren: @Composable () -> Unit
) {
    val fillMaxWidth = component.getBooleanProperty("fillMaxWidth", true)
    Box(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .testTag("a2ui_box_${component.id}"),
        contentAlignment = Alignment.Center
    ) {
        renderChildren()
    }
}

// ------------------------------------------------------------------------
// Display Composables
// ------------------------------------------------------------------------

@Composable
fun RenderA2UIText(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val text = component.getStringProperty("text", "")
    val variant = component.getStringProperty("variant", "bodyMedium").lowercase()
    val isBold = component.getBooleanProperty("bold", false)
    val maxLinesVal = component.getIntProperty("maxLines", Int.MAX_VALUE).let { if (it <= 0) Int.MAX_VALUE else it }
    val colorHex = component.properties["color"]?.toString()
    val textAlignStr = component.getStringProperty("textAlign", "Start").lowercase()

    val textAlign = when (textAlignStr) {
        "center" -> TextAlign.Center
        "end" -> TextAlign.End
        "justify" -> TextAlign.Justify
        else -> TextAlign.Start
    }

    val style = when (variant) {
        "displaylarge" -> MaterialTheme.typography.displayLarge
        "headlinemedium" -> MaterialTheme.typography.headlineMedium
        "headlinesmall" -> MaterialTheme.typography.headlineSmall
        "titlelarge" -> MaterialTheme.typography.titleLarge
        "titlemedium" -> MaterialTheme.typography.titleMedium
        "bodylarge" -> MaterialTheme.typography.bodyLarge
        "labelmedium" -> MaterialTheme.typography.labelMedium
        "labelsmall" -> MaterialTheme.typography.labelSmall
        else -> MaterialTheme.typography.bodyMedium
    }

    val defaultTextColor = when (variant) {
        "labelsmall", "bodysmall" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    val finalColor = parseColorString(colorHex, defaultTextColor)

    Text(
        text = text,
        modifier = modifier.testTag("a2ui_text_${component.id}"),
        style = style,
        fontWeight = if (isBold) FontWeight.Bold else style.fontWeight,
        color = finalColor,
        textAlign = textAlign,
        maxLines = maxLinesVal,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun RenderA2UIIcon(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val iconName = component.getStringProperty("icon", "info")
    val size = component.getIntProperty("size", 24).dp
    val tintHex = component.properties["tint"]?.toString()
    val tintColor = parseColorString(tintHex, MaterialTheme.colorScheme.primary)

    Icon(
        imageVector = mapIconName(iconName),
        contentDescription = iconName,
        modifier = modifier
            .size(size)
            .testTag("a2ui_icon_${component.id}"),
        tint = tintColor
    )
}

@Composable
fun RenderA2UIBadge(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val text = component.getStringProperty("text", "")
    val status = component.getStringProperty("status", "neutral").lowercase()
    val iconName = component.properties["icon"]?.toString()

    val (bg, fg) = when (status) {
        "success" -> AccentSuccess.copy(alpha = 0.16f) to AccentSuccess
        "warning" -> AccentWarning.copy(alpha = 0.16f) to AccentWarning
        "error" -> AccentError.copy(alpha = 0.16f) to AccentError
        "info" -> AccentInfo.copy(alpha = 0.16f) to AccentInfo
        else -> MaterialTheme.colorScheme.surfaceContainerHighest to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier.testTag("a2ui_badge_${component.id}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (!iconName.isNullOrBlank()) {
                Icon(
                    imageVector = mapIconName(iconName),
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RenderA2UIDivider(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val thickness = component.getIntProperty("thickness", 1).dp
    val colorHex = component.properties["color"]?.toString()
    val color = parseColorString(colorHex, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_divider_${component.id}"),
        thickness = thickness,
        color = color
    )
}

@Composable
fun RenderA2UISpacer(
    component: A2UIComponent,
    modifier: Modifier = Modifier
) {
    val height = component.getIntProperty("height", 16).dp
    val width = component.getIntProperty("width", 0).dp

    Spacer(
        modifier = modifier
            .height(height)
            .width(width)
            .testTag("a2ui_spacer_${component.id}")
    )
}

// ------------------------------------------------------------------------
// Controls & Input Composables
// ------------------------------------------------------------------------

@Composable
fun RenderA2UIButton(
    component: A2UIComponent,
    modifier: Modifier = Modifier,
    onAction: (A2UIAction) -> Unit
) {
    val text = component.getStringProperty("text", "Submit")
    val variant = component.getStringProperty("variant", "filled").lowercase()
    val iconName = component.properties["icon"]?.toString()
    val enabled = component.getBooleanProperty("enabled", true)
    val fillMaxWidth = component.getBooleanProperty("fillMaxWidth", false)

    val onClick = {
        component.action?.let { onAction(it) } ?: onAction(
            A2UIAction(name = "button_click", parameters = mapOf("componentId" to component.id))
        )
    }

    val buttonModifier = modifier
        .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
        .testTag("a2ui_button_${component.id}")

    when (variant) {
        "outlined" -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier
            ) {
                ButtonContent(iconName, text)
            }
        }
        "tonal" -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier,
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                ButtonContent(iconName, text)
            }
        }
        "text" -> {
            TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier
            ) {
                ButtonContent(iconName, text)
            }
        }
        "elevated" -> {
            ElevatedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier
            ) {
                ButtonContent(iconName, text)
            }
        }
        else -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = buttonModifier
            ) {
                ButtonContent(iconName, text)
            }
        }
    }
}

@Composable
private fun ButtonContent(iconName: String?, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!iconName.isNullOrBlank()) {
            Icon(
                imageVector = mapIconName(iconName),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RenderA2UITextField(
    component: A2UIComponent,
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = component.getStringProperty("label", "")
    val placeholder = component.getStringProperty("placeholder", "")
    val leadingIcon = component.properties["leadingIcon"]?.toString()
    val singleLine = component.getBooleanProperty("singleLine", true)

    OutlinedTextField(
        value = currentValue,
        onValueChange = onValueChange,
        label = if (label.isNotEmpty()) { { Text(label) } } else null,
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        leadingIcon = if (!leadingIcon.isNullOrBlank()) {
            { Icon(imageVector = mapIconName(leadingIcon), contentDescription = null) }
        } else null,
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_textfield_${component.id}")
    )
}

@Composable
fun RenderA2UISlider(
    component: A2UIComponent,
    currentValue: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = component.getStringProperty("label", "Value")
    val minVal = component.getFloatProperty("min", 0f)
    val maxVal = component.getFloatProperty("max", 100f)
    val unit = component.getStringProperty("unit", "")
    val steps = component.getIntProperty("steps", 0)

    val safeVal = currentValue.coerceIn(minVal, maxVal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_slider_${component.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${"%.1f".format(safeVal)}$unit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = safeVal,
            onValueChange = onValueChange,
            valueRange = minVal..maxVal,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun RenderA2UISwitch(
    component: A2UIComponent,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = component.getStringProperty("label", "")
    val subtitle = component.properties["subtitle"]?.toString()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("a2ui_switch_${component.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
