package com.yamada.studio.tabiji.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import kotlin.math.sqrt

fun Modifier.paperStripes(paper: Color, stripe: Color, band: Dp): Modifier = this.drawBehind {
    drawRect(paper)
    val b: Float = band.toPx()
    val step: Float = 2 * b * sqrt(2f)
    clipRect {
        var x: Float = -size.height
        while (x < size.width) {
            drawLine(stripe, start = Offset(x, 0f), end = Offset(x + size.height, size.height), strokeWidth = b)
            x += step
        }
    }
}

