package com.becetti.tools.domain.models

data class ToolItem(val id: String, val name: String, val description: String, val category: ToolCategory, val icon: String, val route: String)
enum class ToolCategory { CALCULATOR_CONVERTERS, QR_SECURITY, DOCUMENTS_FILES, TEXT_UTILITIES, IMAGE_UTILITIES, DATE_TIME }

object ToolCatalog {
    val all = listOf(
        ToolItem("calc_scientific","Scientific Calculator","Advanced calc with history", ToolCategory.CALCULATOR_CONVERTERS,"calculate","calc_scientific"),
        ToolItem("unit_converter","Unit Converter","Length, weight, temp, area, volume, speed", ToolCategory.CALCULATOR_CONVERTERS,"straighten","unit_converter"),
        ToolItem("currency","Currency Converter","Live rates with offline cache", ToolCategory.CALCULATOR_CONVERTERS,"currency_exchange","currency"),
        ToolItem("percentage","Percentage Calculator","% of number, increase/decrease", ToolCategory.CALCULATOR_CONVERTERS,"percent","percentage"),
        ToolItem("age","Age Calculator","Age & date difference", ToolCategory.DATE_TIME,"cake","age"),
        ToolItem("qr_generator","QR Generator","Text, URL, WiFi, contact", ToolCategory.QR_SECURITY,"qr_code","qr_generator"),
        ToolItem("qr_scanner","QR Scanner","Camera scan with confirmation", ToolCategory.QR_SECURITY,"qr_code_scanner","qr_scanner"),
        ToolItem("password_gen","Password Generator","Secure local generation", ToolCategory.QR_SECURITY,"password","password_gen"),
        ToolItem("password_strength","Password Strength","Local analysis", ToolCategory.QR_SECURITY,"shield","password_strength"),
        ToolItem("image_to_pdf","Image to PDF","Multiple images to PDF", ToolCategory.DOCUMENTS_FILES,"picture_as_pdf","image_to_pdf"),
        ToolItem("pdf_merger","PDF Merger","Merge PDFs", ToolCategory.DOCUMENTS_FILES,"merge","pdf_merger"),
        ToolItem("image_compressor","Image Compressor","Quality & preview", ToolCategory.IMAGE_UTILITIES,"compress","image_compressor"),
        ToolItem("ocr","OCR Text Extractor","Arabic/FR/EN", ToolCategory.DOCUMENTS_FILES,"text_fields","ocr"),
        ToolItem("text_toolkit","Text Toolkit","Word count & transforms", ToolCategory.TEXT_UTILITIES,"text_format","text_toolkit"),
        ToolItem("notes","Notes Manager","Local notes with search", ToolCategory.TEXT_UTILITIES,"note","notes"),
    )
    fun byCategory(c: ToolCategory) = all.filter { it.category == c }
}
