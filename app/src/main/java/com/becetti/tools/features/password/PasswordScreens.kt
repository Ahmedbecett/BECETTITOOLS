package com.becetti.tools.features.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.security.SecurityUtils
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import kotlin.math.ln

@Composable
fun PasswordGenScreen() {
    val s = LocalAppStrings.current
    var len by remember { mutableStateOf(16f) }
    var upper by remember { mutableStateOf(true) }
    var lower by remember { mutableStateOf(true) }
    var digits by remember { mutableStateOf(true) }
    var symbols by remember { mutableStateOf(true) }
    var pwd by remember {
        mutableStateOf(SecurityUtils.secureRandomPassword(16, true, true, true, true))
    }
    var copied by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    val toolMeta = s.tools["password_gen"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "password_gen",
            title = toolMeta?.name ?: "Password Generator",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowTeal) {
            Text(
                "${s.pwdLengthLabel}: ${len.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlowTeal
            )
            Slider(
                value = len,
                onValueChange = {
                    len = it
                    pwd = SecurityUtils.secureRandomPassword(it.toInt(), upper, lower, digits, symbols)
                    copied = false
                },
                valueRange = 4f..64f,
                steps = 59
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = upper, onClick = { upper = !upper }, label = { Text("A-Z") })
                FilterChip(selected = lower, onClick = { lower = !lower }, label = { Text("a-z") })
                FilterChip(selected = digits, onClick = { digits = !digits }, label = { Text("0-9") })
                FilterChip(selected = symbols, onClick = { symbols = !symbols }, label = { Text("#@!") })
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    pwd = SecurityUtils.secureRandomPassword(len.toInt(), upper, lower, digits, symbols)
                    copied = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowTeal)
            ) {
                Text(s.pwdGenerateBtn, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (pwd.isNotEmpty()) {
            GlowingCard(accentColor = GlowEmerald) {
                Text(
                    pwd,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = GlowEmerald
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(pwd))
                        copied = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                ) {
                    Text(if (copied) s.copied else s.copy, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        Text(s.pwdLocalNote, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PasswordStrengthScreen() {
    val s = LocalAppStrings.current
    var input by remember { mutableStateOf("") }
    val score = remember(input) { SecurityUtils.passwordStrength(input) }

    val entropyBits = remember(input) {
        if (input.isEmpty()) 0
        else {
            var pool = 0
            if (input.any { it.isLowerCase() }) pool += 26
            if (input.any { it.isUpperCase() }) pool += 26
            if (input.any { it.isDigit() }) pool += 10
            if (input.any { !it.isLetterOrDigit() }) pool += 32
            if (pool == 0) pool = 26
            (input.length * (ln(pool.toDouble()) / ln(2.0))).toInt()
        }
    }

    val crackTime = when {
        entropyBits < 28 -> "< 1 sec"
        entropyBits < 40 -> "3 mins"
        entropyBits < 56 -> "4 days"
        entropyBits < 72 -> "85 years"
        else -> "10,000+ years"
    }

    val label = when (score) {
        0 -> s.pwdVeryWeak
        1 -> s.pwdWeak
        2 -> s.pwdFair
        3 -> s.pwdGood
        4 -> s.pwdStrong
        else -> s.pwdVeryStrong
    }
    val color = when (score) {
        0, 1 -> Error
        2 -> GlowAmber
        3, 4 -> GlowCyan
        else -> GlowEmerald
    }

    val toolMeta = s.tools["password_strength"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "password_strength",
            title = toolMeta?.name ?: "Password Strength Lab",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = color) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text(s.pwdEnterLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = (score / 5f).coerceIn(0.05f, 1f),
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = color
            )
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlowingCard(accentColor = GlowCyan, modifier = Modifier.weight(1f)) {
                Text(s.pwdEntropyLabel, style = MaterialTheme.typography.bodySmall)
                Text("$entropyBits bits", style = MaterialTheme.typography.titleLarge, color = GlowCyan, fontWeight = FontWeight.Bold)
            }
            GlowingCard(accentColor = GlowViolet, modifier = Modifier.weight(1f)) {
                Text(s.pwdCrackTimeLabel, style = MaterialTheme.typography.bodySmall)
                Text(crackTime, style = MaterialTheme.typography.titleLarge, color = GlowViolet, fontWeight = FontWeight.Bold)
            }
        }

        GlowingCard(accentColor = GlowAmber) {
            Text(s.pwdTips, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(s.pwdLocalNote, style = MaterialTheme.typography.bodySmall)
        }
    }
}
