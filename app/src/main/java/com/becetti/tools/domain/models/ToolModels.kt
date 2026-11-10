package com.becetti.tools.domain.models

import com.becetti.tools.core.localization.AppStrings

data class ToolItem(
    val id: String,
    val name: String,
    val description: String,
    val category: ToolCategory,
    val icon: String,
    val route: String
) {
    fun localizedName(strings: AppStrings): String = strings.tools[id]?.name ?: name
    fun localizedDescription(strings: AppStrings): String = strings.tools[id]?.description ?: description
}

enum class ToolCategory {
    CALCULATOR_CONVERTERS,
    QR_SECURITY,
    DOCUMENTS_FILES,
    TEXT_UTILITIES,
    IMAGE_UTILITIES,
    DATE_TIME
}

object ToolCatalog {
    val all = listOf(
        ToolItem("calc_scientific", "Scientific Calculator", "Advanced calc with history", ToolCategory.CALCULATOR_CONVERTERS, "calculate", "calc_scientific"),
        ToolItem("unit_converter", "Unit Converter", "Length, weight, temp, area, volume, speed & data", ToolCategory.CALCULATOR_CONVERTERS, "straighten", "unit_converter"),
        ToolItem("currency", "Currency Converter", "Live rates with offline cache", ToolCategory.CALCULATOR_CONVERTERS, "currency_exchange", "currency"),
        ToolItem("percentage", "Percentage Calculator", "% of number, increase/decrease", ToolCategory.CALCULATOR_CONVERTERS, "percent", "percentage"),
        ToolItem("age", "Age Calculator", "Age & date difference", ToolCategory.DATE_TIME, "cake", "age"),
        ToolItem("qr_generator", "QR Generator", "Text, URL, WiFi, contact", ToolCategory.QR_SECURITY, "qr_code", "qr_generator"),
        ToolItem("qr_scanner", "QR Scanner", "Camera & image scan with confirmation", ToolCategory.QR_SECURITY, "qr_code_scanner", "qr_scanner"),
        ToolItem("password_gen", "Password Generator", "Secure local generation", ToolCategory.QR_SECURITY, "password", "password_gen"),
        ToolItem("password_strength", "Password Strength", "Local security analysis", ToolCategory.QR_SECURITY, "shield", "password_strength"),
        ToolItem("image_to_pdf", "Image to PDF", "Multiple images to PDF", ToolCategory.DOCUMENTS_FILES, "picture_as_pdf", "image_to_pdf"),
        ToolItem("pdf_merger", "PDF Merger", "Merge PDFs locally", ToolCategory.DOCUMENTS_FILES, "merge", "pdf_merger"),
        ToolItem("image_compressor", "Image Compressor", "Quality & preview", ToolCategory.IMAGE_UTILITIES, "compress", "image_compressor"),
        ToolItem("ocr", "OCR Text Extractor", "Extract text from images", ToolCategory.DOCUMENTS_FILES, "text_fields", "ocr"),
        ToolItem("text_toolkit", "Text Toolkit", "Word count, transforms & Base64", ToolCategory.TEXT_UTILITIES, "text_format", "text_toolkit"),
        ToolItem("notes", "Notes Manager", "Local notes with search", ToolCategory.TEXT_UTILITIES, "note", "notes"),
        ToolItem("bmi_calculator", "BMI & Health Calculator", "Body Mass Index, ideal weight & water", ToolCategory.DATE_TIME, "health", "bmi_calculator"),
        ToolItem("stopwatch_timer", "Stopwatch & Timer", "Millisecond lap stopwatch & countdown", ToolCategory.DATE_TIME, "timer", "stopwatch_timer"),
        ToolItem("device_info", "Device & System Inspector", "RAM, storage, CPU, battery & screen", ToolCategory.DATE_TIME, "device", "device_info"),
        ToolItem("wifi_transfer", "Wi-Fi File Transfer", "Send and receive selected files on your local Wi-Fi", ToolCategory.DOCUMENTS_FILES, "wifi", "wifi_transfer")
    )

    fun byCategory(c: ToolCategory) = all.filter { it.category == c }
}
