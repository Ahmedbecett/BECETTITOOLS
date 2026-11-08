package com.becetti.tools.design.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Extended glyph set for the professional toolkit.
 *
 * Hand-built 24x24 vectors — no `material-icons-extended` dependency (that artifact alone added
 * ~40MB of DEX in v1.0). Filled geometry with even-odd holes keeps icons crisp at 16–48dp while
 * staying only a few hundred bytes each.
 */
object BecettiGlyphs {

    private val ink = SolidColor(Color.Black)
    private const val ODD = PathFillType.EvenOdd

    private fun ico(name: String, block: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply(block).build()

    /* ---------- primitive helpers (all fill-only, RTL-safe geometry) ---------- */

    private fun ImageVector.Builder.rect(l: Float, t: Float, r: Float, b: Float) {
        path(fill = ink) {
            moveTo(l, t); horizontalLineTo(r); verticalLineTo(b); horizontalLineTo(l); close()
        }
    }

    private fun ImageVector.Builder.rrect(l: Float, t: Float, r: Float, b: Float, rad: Float = 1.8f) {
        path(fill = ink) {
            moveTo(l + rad, t); horizontalLineTo(r - rad)
            arcToRelative(rad, rad, 0f, false, true, rad, rad)
            verticalLineTo(b - rad)
            arcToRelative(rad, rad, 0f, false, true, -rad, rad)
            horizontalLineTo(l + rad)
            arcToRelative(rad, rad, 0f, false, true, -rad, -rad)
            verticalLineTo(t + rad)
            arcToRelative(rad, rad, 0f, false, true, rad, -rad)
            close()
        }
    }

    private fun ImageVector.Builder.circle(cx: Float, cy: Float, r: Float) {
        path(fill = ink) {
            moveTo(cx, cy - r)
            arcToRelative(r, r, 0f, true, true, 0f, 2 * r)
            arcToRelative(r, r, 0f, true, true, 0f, -2 * r)
            close()
        }
    }

    private fun ImageVector.Builder.ring(cx: Float, cy: Float, outer: Float, inner: Float) {
        path(fill = ink, pathFillType = ODD) {
            moveTo(cx, cy - outer)
            arcToRelative(outer, outer, 0f, true, true, 0f, 2 * outer)
            arcToRelative(outer, outer, 0f, true, true, 0f, -2 * outer)
            close()
            moveTo(cx, cy - inner)
            arcToRelative(inner, inner, 0f, true, true, 0f, 2 * inner)
            arcToRelative(inner, inner, 0f, true, true, 0f, -2 * inner)
            close()
        }
    }

    private fun ImageVector.Builder.frame(l: Float, t: Float, r: Float, b: Float, w: Float, rad: Float = 2f) {
        path(fill = ink, pathFillType = ODD) {
            moveTo(l + rad, t); horizontalLineTo(r - rad)
            arcToRelative(rad, rad, 0f, false, true, rad, rad)
            verticalLineTo(b - rad)
            arcToRelative(rad, rad, 0f, false, true, -rad, rad)
            horizontalLineTo(l + rad)
            arcToRelative(rad, rad, 0f, false, true, -rad, -rad)
            verticalLineTo(t + rad)
            arcToRelative(rad, rad, 0f, false, true, rad, -rad)
            close()
            val il = l + w; val it2 = t + w; val ir = r - w; val ib = b - w
            val irad = (rad - w).coerceAtLeast(0.4f)
            moveTo(il + irad, it2); horizontalLineTo(ir - irad)
            arcToRelative(irad, irad, 0f, false, true, irad, irad)
            verticalLineTo(ib - irad)
            arcToRelative(irad, irad, 0f, false, true, -irad, irad)
            horizontalLineTo(il + irad)
            arcToRelative(irad, irad, 0f, false, true, -irad, -irad)
            verticalLineTo(it2 + irad)
            arcToRelative(irad, irad, 0f, false, true, irad, -irad)
            close()
        }
    }

    private fun ImageVector.Builder.stroke(
        x1: Float, y1: Float, x2: Float, y2: Float, w: Float
    ) {
        val dx = x2 - x1
        val dy = y2 - y1
        val len = kotlin.math.sqrt(dx * dx + dy * dy).takeIf { it > 0.0001f } ?: return
        val nx = -dy / len * (w / 2f)
        val ny = dx / len * (w / 2f)
        path(fill = ink) {
            moveTo(x1 + nx, y1 + ny); lineTo(x2 + nx, y2 + ny)
            lineTo(x2 - nx, y2 - ny); lineTo(x1 - nx, y1 - ny); close()
        }
    }

    private fun ImageVector.Builder.poly(vararg pts: Pair<Float, Float>) {
        if (pts.size < 3) return
        path(fill = ink) {
            moveTo(pts[0].first, pts[0].second)
            for (i in 1 until pts.size) lineTo(pts[i].first, pts[i].second)
            close()
        }
    }

    /* ------------------------------- Developer ------------------------------- */

    val Braces: ImageVector by lazy {
        ico("Braces") {
            path(fill = ink) {
                moveTo(9.2f, 4f); curveToRelative(-1.5f, 0f, -2.4f, 1.2f, -2.4f, 2.6f)
                verticalLineToRelative(2.1f); curveToRelative(0f, 0.9f, -0.5f, 1.3f, -1.4f, 1.3f)
                verticalLineToRelative(2f); curveToRelative(0.9f, 0f, 1.4f, 0.4f, 1.4f, 1.3f)
                verticalLineToRelative(2.1f); curveToRelative(0f, 1.4f, 0.9f, 2.6f, 2.4f, 2.6f)
                horizontalLineToRelative(1.2f); verticalLineToRelative(-2f); horizontalLineTo(9.9f)
                curveToRelative(-0.5f, 0f, -0.8f, -0.3f, -0.8f, -0.8f); verticalLineToRelative(-2.3f)
                curveToRelative(0f, -0.9f, -0.5f, -1.5f, -1.3f, -1.7f)
                curveToRelative(0.8f, -0.2f, 1.3f, -0.8f, 1.3f, -1.7f); verticalLineTo(7.2f)
                curveToRelative(0f, -0.5f, 0.3f, -0.8f, 0.8f, -0.8f); horizontalLineToRelative(0.5f); close()
                moveTo(14.8f, 4f); verticalLineToRelative(2f); horizontalLineToRelative(0.5f)
                curveToRelative(0.5f, 0f, 0.8f, 0.3f, 0.8f, 0.8f); verticalLineToRelative(2.3f)
                curveToRelative(0f, 0.9f, 0.5f, 1.5f, 1.3f, 1.7f)
                curveToRelative(-0.8f, 0.2f, -1.3f, 0.8f, -1.3f, 1.7f); verticalLineToRelative(2.3f)
                curveToRelative(0f, 0.5f, -0.3f, 0.8f, -0.8f, 0.8f); horizontalLineToRelative(-0.5f); verticalLineToRelative(2f)
                horizontalLineToRelative(1.2f); curveToRelative(1.5f, 0f, 2.4f, -1.2f, 2.4f, -2.6f)
                verticalLineToRelative(-2.1f); curveToRelative(0f, -0.9f, 0.5f, -1.3f, 1.4f, -1.3f); verticalLineToRelative(-2f)
                curveToRelative(-0.9f, 0f, -1.4f, -0.4f, -1.4f, -1.3f); verticalLineTo(6.6f)
                curveToRelative(0f, -1.4f, -0.9f, -2.6f, -2.4f, -2.6f); close()
            }
        }
    }

    val Hash: ImageVector by lazy {
        ico("Hash") {
            stroke(8.6f, 3.5f, 6.6f, 20.5f, 1.9f)
            stroke(15.4f, 3.5f, 13.4f, 20.5f, 1.9f)
            stroke(3.6f, 8.8f, 20.4f, 8.8f, 1.9f)
            stroke(3.6f, 15.2f, 20.4f, 15.2f, 1.9f)
        }
    }

    val IdCard: ImageVector by lazy {
        ico("IdCard") {
            frame(2f, 4.5f, 22f, 19.5f, 1.8f, 2.6f)
            circle(8f, 10.5f, 1.9f)
            rrect(5.4f, 13.4f, 10.6f, 15.2f, 0.9f)
            rrect(12.6f, 9f, 19.2f, 10.6f, 0.8f)
            rrect(12.6f, 12.4f, 19.2f, 14f, 0.8f)
        }
    }

    val RegexStar: ImageVector by lazy {
        ico("RegexStar") {
            stroke(12f, 3.5f, 12f, 14.5f, 2f)
            stroke(4.9f, 6.7f, 19.1f, 11.3f, 2f)
            stroke(4.9f, 11.3f, 19.1f, 6.7f, 2f)
            rrect(3f, 17.4f, 8f, 20.6f, 1f)
            rrect(10f, 17.4f, 15f, 20.6f, 1f)
            rrect(17.4f, 17.4f, 21.4f, 20.6f, 1f)
        }
    }

    val CloudCode: ImageVector by lazy {
        ico("CloudCode") {
            path(fill = ink) {
                moveTo(8.8f, 5.2f); curveToRelative(-2.9f, 0f, -5.3f, 2.2f, -5.6f, 5f)
                curveToRelative(-1.4f, 0.6f, -2.2f, 1.9f, -2.2f, 3.4f)
                curveToRelative(0f, 2.1f, 1.7f, 3.7f, 3.8f, 3.7f); horizontalLineToRelative(0.6f)
                verticalLineToRelative(-2.2f); horizontalLineTo(4.8f)
                curveToRelative(-0.9f, 0f, -1.6f, -0.7f, -1.6f, -1.5f); curveToRelative(0f, -0.8f, 0.6f, -1.4f, 1.4f, -1.5f)
                lineToRelative(0.9f, -0.1f); lineToRelative(0.2f, -0.9f); curveToRelative(0.2f, -1.7f, 1.6f, -3f, 3.3f, -3f)
                curveToRelative(1.2f, 0f, 2.2f, 0.6f, 2.8f, 1.6f); lineToRelative(0.5f, 0.8f); close()
                moveTo(15.8f, 9.2f); curveToRelative(-2.6f, 0f, -4.7f, 2.1f, -4.7f, 4.7f); verticalLineTo(21f)
                horizontalLineToRelative(2.2f); verticalLineToRelative(-7.1f); curveToRelative(0f, -1.4f, 1.1f, -2.5f, 2.5f, -2.5f)
                reflectiveCurveToRelative(2.5f, 1.1f, 2.5f, 2.5f); verticalLineTo(21f); horizontalLineToRelative(2.2f)
                verticalLineToRelative(-7.1f); curveToRelative(0f, -2.6f, -2.1f, -4.7f, -4.7f, -4.7f); close()
            }
        }
    }

    val SplitCompare: ImageVector by lazy {
        ico("SplitCompare") {
            rrect(2f, 3.5f, 9.6f, 20.5f, 2f)
            rrect(12.4f, 3.5f, 20f, 20.5f, 2f)
            path(fill = ink) {
                moveTo(9.6f, 11f); lineTo(12.4f, 11f); lineTo(12.4f, 13f); lineTo(9.6f, 13f); close()
            }
        }
    }

    val LinkChain: ImageVector by lazy {
        ico("LinkChain") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(10.6f, 6.2f); horizontalLineTo(8.2f)
                curveToRelative(-3.2f, 0f, -5.8f, 2.6f, -5.8f, 5.8f)
                reflectiveCurveToRelative(2.6f, 5.8f, 5.8f, 5.8f); horizontalLineToRelative(2.4f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1f, -1f); reflectiveCurveToRelative(-0.4f, -1f, -1f, -1f)
                horizontalLineTo(8.2f); curveToRelative(-2.1f, 0f, -3.8f, -1.7f, -3.8f, -3.8f)
                reflectiveCurveToRelative(1.7f, -3.8f, 3.8f, -3.8f); horizontalLineToRelative(2.4f)
                curveToRelative(0.6f, 0f, 1f, -0.4f, 1f, -1f); reflectiveCurveToRelative(-0.4f, -1f, -1f, -1f); close()
                moveTo(15.8f, 6.2f); horizontalLineTo(14.4f)
                curveToRelative(-0.6f, 0f, -1f, 0.4f, -1f, 1f); reflectiveCurveToRelative(0.4f, 1f, 1f, 1f)
                horizontalLineToRelative(1.4f); curveToRelative(2.1f, 0f, 3.8f, 1.7f, 3.8f, 3.8f)
                reflectiveCurveToRelative(-1.7f, 3.8f, -3.8f, 3.8f); horizontalLineTo(13.4f)
                curveToRelative(-0.6f, 0f, -1f, 0.4f, -1f, 1f); reflectiveCurveToRelative(0.4f, 1f, 1f, 1f)
                horizontalLineToRelative(2.4f); curveToRelative(3.2f, 0f, 5.8f, -2.6f, 5.8f, -5.8f)
                reflectiveCurveToRelative(-2.6f, -5.8f, -5.8f, -5.8f); close()
            }
            rrect(8.4f, 10.9f, 15.6f, 13.1f, 1.1f)
        }
    }

    val LockKey: ImageVector by lazy {
        ico("LockKey") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 2.2f); curveToRelative(-3f, 0f, -5.4f, 2.4f, -5.4f, 5.4f); verticalLineToRelative(1.6f)
                horizontalLineToRelative(2.2f); verticalLineTo(7.6f); curveToRelative(0f, -1.8f, 1.4f, -3.2f, 3.2f, -3.2f)
                reflectiveCurveToRelative(3.2f, 1.4f, 3.2f, 3.2f); verticalLineToRelative(1.6f); horizontalLineToRelative(2.2f)
                verticalLineTo(7.6f); curveToRelative(0f, -3f, -2.4f, -5.4f, -5.4f, -5.4f); close()
            }
            rrect(3.6f, 9.2f, 20.4f, 21.8f, 3f)
            path(fill = SolidColor(Color.Black), pathFillType = ODD) {
                moveTo(12f, 12.4f); curveToRelative(-1.2f, 0f, -2.1f, 0.9f, -2.1f, 2.1f)
                curveToRelative(0f, 0.8f, 0.4f, 1.4f, 1f, 1.8f); verticalLineToRelative(2.2f); horizontalLineToRelative(2.2f)
                verticalLineToRelative(-2.2f); curveToRelative(0.6f, -0.4f, 1f, -1f, 1f, -1.8f)
                curveToRelative(0f, -1.2f, -0.9f, -2.1f, -2.1f, -2.1f); close()
            }
        }
    }

    val Terminal: ImageVector by lazy {
        ico("Terminal") {
            frame(2f, 3.5f, 22f, 20.5f, 1.7f, 2.6f)
            path(fill = ink) {
                moveTo(5.6f, 8.2f); lineTo(8.8f, 11.5f); lineTo(5.6f, 14.8f)
                lineTo(7f, 16.2f); lineTo(11.6f, 11.5f); lineTo(7f, 6.8f); close()
            }
            rrect(12.4f, 13.4f, 18.6f, 15.2f, 0.9f)
        }
    }

    val CodeBrackets: ImageVector by lazy {
        ico("CodeBrackets") {
            path(fill = ink) {
                moveTo(8.4f, 5.2f); lineTo(2.4f, 12f); lineTo(8.4f, 18.8f)
                lineToRelative(1.6f, -2.6f); lineToRelative(-4f, -4.2f); lineToRelative(4f, -4.2f); close()
                moveTo(15.6f, 5.2f); lineToRelative(-1.6f, 2.6f); lineToRelative(4f, 4.2f)
                lineToRelative(-4f, 4.2f); lineToRelative(1.6f, 2.6f); lineTo(21.6f, 12f); close()
            }
        }
    }

    val GithubMark: ImageVector by lazy {
        ico("GithubMark") {
            ring(12f, 9.6f, 7.4f, 6.1f)
            path(fill = ink) {
                moveTo(6.2f, 12.6f); curveToRelative(0f, 4.4f, 2.6f, 6.9f, 5.8f, 7.9f)
                verticalLineToRelative(-2.1f); curveToRelative(-2f, 0.3f, -2.9f, -0.9f, -2.9f, -0.9f)
                curveToRelative(-0.4f, -1f, -1.1f, -1.3f, -1.1f, -1.3f); curveToRelative(-0.9f, -0.6f, 0.1f, -0.6f, 0.1f, -0.6f)
                curveToRelative(1f, 0.1f, 1.5f, 1f, 1.5f, 1f); curveToRelative(0.9f, 1.5f, 2.3f, 1.1f, 2.9f, 0.8f)
                curveToRelative(0.1f, -0.6f, 0.4f, -1f, 0.7f, -1.3f); curveToRelative(-2.3f, -0.3f, -4.7f, -1.2f, -4.7f, -5.2f)
                curveToRelative(0f, -1.2f, 0.4f, -2.1f, 1.1f, -2.9f); curveToRelative(-0.1f, -0.3f, -0.5f, -1.4f, 0.1f, -2.9f)
                curveToRelative(0f, 0f, 0.9f, -0.3f, 2.9f, 1.1f); curveToRelative(0.8f, -0.2f, 1.7f, -0.3f, 2.6f, -0.3f)
                reflectiveCurveToRelative(1.7f, 0.1f, 2.6f, 0.3f); curveToRelative(2f, -1.4f, 2.9f, -1.1f, 2.9f, -1.1f)
                curveToRelative(0.6f, 1.5f, 0.2f, 2.6f, 0.1f, 2.9f); curveToRelative(0.7f, 0.8f, 1.1f, 1.7f, 1.1f, 2.9f)
                curveToRelative(0f, 4f, -2.4f, 4.9f, -4.7f, 5.2f); curveToRelative(0.4f, 0.4f, 0.8f, 1.1f, 0.8f, 2.2f)
                verticalLineToRelative(3.3f); curveToRelative(3.2f, -1f, 5.8f, -3.5f, 5.8f, -7.9f); close()
            }
        }
    }

    /* ------------------------------- Projects ------------------------------- */

    val Kanban: ImageVector by lazy {
        ico("Kanban") {
            rrect(2.6f, 3.6f, 8f, 16.4f, 2f)
            rrect(9.6f, 3.6f, 15f, 12.4f, 2f)
            rrect(16.6f, 3.6f, 22f, 20.4f, 2f)
        }
    }

    val Checklist: ImageVector by lazy {
        ico("Checklist") {
            frame(2f, 3.6f, 22f, 20.4f, 1.7f, 2.6f)
            path(fill = ink) {
                moveTo(5.4f, 8.2f); lineTo(7f, 9.8f); lineTo(10.2f, 6.6f); lineTo(11.6f, 8f)
                lineTo(7f, 12.6f); lineTo(4f, 9.6f); close()
                moveTo(5.4f, 14.6f); lineTo(7f, 16.2f); lineTo(10.2f, 13f); lineTo(11.6f, 14.4f)
                lineTo(7f, 19f); lineTo(4f, 16f); close()
            }
            rrect(12.6f, 8.2f, 19.4f, 9.9f, 0.85f)
            rrect(12.6f, 14.6f, 19.4f, 16.3f, 0.85f)
        }
    }

    val Gauge: ImageVector by lazy {
        ico("Gauge") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 2.6f); curveToRelative(-5.2f, 0f, -9.4f, 4.2f, -9.4f, 9.4f)
                curveToRelative(0f, 2.9f, 1.3f, 5.5f, 3.4f, 7.2f); horizontalLineToRelative(3f)
                curveToRelative(-2.6f, -1.2f, -4.4f, -3.9f, -4.4f, -7f); curveToRelative(0f, -4.1f, 3.3f, -7.4f, 7.4f, -7.4f)
                reflectiveCurveToRelative(7.4f, 3.3f, 7.4f, 7.4f); curveToRelative(0f, 3.1f, -1.8f, 5.8f, -4.4f, 7f)
                horizontalLineToRelative(3f); curveToRelative(2.1f, -1.7f, 3.4f, -4.3f, 3.4f, -7.2f)
                curveToRelative(0f, -5.2f, -4.2f, -9.4f, -9.4f, -9.4f); close()
            }
            stroke(12f, 12f, 16.4f, 7.2f, 2.2f)
            circle(12f, 12f, 1.9f)
        }
    }

    val Wallet: ImageVector by lazy {
        ico("Wallet") {
            rrect(2.4f, 5.4f, 21.6f, 19.4f, 2.6f)
            path(fill = SolidColor(Color.Black), pathFillType = ODD) {
                moveTo(15.4f, 10.8f); horizontalLineTo(21.6f); verticalLineTo(14.6f); horizontalLineTo(15.4f)
                curveToRelative(-1f, 0f, -1.9f, -0.8f, -1.9f, -1.9f); reflectiveCurveToRelative(0.9f, -1.9f, 1.9f, -1.9f); close()
            }
            path(fill = ink) {
                moveTo(4.4f, 3.4f); horizontalLineTo(17.6f); verticalLineTo(5.4f); horizontalLineTo(4.4f); close()
            }
        }
    }

    val BarChart: ImageVector by lazy {
        ico("BarChart") {
            rrect(3f, 12.6f, 7.6f, 21f, 1.5f)
            rrect(9.7f, 7.6f, 14.3f, 21f, 1.5f)
            rrect(16.4f, 3f, 21f, 21f, 1.5f)
        }
    }

    val Briefcase: ImageVector by lazy {
        ico("Briefcase") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(9f, 2.6f); curveToRelative(-1.4f, 0f, -2.6f, 1.2f, -2.6f, 2.6f); verticalLineToRelative(1.2f)
                horizontalLineTo(3.6f); curveToRelative(-1f, 0f, -1.8f, 0.8f, -1.8f, 1.8f); verticalLineToRelative(11.4f)
                curveToRelative(0f, 1f, 0.8f, 1.9f, 1.8f, 1.9f); horizontalLineToRelative(16.8f)
                curveToRelative(1f, 0f, 1.8f, -0.9f, 1.8f, -1.9f); verticalLineTo(8.2f)
                curveToRelative(0f, -1f, -0.8f, -1.8f, -1.8f, -1.8f); horizontalLineTo(17.6f); verticalLineTo(5.2f)
                curveToRelative(0f, -1.4f, -1.2f, -2.6f, -2.6f, -2.6f); close()
                moveTo(9f, 4.8f); horizontalLineToRelative(6f); verticalLineToRelative(1.6f); horizontalLineTo(9f); close()
                moveTo(3.8f, 16.4f); horizontalLineTo(20.2f); verticalLineTo(18f); horizontalLineTo(3.8f); close()
            }
        }
    }

    /* --------------------------------- Files --------------------------------- */

    val FolderOpen: ImageVector by lazy {
        ico("FolderOpen") {
            path(fill = ink) {
                moveTo(2.4f, 5.2f); curveToRelative(0f, -1f, 0.8f, -1.8f, 1.8f, -1.8f); horizontalLineTo(8.6f)
                lineTo(10.6f, 5.6f); horizontalLineTo(19.8f); curveToRelative(1f, 0f, 1.8f, 0.8f, 1.8f, 1.8f)
                verticalLineToRelative(1.8f); horizontalLineTo(2.4f); close()
                moveTo(2.4f, 9.8f); horizontalLineTo(21.6f); lineTo(23.2f, 19.4f)
                curveToRelative(0.1f, 0.9f, -0.6f, 1.6f, -1.5f, 1.6f); horizontalLineTo(2.3f)
                curveToRelative(-0.9f, 0f, -1.6f, -0.7f, -1.5f, -1.6f); close()
            }
        }
    }

    val ArchiveZip: ImageVector by lazy {
        ico("ArchiveZip") {
            frame(2.6f, 3.2f, 21.4f, 20.8f, 1.7f, 2.6f)
            path(fill = ink) {
                moveTo(10.4f, 3.2f); horizontalLineTo(13.6f); verticalLineTo(6.4f); horizontalLineTo(10.4f); close()
                moveTo(10.4f, 7f); horizontalLineTo(13.6f); verticalLineTo(9.4f); horizontalLineTo(10.4f); close()
                moveTo(10.4f, 10f); horizontalLineTo(13.6f); verticalLineTo(12.4f); horizontalLineTo(10.4f); close()
                moveTo(9.6f, 13f); horizontalLineTo(14.4f); verticalLineToRelative(3.2f)
                curveToRelative(0f, 1.3f, -1.1f, 2.4f, -2.4f, 2.4f); reflectiveCurveToRelative(-2.4f, -1.1f, -2.4f, -2.4f); close()
            }
        }
    }

    val Database: ImageVector by lazy {
        ico("Database") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 2.2f); curveToRelative(-4.4f, 0f, -8f, 1.5f, -8f, 3.4f); verticalLineToRelative(12.8f)
                curveToRelative(0f, 1.9f, 3.6f, 3.4f, 8f, 3.4f); reflectiveCurveToRelative(8f, -1.5f, 8f, -3.4f)
                verticalLineTo(5.6f); curveToRelative(0f, -1.9f, -3.6f, -3.4f, -8f, -3.4f); close()
                moveTo(12f, 4.2f); curveToRelative(3.4f, 0f, 6f, 0.8f, 6f, 1.4f)
                reflectiveCurveToRelative(-2.6f, 1.4f, -6f, 1.4f); reflectiveCurveToRelative(-6f, -0.8f, -6f, -1.4f)
                reflectiveCurveTo(8.6f, 4.2f, 12f, 4.2f); close()
                moveTo(6f, 10.4f); curveToRelative(1.5f, 0.7f, 3.6f, 1.1f, 6f, 1.1f)
                reflectiveCurveToRelative(4.5f, -0.4f, 6f, -1.1f); verticalLineToRelative(2.2f)
                curveToRelative(-1.5f, 0.7f, -3.6f, 1.1f, -6f, 1.1f); reflectiveCurveToRelative(-4.5f, -0.4f, -6f, -1.1f); close()
                moveTo(6f, 15.2f); curveToRelative(1.5f, 0.7f, 3.6f, 1.1f, 6f, 1.1f)
                reflectiveCurveToRelative(4.5f, -0.4f, 6f, -1.1f); verticalLineToRelative(2.1f)
                curveToRelative(-1.5f, 0.7f, -3.6f, 1.1f, -6f, 1.1f); reflectiveCurveToRelative(-4.5f, -0.4f, -6f, -1.1f); close()
            }
        }
    }

    val CopyStack: ImageVector by lazy {
        ico("CopyStack") {
            rrect(8f, 2.6f, 21.4f, 16f, 2.2f)
            path(fill = SolidColor(Color.Black), pathFillType = ODD) {
                moveTo(10.2f, 4.8f); horizontalLineTo(19.2f); verticalLineTo(13.8f); horizontalLineTo(10.2f); close()
            }
            rrect(2.6f, 8f, 16f, 21.4f, 2.2f)
            path(fill = SolidColor(Color.Black), pathFillType = ODD) {
                moveTo(4.8f, 10.2f); horizontalLineTo(13.8f); verticalLineTo(19.2f); horizontalLineTo(4.8f); close()
            }
        }
    }

    val SwapArrows: ImageVector by lazy {
        ico("SwapArrows") {
            path(fill = ink) {
                moveTo(7.2f, 2.6f); lineTo(2.6f, 7.6f); lineTo(7.2f, 12.6f); lineTo(7.2f, 8.9f)
                horizontalLineTo(16.4f); verticalLineTo(6.3f); horizontalLineTo(7.2f); close()
                moveTo(16.8f, 11.4f); lineTo(16.8f, 15.1f); horizontalLineTo(7.6f); verticalLineTo(17.7f)
                horizontalLineTo(16.8f); lineTo(16.8f, 21.4f); lineTo(21.4f, 16.4f); close()
            }
        }
    }

    val StoragePie: ImageVector by lazy {
        ico("StoragePie") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 2.4f); curveToRelative(-5.3f, 0f, -9.6f, 4.3f, -9.6f, 9.6f)
                reflectiveCurveToRelative(4.3f, 9.6f, 9.6f, 9.6f); reflectiveCurveToRelative(9.6f, -4.3f, 9.6f, -9.6f)
                curveToRelative(0f, 0f, -9.6f, 0f, -9.6f, 0f); close()
                moveTo(13.8f, 4.6f); verticalLineToRelative(5.6f); horizontalLineTo(19.4f)
                curveToRelative(-0.6f, -3f, -2.6f, -5f, -5.6f, -5.6f); close()
            }
        }
    }

    /* --------------------------------- Media --------------------------------- */

    val ImageFrame: ImageVector by lazy {
        ico("ImageFrame") {
            frame(2.2f, 3.6f, 21.8f, 20.4f, 1.7f, 2.6f)
            circle(8.2f, 8.8f, 1.7f)
            poly(5.2f, 16.6f, 10f, 10.6f, 13.2f, 14.6f, 15f, 12.6f, 19f, 16.6f)
        }
    }

    val Crop: ImageVector by lazy {
        ico("Crop") {
            rrect(6.4f, 1.6f, 8.4f, 15.6f, 0.9f)
            rrect(1.6f, 6.4f, 15.6f, 8.4f, 0.9f)
            rrect(15.6f, 8.4f, 17.6f, 22.4f, 0.9f)
            rrect(8.4f, 15.6f, 22.4f, 17.6f, 0.9f)
        }
    }

    val Watermark: ImageVector by lazy {
        ico("Watermark") {
            frame(2.2f, 3.6f, 21.8f, 20.4f, 1.7f, 2.6f)
            path(fill = ink) {
                moveTo(6f, 12.4f); horizontalLineTo(11f); verticalLineTo(14f); horizontalLineTo(6f); close()
                moveTo(6f, 15.4f); horizontalLineTo(14f); verticalLineTo(17f); horizontalLineTo(6f); close()
            }
            rrect(12.6f, 8.4f, 18f, 10.6f, 1.1f)
        }
    }

    val Waveform: ImageVector by lazy {
        ico("Waveform") {
            rrect(2.4f, 9.4f, 4.4f, 14.6f, 1f)
            rrect(5.6f, 6.4f, 7.6f, 17.6f, 1f)
            rrect(8.8f, 3.2f, 10.8f, 20.8f, 1f)
            rrect(12f, 7.4f, 14f, 16.6f, 1f)
            rrect(15.2f, 5.2f, 17.2f, 18.8f, 1f)
            rrect(18.4f, 9.4f, 20.4f, 14.6f, 1f)
        }
    }

    val Mic: ImageVector by lazy {
        ico("Mic") {
            rrect(8.6f, 2.4f, 15.4f, 14.6f, 3.4f)
            path(fill = ink, pathFillType = ODD) {
                moveTo(5.6f, 10.6f); horizontalLineTo(7.6f); verticalLineTo(12.4f)
                curveToRelative(0f, 2.4f, 2f, 4.4f, 4.4f, 4.4f); reflectiveCurveToRelative(4.4f, -2f, 4.4f, -4.4f)
                verticalLineTo(10.6f); horizontalLineToRelative(2f); verticalLineTo(12.4f)
                curveToRelative(0f, 3.1f, -2.2f, 5.7f, -5.1f, 6.2f); verticalLineToRelative(2.8f)
                horizontalLineToRelative(-2.6f); verticalLineToRelative(-2.8f)
                curveToRelative(-2.9f, -0.5f, -5.1f, -3.1f, -5.1f, -6.2f); close()
            }
        }
    }

    val VideoPlay: ImageVector by lazy {
        ico("VideoPlay") {
            rrect(2.2f, 4.6f, 21.8f, 19.4f, 2.6f)
            poly(9.6f, 8.6f, 16.2f, 12f, 9.6f, 15.4f)
        }
    }

    val Scissors: ImageVector by lazy {
        ico("Scissors") {
            ring(6.2f, 18f, 3.4f, 1.6f)
            ring(6.2f, 6f, 3.4f, 1.6f)
            path(fill = ink) {
                moveTo(8.4f, 8.4f); lineTo(20.4f, 18.2f); lineTo(19f, 19.6f); lineTo(7.4f, 10.4f); close()
                moveTo(8.4f, 15.6f); lineTo(19f, 4.4f); lineTo(20.4f, 5.8f); lineTo(9.8f, 17f); close()
            }
        }
    }

    val FilmStrip: ImageVector by lazy {
        ico("FilmStrip") {
            frame(2.2f, 4.2f, 21.8f, 19.8f, 1.7f, 2.4f)
            rrect(4.6f, 6.6f, 7f, 9f, 0.7f)
            rrect(4.6f, 15f, 7f, 17.4f, 0.7f)
            rrect(17f, 6.6f, 19.4f, 9f, 0.7f)
            rrect(17f, 15f, 19.4f, 17.4f, 0.7f)
            poly(9.6f, 9.4f, 15.4f, 12f, 9.6f, 14.6f)
        }
    }

    /* -------------------------------- Network -------------------------------- */

    val Globe: ImageVector by lazy {
        ico("Globe") {
            frame(2.4f, 2.4f, 21.6f, 21.6f, 1.8f, 9.6f)
            rrect(10.9f, 2.4f, 13.1f, 21.6f, 0f)
            rrect(2.4f, 10.9f, 21.6f, 13.1f, 0f)
            rrect(4.4f, 7f, 19.6f, 8.4f, 0.7f)
            rrect(4.4f, 15.6f, 19.6f, 17f, 0.7f)
        }
    }

    val NetworkNodes: ImageVector by lazy {
        ico("NetworkNodes") {
            circle(12f, 5f, 2.8f)
            circle(5f, 17.4f, 2.8f)
            circle(19f, 17.4f, 2.8f)
            stroke(12f, 7.4f, 6.2f, 15.2f, 1.7f)
            stroke(12f, 7.4f, 17.8f, 15.2f, 1.7f)
            stroke(7.8f, 17.4f, 16.2f, 17.4f, 1.7f)
        }
    }

    val HttpWindow: ImageVector by lazy {
        ico("HttpWindow") {
            frame(2.2f, 3.4f, 21.8f, 20.6f, 1.7f, 2.6f)
            rrect(2.2f, 3.4f, 21.8f, 7.4f, 2f)
            rrect(5f, 10.4f, 18.6f, 12f, 0.8f)
            rrect(5f, 14.2f, 14.6f, 15.8f, 0.8f)
        }
    }

    val Pulse: ImageVector by lazy {
        ico("Pulse") {
            path(fill = ink) {
                moveTo(1.6f, 11f); horizontalLineTo(6f); lineTo(8.4f, 4.4f); lineTo(12.4f, 18.6f)
                lineTo(14.8f, 11f); horizontalLineTo(22.4f); verticalLineTo(13f); horizontalLineTo(16.4f)
                lineTo(12.4f, 21.4f); lineTo(8.4f, 7.6f); lineTo(7.6f, 13f); horizontalLineTo(1.6f); close()
            }
        }
    }

    val PaletteDots: ImageVector by lazy {
        ico("PaletteDots") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 2.4f); curveToRelative(-5.3f, 0f, -9.6f, 4.3f, -9.6f, 9.6f)
                reflectiveCurveToRelative(4.3f, 9.6f, 9.6f, 9.6f); curveToRelative(1.4f, 0f, 2.2f, -0.9f, 2.2f, -2f)
                curveToRelative(0f, -0.6f, -0.2f, -1f, -0.5f, -1.4f); curveToRelative(-0.3f, -0.4f, -0.5f, -0.8f, -0.5f, -1.3f)
                curveToRelative(0f, -1.1f, 0.9f, -1.9f, 2f, -1.9f); horizontalLineToRelative(2.3f)
                curveToRelative(2.9f, 0f, 5.3f, -2.4f, 5.3f, -5.3f); curveToRelative(0f, -4f, -4.8f, -7.3f, -10.8f, -7.3f); close()
                circle(7.4f, 9.4f, 1.5f)
                circle(11.2f, 6.6f, 1.5f)
                circle(15.4f, 8.2f, 1.5f)
                circle(6.6f, 14f, 1.5f)
            }
        }
    }

    /* -------------------------------- Business -------------------------------- */

    val Invoice: ImageVector by lazy {
        ico("Invoice") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(5f, 2.2f); horizontalLineTo(19f); verticalLineTo(21.8f); lineTo(17.4f, 20.6f)
                lineTo(15.8f, 21.8f); lineTo(14.2f, 20.6f); lineTo(12.6f, 21.8f); lineTo(11f, 20.6f)
                lineTo(9.4f, 21.8f); lineTo(7.8f, 20.6f); lineTo(6.2f, 21.8f); lineTo(5f, 20.8f); close()
                moveTo(7.2f, 4.4f); verticalLineTo(17.4f); horizontalLineTo(16.8f); verticalLineTo(4.4f); close()
            }
            path(fill = ink) {
                moveTo(8.8f, 6.4f); horizontalLineTo(15.2f); verticalLineTo(7.8f); horizontalLineTo(8.8f); close()
                moveTo(8.8f, 9.4f); horizontalLineTo(15.2f); verticalLineTo(10.8f); horizontalLineTo(8.8f); close()
                moveTo(8.8f, 12.4f); horizontalLineTo(12.6f); verticalLineTo(13.8f); horizontalLineTo(8.8f); close()
            }
        }
    }

    val Cart: ImageVector by lazy {
        ico("Cart") {
            path(fill = ink) {
                moveTo(1.6f, 3.2f); horizontalLineTo(5.4f); lineTo(8.2f, 14.2f); horizontalLineTo(18.4f)
                lineTo(21.4f, 6.4f); horizontalLineTo(7f); verticalLineTo(4.4f); horizontalLineTo(1.6f); close()
            }
            circle(8.6f, 19f, 2.2f)
            circle(17.6f, 19f, 2.2f)
        }
    }

    val BoxCube: ImageVector by lazy {
        ico("BoxCube") {
            poly(12f, 2f, 21.4f, 7f, 12f, 12f, 2.6f, 7f)
            path(fill = ink) {
                moveTo(3.4f, 8.6f); lineTo(11f, 12.8f); verticalLineTo(22f); lineTo(3.4f, 17.8f); close()
                moveTo(20.6f, 8.6f); lineTo(13f, 12.8f); verticalLineTo(22f); lineTo(20.6f, 17.8f); close()
            }
        }
    }

    val Barcode: ImageVector by lazy {
        ico("Barcode") {
            rrect(2f, 4.6f, 3.4f, 19.4f, 0.6f)
            rrect(4.6f, 4.6f, 6.6f, 19.4f, 0.6f)
            rrect(7.8f, 4.6f, 9.2f, 19.4f, 0.6f)
            rrect(10.4f, 4.6f, 12.6f, 19.4f, 0.6f)
            rrect(13.8f, 4.6f, 15.2f, 19.4f, 0.6f)
            rrect(16.4f, 4.6f, 18.6f, 19.4f, 0.6f)
            rrect(19.8f, 4.6f, 21.2f, 19.4f, 0.6f)
        }
    }

    val Coins: ImageVector by lazy {
        ico("Coins") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(9f, 3f); curveToRelative(-3.6f, 0f, -6.4f, 1.6f, -6.4f, 3.6f); verticalLineToRelative(4.4f)
                curveToRelative(0f, 2f, 2.8f, 3.6f, 6.4f, 3.6f); reflectiveCurveToRelative(6.4f, -1.6f, 6.4f, -3.6f)
                verticalLineTo(6.6f); curveToRelative(0f, -2f, -2.8f, -3.6f, -6.4f, -3.6f); close()
                moveTo(15.4f, 9.2f); verticalLineToRelative(1.8f); curveToRelative(0f, 2f, -2.8f, 3.6f, -6.4f, 3.6f)
                curveToRelative(-0.5f, 0f, -1f, 0f, -1.4f, -0.1f); verticalLineTo(15f)
                curveToRelative(0f, 2f, 2.8f, 3.6f, 6.4f, 3.6f); reflectiveCurveToRelative(6.4f, -1.6f, 6.4f, -3.6f)
                verticalLineToRelative(-2.2f); curveToRelative(0f, -2f, -2.8f, -3.6f, -5f, -3.6f); close()
            }
            path(fill = ink, pathFillType = ODD) {
                moveTo(21.4f, 3.4f); horizontalLineTo(23.4f); verticalLineTo(19.4f); horizontalLineTo(21.4f); close()
                moveTo(18.4f, 3.4f); horizontalLineTo(20.4f); verticalLineTo(19.4f); horizontalLineTo(18.4f); close()
            }
        }
    }

    val TrendUp: ImageVector by lazy {
        ico("TrendUp") {
            path(fill = ink) {
                moveTo(15.6f, 4f); lineTo(22.4f, 4f); lineTo(22.4f, 10.8f); lineTo(19.9f, 8.3f)
                lineTo(14.2f, 14f); lineTo(10f, 9.8f); lineTo(3.4f, 16.4f); lineTo(1.6f, 14.6f)
                lineTo(10f, 6.2f); lineTo(14.2f, 10.4f); lineTo(18.1f, 6.5f); close()
            }
        }
    }

    /* -------------------------------- Everyday -------------------------------- */

    val CalendarGrid: ImageVector by lazy {
        ico("CalendarGrid") {
            frame(2.2f, 4.6f, 21.8f, 21.8f, 1.7f, 2.6f)
            rrect(2.2f, 4.6f, 21.8f, 8.6f, 2.4f)
            rrect(6f, 1.8f, 8f, 6.2f, 0.9f)
            rrect(16f, 1.8f, 18f, 6.2f, 0.9f)
            rrect(5.4f, 11.4f, 8.4f, 14f, 0.7f)
            rrect(10.5f, 11.4f, 13.5f, 14f, 0.7f)
            rrect(15.6f, 11.4f, 18.6f, 14f, 0.7f)
            rrect(5.4f, 15.8f, 8.4f, 18.4f, 0.7f)
            rrect(10.5f, 15.8f, 13.5f, 18.4f, 0.7f)
        }
    }

    val ClockFace: ImageVector by lazy {
        ico("ClockFace") {
            ring(12f, 12f, 9.6f, 7.8f)
            path(fill = ink) {
                moveTo(11f, 6.4f); horizontalLineTo(13f); verticalLineTo(12.6f); horizontalLineTo(17.4f)
                verticalLineTo(14.4f); horizontalLineTo(11f); close()
            }
        }
    }

    val Grid4: ImageVector by lazy {
        ico("Grid4") {
            rrect(3f, 3f, 10.4f, 10.4f, 2.4f)
            rrect(13.6f, 3f, 21f, 10.4f, 2.4f)
            rrect(3f, 13.6f, 10.4f, 21f, 2.4f)
            rrect(13.6f, 13.6f, 21f, 21f, 2.4f)
        }
    }

    val Bolt: ImageVector by lazy {
        ico("Bolt") {
            path(fill = ink) {
                moveTo(13.6f, 1.8f); lineTo(4.4f, 13.4f); horizontalLineTo(10.2f); lineTo(9.4f, 22.2f)
                lineTo(19.6f, 10.2f); horizontalLineTo(13.4f); close()
            }
        }
    }

    val ShieldCheck: ImageVector by lazy {
        ico("ShieldCheck") {
            path(fill = ink, pathFillType = ODD) {
                moveTo(12f, 1.8f); lineTo(3.6f, 5.2f); verticalLineTo(11.6f)
                curveToRelative(0f, 5.4f, 3.6f, 8.6f, 8.4f, 10.6f); curveToRelative(4.8f, -2f, 8.4f, -5.2f, 8.4f, -10.6f)
                verticalLineTo(5.2f); close()
                moveTo(12f, 4.4f); lineTo(18.2f, 6.8f); verticalLineTo(11.6f)
                curveToRelative(0f, 3.8f, -2.3f, 6.2f, -6.2f, 7.9f); curveToRelative(-3.9f, -1.7f, -6.2f, -4.1f, -6.2f, -7.9f)
                verticalLineTo(6.8f); close()
            }
            path(fill = ink) {
                moveTo(8.2f, 11.4f); lineTo(10.8f, 14f); lineTo(16f, 8.6f); lineTo(17.4f, 10f)
                lineTo(10.8f, 16.8f); lineTo(6.8f, 12.8f); close()
            }
        }
    }

    val GaugeRuler: ImageVector by lazy {
        ico("GaugeRuler") {
            path(fill = ink) {
                moveTo(2.2f, 15.4f); lineTo(13.4f, 4.2f); lineTo(19.8f, 10.6f); lineTo(8.6f, 21.8f); close()
            }
            path(fill = SolidColor(Color.Black), pathFillType = ODD) {
                moveTo(7.2f, 13.2f); lineTo(9f, 11.4f); lineTo(10.2f, 12.6f); lineTo(8.4f, 14.4f); close()
                moveTo(10.4f, 10f); lineTo(12.2f, 8.2f); lineTo(13.4f, 9.4f); lineTo(11.6f, 11.2f); close()
                moveTo(13.6f, 6.8f); lineTo(15.4f, 5f); lineTo(16.6f, 6.2f); lineTo(14.8f, 8f); close()
            }
        }
    }
}
