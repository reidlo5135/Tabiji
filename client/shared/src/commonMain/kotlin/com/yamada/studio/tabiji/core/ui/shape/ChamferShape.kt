package com.yamada.studio.tabiji.core.ui.shape

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.theme.TabijiRadius

class ChamferShape(
    private val cut: Dp = TabijiRadius.buttonChamfer,
    private val corner: Dp = 12.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val c = with(density) { cut.toPx() }
        val r = with(density) { corner.toPx() }
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(w - c, 0f)
            lineTo(w, c)
            lineTo(w, h - r)
            arcTo(Rect(w - 2*r, h - 2*r, w, h), 0f, 90f, false)
            lineTo(r, h)
            arcTo(Rect(0f, h - 2*r, 2*r, h), 90f, 90f, false)
            lineTo(0f, r)
            arcTo(Rect(0f, 0f, 2*r, 2*r), 180f, 90f, false)
            close()
        }
        return Outline.Generic(path)
    }
}
