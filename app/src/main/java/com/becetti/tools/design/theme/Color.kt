package com.becetti.tools.design.theme

import androidx.compose.ui.graphics.Color

/* ------------------------------------------------------------------------------------------------
 * BECETTI TOOLS — premium palette (spec §2).
 * Dark is the primary identity; light is a first-class peer, not an afterthought.
 * ---------------------------------------------------------------------------------------------- */

// Dark theme surfaces
val BgDark = Color(0xFF0B1020)            // main background
val SurfaceDark = Color(0xFF111A2E)       // secondary background / nav bar
val CardDark = Color(0xFF172238)          // cards
val CardDarkElevated = Color(0xFF1D2B43)  // elevated cards
val BorderDark = Color(0xFF29364D)        // borders

// Light theme surfaces
val BgLight = Color(0xFFF5F7FC)
val SurfaceLight = Color(0xFFFFFFFF)
val CardLight = Color(0xFFFFFFFF)
val CardLightElevated = Color(0xFFF0F3FA)
val BorderLight = Color(0xFFE2E8F0)

// Brand accents
val AccentPrimary = Color(0xFF6C63FF)     // dark theme primary
val AccentSecondary = Color(0xFF00C2A8)   // secondary / success accent
val AccentHighlight = Color(0xFF38BDF8)   // highlight / info accent
val AccentPrimaryLight = Color(0xFF6257E8)
val AccentSecondaryLight = Color(0xFF0D9F8A)

// Text
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF172033)
val TextSecondaryLight = Color(0xFF64748B)

// Semantic
val ColorSuccess = Color(0xFF22C55E)
val ColorWarning = Color(0xFFF59E0B)
val ColorError = Color(0xFFEF4444)
val ColorInfo = Color(0xFF38BDF8)

/* ------------------------------------------------------------------------------------------------
 * Category accents — deliberately restrained: two brand accents + semantic hues used with intent.
 * ---------------------------------------------------------------------------------------------- */
object ToolAccents {
    val Developer = AccentPrimary            // #6C63FF
    val Projects = AccentSecondary           // #00C2A8
    val Files = AccentHighlight              // #38BDF8
    val Media = ColorWarning                 // #F59E0B
    val Network = Color(0xFF8B5CF6)          // violet, brand-adjacent
    val Business = ColorSuccess              // #22C55E
    val Everyday = Color(0xFFFB7185)         // rose
    val Utility = Color(0xFF64748B)
}

/* ------------------------------------------------------------------------------------------------
 * Legacy aliases — the v1.0 screens reference these names. They now resolve to the new identity so
 * every legacy screen adopts the premium palette without a rewrite.
 * ---------------------------------------------------------------------------------------------- */
val PrimaryOrange = AccentPrimary
val DarkOrange = Color(0xFF564DE0)
val LightOrange = Color(0xFFEDEBFF)
val DeepNavy = SurfaceDark
val SecondaryNavy = CardDark
val LightGray = BgLight
val BackgroundLight = BgLight
val TextDark = TextPrimaryLight
val TextGray = TextSecondaryLight
val Success = ColorSuccess
val Error = ColorError
val Border = BorderLight

val GlowOrange = AccentPrimary
val GlowAmber = ColorWarning
val GlowEmerald = AccentSecondary
val GlowCyan = AccentHighlight
val GlowBlue = Color(0xFF3B82F6)
val GlowIndigo = Color(0xFF6366F1)
val GlowViolet = Color(0xFFA855F7)
val GlowRose = Color(0xFFF43F5E)
val GlowTeal = Color(0xFF14B8A6)
val GlowPink = Color(0xFFEC4899)
