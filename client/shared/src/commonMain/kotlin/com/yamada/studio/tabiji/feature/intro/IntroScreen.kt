package com.yamada.studio.tabiji.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.Res
import tabiji.shared.generated.resources.intro_subtitle
import tabiji.shared.generated.resources.intro_wordmark


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
            modifier = Modifier.fillMaxSize().background(TabijiTheme.colors.ink),
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

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.intro_wordmark),
                style = TabijiTheme.typography.wordmark,
                color = Color.White
            )

            Text(
                text = stringResource(Res.string.intro_subtitle),
                style = TabijiTheme.typography.displayM.copy(fontSize = 17.sp, letterSpacing = 0.1.em),
                color = Color.White
            )
        }
    }
}

@Composable
private fun SeasonQuadrant(
    season: Season,
    selected: Season?,
    modifier: Modifier = Modifier,
    onSelect: (Season) -> Unit
) {
    Box(
        modifier = modifier.clickable(interactionSource = null, indication = null) {
            onSelect(season)
        }
    ) {
        Image(
            painter = painterResource(season.image),
            contentDescription = stringResource(season.label),
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(0f, season.focusY),
            modifier = Modifier.fillMaxSize(),
        )

        val overlay: Color = when(selected) {
            null -> Color.Transparent
            season -> Color.White.copy(alpha = 0.08f)
            else -> Color.Black.copy(alpha = 0.06f)
        }

        Box(
            modifier = Modifier.matchParentSize().background(overlay)
        )
    }
}
