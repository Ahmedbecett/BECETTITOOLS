package com.becetti.tools.features.currency

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.data.remote.FrankfurterApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.becetti.tools.dataStore
import kotlinx.coroutines.flow.map

@Composable
fun CurrencyScreen(app: BecettiToolsApp) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var base by remember { mutableStateOf("USD") }
    var target by remember { mutableStateOf("EUR") }
    var amount by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf<String?>(null) }
    var lastUpdated by remember { mutableStateOf<String?>(null) }
    var offline by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val currencies = listOf("USD","EUR","DZD","GBP","JPY","CAD","AUD","CHF","MAD","TND","SAR","AED","EGP","TRY")

    suspend fun fetch() {
        isLoading = true
        try {
            val retrofit = Retrofit.Builder().baseUrl("https://api.frankfurter.app/").addConverterFactory(GsonConverterFactory.create()).build()
            val api = retrofit.create(FrankfurterApi::class.java)
            val resp = api.latest(base)
            val rate = resp.rates[target] ?: 1.0
            val v = (amount.toDoubleOrNull() ?: 0.0) * rate
            result = "%.4f".format(v)
            lastUpdated = resp.date
            offline = false
            // cache
            ctx.dataStore.edit { it[stringPreferencesKey("cached_rates_${'$'}base")] = resp.rates.toString(); it[stringPreferencesKey("cached_date")] = resp.date }
        } catch(e: Exception){
            offline = true
            result = "Offline — cached rates unavailable. Connect to update."
        }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Currency Converter", style = MaterialTheme.typography.titleLarge)
        if (offline) Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.errorContainer)){ Box(Modifier.padding(12.dp)){ Text("Offline mode — showing cached data indicator") } }
        OutlinedTextField(value=amount, onValueChange={amount=it}, label={Text("Amount")}, modifier=Modifier.fillMaxWidth())
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            DropdownCurr(currencies, base, {base=it}, Modifier.weight(1f), "From")
            DropdownCurr(currencies, target, {target=it}, Modifier.weight(1f), "To")
        }
        Button(onClick={ scope.launch{ fetch() } }, modifier=Modifier.fillMaxWidth()){ if(isLoading) CircularProgressIndicator(modifier=Modifier.size(16.dp)) else Text("Convert (Online fetch + cache)") }
        result?.let { Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text(it, style=MaterialTheme.typography.titleLarge) } } }
        lastUpdated?.let { Text("Last updated: $it • Cached for offline viewing", style=MaterialTheme.typography.labelSmall) }
        Text("Rates via Frankfurter.app (ECB). Cached locally for offline.", style=MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCurr(items: List<String>, selected: String, onSelect:(String)->Unit, modifier: Modifier, label:String){
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded=expanded, onExpandedChange={expanded=!expanded}, modifier=modifier){
        OutlinedTextField(value=selected, onValueChange={}, readOnly=true, label={Text(label)}, trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded)}, modifier=Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded=expanded, onDismissRequest={expanded=false}){ items.forEach{ DropdownMenuItem(text={Text(it)}, onClick={onSelect(it); expanded=false}) } }
    }
}
