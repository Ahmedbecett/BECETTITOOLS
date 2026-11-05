package com.becetti.tools.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.becetti.tools.design.theme.PrimaryOrange

@Composable
fun BecettiTopBar(title: String, navigationIcon: ImageVector? = null, onNav: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        actions = actions
    )
}

@Composable
fun BecettiCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
        Text(text)
    }
}

@Composable
fun AdPlaceholder(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Sponsored Offers — Tap to explore", style = MaterialTheme.typography.labelMedium, color = PrimaryOrange)
        }
    }
}
