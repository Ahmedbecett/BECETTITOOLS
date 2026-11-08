package com.becetti.tools.core.navigation

sealed class Route(val route: String) {
    object Home : Route("home")

    /* ---- v2 bottom navigation destinations ---- */
    object Developers : Route("tab_developers")
    object Projects : Route("tab_projects")
    object ToolsTab : Route("tab_tools")
    object Profile : Route("tab_profile")
    object Category : Route("category/{groupId}") {
        fun create(groupId: String) = "category/$groupId"
    }
    object ProjectDetail : Route("project/{projectId}") {
        fun create(projectId: String) = "project/$projectId"
    }
    object Categories : Route("categories")
    object Favorites : Route("favorites")
    object History : Route("history")
    object Settings : Route("settings")

    // 18 smart tools
    object ScientificCalculator : Route("calc_scientific")
    object UnitConverter : Route("unit_converter")
    object CurrencyConverter : Route("currency")
    object PercentageCalculator : Route("percentage")
    object AgeCalculator : Route("age")
    object QrGenerator : Route("qr_generator")
    object QrScanner : Route("qr_scanner")
    object PasswordGen : Route("password_gen")
    object PasswordStrength : Route("password_strength")
    object ImageToPdf : Route("image_to_pdf")
    object PdfMerger : Route("pdf_merger")
    object ImageCompressor : Route("image_compressor")
    object Ocr : Route("ocr")
    object TextToolkit : Route("text_toolkit")
    object Notes : Route("notes")
    object BmiCalculator : Route("bmi_calculator")
    object StopwatchTimer : Route("stopwatch_timer")
    object DeviceInfo : Route("device_info")
    object NoteDetail : Route("note_detail/{id}") { fun create(id: Long) = "note_detail/$id" }
}
