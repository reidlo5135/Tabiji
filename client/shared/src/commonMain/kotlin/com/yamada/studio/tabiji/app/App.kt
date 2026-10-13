package com.yamada.studio.tabiji.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun App() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Tabiji(旅路)", color = Color(0xFF1C2633)) // TODO(A5) : TabijiTheme
    }
}
