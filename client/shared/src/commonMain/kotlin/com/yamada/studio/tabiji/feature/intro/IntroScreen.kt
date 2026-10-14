package com.yamada.studio.tabiji.feature.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF2A3644)), // TODO(A5): TabijiTheme 토큰으로
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                SeasonQuadrant(
                    Color(0xFFC95E7E), // TODO(A6): 계절 이미지로
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
                SeasonQuadrant(
                    Color(0xFF1F96B8), // TODO(A6): 계절 이미지로
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
            }

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                SeasonQuadrant(
                    Color(0xFFC06F22), // TODO(A6): 계절 이미지로
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
                SeasonQuadrant(
                    Color(0xFF5674AE), // TODO(A6): 계절 이미지로
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
            }
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Tabiji",
            color = Color.White
        )
    }
}

@Composable
private fun SeasonQuadrant(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(color)
    )
}
