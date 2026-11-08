package com.becetti.tools.domain.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.becetti.tools.core.localization.AppStrings
import com.becetti.tools.core.localization.toolDescription
import com.becetti.tools.core.localization.toolName
import com.becetti.tools.design.components.BecettiGlyphs
import com.becetti.tools.design.components.BecettiIcons
import com.becetti.tools.design.theme.ToolAccents

/** The seven professional modules of the toolkit. */
enum class ToolGroup(val id: String) {
    DEVELOPER("developer"),
    PROJECTS("projects"),
    FILES("files"),
    MEDIA("media"),
    NETWORK("network"),
    BUSINESS("business"),
    EVERYDAY("everyday");

    val accent: Color
        get() = when (this) {
            DEVELOPER -> ToolAccents.Developer
            PROJECTS -> ToolAccents.Projects
            FILES -> ToolAccents.Files
            MEDIA -> ToolAccents.Media
            NETWORK -> ToolAccents.Network
            BUSINESS -> ToolAccents.Business
            EVERYDAY -> ToolAccents.Everyday
        }

    val icon: ImageVector
        get() = when (this) {
            DEVELOPER -> BecettiGlyphs.CodeBrackets
            PROJECTS -> BecettiGlyphs.Kanban
            FILES -> BecettiGlyphs.FolderOpen
            MEDIA -> BecettiGlyphs.ImageFrame
            NETWORK -> BecettiGlyphs.Globe
            BUSINESS -> BecettiGlyphs.Briefcase
            EVERYDAY -> BecettiGlyphs.Grid4
        }
}

/**
 * Single source of truth for every tool: id, navigation route, module, glyph, search keywords and
 * whether it is featured on the home dashboard. Titles/descriptions are resolved per language.
 */
data class ToolSpec(
    val id: String,
    val route: String,
    val group: ToolGroup,
    val icon: ImageVector,
    val keywords: String = "",
    val featured: Boolean = false
)

object ToolRegistry {

