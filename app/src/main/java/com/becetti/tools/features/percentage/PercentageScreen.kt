package com.becetti.tools.features.percentage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PercentageScreen(){
    var mode by remember { mutableStateOf(0) }
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    val result = remember(a,b,mode){
        val av = a.toDoubleOrNull()?:0.0; val bv=b.toDoubleOrNull()?:0.0
        when(mode){ 0-> av*bv/100; 1-> av*(1+bv/100); 2-> av*(1-bv/100); else->0.0}
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Percentage Calculator", style=MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            FilterChip(selected=mode==0, onClick={mode=0}, label={Text("% of number")})
            FilterChip(selected=mode==1, onClick={mode=1}, label={Text("Increase")})
            FilterChip(selected=mode==2, onClick={mode=2}, label={Text("Decrease")})
        }
        OutlinedTextField(value=a, onValueChange={a=it}, label={Text(if(mode==0) "Number" else "Value")}, modifier=Modifier.fillMaxWidth())
        OutlinedTextField(value=b, onValueChange={b=it}, label={Text("Percentage %")}, modifier=Modifier.fillMaxWidth())
        Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text("Result: $result", style=MaterialTheme.typography.titleLarge) } }
        val hint = when(mode){
            0-> "Example: 20% of 150 = 30"
            1-> "Example: 150 +10% = 165"
            else-> "Example: 150 -10% = 135"
        }
        Text(hint, style=MaterialTheme.typography.bodySmall)
    }
}
