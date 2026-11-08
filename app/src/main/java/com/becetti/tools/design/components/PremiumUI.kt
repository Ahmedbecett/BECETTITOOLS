package com.becetti.tools.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.becetti.tools.design.theme.*
import kotlinx.coroutines.delay

/* ================================================================================================
 * BECETTI TOOLS — shared UI kit.
 * One place for surfaces, inputs, results, states and motion so 60+ tool screens stay visually
 * identical and every screen feels like part of one application (spec §4).
 * ============================================================================================== */

private val CardShape = RoundedCornerShape(20.dp)
private val InnerShape = RoundedCornerShape(14.dp)

/** Gentle press feedback used by every tappable card (spec §3). */
@Composable
fun Modifier.pressScale(interactionSource: MutableInteractionSource, pressed: Float = 0.975f): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressed else 1f,
        animationSpec = tween(motionDuration(Motion.Fast)),
        label = "pressScale"
    )
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}

/**
 * Staggered entrance for cards/sections: 14dp rise + fade, 240ms, index-based delay.
 * Instant when reduced motion is on.
 */
@Composable
fun Modifier.entrance(index: Int = 0): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(motionDelay(index.coerceAtMost(12) * Motion.Stagger).toLong())
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(motionDuration(Motion.Base)),
        label = "entranceAlpha"
    )
    val drift by animateFloatAsState(
        targetValue = if (visible) 0f else 14f,
        animationSpec = tween(motionDuration(Motion.Base)),
        label = "entranceDrift"
    )
    return this.graphicsLayer { this.alpha = alpha; translationY = drift * density }
}

/** Page shell for a tool: scrollable body + icon header + optional one-line instruction. */
@Composable
fun ToolPage(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color = MaterialTheme.colorScheme.primary,
    hint: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = BecettiTheme.palette
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.entrance(0),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.16f))
                    .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (hint != null) {
            InfoBanner(text = hint, accent = accent, icon = BecettiGlyphs.Bolt)
        }
        content()
        Spacer(Modifier.height(6.dp))
    }
}

/** Standard surface: 20dp radius, hairline accent border, card colour from the active palette. */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    showAccent: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = BecettiTheme.palette
    val interaction = remember { MutableInteractionSource() }
    var mod = modifier.fillMaxWidth()
    if (onClick != null) {
        mod = mod
            .pressScale(interaction)
            .clip(CardShape)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
    }
    Surface(
        modifier = mod,
        shape = CardShape,
        color = palette.card,
        border = BorderStroke(1.dp, if (showAccent) accent.copy(alpha = 0.22f) else palette.border)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

/* ------------------------------------------------------------------------------------------------
 * Sections, headers and labels
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val palette = BecettiTheme.palette
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
                Text(actionLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun SoftDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BecettiTheme.palette.border.copy(alpha = 0.7f))
    )
}

@Composable
fun InfoBanner(text: String, accent: Color = MaterialTheme.colorScheme.primary, icon: ImageVector? = null) {
    val palette = BecettiTheme.palette
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(InnerShape)
            .background(accent.copy(alpha = if (palette.isDark) 0.10f else 0.08f))
            .border(1.dp, accent.copy(alpha = 0.25f), InnerShape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
        }
        Text(text, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary, modifier = Modifier.weight(1f))
    }
}

/* ------------------------------------------------------------------------------------------------
 * Inputs
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = BecettiTheme.palette.textPrimary)
        if (trailing != null) {
            Text(trailing, style = MaterialTheme.typography.labelMedium, color = BecettiTheme.palette.textSecondary)
        }
    }
}

@Composable
fun BecettiField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 12,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    isError: Boolean = false,
    supporting: String? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    val palette = BecettiTheme.palette
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = placeholder?.let {
                { Text(it, style = MaterialTheme.typography.bodyMedium, color = palette.textSecondary.copy(alpha = 0.75f)) }
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            enabled = enabled,
            isError = isError,
            shape = InnerShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent,
                unfocusedBorderColor = palette.border,
                cursorColor = accent,
                focusedTextColor = palette.textPrimary,
                unfocusedTextColor = palette.textPrimary
            )
        )
        if (supporting != null) {
            Text(
                supporting,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError) palette.error else palette.textSecondary
            )
        }
    }
}

/* ------------------------------------------------------------------------------------------------
 * Buttons
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    outlined: Boolean = false,
    fillWidth: Boolean = true
) {
    val palette = BecettiTheme.palette
    val shape = InnerShape
    val content: @Composable RowScope.() -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = if (outlined) accent else Color.White,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = if (fillWidth) modifier.fillMaxWidth() else modifier,
            enabled = enabled && !loading,
            shape = shape,
            border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = accent)
        ) { content() }
    } else {
        Button(
            onClick = onClick,
            modifier = if (fillWidth) modifier.fillMaxWidth() else modifier,
            enabled = enabled && !loading,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = accent,
                contentColor = Color.White,
                disabledContainerColor = accent.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) { content() }
    }
}

/** Compact icon action used inside result panels (copy / share / save). */
@Composable
fun IconAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    accent: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    val palette = BecettiTheme.palette
    Surface(
        shape = RoundedCornerShape(11.dp),
        color = if (enabled) accent.copy(alpha = 0.13f) else palette.chipSurface,
        border = BorderStroke(1.dp, if (enabled) accent.copy(alpha = 0.3f) else palette.border),
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (enabled) accent else palette.textSecondary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) accent else palette.textSecondary
            )
        }
    }
}

