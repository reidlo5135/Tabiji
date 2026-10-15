package com.yamada.studio.tabiji.core.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.DpOffset

fun Modifier.inkShadow(
    offset: DpOffset,
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)
    translate(left = offset.x.toPx(), top = offset.y.toPx()) {
        drawOutline(outline, color)
    }
}
