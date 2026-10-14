package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import tabiji.shared.generated.resources.*


@Immutable
data class TabijiTypography(
    val displayXL: TextStyle,
    val displayL: TextStyle,
    val displayM: TextStyle,
    val displayS: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
    val badge: TextStyle,
    val numeric: TextStyle,
    val wordmark: TextStyle,
)

@Composable
fun tabijiTypography(): TabijiTypography {
    val pretendard = FontFamily(
        Font(Res.font.pretendardjp_semibold, FontWeight.SemiBold),
        Font(Res.font.pretendardjp_bold, FontWeight.Bold),
    )
    val sora = FontFamily(
        Font(Res.font.sora_semibold, FontWeight.SemiBold),
        Font(Res.font.sora_bold, FontWeight.Bold)
    )
    val display = FontFamily(
        Font(Res.font.jua_regular, FontWeight.Normal),
        Font(Res.font.pretendardjp_semibold, FontWeight.SemiBold),
    )

    return TabijiTypography(
        displayXL = TextStyle(fontFamily = display, fontSize = 26.sp, lineHeight = 32.sp),
        body = TextStyle(fontFamily = pretendard, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        wordmark = TextStyle(
            fontFamily = sora, fontSize = 58.sp, lineHeight = 64.sp,
            fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em,
        ),
        displayL = TextStyle(fontFamily = display, fontSize = 20.sp, lineHeight = 26.sp),
        displayM = TextStyle(fontFamily = display, fontSize = 16.sp, lineHeight = 22.sp),
        displayS = TextStyle(fontFamily = display, fontSize = 14.sp, lineHeight = 18.sp),
        caption = TextStyle(fontFamily = pretendard, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
        badge = TextStyle(fontFamily = pretendard, fontSize = 11.sp, fontWeight = FontWeight.Bold, lineHeight = 14.sp),
        numeric = TextStyle(fontFamily = sora, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
    )
}
