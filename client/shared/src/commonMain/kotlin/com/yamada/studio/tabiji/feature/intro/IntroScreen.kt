package com.yamada.studio.tabiji.feature.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf<Season?>(null) }
    val toggle: (Season) -> Unit = {
        selected = if (selected == it) null else it
    }

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
                    season = Season.SPRING,
                    selected = selected,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onSelect = toggle,
                )
                SeasonQuadrant(
                    season = Season.SUMMER,
                    selected = selected,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onSelect = toggle,
                )
            }

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                SeasonQuadrant(
                    season = Season.AUTUMN,
                    selected = selected,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onSelect = toggle,
                )
                SeasonQuadrant(
                    season = Season.WINTER,
                    selected = selected,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onSelect = toggle,
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
private fun SeasonQuadrant(
    season: Season,
    selected: Season?,
    modifier: Modifier = Modifier,
    onSelect: (Season) -> Unit
) {
    val overlay: Color = when(selected) {
        null -> Color.Transparent
        season -> Color.White.copy(alpha = 0.08f)
        else -> Color.Black.copy(alpha = 0.06f)
    }

    Box(
        modifier = modifier
            .background(seasonColor(season))
            .background(overlay)
            .clickable(interactionSource = null, indication = null) {
                onSelect(season)
            }
    )
}

private fun seasonColor(season: Season): Color {
    // TODO(A6): 계절 이미지로
    return when (season) {
        Season.SPRING -> Color(0xFFC95E7E)
        Season.SUMMER -> Color(0xFF1F96B8)
        Season.AUTUMN -> Color(0xFFC06F22)
        Season.WINTER -> Color(0xFF5674AE)
    }
}

