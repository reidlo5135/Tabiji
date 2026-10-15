package com.yamada.studio.tabiji.core.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme


@Composable
fun InkButton(
    fill: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(TabijiTheme.radius.button),
    shadowDepth: Dp = TabijiTheme.shadow.button.y,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val sink by animateDpAsState(
        targetValue = if (pressed) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = if (pressed) 80 else 120)
    )

    Row(
        modifier = modifier
            .offset(y = sink)
            .inkShadow(DpOffset(0.dp, shadowDepth - sink), TabijiTheme.colors.ink, shape)
            .background(fill, shape)
            .border(TabijiTheme.border.panel, TabijiTheme.colors.ink, shape)
            .clickable(interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = TabijiTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}
