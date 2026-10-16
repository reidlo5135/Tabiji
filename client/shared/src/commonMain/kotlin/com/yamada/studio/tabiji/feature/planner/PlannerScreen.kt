package com.yamada.studio.tabiji.feature.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass
import com.yamada.studio.tabiji.core.ui.layout.WidthClass
import com.yamada.studio.tabiji.core.ui.modifier.inkPanel
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.modifier.paperStripes
import com.yamada.studio.tabiji.core.ui.theme.TabijiBorder
import com.yamada.studio.tabiji.core.ui.theme.TabijiColors
import com.yamada.studio.tabiji.core.ui.theme.TabijiSpacing
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme

@Composable
fun PlannerScreen(
    modifier: Modifier = Modifier
) {
    val widthClass: WidthClass = LocalWidthClass.current

    when(widthClass) {
        WidthClass.COMPACT -> PhonePlanner(modifier = modifier)
        WidthClass.MEDIUM -> DesktopPlanner(modifier = modifier)
        WidthClass.EXPANDED -> DesktopPlanner(modifier = modifier)
    }
}

@Composable
private fun DesktopPlanner(
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .paperStripes(c.paper, c.stripe, band = StripeBandDesktop)
    ) {
        MapFrame(
            controlSize = MapControlSizeDesktop,
            controlInset = TabijiSpacing.md,
            showZoom = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = MapFrameStartBottomDesktop, top = MapFrameTop,
                    end = MapFrameEndDesktop, bottom = MapFrameStartBottomDesktop
                )
        )

        PlannerSearchBar(
            modifier = Modifier
                .padding(TabijiSpacing.lg)
                .size(SearchBarSize)
        )

        Column(
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(TabijiSpacing.lg)
                .width(DayPanelWidth)
                .fillMaxHeight()
                .inkPanel(c)
        ) {
            DayPanelHeader(
                modifier = Modifier.height(DayHeaderHeightDesktop)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(c.soft)
            ) {

            }

            DayPanelFooter(
                contentPadding = FooterPaddingDesktop,
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun PhonePlanner(
    modifier: Modifier = Modifier
) {
    val c: TabijiColors = TabijiTheme.colors
    val sheetShape: Shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner)

    Box(
        modifier = modifier
            .fillMaxSize()
            .paperStripes(paper = c.paper, stripe = c.stripe, band = StripeBandPhone)
    ) {
        PlannerSearchBar(
            modifier = Modifier
                .padding(SearchBarPaddingPhone)
                .fillMaxWidth()
                .height(SearchBarHeightPhone)
        )

        MapFrame(
            controlSize = MapControlSizePhone,
            controlInset = MapControlInsetPhone,
            showZoom = false,
            modifier = Modifier
                .padding(
                    start = TabijiSpacing.md, top = MapFrameTopPhone,
                    end = TabijiSpacing.md, bottom = MapFrameBottomPhone
                )
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(SheetHeight)
                .inkShadow(SheetShadowOffset, c.shadow, sheetShape)
                .background(c.card, sheetShape)
                .border(TabijiBorder.panel, c.ink, sheetShape)
                .clip(sheetShape)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(GrabBarAreaHeight),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(GrabBarSize)
                        .background(c.ink.copy(alpha = GrabBarAlpha), CircleShape)
                )
            }

            DayPanelHeader(
                modifier = Modifier.height(DayHeaderHeightPhone)
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().background(c.soft)
            )

            DayPanelFooter(
                contentPadding = FooterPaddingPhone,
                modifier = Modifier
            )
        }
    }
}


private val DayPanelWidth: Dp = 376.dp
private val MapFrameStartBottomDesktop: Dp = 16.dp
private val MapFrameTop: Dp = 76.dp
private val MapFrameEndDesktop: Dp = 408.dp
private val SearchBarSize: DpSize = DpSize(480.dp, 48.dp)
private val DayHeaderHeightDesktop: Dp = 52.dp
private val DayHeaderHeightPhone: Dp = 44.dp
private val FooterPaddingDesktop: PaddingValues = PaddingValues(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 12.dp)
private val FooterPaddingPhone: PaddingValues = PaddingValues(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 24.dp)
private val MapControlSizeDesktop: Dp = 46.dp
private val MapControlSizePhone: Dp = 44.dp
private val MapControlInsetPhone: Dp = 10.dp
private val SheetHeight: Dp = 384.dp
private val SheetCorner: Dp = 18.dp
private val MapFrameTopPhone: Dp = 64.dp
private val MapFrameBottomPhone: Dp = 396.dp
private val StripeBandDesktop: Dp = 16.dp
private val StripeBandPhone: Dp = 14.dp
private val GrabBarAreaHeight: Dp = 16.dp
private val GrabBarSize: DpSize = DpSize(36.dp, 4.dp)
private const val GrabBarAlpha: Float = 0.3f
private val SearchBarPaddingPhone: PaddingValues = PaddingValues(start = 12.dp, top = 10.dp, end = 12.dp)
private val SearchBarHeightPhone: Dp = 44.dp
private val SheetShadowOffset: DpOffset = DpOffset(0.dp, (-4).dp)
