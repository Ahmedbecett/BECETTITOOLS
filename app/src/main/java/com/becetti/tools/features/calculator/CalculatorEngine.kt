package com.becetti.tools.features.calculator

import kotlin.math.*

object CalculatorEngine {
    fun evaluate(expr: String): Double {
        if (expr.isBlank()) return 0.0
        var sanitized = expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("√(", "sqrt(")
            .replace("√", "sqrt")
        val openCount = sanitized.count { it == '(' }
        val closeCount = sanitized.count { it == ')' }
        if (openCount > closeCount) {
            sanitized += ")".repeat(openCount - closeCount)
        }
        val res = evalExpression(sanitized)
        return if (res.isInfinite() || res.isNaN()) Double.NaN else res
    }

    fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        val rounded = (value * 1e10).roundToLong() / 1e10
        return if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            rounded.toString()
        }
    }

    private fun evalExpression(s: String): Double {
        return try {
            SimpleParser(s).parse()
        } catch (e: Exception) {
            Double.NaN
        }
    }

    private class SimpleParser(val input: String) {
        var pos = 0

        fun parse(): Double {
            val v = parseAdd()
            skip()
            if (pos < input.length) error("Unexpected token at $pos")
            return v
        }

        fun parseAdd(): Double {
            var v = parseMul()
            while (true) {
                skip()
                if (eat('+')) v += parseMul()
                else if (eat('-')) v -= parseMul()
                else break
            }
            return v
        }

        fun parseMul(): Double {
            var v = parsePow()
            while (true) {
                skip()
                if (eat('*')) {
                    v *= parsePow()
                } else if (eat('/')) {
                    val d = parsePow()
                    if (d == 0.0) return Double.NaN
                    v /= d
                } else if (eat('%')) {
                    val d = parsePow()
                    if (d == 0.0) return Double.NaN
                    v %= d
                } else break
            }
            return v
        }

        fun parsePow(): Double {
            var v = parseUnary()
            skip()
            if (eat('^')) v = v.pow(parsePow())
            return v
        }

        fun parseUnary(): Double {
            skip()
            if (eat('+')) return parseUnary()
            if (eat('-')) return -parseUnary()
            return parsePrimary()
        }

        fun parsePrimary(): Double {
            skip()
            if (eat('(')) {
                val v = parseAdd()
                eat(')')
                return v
            }
            if (peekWord("sqrt")) {
                consume("sqrt")
                val v = if (eat('(')) { val a = parseAdd(); eat(')'); a } else parseUnary()
                return if (v < 0) Double.NaN else sqrt(v)
            }
            if (peekWord("sin")) {
                consume("sin"); eat('('); val v = parseAdd(); eat(')')
                return sin(Math.toRadians(v))
            }
            if (peekWord("cos")) {
                consume("cos"); eat('('); val v = parseAdd(); eat(')')
                return cos(Math.toRadians(v))
            }
            if (peekWord("tan")) {
                consume("tan"); eat('('); val v = parseAdd(); eat(')')
                return tan(Math.toRadians(v))
            }
            if (peekWord("log")) {
                consume("log"); eat('('); val v = parseAdd(); eat(')')
                return if (v <= 0) Double.NaN else log10(v)
            }
            if (peekWord("ln")) {
                consume("ln"); eat('('); val v = parseAdd(); eat(')')
                return if (v <= 0) Double.NaN else ln(v)
            }
            if (peekWord("pi")) { consume("pi"); return PI }
            if (peekWord("e")) { consume("e"); return E }
            return parseNumber()
        }

        fun parseNumber(): Double {
            skip()
            val s = pos
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
            if (s == pos) error("Expected number")
            return input.substring(s, pos).toDouble()
        }

        fun skip() {
            while (pos < input.length && input[pos].isWhitespace()) pos++
        }

        fun eat(c: Char): Boolean {
            skip()
            if (pos < input.length && input[pos] == c) {
                pos++
                return true
            }
            return false
        }

        fun peekWord(w: String) = input.regionMatches(pos, w, 0, w.length, ignoreCase = true)
        fun consume(w: String) { pos += w.length }
    }
}
