package com.yamada.studio.tabiji.core.ui.theme

import androidx.compose.ui.graphics.Color

enum class TabijiPalette(val colors: TabijiColors) {
    CLOUD(
        TabijiColors(
            paper = Color(0xFFF2F7FC),
            stripe = Color(0xFFE8F1F9),
            ink = Color(0xFF2A3644),
            card = Color(0xFFFFFFFF),
            soft = Color(0xFFEAF3FB),
            accent = Color(0xFF3E8FC7),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF5B6878),
            headers = listOf(Color(0xFF4A8FC2), Color(0xFF5F74B8), Color(0xFF4A9A93)),
            shadow = Color(0x382A3644),
            skel = Color(0xFFDEE6EE),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    SPRING(
        TabijiColors(
            paper = Color(0xFFFCF3F5),
            stripe = Color(0xFFF7E8EC),
            ink = Color(0xFF43313A),
            card = Color(0xFFFFFFFF),
            soft = Color(0xFFF9E9EE),
            accent = Color(0xFFC95E7E),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF6E5A63),
            headers = listOf(Color(0xFFC95E7E), Color(0xFF6F9148), Color(0xFF9A77C2)),
            shadow = Color(0x3843313A),
            skel = Color(0xFFEEDDE2),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    SUMMER(
        TabijiColors(
            paper = Color(0xFFEFF9FB),
            stripe = Color(0xFFE2F2F6),
            ink = Color(0xFF1E3B4C),
            card = Color(0xFFFFFFFF),
            soft = Color(0xFFE2F4F8),
            accent = Color(0xFF1F96B8),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF4E6B7A),
            headers = listOf(Color(0xFF1F96B8), Color(0xFF2C7FC9), Color(0xFF2E9E85)),
            shadow = Color(0x381E3B4C),
            skel = Color(0xFFD8EBF1),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    AUTUMN(
        TabijiColors(
            paper = Color(0xFFFBF3E6),
            stripe = Color(0xFFF4E8D3),
            ink = Color(0xFF46321F),
            card = Color(0xFFFFFDF8),
            soft = Color(0xFFF7EAD6),
            accent = Color(0xFFC06F22),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF77614A),
            headers = listOf(Color(0xFFC06F22), Color(0xFF8F741A), Color(0xFFB35C3B)),
            shadow = Color(0x3846321F),
            skel = Color(0xFFECDFC8),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    WINTER(
        TabijiColors(
            paper = Color(0xFFF1F5FA),
            stripe = Color(0xFFE6EDF6),
            ink = Color(0xFF2A3650),
            card = Color(0xFFFFFFFF),
            soft = Color(0xFFE8EEF8),
            accent = Color(0xFF5674AE),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF5B6880),
            headers = listOf(Color(0xFF5674AE), Color(0xFF6676B8), Color(0xFF7E6FB5)),
            shadow = Color(0x382A3650),
            skel = Color(0xFFDCE4F0),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    KOREA(
        TabijiColors(
            paper = Color(0xFFFAF4EA),
            stripe = Color(0xFFF2E9DA),
            ink = Color(0xFF30261C),
            card = Color(0xFFFFFCF5),
            soft = Color(0xFFEDE4D2),
            accent = Color(0xFF2F5E9E),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF6B5D4A),
            headers = listOf(Color(0xFF2F5E9E), Color(0xFFBE4450), Color(0xFF2E7D5B), Color(0xFFA8801F)),
            shadow = Color(0x3830261C),
            skel = Color(0xFFE8DFCF),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    CHINA(
        TabijiColors(
            paper = Color(0xFFFCF2EA),
            stripe = Color(0xFFF6E6D8),
            ink = Color(0xFF3C241C),
            card = Color(0xFFFFFAF3),
            soft = Color(0xFFF6E3D6),
            accent = Color(0xFFBE4435),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF7A5A48),
            headers = listOf(Color(0xFFBE4435), Color(0xFFA8791C), Color(0xFF357F63)),
            shadow = Color(0x383C241C),
            skel = Color(0xFFEFDFD2),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
    JAPAN(
        TabijiColors(
            paper = Color(0xFFF6F5F0),
            stripe = Color(0xFFECEAE2),
            ink = Color(0xFF2B2B30),
            card = Color(0xFFFFFEFA),
            soft = Color(0xFFE9E7DC),
            accent = Color(0xFF2B4C7E),
            onAccent = Color(0xFFFFFFFF),
            sub = Color(0xFF5F5F66),
            headers = listOf(Color(0xFF2B4C7E), Color(0xFFC25548), Color(0xFF5F7038)),
            shadow = Color(0x382B2B30),
            skel = Color(0xFFE2E0D6),
            est = Color(0xFFE3A23B),
            err = Color(0xFFC0463C),
            errSoft = Color(0xFFF7E3E0),
            ok = Color(0xFF2E7D5B)
        )
    ),
}
