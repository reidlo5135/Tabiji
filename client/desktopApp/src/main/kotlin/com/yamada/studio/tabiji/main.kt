package com.yamada.studio.tabiji

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yamada.studio.tabiji.app.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
