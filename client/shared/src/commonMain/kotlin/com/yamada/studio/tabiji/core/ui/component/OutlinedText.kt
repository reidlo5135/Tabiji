package com.yamada.studio.tabiji.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme

@Composable
fun OutlinedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = TabijiTheme.colors.ink,
    outlineWidth: Dp = TabijiTheme.border.panel,
    shadowOffset: DpOffset = TabijiTheme.shadow.panel
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = style,
            color = outlineColor,
            modifier = Modifier.offset(shadowOffset.x, shadowOffset.y)
        )

        val strokePx = with(LocalDensity.current) { (outlineWidth * 2).toPx() }
        Text(
            text = text,
            style = style.copy(drawStyle = Stroke(width = strokePx, join = StrokeJoin.Round)),
            color = outlineColor,
        )

        Text(
            text = text,
            style = style,
            color = fillColor,
        )
    }
}
