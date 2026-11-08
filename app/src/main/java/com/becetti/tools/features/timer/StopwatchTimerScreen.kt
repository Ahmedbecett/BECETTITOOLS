package com.becetti.tools.features.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun StopwatchTimerScreen() {
    val s = LocalAppStrings.current
    val accent = GlowViolet
    var tab by remember { mutableStateOf(0) } // 0 = Stopwatch, 1 = Countdown Timer

    // Stopwatch state
    var swRunning by remember { mutableStateOf(false) }
    var swElapsedMs by remember { mutableStateOf(0L) }
    var laps by remember { mutableStateOf(listOf<String>()) }

    // Timer state
    var timerRunning by remember { mutableStateOf(false) }
    var timerTotalSec by remember { mutableStateOf(300L) }
    var timerRemainingSec by remember { mutableStateOf(300L) }
    var timerFinished by remember { mutableStateOf(false) }

    LaunchedEffect(swRunning) {
        var lastTick = System.currentTimeMillis()
        while (swRunning) {
            delay(30L)
            val now = System.currentTimeMillis()
            swElapsedMs += (now - lastTick)
            lastTick = now
        }
    }

    LaunchedEffect(timerRunning) {
        while (timerRunning && timerRemainingSec > 0) {
            delay(1000L)
            if (timerRunning && timerRemainingSec > 0) {
                timerRemainingSec -= 1L
                if (timerRemainingSec == 0L) {
                    timerRunning = false
                    timerFinished = true
                }
            }
        }
    }

    fun formatStopwatch(ms: Long): String {
        val min = (ms / 60000L)
        val sec = (ms % 60000L) / 1000L
        val centi = (ms % 1000L) / 10L
        return String.format(Locale.US, "%02d:%02d.%02d", min, sec, centi)
    }

    fun formatTimer(sec: Long): String {
        val m = sec / 60L
        val r = sec % 60L
        return String.format(Locale.US, "%02d:%02d", m, r)
    }

    val toolMeta = s.tools["stopwatch_timer"]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlowingToolHeader(
            toolId = "stopwatch_timer",
            title = toolMeta?.name ?: "Stopwatch & Timer",
            subtitle = toolMeta?.description ?: ""
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = tab == 0,
                onClick = { tab = 0 },
                label = { Text(s.swStopwatchTab) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = tab == 1,
                onClick = { tab = 1 },
                label = { Text(s.swTimerTab) },
                modifier = Modifier.weight(1f)
            )
        }

        if (tab == 0) {
            GlowingCard(accentColor = accent) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatStopwatch(swElapsedMs),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = accent
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { swRunning = !swRunning },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (swRunning) GlowAmber else GlowEmerald
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (swRunning) s.swPause else s.swStart)
                    }
                    OutlinedButton(
                        onClick = {
                            if (swElapsedMs > 0L) {
                                laps = listOf("#${laps.size + 1}  ${formatStopwatch(swElapsedMs)}") + laps
                            }
                        },
                        enabled = swElapsedMs > 0L,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(s.swLap)
                    }
                    OutlinedButton(
                        onClick = {
                            swRunning = false
                            swElapsedMs = 0L
                            laps = emptyList()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(s.swReset)
                    }
                }
            }

            if (laps.isNotEmpty()) {
                GlowingCard(accentColor = GlowCyan) {
                    laps.forEach { lap ->
                        Text(
                            text = lap,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Divider()
                    }
                }
            }
        } else {
            GlowingCard(accentColor = GlowOrange) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTimer(timerRemainingSec),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (timerFinished) GlowEmerald else GlowOrange
                    )
                    if (timerFinished) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            s.swTimeUp,
                            color = GlowEmerald,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(60L to "1m", 300L to "5m", 600L to "10m", 900L to "15m", 1500L to "25m").forEach { (sec, label) ->
                        AssistChip(
                            onClick = {
                                timerRunning = false
                                timerFinished = false
                                timerTotalSec = sec
                                timerRemainingSec = sec
                            },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            timerFinished = false
                            if (timerRemainingSec == 0L) timerRemainingSec = timerTotalSec
                            timerRunning = !timerRunning
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (timerRunning) GlowAmber else GlowOrange
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (timerRunning) s.swPause else s.swStart)
                    }
                    OutlinedButton(
                        onClick = {
                            timerRunning = false
                            timerFinished = false
                            timerRemainingSec = timerTotalSec
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(s.swReset)
                    }
                }
            }
        }
    }
}
