package com.yamada.studio.tabiji.core.ui.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WidthClass {
    COMPACT, MEDIUM, EXPANDED;

    companion object {
        fun of(width: Dp): WidthClass = when {
            width < 600.dp -> COMPACT
            width < 840.dp -> MEDIUM
            else -> EXPANDED
        }
    }
}

val LocalWidthClass = staticCompositionLocalOf { WidthClass.EXPANDED }

@Composable
fun ProvideWidthClass(content: @Composable () -> Unit) {
    BoxWithConstraints {
        CompositionLocalProvider(LocalWidthClass provides WidthClass.of(maxWidth), content = content)
    }
}
