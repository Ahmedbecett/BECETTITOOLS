package com.becetti.tools.core.security

import java.security.SecureRandom

object SecurityUtils {
    fun secureRandomPassword(length: Int, useUpper: Boolean, useLower: Boolean, useDigits: Boolean, useSymbols: Boolean): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()-_=+[]{}|;:,.<>?"
        var chars = ""
        if (useUpper) chars += upper
        if (useLower) chars += lower
        if (useDigits) chars += digits
        if (useSymbols) chars += symbols
        if (chars.isEmpty()) chars = lower + digits
        val rnd = SecureRandom()
        return (1..length).map { chars[rnd.nextInt(chars.length)] }.joinToString("")
    }

    fun passwordStrength(pw: String): Int {
        var score = 0
        if (pw.length >= 8) score++
        if (pw.length >= 12) score++
        if (pw.any { it.isUpperCase() }) score++
        if (pw.any { it.isDigit() }) score++
        if (pw.any { !it.isLetterOrDigit() }) score++
        return score.coerceIn(0,5)
    }
}
