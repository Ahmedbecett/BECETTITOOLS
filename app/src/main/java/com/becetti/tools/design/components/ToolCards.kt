package com.becetti.tools.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.domain.models.ToolGroup
import com.becetti.tools.domain.models.ToolSpec

/**
 * The one tool card used everywhere (home, category pages, search results, favorites).
 * Consistent radius/border/typography — the visual glue of the whole app.
 */
@Composable
fun ToolCard(
    spec: ToolSpec,
    strings: AppStrings,
    favorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    showFavorite: Boolean = true
) {
    val palette = BecettiTheme.palette
    val accent = spec.group.accent
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = palette.card,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.20f))
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f))
                    .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(spec.icon, contentDescription = null, tint = accent, modifier = Modifier.size(21.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    strings.toolName(spec.id),
                    style = MaterialTheme.typography.titleSmall,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    strings.toolDescription(spec.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showFavorite) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = if (favorite) strings.gFavoriteRemove else strings.gFavoriteAdd,
                        tint = if (favorite) accent else palette.textSecondary.copy(alpha = 0.55f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/** Compact chip-shaped shortcut used in horizontal "recently used" / "favorites" rows. */
@Composable
fun ToolChip(
    spec: ToolSpec,
    strings: AppStrings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = BecettiTheme.palette
    val accent = spec.group.accent
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .pressScale(interaction, 0.96f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = palette.chipSurface,
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(spec.icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(
                strings.toolName(spec.id),
                style = MaterialTheme.typography.labelMedium,
                color = palette.textPrimary,
                maxLines = 1
            )
        }
    }
}

/** Large accent card for the featured carousel. */
@Composable
fun FeaturedToolCard(
    spec: ToolSpec,
    strings: AppStrings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = BecettiTheme.palette
    val accent = spec.group.accent
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .width(190.dp)
            .pressScale(interaction)
            .clip(RoundedCornerShape(20.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardElevated,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.32f))
    ) {
        Column(
            Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = if (palette.isDark) 0.22f else 0.14f), Color.Transparent)
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(spec.icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Text(
                strings.toolName(spec.id),
                style = MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                strings.toolDescription(spec.id),
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Category card for the home grid and the tools hub. */
@Composable
fun CategoryCard(
    group: ToolGroup,
    strings: AppStrings,
    count: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val palette = BecettiTheme.palette
    val accent = group.accent
    val interaction = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, 0.97f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = palette.card,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f))
    ) {
        Column(
            Modifier
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = if (palette.isDark) 0.16f else 0.10f), Color.Transparent)
                    )
                )
                .padding(if (compact) 12.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 32.dp else 36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(group.icon, contentDescription = null, tint = accent, modifier = Modifier.size(if (compact) 17.dp else 19.dp))
                }
                Text(
                    "$count",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent.copy(alpha = 0.9f)
                )
            }
            Text(
                strings.groupName(group.id),
                style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleSmall,
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!compact) {
                Text(
                    strings.groupDescription(group.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
