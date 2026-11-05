package com.becetti.tools.features.calculator

import kotlin.math.*

object CalculatorEngine {
    fun evaluate(expr: String): Double {
        // Simple safe evaluator using Shunting-yard for + - * / ^ % sqrt sin cos tan log ln
        // For brevity, delegate to exp4j-like manual; here we support basic + Android's JS engine fallback via simple parser.
        // This is a minimal implementation covering spec: + - * / % sqrt pow trig
        val sanitized = expr.replace("×","*").replace("÷","/").replace("√","sqrt")
        return evalExpression(sanitized)
    }
    private fun evalExpression(s: String): Double {
        // Use javax.script if available? Not on Android. So we implement recursive descent for subset.
        // Lightweight: use Kotlin's kotlin.math and brute parse with operator precedence.
        // For production, replace with exp4j lib (add dependency if needed). Here implement basic.
        return try {
            SimpleParser(s).parse()
        } catch (e: Exception) { Double.NaN }
    }
    private class SimpleParser(val input: String) {
        var pos=0
        fun parse(): Double { val v=parseAdd(); if(pos<input.length) error("Unexpected"); return v }
        fun parseAdd(): Double { var v=parseMul(); while(true){ skip(); if(eat('+')) v+=parseMul() else if(eat('-')) v-=parseMul() else break }; return v }
        fun parseMul(): Double { var v=parsePow(); while(true){ skip(); if(eat('*')) v*=parsePow() else if(eat('/')) v/=parsePow() else if(eat('%')) v%=parsePow() else break }; return v }
        fun parsePow(): Double { var v=parseUnary(); skip(); if(eat('^')) v=v.pow(parsePow()); return v }
        fun parseUnary(): Double { skip(); if(eat('+')) return parseUnary(); if(eat('-')) return -parseUnary(); return parsePrimary() }
        fun parsePrimary(): Double {
            skip()
            if(eat('(')){ val v=parseAdd(); eat(')'); return v }
            if(peekWord("sqrt")){ consume("sqrt"); eat('('); val v=parseAdd(); eat(')'); return sqrt(v) }
            if(peekWord("sin")){ consume("sin"); eat('('); val v=parseAdd(); eat(')'); return sin(Math.toRadians(v)) }
            if(peekWord("cos")){ consume("cos"); eat('('); val v=parseAdd(); eat(')'); return cos(Math.toRadians(v)) }
            if(peekWord("tan")){ consume("tan"); eat('('); val v=parseAdd(); eat(')'); return tan(Math.toRadians(v)) }
            if(peekWord("log")){ consume("log"); eat('('); val v=parseAdd(); eat(')'); return log10(v) }
            if(peekWord("ln")){ consume("ln"); eat('('); val v=parseAdd(); eat(')'); return ln(v) }
            if(peekWord("pi")){ consume("pi"); return PI }
            if(peekWord("e")){ consume("e"); return E }
            return parseNumber()
        }
        fun parseNumber(): Double { skip(); val s = pos; while(pos<input.length && (input[pos].isDigit()||input[pos]=='.')) pos++; return input.substring(s,pos).toDouble() }
        fun skip(){ while(pos<input.length && input[pos].isWhitespace()) pos++ }
        fun eat(c: Char): Boolean { skip(); if(pos<input.length && input[pos]==c){pos++; return true}; return false }
        fun peekWord(w:String)=input.regionMatches(pos,w,0,w.length,ignoreCase=true)
        fun consume(w:String){ pos+=w.length }
    }
}
