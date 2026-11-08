package com.becetti.tools.design.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.becetti.tools.design.theme.*

/**
 * Custom vector icons and glowing accent colors for all 18 BECETTI TOOLS,
 * categories, settings, and privacy sections — zero dependency on `material-icons-extended`.
 */
object BecettiIcons {

    private fun buildIcon(name: String, block: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero,
                pathBuilder = block
            )
        }.build()
    }

    val Category: ImageVector by lazy {
        buildIcon("Category") {
            moveTo(12f, 2f); lineToRelative(-5.5f, 9f); horizontalLineToRelative(11f); close()
            moveTo(17.5f, 13f)
            curveToRelative(-2.49f, 0f, -4.5f, 2.01f, -4.5f, 4.5f)
            reflectiveCurveToRelative(2.01f, 4.5f, 4.5f, 4.5f)
            reflectiveCurveToRelative(4.5f, -2.01f, 4.5f, -4.5f)
            reflectiveCurveToRelative(-2.01f, -4.5f, -4.5f, -4.5f); close()
            moveTo(3f, 13.5f); horizontalLineToRelative(8f); verticalLineToRelative(8f); horizontalLineTo(3f); close()
        }
    }

    val History: ImageVector by lazy {
        buildIcon("History") {
            moveTo(13f, 3f)
            curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
            horizontalLineTo(1f); lineToRelative(3.89f, 3.89f); lineToRelative(0.07f, 0.14f)
            lineTo(9f, 12f); horizontalLineTo(6f)
            curveToRelative(0f, -3.87f, 3.13f, -7f, 7f, -7f)
            reflectiveCurveToRelative(7f, 3.13f, 7f, 7f)
            reflectiveCurveToRelative(-3.13f, 7f, -7f, 7f)
            curveToRelative(-1.93f, 0f, -3.68f, -0.79f, -4.94f, -2.06f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
            curveToRelative(4.97f, 0f, 9f, -4.03f, 9f, -9f)
            reflectiveCurveToRelative(-4.03f, -9f, -9f, -9f); close()
            moveTo(12f, 8f); verticalLineToRelative(5f); lineToRelative(4.28f, 2.54f)
            lineToRelative(0.72f, -1.21f); lineToRelative(-3.5f, -2.08f); verticalLineTo(8f); horizontalLineTo(12f); close()
        }
    }

    val ChevronRight: ImageVector by lazy {
        buildIcon("ChevronRight") {
            moveTo(10f, 6f); lineTo(8.59f, 7.41f); lineTo(13.17f, 12f)
            lineToRelative(-4.58f, 4.59f); lineTo(10f, 18f); lineToRelative(6f, -6f); close()
        }
    }

    val Calculator: ImageVector by lazy {
        buildIcon("Calculator") {
            moveTo(19f, 3f); horizontalLineTo(5f)
            curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f); verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f); verticalLineTo(5f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f); close()
            moveTo(7f, 7f); horizontalLineToRelative(10f); verticalLineToRelative(3f); horizontalLineTo(7f); close()
            moveTo(7f, 12f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineTo(7f); close()
            moveTo(11f, 12f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineTo(11f); close()
            moveTo(15f, 12f); horizontalLineToRelative(2f); verticalLineToRelative(6f); horizontalLineTo(15f); close()
            moveTo(7f, 16f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineTo(7f); close()
            moveTo(11f, 16f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineTo(11f); close()
        }
    }

    val UnitConverter: ImageVector by lazy {
        buildIcon("UnitConverter") {
            moveTo(21f, 6f); horizontalLineTo(3f)
            curveTo(1.9f, 6f, 1f, 6.9f, 1f, 8f); verticalLineToRelative(8f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); horizontalLineToRelative(18f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f); verticalLineTo(8f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f); close()
            moveTo(21f, 16f); horizontalLineTo(3f); verticalLineTo(8f); horizontalLineToRelative(2f)
            verticalLineToRelative(4f); horizontalLineToRelative(2f); verticalLineTo(8f); horizontalLineToRelative(2f)
            verticalLineToRelative(4f); horizontalLineToRelative(2f); verticalLineTo(8f); horizontalLineToRelative(2f)
            verticalLineToRelative(4f); horizontalLineToRelative(2f); verticalLineTo(8f); horizontalLineToRelative(2f)
            verticalLineToRelative(4f); horizontalLineToRelative(2f); verticalLineTo(8f); horizontalLineToRelative(2f)
            verticalLineToRelative(8f); close()
        }
    }

    val Currency: ImageVector by lazy {
        buildIcon("Currency") {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
            reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
            reflectiveCurveTo(17.52f, 2f, 12f, 2f); close()
            moveTo(13f, 17.93f); verticalLineTo(19f); horizontalLineToRelative(-2f); verticalLineToRelative(-1.07f)
            curveToRelative(-1.7f, -0.31f, -3f, -1.49f, -3f, -2.93f); horizontalLineToRelative(2f)
            curveToRelative(0f, 0.83f, 0.9f, 1.5f, 2f, 1.5f)
            reflectiveCurveToRelative(2f, -0.67f, 2f, -1.5f)
            curveToRelative(0f, -0.83f, -0.9f, -1.5f, -2f, -1.5f)
            curveToRelative(-2.21f, 0f, -4f, -1.34f, -4f, -3f)
            curveToRelative(0f, -1.44f, 1.3f, -2.62f, 3f, -2.93f)
            verticalLineTo(5f); horizontalLineToRelative(2f); verticalLineToRelative(1.07f)
            curveToRelative(1.7f, 0.31f, 3f, 1.49f, 3f, 2.93f); horizontalLineToRelative(-2f)
            curveToRelative(0f, -0.83f, -0.9f, -1.5f, -2f, -1.5f)
            reflectiveCurveToRelative(-2f, 0.67f, -2f, 1.5f)
            curveToRelative(0f, 0.83f, 0.9f, 1.5f, 2f, 1.5f)
            curveToRelative(2.21f, 0f, 4f, 1.34f, 4f, 3f)
            curveToRelative(0f, 1.44f, -1.3f, 2.62f, -3f, 2.93f); close()
        }
    }

    val Percentage: ImageVector by lazy {
        buildIcon("Percentage") {
            moveTo(7.5f, 4f)
            curveTo(5.57f, 4f, 4f, 5.57f, 4f, 7.5f)
            reflectiveCurveTo(5.57f, 11f, 7.5f, 11f)
            reflectiveCurveTo(11f, 9.43f, 11f, 7.5f)
            reflectiveCurveTo(9.43f, 4f, 7.5f, 4f); close()
            moveTo(16.5f, 13f)
            curveToRelative(-1.93f, 0f, -3.5f, 1.57f, -3.5f, 3.5f)
            reflectiveCurveToRelative(1.57f, 3.5f, 3.5f, 3.5f)
            reflectiveCurveToRelative(3.5f, -1.57f, 3.5f, -3.5f)
            reflectiveCurveToRelative(-1.57f, -3.5f, -3.5f, -3.5f); close()
            moveTo(5.41f, 20f); lineTo(4f, 18.59f); lineTo(18.59f, 4f); lineTo(20f, 5.41f); lineTo(5.41f, 20f); close()
        }
    }

    val AgeCake: ImageVector by lazy {
        buildIcon("AgeCake") {
            moveTo(12f, 6f)
            curveToRelative(1.11f, 0f, 2f, -0.9f, 2f, -2f)
            curveToRelative(0f, -0.38f, -0.1f, -0.73f, -0.29f, -1.03f)
            lineTo(12f, 0f); lineToRelative(-1.71f, 2.97f)
            curveTo(10.1f, 3.27f, 10f, 3.62f, 10f, 4f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); close()
            moveTo(18f, 9f); horizontalLineToRelative(-5f); verticalLineTo(7f); horizontalLineToRelative(-2f)
            verticalLineToRelative(2f); horizontalLineTo(6f)
            curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f); verticalLineToRelative(9f)
            curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f); horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f); verticalLineToRelative(-9f)
            curveToRelative(0f, -1.66f, -1.34f, -3f, -3f, -3f); close()
        }
    }

    val QrCode: ImageVector by lazy {
        buildIcon("QrCode") {
            moveTo(3f, 11f); horizontalLineToRelative(8f); verticalLineTo(3f); horizontalLineTo(3f); verticalLineToRelative(8f); close()
            moveTo(5f, 5f); horizontalLineToRelative(4f); verticalLineToRelative(4f); horizontalLineTo(5f); verticalLineTo(5f); close()
            moveTo(3f, 21f); horizontalLineToRelative(8f); verticalLineToRelative(-8f); horizontalLineTo(3f); verticalLineToRelative(8f); close()
            moveTo(5f, 15f); horizontalLineToRelative(4f); verticalLineToRelative(4f); horizontalLineTo(5f); verticalLineToRelative(-4f); close()
            moveTo(13f, 3f); verticalLineToRelative(8f); horizontalLineToRelative(8f); verticalLineTo(3f); horizontalLineToRelative(-8f); close()
            moveTo(19f, 9f); horizontalLineToRelative(-4f); verticalLineTo(5f); horizontalLineToRelative(4f); verticalLineToRelative(4f); close()
            moveTo(19f, 19f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(13f, 13f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(15f, 15f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(13f, 17f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(15f, 19f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(17f, 17f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(17f, 13f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
            moveTo(19f, 15f); horizontalLineToRelative(2f); verticalLineToRelative(2f); horizontalLineToRelative(-2f); close()
        }
    }

    val QrScanner: ImageVector by lazy {
        buildIcon("QrScanner") {
            moveTo(9.5f, 6.5f); verticalLineToRelative(3f); horizontalLineToRelative(-3f); verticalLineToRelative(-3f); horizontalLineToRelative(3f)
            moveTo(11f, 5f); horizontalLineTo(5f); verticalLineToRelative(6f); horizontalLineToRelative(6f); verticalLineTo(5f); close()
            moveTo(9.5f, 14.5f); verticalLineToRelative(3f); horizontalLineToRelative(-3f); verticalLineToRelative(-3f); horizontalLineToRelative(3f)
            moveTo(11f, 13f); horizontalLineTo(5f); verticalLineToRelative(6f); horizontalLineToRelative(6f); verticalLineToRelative(-6f); close()
            moveTo(17.5f, 6.5f); verticalLineToRelative(3f); horizontalLineToRelative(-3f); verticalLineToRelative(-3f); horizontalLineToRelative(3f)
            moveTo(19f, 5f); horizontalLineToRelative(-6f); verticalLineToRelative(6f); horizontalLineToRelative(6f); verticalLineTo(5f); close()
            moveTo(2f, 7f); horizontalLineTo(4f); verticalLineTo(4f); horizontalLineToRelative(3f); verticalLineTo(2f); horizontalLineTo(2f); close()
            moveTo(22f, 7f); horizontalLineToRelative(-2f); verticalLineTo(4f); horizontalLineToRelative(-3f); verticalLineTo(2f); horizontalLineToRelative(5f); close()
            moveTo(2f, 17f); horizontalLineToRelative(2f); verticalLineToRelative(3f); horizontalLineToRelative(3f); verticalLineToRelative(2f); horizontalLineTo(2f); close()
            moveTo(22f, 17f); horizontalLineToRelative(-2f); verticalLineToRelative(3f); horizontalLineToRelative(-3f); verticalLineToRelative(2f); horizontalLineToRelative(5f); close()
        }
    }

    val KeyPassword: ImageVector by lazy {
        buildIcon("KeyPassword") {
            moveTo(12.65f, 10f)
            curveTo(11.83f, 7.67f, 9.61f, 6f, 7f, 6f)
            curveToRelative(-3.31f, 0f, -6f, 2.69f, -6f, 6f)
            reflectiveCurveToRelative(2.69f, 6f, 6f, 6f)
            curveToRelative(2.61f, 0f, 4.83f, -1.67f, 5.65f, -4f)
            horizontalLineTo(17f); verticalLineToRelative(4f); horizontalLineToRelative(4f)
            verticalLineToRelative(-4f); horizontalLineToRelative(2f); verticalLineToRelative(-4f)
            horizontalLineTo(12.65f); close()
            moveTo(7f, 14f)
            curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
            reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
            reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
            reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f); close()
        }
    }

    val ShieldSecurity: ImageVector by lazy {
        buildIcon("ShieldSecurity") {
            moveTo(12f, 1f); lineTo(3f, 5f); verticalLineToRelative(6f)
            curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
            curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
            verticalLineTo(5f); lineToRelative(-9f, -4f); close()
            moveTo(10f, 17f); lineToRelative(-4f, -4f); lineToRelative(1.41f, -1.41f)
            lineTo(10f, 14.17f); lineToRelative(6.59f, -6.59f); lineTo(18f, 9f); lineToRelative(-8f, 8f); close()
        }
    }

    val PdfDoc: ImageVector by lazy {
        buildIcon("PdfDoc") {
            moveTo(14f, 2f); horizontalLineTo(6f)
            curveTo(4.9f, 2f, 4f, 2.9f, 4f, 4f); verticalLineToRelative(16f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); horizontalLineToRelative(12f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f); verticalLineTo(8f); lineToRelative(-6f, -6f); close()
            moveTo(13f, 9f); verticalLineTo(3.5f); lineTo(18.5f, 9f); horizontalLineTo(13f); close()
            moveTo(8f, 13f); horizontalLineToRelative(8f); verticalLineToRelative(2f); horizontalLineTo(8f); close()
            moveTo(8f, 17f); horizontalLineToRelative(5f); verticalLineToRelative(2f); horizontalLineTo(8f); close()
        }
    }

    val PdfMerge: ImageVector by lazy {
        buildIcon("PdfMerge") {
            moveTo(17f, 20.41f); lineTo(18.41f, 19f); lineTo(15f, 15.59f); lineTo(13.59f, 17f); lineTo(17f, 20.41f); close()
            moveTo(7.5f, 8f); horizontalLineTo(11f); verticalLineToRelative(5.59f); lineTo(5.59f, 19f)
            lineTo(7f, 20.41f); lineToRelative(6f, -6f); verticalLineTo(8f); horizontalLineToRelative(3.5f)
            lineTo(12f, 3.5f); lineTo(7.5f, 8f); close()
        }
    }

    val ImageCompress: ImageVector by lazy {
        buildIcon("ImageCompress") {
            moveTo(21f, 19f); verticalLineTo(5f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f); horizontalLineTo(5f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f); verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f); close()
            moveTo(8.5f, 13.5f); lineToRelative(2.5f, 3.01f); lineTo(14.5f, 12f)
            lineToRelative(4.5f, 6f); horizontalLineTo(5f); lineToRelative(3.5f, -4.5f); close()
        }
    }

    val OcrScan: ImageVector by lazy {
        buildIcon("OcrScan") {
            moveTo(4f, 6f); horizontalLineToRelative(16f); verticalLineToRelative(2f); horizontalLineTo(4f); close()
            moveTo(4f, 11f); horizontalLineToRelative(16f); verticalLineToRelative(2f); horizontalLineTo(4f); close()
            moveTo(4f, 16f); horizontalLineToRelative(10f); verticalLineToRelative(2f); horizontalLineTo(4f); close()
            moveTo(19f, 15f); lineToRelative(3f, 3f); lineToRelative(-3f, 3f); verticalLineToRelative(-6f); close()
        }
    }

    val TextTools: ImageVector by lazy {
        buildIcon("TextTools") {
            moveTo(2.5f, 4f); verticalLineToRelative(3f); horizontalLineToRelative(5f)
            verticalLineToRelative(12f); horizontalLineToRelative(3f); verticalLineTo(7f)
            horizontalLineToRelative(5f); verticalLineTo(4f); horizontalLineTo(2.5f); close()
            moveTo(21.5f, 9f); horizontalLineToRelative(-9f); verticalLineToRelative(3f)
            horizontalLineToRelative(3f); verticalLineToRelative(7f); horizontalLineToRelative(3f)
            verticalLineToRelative(-7f); horizontalLineToRelative(3f); verticalLineTo(9f); close()
        }
    }

    val NotesDoc: ImageVector by lazy {
        buildIcon("NotesDoc") {
            moveTo(3f, 18f); horizontalLineToRelative(12f); verticalLineToRelative(-2f); horizontalLineTo(3f); verticalLineToRelative(2f); close()
            moveTo(3f, 6f); verticalLineToRelative(2f); horizontalLineToRelative(18f); verticalLineTo(6f); horizontalLineTo(3f); close()
            moveTo(3f, 13f); horizontalLineToRelative(18f); verticalLineToRelative(-2f); horizontalLineTo(3f); verticalLineToRelative(2f); close()
        }
    }

    val BmiHealth: ImageVector by lazy {
        buildIcon("BmiHealth") {
            moveTo(12f, 21.35f); lineToRelative(-1.45f, -1.32f)
            curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
            curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
            curveToRelative(1.74f, 0f, 3.41f, 0.81f, 4.5f, 2.09f)
            curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
            curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
            curveToRelative(0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
            lineTo(12f, 21.35f); close()
        }
    }

    val StopwatchTimer: ImageVector by lazy {
        buildIcon("StopwatchTimer") {
            moveTo(15f, 1f); horizontalLineTo(9f); verticalLineToRelative(2f); horizontalLineToRelative(6f); verticalLineTo(1f); close()
            moveTo(11f, 14f); horizontalLineToRelative(2f); verticalLineTo(8f); horizontalLineToRelative(-2f); verticalLineToRelative(6f); close()
            moveTo(19.03f, 7.39f); lineToRelative(1.42f, -1.42f)
            curveToRelative(-0.43f, -0.51f, -0.9f, -0.99f, -1.41f, -1.41f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(16.07f, 4.74f, 14.12f, 4f, 12f, 4f)
            curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
            reflectiveCurveToRelative(4.02f, 9f, 9f, 9f)
            reflectiveCurveToRelative(9f, -4.03f, 9f, -9f)
            curveToRelative(0f, -2.12f, -0.74f, -4.07f, -1.97f, -5.61f); close()
        }
    }

    val DeviceInfo: ImageVector by lazy {
        buildIcon("DeviceInfo") {
            moveTo(17f, 1.01f); lineTo(7f, 1f)
            curveTo(5.9f, 1f, 5f, 1.9f, 5f, 3f); verticalLineToRelative(18f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f); horizontalLineToRelative(10f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f); verticalLineTo(3f)
            curveToRelative(0f, -1.1f, -0.9f, -1.99f, -2f, -1.99f); close()
            moveTo(17f, 19f); horizontalLineTo(7f); verticalLineTo(5f); horizontalLineToRelative(10f); verticalLineToRelative(14f); close()
        }
    }

    val LanguageGlobe: ImageVector by lazy {
        buildIcon("LanguageGlobe") {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
            reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
            reflectiveCurveTo(17.52f, 2f, 12f, 2f); close()
            moveTo(4f, 12f)
            curveToRelative(0f, -1.85f, 0.63f, -3.55f, 1.69f, -4.9f)
            lineTo(16.9f, 18.31f)
            curveTo(15.55f, 19.37f, 13.85f, 20f, 12f, 20f)
            curveToRelative(-4.41f, 0f, -8f, -3.59f, -8f, -8f); close()
            moveTo(18.31f, 16.9f); lineTo(7.1f, 5.69f)
            curveTo(8.45f, 4.63f, 10.15f, 4f, 12f, 4f)
            curveToRelative(4.41f, 0f, 8f, 3.59f, 8f, 8f)
            curveToRelative(0f, 1.85f, -0.63f, 3.55f, -1.69f, 4.9f); close()
        }
    }

    val SparklePalette: ImageVector by lazy {
        buildIcon("SparklePalette") {
            moveTo(12f, 3f); lineToRelative(2.2f, 5.8f); lineTo(20f, 11f)
            lineToRelative(-5.8f, 2.2f); lineTo(12f, 19f); lineToRelative(-2.2f, -5.8f)
            lineTo(4f, 11f); lineToRelative(5.8f, -2.2f); close()
        }
    }

    fun iconForTool(toolId: String): ImageVector = when (toolId) {
        "calc_scientific" -> Calculator
        "unit_converter" -> UnitConverter
        "currency" -> Currency
        "percentage" -> Percentage
        "age" -> AgeCake
        "qr_generator" -> QrCode
        "qr_scanner" -> QrScanner
        "password_gen" -> KeyPassword
        "password_strength" -> ShieldSecurity
        "image_to_pdf" -> PdfDoc
        "pdf_merger" -> PdfMerge
        "image_compressor" -> ImageCompress
        "ocr" -> OcrScan
        "text_toolkit" -> TextTools
        "notes" -> NotesDoc
        "bmi_calculator" -> BmiHealth
        "stopwatch_timer" -> StopwatchTimer
        "device_info" -> DeviceInfo
        else -> SparklePalette
    }

    fun colorForTool(toolId: String): Color = when (toolId) {
        "calc_scientific" -> GlowOrange
        "unit_converter" -> GlowCyan
        "currency" -> GlowEmerald
        "percentage" -> GlowAmber
        "age" -> GlowPink
        "qr_generator" -> GlowViolet
        "qr_scanner" -> GlowBlue
        "password_gen" -> GlowTeal
        "password_strength" -> GlowEmerald
        "image_to_pdf" -> GlowRose
        "pdf_merger" -> GlowIndigo
        "image_compressor" -> GlowAmber
        "ocr" -> GlowCyan
        "text_toolkit" -> GlowBlue
        "notes" -> GlowOrange
        "bmi_calculator" -> GlowRose
        "stopwatch_timer" -> GlowViolet
        "device_info" -> GlowTeal
        else -> GlowOrange
    }
}
