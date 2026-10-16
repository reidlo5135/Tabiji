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
import com.yamada.studio.tabiji.core.ui.icon.TabijiIcons
import com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass
import com.yamada.studio.tabiji.core.ui.layout.WidthClass
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.*

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

        val compact = widthClass == WidthClass.COMPACT

        val stackWidth  = if (compact) StackWidthCompact        else StackWidthWide
        val stackGap    = if (compact) StackGapCompact          else StackGapWide
        val startHeight = if (compact) StartButtonHeightCompact else StartButtonHeightWide
        val menuHeight  = if (compact) MenuButtonHeightCompact  else MenuButtonHeightWide
        val introSize   = if (compact) IntroIconSizeCompact else IntroIconSizeWide

        val titleStyle: TextStyle = if(widthClass == WidthClass.COMPACT) TabijiTheme.typography.wordmark.copy(fontSize = 46.sp, lineHeight = 52.sp) else TabijiTheme.typography.wordmark
        val subTitleStyle: TextStyle = TabijiTheme.typography.displayM.copy(fontSize = 17.sp, letterSpacing = 0.1.em)

        Column(
            modifier = Modifier.align(Alignment.Center).width(stackWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(stackGap),
        ) {
            Column(
                modifier = Modifier,
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

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.md),
            ) {

                IntroMenuButton(
                    title = stringResource(Res.string.intro_start_title),
                    subtitle = stringResource(Res.string.intro_start_subtitle),
                    modifier = Modifier,
                    primary = true,
                    height = startHeight,
                    leadingIcon = TabijiIcons.Place,
                    iconSize = introSize,
                    onClick = { println("Tabiji/intro: start") } // TODO(B6): 내비게이션
                )

                IntroMenuButton(
                    title = stringResource(Res.string.intro_trips_title),
                    subtitle = stringResource(Res.string.intro_trips_subtitle),
                    modifier = Modifier,
                    primary = false,
                    height = menuHeight,
                    leadingIcon = TabijiIcons.Calendar,
                    iconSize = introSize,
                    onClick = { println("Tabiji/intro: trip") } // TODO(B6): 내비게이션
                )

                IntroMenuButton(
                    title = stringResource(Res.string.intro_settings_title),
                    subtitle = stringResource(Res.string.intro_settings_subtitle),
                    modifier = Modifier,
                    primary = false,
                    height = menuHeight,
                    leadingIcon = TabijiIcons.Settings,
                    iconSize = introSize,
                    onClick = { println("Tabiji/intro: setting") } // TODO(B6): 내비게이션
                )
            }
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
private val StackGapWide: Dp = 24.dp
private val StackGapCompact: Dp = 20.dp
private val SubtitleShadow: DpOffset = DpOffset(2.dp, 2.dp)
private val WordmarkGap: Dp = 6.dp

private val StartButtonHeightCompact: Dp = 56.dp
private val StartButtonHeightWide: Dp = 58.dp
private val MenuButtonHeightCompact: Dp = 50.dp
private val MenuButtonHeightWide: Dp = 52.dp
private val IntroIconSizeWide: Dp = 22.dp
private val IntroIconSizeCompact: Dp = 20.dp