/* ------------------------------------------------------------------------------------------------
 * Result presentation
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun ResultPanel(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
    mono: Boolean = true,
    maxHeight: Dp = 320.dp,
    onCopy: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    copyLabel: String = "Copy",
    shareLabel: String = "Share",
    saveLabel: String = "Save"
) {
    val palette = BecettiTheme.palette
    Panel(modifier = modifier, accent = accent) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (onCopy != null) IconAction(BecettiGlyphs.CopyStack, copyLabel, onCopy, accent)
                if (onSave != null) IconAction(BecettiGlyphs.FolderOpen, saveLabel, onSave, accent)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .clip(InnerShape)
                .background(if (palette.isDark) Color(0xFF0A1224) else Color(0xFFF7F9FE))
                .border(1.dp, palette.border, InnerShape)
                .padding(12.dp)
        ) {
            Text(
                text = text,
                style = if (mono) MonoTextStyle else MaterialTheme.typography.bodyMedium,
                color = palette.textPrimary,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
        if (onShare != null) {
            IconAction(BecettiGlyphs.SwapArrows, shareLabel, onShare, accent)
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    caption: String? = null
) {
    val palette = BecettiTheme.palette
    Column(
        modifier
            .clip(InnerShape)
            .background(accent.copy(alpha = if (palette.isDark) 0.10f else 0.08f))
            .border(1.dp, accent.copy(alpha = 0.22f), InnerShape)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary, maxLines = 1)
        Spacer(Modifier.height(3.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (caption != null) {
            Text(caption, style = MaterialTheme.typography.labelSmall, color = accent, maxLines = 1)
        }
    }
}

@Composable
fun KeyValueRow(label: String, value: String, mono: Boolean = false, accent: Color? = null) {
    val palette = BecettiTheme.palette
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = if (mono) MonoTextStyle else MaterialTheme.typography.bodySmall,
            color = accent ?: palette.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
    }
}

/* ------------------------------------------------------------------------------------------------
 * Choice chips
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun ChipSelector(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (id, label) ->
            val isSelected = id == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(id) },
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(11.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent.copy(alpha = 0.18f),
                    selectedLabelColor = accent,
                    labelColor = BecettiTheme.palette.textSecondary,
                    containerColor = BecettiTheme.palette.chipSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = BecettiTheme.palette.border,
                    selectedBorderColor = accent.copy(alpha = 0.55f)
                )
            )
        }
    }
}

/* ------------------------------------------------------------------------------------------------
 * Empty / loading / success states
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: ImageVector = BecettiGlyphs.FolderOpen,
    accent: Color = MaterialTheme.colorScheme.primary,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val palette = BecettiTheme.palette
    val float = if (LocalReduceMotion.current) 0f else 1f
    val pulse by animateFloatAsState(
        targetValue = if (float > 0f) 1f else 0.94f,
        animationSpec = tween(motionDuration(600)),
        label = "emptyPulse"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.10f))
                .border(1.dp, accent.copy(alpha = 0.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent.copy(alpha = 0.85f), modifier = Modifier.size(34.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary, textAlign = TextAlign.Center)
        Text(
            message,
            style = MaterialTheme.typography.bodySmall,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(2.dp))
            ActionButton(actionLabel, onAction, modifier = Modifier.widthIn(max = 260.dp), accent = accent, fillWidth = false)
        }
    }
}

/** Real progress for real work — never shown for decoration. */
@Composable
fun LoadingBlock(text: String, progress: Float? = null, accent: Color = MaterialTheme.colorScheme.primary) {
    val palette = BecettiTheme.palette
    Column(
        Modifier
            .fillMaxWidth()
            .clip(InnerShape)
            .background(accent.copy(alpha = 0.07f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = accent, strokeWidth = 2.dp)
            Text(text, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
        }
        if (progress != null) {
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = accent,
                backgroundColor = accent.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
fun SuccessTick(visible: Boolean, message: String, accent: Color = BecettiTheme.palette.success) {
    AnimatedVisibility(visible = visible) {
        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "tickScale"
        )
        Row(
            Modifier
                .fillMaxWidth()
                .clip(InnerShape)
                .background(accent.copy(alpha = 0.12f))
                .border(1.dp, accent.copy(alpha = 0.35f), InnerShape)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(20.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(BecettiGlyphs.ShieldCheck, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
            Text(message, style = MaterialTheme.typography.bodySmall, color = accent)
        }
    }
}

/* ------------------------------------------------------------------------------------------------
 * Dialogs & sheets
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = CardShape,
        containerColor = BecettiTheme.palette.card,
        title = { Text(title, style = MaterialTheme.typography.titleMedium, color = BecettiTheme.palette.textPrimary) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, color = BecettiTheme.palette.textSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel, color = accent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel, color = BecettiTheme.palette.textSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecettiSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!show) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BecettiTheme.palette.card,
        contentColor = BecettiTheme.palette.textPrimary,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BecettiTheme.palette.border)
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = BecettiTheme.palette.textPrimary)
            content()
        }
    }
}

/* ------------------------------------------------------------------------------------------------
 * Rich header used by the dashboard & category pages
 * ---------------------------------------------------------------------------------------------- */

@Composable
fun HeroBanner(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val palette = BecettiTheme.palette
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = if (palette.isDark) palette.cardElevated else palette.card,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = if (palette.isDark) 0.20f else 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall, color = palette.textPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                }
                if (trailing != null) trailing()
            }
            content()
        }
    }
}
