package com.yamada.studio.tabiji.feature.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yamada.studio.tabiji.core.ui.component.InkButton
import com.yamada.studio.tabiji.core.ui.shape.ChamferShape
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme


@Composable
fun IntroMenuButton(
    title: String,
    subtitle: String,
    primary: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = TabijiTheme.colors
    val t = TabijiTheme.typography

    val fill: Color = if (primary) c.accent else c.card
    val shape: Shape = if (primary) ChamferShape() else RoundedCornerShape(TabijiTheme.radius.button)
    val titleColor: Color = if(primary) c.onAccent else c.ink
    val subtitleColor: Color = if(primary) c.onAccent.copy(alpha = 0.85f) else c.sub
    val titleStyle: TextStyle = if(primary) t.displayM.copy(fontSize = 18.sp, lineHeight = 22.sp) else t.displayM.copy(fontSize = 16.sp, lineHeight = 20.sp)
    val subtitleStyle: TextStyle = t.caption.copy(fontSize = 11.sp, lineHeight = 14.sp)

    InkButton(
        fill = fill,
        shape = shape,
        shadowDepth = IntroButtonShadow,
        modifier = modifier.fillMaxWidth().height(height = height),
        onClick = onClick,
    ) {
        // TODO(B3): 아이콘
        Column(
            modifier = Modifier,
        ) {
            Text(
                text = title,
                style = titleStyle,
                color = titleColor,
            )

            Text(
                text = subtitle,
                style = subtitleStyle,
                color = subtitleColor,
            )
        }
    }
}

private val IntroButtonShadow: Dp = 4.dp
