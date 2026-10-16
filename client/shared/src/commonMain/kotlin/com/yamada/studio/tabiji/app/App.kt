package com.yamada.studio.tabiji.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yamada.studio.tabiji.core.ui.layout.ProvideWidthClass
import com.yamada.studio.tabiji.core.ui.theme.TabijiPalette
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import com.yamada.studio.tabiji.core.ui.theme.TokenSwatches
import com.yamada.studio.tabiji.feature.intro.IntroScreen
import com.yamada.studio.tabiji.feature.planner.PlannerScreen

@Composable
fun App(
    screen: DebugScreen = DebugScreen.INTRO,
    palette: TabijiPalette = TabijiPalette.CLOUD,
) {
    ProvideWidthClass {
        TabijiTheme(palette) {
            when(screen) {
                DebugScreen.INTRO -> IntroScreen()
                DebugScreen.PLANNER -> PlannerScreen()
                DebugScreen.TOKENS -> TokenSwatches(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
