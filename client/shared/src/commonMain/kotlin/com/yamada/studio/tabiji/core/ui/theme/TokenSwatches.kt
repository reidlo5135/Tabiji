package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp


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

    Column(
        modifier = modifier.background(c.paper).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        rows.forEach { (name, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(32.dp).background(color).border(2.dp, c.ink)
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
    }
}
