package com.yamada.studio.tabiji

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.yamada.studio.tabiji.app.App
import com.yamada.studio.tabiji.app.appIcon
import com.yamada.studio.tabiji.app.appTitle
import com.yamada.studio.tabiji.core.ui.theme.TabijiPalette
import java.awt.GraphicsEnvironment

fun main() = application {
    val screen = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize((screen.width * 0.85F).dp, (screen.height * 0.75F).dp)
    )

    var palette by remember { mutableStateOf(TabijiPalette.CLOUD) }
    var showTokens by remember { mutableStateOf(false) }

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = appTitle(),
        icon = appIcon(),
    ) {
        window.minimumSize = java.awt.Dimension(360, 600)

        // 디버그 메뉴 — B6 설정 화면이 생기면 정리
        MenuBar {
            Menu("Theme") {
                TabijiPalette.entries.forEach { p ->
                    RadioButtonItem(p.name, selected = palette === p, onClick = { palette = p })
                }
            }
            Menu("Debug") {
                CheckboxItem("Token Page", checked = showTokens, onCheckedChange = { showTokens = it })
            }
        }
        App(palette = palette, showTokens = showTokens)
    }
}
