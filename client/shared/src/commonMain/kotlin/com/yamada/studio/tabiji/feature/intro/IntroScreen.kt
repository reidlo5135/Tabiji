package com.yamada.studio.tabiji.feature.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import com.yamada.studio.tabiji.core.ui.component.OutlinedText
import com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass
import com.yamada.studio.tabiji.core.ui.layout.WidthClass
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.Res
import tabiji.shared.generated.resources.intro_subtitle
import tabiji.shared.generated.resources.intro_wordmark

@Composable
private fun QuadrantStack(selected: Season?, onSelect: (Season) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().background(TabijiTheme.colors.ink),
        verticalArrangement = Arrangement.spacedBy(QuadrantInkGap),
    ) {
        Season.entries.forEach { season ->
            SeasonQuadrant(season, selected, season.phoneFocusY, Modifier.weight(1f).fillMaxWidth(), onSelect)
        }
    }
}

@Composable
private fun QuadrantGrid(selected: Season?, onSelect: (Season) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().background(TabijiTheme.colors.ink),
        verticalArrangement = Arrangement.spacedBy(QuadrantInkGap),
    ) {
        Season.entries.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(QuadrantInkGap),
            ) {
                row.forEach { season ->
                    SeasonQuadrant(season, selected, season.desktopFocusY, Modifier.weight(1f).fillMaxSize(), onSelect)
                }
            }
        }
    }
}

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    var selected by remember { mutableStateOf<Season?>(null) }
    val toggle: (Season) -> Unit = {
        selected = if (selected == it) null else it
    }
    val widthClass: WidthClass = LocalWidthClass.current

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (widthClass) {
            WidthClass.COMPACT -> QuadrantStack(selected = selected, onSelect = toggle, modifier = Modifier.fillMaxSize())
            WidthClass.MEDIUM -> QuadrantGrid(selected = selected, onSelect = toggle, modifier = Modifier.fillMaxSize())
            WidthClass.EXPANDED -> QuadrantGrid(selected = selected, onSelect = toggle, modifier = Modifier.fillMaxSize())
        }

        val stackWidth: Dp = if (widthClass == WidthClass.COMPACT) StackWidthCompact else StackWidthWide
        val titleStyle: TextStyle = if(widthClass == WidthClass.COMPACT) TabijiTheme.typography.wordmark.copy(fontSize = 46.sp, lineHeight = 52.sp) else TabijiTheme.typography.wordmark
        val subTitleStyle: TextStyle = TabijiTheme.typography.displayM.copy(fontSize = 17.sp, letterSpacing = 0.1.em)

        Column(
            modifier = Modifier.align(Alignment.Center).width(stackWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(WordmarkGap)
        ) {
            OutlinedText(
                text = stringResource(Res.string.intro_wordmark),
                style = titleStyle,
            )

            OutlinedText(
                text = stringResource(Res.string.intro_subtitle),
                style = subTitleStyle,
                outlineWidth = SubtitleOutline,
                shadowOffset = SubtitleShadow
            )
        }
    }
}

@Composable
private fun SeasonQuadrant(
    season: Season,
    selected: Season?,
    focusY: Float,
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
            alignment = BiasAlignment(0f, focusY),
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

private val QuadrantInkGap: Dp = 3.dp
private val StackWidthCompact: Dp = 310.dp
private val StackWidthWide: Dp = 360.dp
private val SubtitleOutline: Dp = (1.5).dp
private val SubtitleShadow: DpOffset = DpOffset(2.dp, 2.dp)
private val WordmarkGap: Dp = 6.dp
