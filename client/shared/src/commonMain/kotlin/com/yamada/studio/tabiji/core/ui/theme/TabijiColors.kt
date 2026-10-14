package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TabijiColors(
    val paper: Color,
    val stripe: Color,
    val ink: Color,
    val card: Color,
    val soft: Color,
    val accent: Color,
    val onAccent: Color,
    val sub: Color,
    val headers: List<Color>,
    val shadow: Color,
    val skel: Color,
    val est: Color,
    val err: Color,
    val errSoft: Color,
    val ok: Color,
)
