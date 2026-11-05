package com.becetti.tools.features.age

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.*
import java.time.temporal.ChronoUnit

@Composable
fun AgeCalculatorScreen(){
    var birth by remember { mutableStateOf(LocalDate.of(2000,1,1).toString()) }
    var target by remember { mutableStateOf(LocalDate.now().toString()) }
    val result = remember(birth, target){
        try{
            val b=LocalDate.parse(birth); val t=LocalDate.parse(target)
            val period = Period.between(b,t)
            val totalDays = ChronoUnit.DAYS.between(b,t)
            val nextBirthday = b.withYear(t.year).let{ if(!it.isAfter(t)) it.plusYears(1) else it }
            val daysToNext = ChronoUnit.DAYS.between(t, nextBirthday)
            "Age: ${period.years}y ${period.months}m ${period.days}d\nTotal days: $totalDays\nNext birthday: $nextBirthday (in $daysToNext days)\nDate diff: $period"
        }catch(e: Exception){ "Invalid date. Use YYYY-MM-DD" }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Age Calculator", style=MaterialTheme.typography.titleLarge)
        OutlinedTextField(value=birth, onValueChange={birth=it}, label={Text("Birth date (YYYY-MM-DD)")}, modifier=Modifier.fillMaxWidth())
        OutlinedTextField(value=target, onValueChange={target=it}, label={Text("Target date (YYYY-MM-DD)")}, modifier=Modifier.fillMaxWidth())
        Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text(result) } }
    }
}
