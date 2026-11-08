package com.becetti.tools.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.design.components.EmptyState
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.domain.models.ToolSpec
import com.becetti.tools.features.age.AgeCalculatorScreen
import com.becetti.tools.features.calculator.CalculatorScreen
import com.becetti.tools.features.converter.UnitConverterScreen
import com.becetti.tools.features.currency.CurrencyScreen
import com.becetti.tools.features.device.DeviceInfoScreen
import com.becetti.tools.features.developer.ApiClientScreen
import com.becetti.tools.features.developer.Base64Screen
import com.becetti.tools.features.developer.DiffScreen
import com.becetti.tools.features.developer.GitHubScreen
import com.becetti.tools.features.developer.HashScreen
import com.becetti.tools.features.developer.JsonXmlScreen
import com.becetti.tools.features.developer.RegexTestScreen
import com.becetti.tools.features.developer.UrlCodecScreen
import com.becetti.tools.features.developer.UuidScreen
import com.becetti.tools.features.network.ColorConverterScreen
import com.becetti.tools.features.network.DiagnosticsScreen
import com.becetti.tools.features.network.DnsLookupScreen
import com.becetti.tools.features.network.HttpHeadersScreen
import com.becetti.tools.features.network.IpInfoScreen
import com.becetti.tools.features.network.LinkInspectorScreen
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

        /* -------------------------------- Developer Studio -------------------------------- */
        "dev_json" -> JsonXmlScreen()
        "dev_base64" -> Base64Screen()
        "dev_hash" -> HashScreen()
        "dev_uuid" -> UuidScreen()
        "dev_regex" -> RegexTestScreen()
        "dev_api" -> ApiClientScreen()
        "dev_diff" -> DiffScreen()
        "dev_url" -> UrlCodecScreen()
        "dev_github" -> GitHubScreen()

        /* --------------------------------- Web & Network ---------------------------------- */
        "net_dns" -> DnsLookupScreen()
        "net_ip" -> IpInfoScreen()
        "net_headers" -> HttpHeadersScreen()
        "net_diagnostics" -> DiagnosticsScreen()
        "net_link" -> LinkInspectorScreen()
        "net_color" -> ColorConverterScreen()

        else -> EmptyState(
            title = s.toolName(spec.id),
            message = s.gEmpty,
            icon = spec.icon,
            accent = palette.textSecondary
        )
    }
}
