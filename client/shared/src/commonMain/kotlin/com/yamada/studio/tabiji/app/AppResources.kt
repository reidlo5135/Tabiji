package com.yamada.studio.tabiji.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.Res
import tabiji.shared.generated.resources.app_icon
import tabiji.shared.generated.resources.app_name

@Composable
fun appIcon(): Painter = painterResource(Res.drawable.app_icon)

@Composable
fun appTitle(): String = stringResource(Res.string.app_name)

