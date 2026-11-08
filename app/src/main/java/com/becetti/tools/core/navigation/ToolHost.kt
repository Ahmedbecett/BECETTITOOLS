package com.becetti.tools.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.localization.toolName
import com.becetti.tools.design.components.EmptyState
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.domain.models.ToolSpec
import com.becetti.tools.features.age.AgeCalculatorScreen
import com.becetti.tools.features.calculator.CalculatorScreen
import com.becetti.tools.features.converter.UnitConverterScreen
import com.becetti.tools.features.currency.CurrencyScreen
import com.becetti.tools.features.device.DeviceInfoScreen
import com.becetti.tools.features.health.BmiCalculatorScreen
import com.becetti.tools.features.image.ImageCompressorScreen
import com.becetti.tools.features.notes.NotesScreen
import com.becetti.tools.features.ocr.OcrScreen
import com.becetti.tools.features.password.PasswordGenScreen
import com.becetti.tools.features.password.PasswordStrengthScreen
import com.becetti.tools.features.pdf.ImageToPdfScreen
import com.becetti.tools.features.pdf.PdfMergerScreen
import com.becetti.tools.features.percentage.PercentageScreen
import com.becetti.tools.features.qr.QrGeneratorScreen
import com.becetti.tools.features.qr.QrScannerScreen
import com.becetti.tools.features.text.TextToolkitScreen
import com.becetti.tools.features.timer.StopwatchTimerScreen

/**
 * Single entry point for every tool destination: records usage, then renders the matching screen.
 * Keeping the dispatch here means the registry, navigation and UI can never drift apart.
 */
@Composable
fun ToolHost(spec: ToolSpec, app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette

    LaunchedEffect(spec.id) {
        runCatching { app.toolRepository.markRecent(spec.id) }
        app.activityStore.log("tool", s.toolName(spec.id))
    }

    when (spec.id) {
        /* ------------------------------- Everyday / legacy ------------------------------- */
        "calc_scientific" -> CalculatorScreen(app)
        "unit_converter" -> UnitConverterScreen()
        "currency" -> CurrencyScreen(app)
        "percentage" -> PercentageScreen()
        "age" -> AgeCalculatorScreen()
        "qr_generator" -> QrGeneratorScreen()
        "qr_scanner" -> QrScannerScreen()
        "password_gen" -> PasswordGenScreen()
        "password_strength" -> PasswordStrengthScreen()
        "image_to_pdf" -> ImageToPdfScreen()
        "pdf_merger" -> PdfMergerScreen()
        "image_compressor" -> ImageCompressorScreen()
        "ocr" -> OcrScreen(app)
        "text_toolkit" -> TextToolkitScreen()
        "notes" -> NotesScreen(app, nav)
        "bmi_calculator" -> BmiCalculatorScreen()
        "stopwatch_timer" -> StopwatchTimerScreen()
        "device_info" -> DeviceInfoScreen()

        else -> EmptyState(
            title = s.toolName(spec.id),
            message = s.gEmpty,
            icon = spec.icon,
            accent = palette.textSecondary
        )
    }
}
