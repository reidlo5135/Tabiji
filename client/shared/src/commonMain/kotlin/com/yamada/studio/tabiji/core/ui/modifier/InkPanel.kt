package com.yamada.studio.tabiji.core.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.yamada.studio.tabiji.core.ui.theme.TabijiBorder
import com.yamada.studio.tabiji.core.ui.theme.TabijiColors
import com.yamada.studio.tabiji.core.ui.theme.TabijiRadius
import com.yamada.studio.tabiji.core.ui.theme.TabijiShadow

fun Modifier.inkPanel(colors: TabijiColors, shape: Shape = RoundedCornerShape(TabijiRadius.panel)): Modifier =
    this.inkShadow(TabijiShadow.panel, colors.shadow, shape)
        .background(colors.card, shape)
        .border(TabijiBorder.panel, colors.ink, shape)
        .clip(shape)
