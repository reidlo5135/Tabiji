package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.icon.TabijiIcons
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.shape.ChamferShape
import org.jetbrains.compose.resources.painterResource


@Composable
fun TokenSwatches(modifier: Modifier = Modifier) {
    val c = TabijiTheme.colors
    val rows = listOf(
        "paper" to c.paper,
        "stripe" to c.stripe,
        "ink" to c.ink,
        "card" to c.card,
        "soft" to c.soft,
        "accent" to c.accent,
        "onAccent" to c.onAccent,
        "sub" to c.sub,
        "shadow" to c.shadow,
        "skel" to c.skel,
        "est" to c.est,
        "err" to c.err,
        "errSoft" to c.errSoft,
        "ok" to c.ok,
    ) + c.headers.mapIndexed { i, color -> "h${i + 1}" to color }

    val t = TabijiTheme.typography
    val typeRows = listOf(
        "displayXL" to t.displayXL,
        "displayL" to t.displayL,
        "displayM" to t.displayM,
        "displayS" to t.displayS,
        "body" to t.body,
        "caption" to t.caption,
        "badge" to t.badge,
        "numeric" to t.numeric,
        "wordmark" to t.wordmark
    )

    Column(
        modifier = modifier.background(c.paper).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.xs),
    ) {
        rows.forEach { (name, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.md)
            ) {
                Box(
                    modifier = Modifier.size(186.dp, 48.dp)
                        .inkShadow(DpOffset(0.dp, 4.dp), c.ink, ChamferShape())
                        .background(c.accent, ChamferShape())
                        .border(TabijiTheme.border.panel, c.ink, ChamferShape())
                )
                Text(
                    text = name,
                    color = c.ink
                )
                Text(
                    text = "#" + color.toArgb().toUInt().toString(16).padStart(8, '0').uppercase(),
                    color = c.ink
                )
            }
        }

        // 디버그 샘플
        typeRows.forEach { (name, type) ->
            Text(
                text = "$name Day 2 · 시즈오카 센겐 신사 · 駿府城 · 旅路 1.8km",
                style = type,
                color = c.ink
            )
        }

        TabijiIcons.all.chunked(8).forEach { rowIcons ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.md)
            ) {
                rowIcons.forEach {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        tint = c.ink,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
