package com.yamada.studio.tabiji

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yamada.studio.tabiji.app.App
import com.yamada.studio.tabiji.core.ui.theme.TabijiPalette

fun main() = application {
    var palette by remember { mutableStateOf(TabijiPalette.CLOUD) }
    var showTokens by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Tabiji"
    ) {
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
