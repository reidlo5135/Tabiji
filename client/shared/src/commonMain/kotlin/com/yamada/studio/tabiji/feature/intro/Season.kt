package com.yamada.studio.tabiji.feature.intro

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import tabiji.shared.generated.resources.*

enum class Season(
    val image: DrawableResource,
    val label: StringResource,
    val desktopFocusY: Float,
    val phoneFocusY: Float,
) {
    SPRING(Res.drawable.intro_spring, Res.string.season_spring, 0f, 0f),
    SUMMER(Res.drawable.intro_summer, Res.string.season_summer, 0f, 0f),
    AUTUMN(Res.drawable.intro_autumn, Res.string.season_autumn, 0f, 0f),
    WINTER(Res.drawable.intro_winter, Res.string.season_winter, 0f, 0f),
}
