package com.becetti.tools.features.currency

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.utils.becettiDataStore
import com.becetti.tools.data.remote.FrankfurterApi
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

private val FALLBACK_USD_RATES = mapOf(
    "USD" to 1.0,
    "EUR" to 0.92,
    "DZD" to 134.50,
    "GBP" to 0.79,
    "SAR" to 3.75,
    "AED" to 3.6725,
    "QAR" to 3.64,
    "KWD" to 0.307,
    "MAD" to 9.95,
    "TND" to 3.11,
    "EGP" to 49.25,
    "TRY" to 34.30,
    "CAD" to 1.39,
    "AUD" to 1.53,
    "CHF" to 0.88,
    "JPY" to 152.30
)

@Composable
fun CurrencyScreen(app: BecettiToolsApp) {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var base by remember { mutableStateOf("USD") }
    var target by remember { mutableStateOf("DZD") }
    var amount by remember { mutableStateOf("100") }
    var activeRates by remember { mutableStateOf(FALLBACK_USD_RATES) }
    var lastUpdated by remember { mutableStateOf("2026-11-08") }
    var offline by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val currencies = listOf(
        "USD", "EUR", "DZD", "GBP", "SAR", "AED", "QAR", "KWD",
        "MAD", "TND", "EGP", "TRY", "CAD", "AUD", "CHF", "JPY"
    )

    fun convertFromUsdTable(usdRates: Map<String, Double>, fromCurr: String, toCurr: String, amt: Double): Double {
        if (fromCurr == toCurr) return amt
        val fromRate = usdRates[fromCurr] ?: FALLBACK_USD_RATES[fromCurr] ?: 1.0
        val toRate = usdRates[toCurr] ?: FALLBACK_USD_RATES[toCurr] ?: 1.0
        return (amt / fromRate) * toRate
    }

    val numericAmt = amount.toDoubleOrNull() ?: 0.0
    val liveConverted = remember(numericAmt, base, target, activeRates) {
        convertFromUsdTable(activeRates, base, target, numericAmt)
    }

    suspend fun fetch() {
        isLoading = true
        val gson = Gson()
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.frankfurter.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(FrankfurterApi::class.java)
            val resp = api.latest("USD")
            val mergedUsdRates = FALLBACK_USD_RATES.toMutableMap().apply {
                putAll(resp.rates)
                put("USD", 1.0)
            }
            activeRates = mergedUsdRates
            lastUpdated = resp.date
            offline = false
            ctx.becettiDataStore.edit {
                it[stringPreferencesKey("cached_rates_usd")] = gson.toJson(mergedUsdRates)
                it[stringPreferencesKey("cached_date")] = resp.date
            }
        } catch (e: Exception) {
            offline = true
            val prefs = ctx.becettiDataStore.data.first()
            val cachedJson = prefs[stringPreferencesKey("cached_rates_usd")]
            val cachedDate = prefs[stringPreferencesKey("cached_date")] ?: "2026-11-08"
            val ratesMap: Map<String, Double> = if (!cachedJson.isNullOrBlank()) {
                runCatching {
                    val type = object : TypeToken<Map<String, Double>>() {}.type
                    gson.fromJson<Map<String, Double>>(cachedJson, type)
                }.getOrDefault(FALLBACK_USD_RATES)
            } else {
                FALLBACK_USD_RATES
            }
            activeRates = ratesMap
            lastUpdated = cachedDate
        }
        isLoading = false
    }

    val toolMeta = s.tools["currency"]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "currency",
            title = toolMeta?.name ?: "Currency Converter",
            subtitle = toolMeta?.description ?: ""
        )

        if (offline) {
            GlowingCard(accentColor = GlowAmber) {
                Text("⚡ ${s.currencyOfflineBanner}", color = GlowAmber, fontWeight = FontWeight.SemiBold)
            }
        }

        GlowingCard(accentColor = GlowEmerald) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text(s.currencyAmountLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownCurr(currencies, base, { base = it }, Modifier.weight(1f), s.unitFromLabel)
                FilledTonalButton(
                    onClick = {
                        val tmp = base
                        base = target
                        target = tmp
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("⇄")
                }
                DropdownCurr(currencies, target, { target = it }, Modifier.weight(1f), s.unitToLabel)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { scope.launch { fetch() } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("↻ ${s.currencyConvertBtn}", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        GlowingCard(accentColor = GlowCyan) {
            Text(s.resultLabel, style = MaterialTheme.typography.labelLarge, color = GlowCyan)
            Spacer(Modifier.height(4.dp))
            Text(
                String.format(Locale.US, "%.2f %s = %.2f %s", numericAmt, base, liveConverted, target),
                style = MaterialTheme.typography.titleLarge,
                color = GlowCyan,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            val unitRate = convertFromUsdTable(activeRates, base, target, 1.0)
            Text(
                String.format(Locale.US, "1 %s = %.4f %s • %s: %s", base, unitRate, target, s.currencyLastUpdated, lastUpdated),
                style = MaterialTheme.typography.bodySmall
            )
        }

        GlowingCard(accentColor = GlowOrange) {
            Text(
                s.currencyPopularTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlowOrange
            )
            Spacer(Modifier.height(8.dp))
            listOf(1.0, 10.0, 50.0, 100.0, 500.0, 1000.0).forEach { qAmt ->
                val conv = convertFromUsdTable(activeRates, base, target, qAmt)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${qAmt.toInt()} $base", fontWeight = FontWeight.Medium)
                    Text(
                        String.format(Locale.US, "%.2f %s", conv, target),
                        color = GlowEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider()
            }
        }

        Text(s.currencySourceNote, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCurr(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = { onSelect(it); expanded = false })
            }
        }
    }
}