    val all: List<ToolSpec> = listOf(
        /* ------------------------------- Developer Studio ------------------------------- */
        ToolSpec("dev_json", "dev_json", ToolGroup.DEVELOPER, BecettiGlyphs.Braces, "json xml format pretty minify validate convert تنسيق تحقق", true),
        ToolSpec("dev_api", "dev_api", ToolGroup.DEVELOPER, BecettiGlyphs.CloudCode, "rest api http request get post json header api test", true),
        ToolSpec("dev_hash", "dev_hash", ToolGroup.DEVELOPER, BecettiGlyphs.Hash, "sha sha1 sha256 sha512 md5 hash digest تجزئة"),
        ToolSpec("dev_base64", "dev_base64", ToolGroup.DEVELOPER, BecettiGlyphs.CopyStack, "base64 encode decode file text ترميز"),
        ToolSpec("dev_uuid", "dev_uuid", ToolGroup.DEVELOPER, BecettiGlyphs.IdCard, "uuid guid random id معرف"),
        ToolSpec("dev_regex", "dev_regex", ToolGroup.DEVELOPER, BecettiGlyphs.RegexStar, "regex regular expression pattern match replace تعبير نمطي"),
        ToolSpec("dev_diff", "dev_diff", ToolGroup.DEVELOPER, BecettiGlyphs.SplitCompare, "diff compare code text merge مقارنة"),
        ToolSpec("dev_url", "dev_url", ToolGroup.DEVELOPER, BecettiGlyphs.LinkChain, "url uri encode decode query parameter رابط ترميز"),
        ToolSpec("dev_github", "dev_github", ToolGroup.DEVELOPER, BecettiGlyphs.GithubMark, "github repo profile release api مستودع"),

        /* --------------------------------- File Master ---------------------------------- */
        ToolSpec("file_manager", "file_manager", ToolGroup.FILES, BecettiGlyphs.FolderOpen, "file manager browse folder rename delete copy ملفات", true),
        ToolSpec("file_zip", "file_zip", ToolGroup.FILES, BecettiGlyphs.ArchiveZip, "zip archive compress extract ضغط أرشيف", true),
        ToolSpec("file_pdf", "file_pdf", ToolGroup.FILES, BecettiIcons.PdfDoc, "pdf split pages merge extract تقسيم صفحات"),
        ToolSpec("file_storage", "file_storage", ToolGroup.FILES, BecettiGlyphs.StoragePie, "storage analyzer space size تخزين مساحة", true),
        ToolSpec("file_duplicates", "file_duplicates", ToolGroup.FILES, BecettiGlyphs.CopyStack, "duplicate finder hash identical مكرر"),
        ToolSpec("file_compare", "file_compare", ToolGroup.FILES, BecettiGlyphs.SplitCompare, "file compare binary text diff مقارنة ملفات"),
        ToolSpec("file_convert", "file_convert", ToolGroup.FILES, BecettiGlyphs.Invoice, "convert document text markdown csv pdf تحويل مستند"),
        ToolSpec("image_to_pdf", "image_to_pdf", ToolGroup.FILES, BecettiIcons.PdfDoc, "image pdf photo scan صور"),
        ToolSpec("pdf_merger", "pdf_merger", ToolGroup.FILES, BecettiIcons.PdfMerge, "pdf merge combine دمج"),
        ToolSpec("ocr", "ocr", ToolGroup.FILES, BecettiIcons.OcrScan, "ocr text extract image مسح نص"),

        /* ------------------------------ Image & Media Studio ---------------------------- */
        ToolSpec("media_image", "media_image", ToolGroup.MEDIA, BecettiGlyphs.Crop, "image resize crop convert watermark compress صورة قص", true),
        ToolSpec("image_compressor", "image_compressor", ToolGroup.MEDIA, BecettiIcons.ImageCompress, "image compress quality jpeg ضغط صورة"),
        ToolSpec("media_audio", "media_audio", ToolGroup.MEDIA, BecettiGlyphs.Mic, "audio record trim wav mp3 صوت تسجيل", true),
        ToolSpec("media_video", "media_video", ToolGroup.MEDIA, BecettiGlyphs.VideoPlay, "video trim thumbnail frame mp4 فيديو اقتطاع", true),

        /* --------------------------------- Web & Network -------------------------------- */
        ToolSpec("net_dns", "net_dns", ToolGroup.NETWORK, BecettiGlyphs.NetworkNodes, "dns lookup resolve host ip domain نطاق"),
        ToolSpec("net_ip", "net_ip", ToolGroup.NETWORK, BecettiGlyphs.Globe, "ip address public local info عنوان", true),
        ToolSpec("net_headers", "net_headers", ToolGroup.NETWORK, BecettiGlyphs.HttpWindow, "http header response status redirect ترويسة"),
        ToolSpec("net_diagnostics", "net_diagnostics", ToolGroup.NETWORK, BecettiGlyphs.Pulse, "connection diagnostics ping latency speed اتصال تشخيص"),
        ToolSpec("net_link", "net_link", ToolGroup.NETWORK, BecettiGlyphs.LinkChain, "link inspector url parse safety رابط فحص"),
        ToolSpec("net_color", "net_color", ToolGroup.NETWORK, BecettiGlyphs.PaletteDots, "color converter hex rgb hsl cmyk لون تحويل"),
        ToolSpec("password_gen", "password_gen", ToolGroup.NETWORK, BecettiIcons.KeyPassword, "password generator secure random كلمة مرور"),
        ToolSpec("password_strength", "password_strength", ToolGroup.NETWORK, BecettiIcons.ShieldSecurity, "password strength entropy security أمان"),

        /* -------------------------------- Business Toolkit ------------------------------ */
        ToolSpec("biz_invoice", "biz_invoice", ToolGroup.BUSINESS, BecettiGlyphs.Invoice, "invoice bill pdf export فاتورة", true),
        ToolSpec("biz_quote", "biz_quote", ToolGroup.BUSINESS, BecettiGlyphs.Invoice, "quotation quote offer devis عرض سعر"),
        ToolSpec("biz_customers", "biz_customers", ToolGroup.BUSINESS, BecettiGlyphs.Briefcase, "customer client contact عميل"),
        ToolSpec("biz_products", "biz_products", ToolGroup.BUSINESS, BecettiGlyphs.BoxCube, "product catalog price sku منتج"),
        ToolSpec("biz_inventory", "biz_inventory", ToolGroup.BUSINESS, BecettiGlyphs.BoxCube, "inventory stock movement مخزون", true),
        ToolSpec("biz_barcode", "biz_barcode", ToolGroup.BUSINESS, BecettiGlyphs.Barcode, "barcode ean code128 qr label باركود", true),
        ToolSpec("biz_report", "biz_report", ToolGroup.BUSINESS, BecettiGlyphs.BarChart, "sales report revenue csv تقرير مبيعات"),
        ToolSpec("biz_expenses", "biz_expenses", ToolGroup.BUSINESS, BecettiGlyphs.Coins, "expense profit margin cost مصاريف ربح"),

        /* --------------------------------- Everyday Tools ------------------------------- */
        ToolSpec("calc_scientific", "calc_scientific", ToolGroup.EVERYDAY, BecettiIcons.Calculator, "calculator scientific حاسبة", true),
        ToolSpec("unit_converter", "unit_converter", ToolGroup.EVERYDAY, BecettiGlyphs.GaugeRuler, "unit converter length weight temperature وحدة تحويل", true),
        ToolSpec("currency", "currency", ToolGroup.EVERYDAY, BecettiIcons.Currency, "currency exchange rate عملة صرف"),
        ToolSpec("percentage", "percentage", ToolGroup.EVERYDAY, BecettiIcons.Percentage, "percentage discount نسبة"),
        ToolSpec("age", "age", ToolGroup.EVERYDAY, BecettiIcons.AgeCake, "age birthday date عمر"),
        ToolSpec("date_time", "date_time", ToolGroup.EVERYDAY, BecettiGlyphs.ClockFace, "date time timestamp week duration تاريخ وقت", true),
        ToolSpec("calendar", "calendar", ToolGroup.EVERYDAY, BecettiGlyphs.CalendarGrid, "calendar month note تقويم"),
        ToolSpec("stopwatch_timer", "stopwatch_timer", ToolGroup.EVERYDAY, BecettiIcons.StopwatchTimer, "stopwatch timer countdown مؤقت ساعة"),
        ToolSpec("qr_generator", "qr_generator", ToolGroup.EVERYDAY, BecettiIcons.QrCode, "qr code generator رمز"),
        ToolSpec("qr_scanner", "qr_scanner", ToolGroup.EVERYDAY, BecettiIcons.QrScanner, "qr scan camera scanner مسح"),
        ToolSpec("notes", "notes", ToolGroup.EVERYDAY, BecettiIcons.NotesDoc, "notes memo ملاحظات"),
        ToolSpec("text_toolkit", "text_toolkit", ToolGroup.EVERYDAY, BecettiIcons.TextTools, "text word count case convert نص"),
        ToolSpec("bmi_calculator", "bmi_calculator", ToolGroup.EVERYDAY, BecettiIcons.BmiHealth, "bmi health weight صحة"),
        ToolSpec("device_info", "device_info", ToolGroup.EVERYDAY, BecettiIcons.DeviceInfo, "device system info جهاز")
    )

    val featured: List<ToolSpec> get() = all.filter { it.featured }

    fun byGroup(g: ToolGroup): List<ToolSpec> = all.filter { it.group == g }

    fun byId(id: String): ToolSpec? = all.firstOrNull { it.id == id }

    fun byRoute(route: String?): ToolSpec? = all.firstOrNull { it.route == route }

    fun countFor(g: ToolGroup): Int = byGroup(g).size

    /** Language-aware search over title, description and keywords. */
    fun search(strings: AppStrings, query: String): List<ToolSpec> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return all.filter { spec ->
            strings.toolName(spec.id).lowercase().contains(q) ||
                strings.toolDescription(spec.id).lowercase().contains(q) ||
                spec.id.contains(q) ||
                spec.keywords.lowercase().contains(q)
        }
    }

    fun groups(): List<ToolGroup> = ToolGroup.values().toList()
}
