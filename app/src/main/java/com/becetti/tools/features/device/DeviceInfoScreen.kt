package com.becetti.tools.features.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import java.util.Locale

@Composable
fun DeviceInfoScreen() {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current

    val modelInfo = remember {
        "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL} (${Build.DEVICE})"
    }
    val osInfo = remember {
        "Android ${Build.VERSION.RELEASE} • API ${Build.VERSION.SDK_INT}"
    }
    val abiInfo = remember {
        Build.SUPPORTED_ABIS.joinToString(", ")
    }
    val ramInfo = remember {
        runCatching {
            val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            val totalMb = mi.totalMem / (1024 * 1024)
            val availMb = mi.availMem / (1024 * 1024)
            val usedMb = totalMb - availMb
            "$usedMb MB / $totalMb MB (${availMb} MB Free)"
        }.getOrDefault("Unavailable")
    }
    val storageInfo = remember {
        runCatching {
            val stat = StatFs(Environment.getDataDirectory().path)
            val totalGb = (stat.blockCountLong * stat.blockSizeLong) / (1024.0 * 1024.0 * 1024.0)
            val freeGb = (stat.availableBlocksLong * stat.blockSizeLong) / (1024.0 * 1024.0 * 1024.0)
            String.format(Locale.US, "%.1f GB Free / %.1f GB Total", freeGb, totalGb)
        }.getOrDefault("Unavailable")
    }
    val screenInfo = remember {
        val dm = ctx.resources.displayMetrics
        "${dm.widthPixels} × ${dm.heightPixels} px • ${dm.densityDpi} dpi"
    }
    val batteryInfo = remember {
        runCatching {
            val intent = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
            "$pct% ${if (charging) "⚡ Charging" else ""}"
        }.getOrDefault("100%")
    }

    val toolMeta = s.tools["device_info"]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "device_info",
            title = toolMeta?.name ?: "Device & System Inspector",
            subtitle = toolMeta?.description ?: ""
        )

        val specs = listOf(
            Triple(s.devModel, modelInfo, GlowTeal),
            Triple(s.devAndroidVer, osInfo, GlowEmerald),
            Triple(s.devCpuAbi, abiInfo, GlowCyan),
            Triple(s.devRamUsage, ramInfo, GlowOrange),
            Triple(s.devStorageUsage, storageInfo, GlowViolet),
            Triple(s.devScreenInfo, screenInfo, GlowBlue),
            Triple(s.devBatteryLevel, batteryInfo, GlowAmber)
        )

        specs.forEach { (label, value, color) ->
            GlowingCard(accentColor = color) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = color)
                Spacer(Modifier.height(4.dp))
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
