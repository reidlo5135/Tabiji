package com.yamada.studio.tabiji.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yamada.studio.tabiji.core.ui.layout.ProvideWidthClass
import com.yamada.studio.tabiji.core.ui.theme.TabijiPalette
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import com.yamada.studio.tabiji.core.ui.theme.TokenSwatches
import com.yamada.studio.tabiji.feature.intro.IntroScreen

@Composable
fun App(palette: TabijiPalette = TabijiPalette.CLOUD, showTokens: Boolean = false) {
    ProvideWidthClass {
        TabijiTheme(palette) {
            if (showTokens) {
                TokenSwatches(modifier = Modifier.fillMaxSize())
            } else {
                IntroScreen()
            }
        }
    }
}
