package com.becetti.tools.features.converter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class UnitCategory(val units: List<String>) {
    LENGTH(listOf("mm","cm","m","km","inch","ft","yard","mile")),
    WEIGHT(listOf("mg","g","kg","ton","oz","lb")),
    TEMPERATURE(listOf("C","F","K")),
    AREA(listOf("mm2","cm2","m2","km2","ft2","acre","hectare")),
    VOLUME(listOf("ml","l","m3","cup","gallon","fl oz")),
    SPEED(listOf("m/s","km/h","mph","knot"))
}

object UnitConverter {
    fun convert(value: Double, from: String, to: String, cat: UnitCategory): Double {
        if (from==to) return value
        return when(cat){
            UnitCategory.LENGTH -> toMeter(value, from) / toMeter(1.0,to)
            UnitCategory.WEIGHT -> toKg(value, from) / toKg(1.0,to)
            UnitCategory.TEMPERATURE -> convertTemp(value, from,to)
            UnitCategory.AREA -> toM2(value, from)/toM2(1.0,to)
            UnitCategory.VOLUME -> toLiter(value, from)/toLiter(1.0,to)
            UnitCategory.SPEED -> toMs(value, from)/toMs(1.0,to)
        }
    }
    private fun toMeter(v: Double,u:String)= when(u){ "mm"->v/1000; "cm"->v/100; "m"->v; "km"->v*1000; "inch"->v*0.0254; "ft"->v*0.3048; "yard"->v*0.9144; "mile"->v*1609.34; else->v}
    private fun toKg(v: Double,u:String)= when(u){ "mg"->v/1e6; "g"->v/1000; "kg"->v; "ton"->v*1000; "oz"->v*0.0283495; "lb"->v*0.453592; else->v}
    private fun convertTemp(v: Double, from:String, to:String): Double {
        val c = when(from){ "C"->v; "F"->(v-32)*5/9; "K"->v-273.15; else->v }
        return when(to){ "C"->c; "F"->c*9/5+32; "K"->c+273.15; else->c }
    }
    private fun toM2(v: Double,u:String)= when(u){ "mm2"->v/1e6; "cm2"->v/1e4; "m2"->v; "km2"->v*1e6; "ft2"->v*0.092903; "acre"->v*4046.86; "hectare"->v*10000; else->v}
    private fun toLiter(v: Double,u:String)= when(u){ "ml"->v/1000; "l"->v; "m3"->v*1000; "cup"->v*0.236588; "gallon"->v*3.78541; "fl oz"->v*0.0295735; else->v}
    private fun toMs(v: Double,u:String)= when(u){ "m/s"->v; "km/h"->v/3.6; "mph"->v*0.44704; "knot"->v*0.514444; else->v}
}

@Composable
fun UnitConverterScreen() {
    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    var from by remember { mutableStateOf(category.units.first()) }
    var to by remember { mutableStateOf(category.units.last()) }
    var input by remember { mutableStateOf("1") }
    val output = remember(input, from, to, category) {
        val v = input.toDoubleOrNull() ?: 0.0
        try { UnitConverter.convert(v, from, to, category).toString() } catch(e: Exception){ "Error" }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Unit Converter", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { UnitCategory.values().forEach { c -> FilterChip(selected = c==category, onClick={ category=c; from=c.units.first(); to=c.units.last()}, label={Text(c.name)}) } }
        OutlinedTextField(value=input, onValueChange={input=it}, label={Text("Value")}, modifier=Modifier.fillMaxWidth())
        Row(horizontalArrangement= Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()){
            Dropdown(category.units, from, {from=it}, Modifier.weight(1f), "From")
            Dropdown(category.units, to, {to=it}, Modifier.weight(1f), "To")
        }
        Card(modifier=Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text("Result: $output $to", style=MaterialTheme.typography.titleLarge) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Dropdown(items: List<String>, selected: String, onSelect:(String)->Unit, modifier: Modifier=Modifier, label:String) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded=expanded, onExpandedChange={expanded=!expanded}, modifier=modifier){
        OutlinedTextField(value=selected, onValueChange={}, readOnly=true, label={Text(label)}, trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded)}, modifier=Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded=expanded, onDismissRequest={expanded=false}){ items.forEach{ item-> DropdownMenuItem(text={Text(item)}, onClick={onSelect(item); expanded=false}) } }
    }
}
