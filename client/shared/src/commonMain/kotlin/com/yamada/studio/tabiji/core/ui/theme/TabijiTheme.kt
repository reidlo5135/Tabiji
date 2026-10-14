package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalTabijiColors = staticCompositionLocalOf { TabijiPalette.CLOUD.colors }
val LocalTabijiTypography = staticCompositionLocalOf<TabijiTypography> { error("TabijiTheme 밖에서 typography를 읽었어요") }

@Composable
fun TabijiTheme(palette: TabijiPalette, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTabijiColors provides palette.colors.animated(),
        LocalTabijiTypography provides tabijiTypography(),
        content = content)
}

object TabijiTheme {
    val colors: TabijiColors
        @Composable get() = LocalTabijiColors.current

    val typography: TabijiTypography
        @Composable get() = LocalTabijiTypography.current
}

private val ThemeSwitchSpec = tween<Color>(durationMillis = 240, easing = CubicBezierEasing(0.2f, 0f, 0f, 1f))

@Composable
private fun animate(target: Color): Color = animateColorAsState(target, ThemeSwitchSpec).value

@Composable
private fun TabijiColors.animated(): TabijiColors = TabijiColors(
    paper = animate(paper),
    stripe = animate(stripe),
    ink = animate(ink),
    card = animate(card),
    soft = animate(soft),
    accent = animate(accent),
    onAccent = animate(onAccent),
    sub = animate(sub),
    headers = headers.map { animate(it) },
    shadow = animate(shadow),
    skel = animate(skel),
    est = animate(est),
    err = animate(err),
    errSoft = animate(errSoft),
    ok = animate(ok),
)
