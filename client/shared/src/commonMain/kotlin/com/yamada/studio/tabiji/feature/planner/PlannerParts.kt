package com.yamada.studio.tabiji.feature.planner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yamada.studio.tabiji.core.ui.component.InkButton
import com.yamada.studio.tabiji.core.ui.modifier.inkPanel
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.shape.ChamferShape
import com.yamada.studio.tabiji.core.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.*

@Composable
internal fun MapFrame(
    controlSize: Dp,
    controlInset: Dp,
    showZoom: Boolean,
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors

    Box(
        modifier = modifier.inkPanel(c)
    ) {
        Image(
            painter = painterResource(Res.drawable.planner_map_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        MapControls(
            size = controlSize,
            showZoom = showZoom,
            modifier = Modifier.align(Alignment.BottomEnd).padding(controlInset)
        )
    }
}

@Composable
internal fun MapControls(
    size: Dp,
    showZoom: Boolean,
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors
    val shape: Shape = RoundedCornerShape(TabijiRadius.button)
    val buttonModifier: Modifier = Modifier
        .size(size)
        .inkShadow(TabijiShadow.button, c.shadow, shape)
        .background(c.card, shape)
        .border(TabijiBorder.panel, c.ink, shape)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TabijiTheme.spacing.sm)
    ) {
        Box(modifier = buttonModifier)
        if (showZoom) Box(modifier = buttonModifier)
    }
}

@Composable
internal fun PlannerSearchBar(
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors

    Row(
        modifier = modifier
            .inkPanel(c)
            .padding(start = SearchBarPaddingStart, end = SearchBarPaddingEnd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowGap)
    ) {
        Text(
            text = stringResource(Res.string.planner_search_hint),
            style = TabijiTheme.typography.body,
            color = c.sub,
            modifier = Modifier.weight(1f)
        )

        InkButton(
            fill = c.accent,
            modifier = Modifier.height(SearchButtonHeight),
            shape = RoundedCornerShape(SearchButtonCorner),
            shadowDepth = SearchButtonShadow,
            onClick = {}
        ) {
            Text(
                text = stringResource(Res.string.planner_search),
                style = TabijiTheme.typography.displayS,
                color = TabijiTheme.colors.onAccent
            )
        }
    }
}

@Composable
internal fun DayPanelHeader(
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(c.headers[0])
                .padding(horizontal = HeaderPaddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RowGap)
        ) {
            Text(
                text = DUMMY_DAY_TITLE,
                style = TabijiTheme.typography.displayL,
                color = Color.White
            )

            Text(
                text = DUMMY_DAY_DATE,
                style = TabijiTheme.typography.body.copy(fontSize = 13.sp),
                color = Color.White.copy(alpha = 0.92f)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = DUMMY_DAY_MOVE,
                style = TabijiTheme.typography.badge.copy(fontSize = 12.sp),
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .border(width = TabijiBorder.panel, color = c.ink, shape = CircleShape)
                    .padding(horizontal = BadgePaddingHorizontal, vertical = BadgePaddingVertical)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TabijiBorder.divider)
                .background(c.ink)
        )
    }
}

@Composable
internal fun DayPanelFooter(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val c: TabijiColors = TabijiTheme.colors

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TabijiBorder.divider)
                .background(c.ink),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(c.card)
                .padding(contentPadding)
        ) {
            InkButton(
                fill = c.accent,
                shape = ChamferShape(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AiButtonHeight),
                onClick = {}
            ) {
                Text(
                    text = stringResource(Res.string.planner_ai_optimize),
                    style = TabijiTheme.typography.displayM.copy(fontSize = 18.sp, lineHeight = 22.sp, color = c.onAccent)
                )
            }
        }
    }
}

private val AiButtonHeight: Dp = 50.dp
private val SearchButtonHeight: Dp = 34.dp
private val SearchButtonCorner: Dp = 8.dp
private val SearchButtonShadow: Dp = 2.dp
private val SearchBarPaddingStart: Dp = 14.dp
private val SearchBarPaddingEnd: Dp = 8.dp
private val RowGap: Dp = 10.dp
private val HeaderPaddingHorizontal: Dp = 14.dp
private val BadgePaddingHorizontal: Dp = 10.dp
private val BadgePaddingVertical: Dp = 2.dp
