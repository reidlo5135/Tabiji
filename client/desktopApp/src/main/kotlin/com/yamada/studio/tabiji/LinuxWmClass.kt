package com.yamada.studio.tabiji

import java.awt.Toolkit

/**
 * Linux 데스크톱(GNOME 등)이 실행 중인 창을 설치된 `.desktop` 항목과 짝짓게 한다.
 *
 * GNOME은 창의 WM_CLASS를 `.desktop` 파일 이름(또는 `StartupWMClass`)과 비교해 독 아이콘을 고른다.
 * AWT는 기본으로 메인 클래스 이름(`com-yamada-studio-tabiji-MainKt`)을 WM_CLASS로 쓰는데,
 * jpackage가 만든 항목 이름은 `tabiji-Tabiji.desktop`이라 짝이 안 맞아 기본 톱니바퀴 아이콘이 뜬다.
 * AWT의 비공개 필드 `XToolkit.awtAppClassName`을 바꿔 WM_CLASS를 항목 이름과 맞춘다.
 *
 * - 창을 만들기 전에 불러야 한다
 * - `--add-opens java.desktop/sun.awt.X11=ALL-UNNAMED`가 필요하다 (`desktopApp/build.gradle.kts`의 `jvmArgs`)
 * - Linux가 아니거나 필드를 못 바꾸면 조용히 넘어간다. 아이콘만 기본값이 될 뿐 앱 동작에는 영향이 없다
 */
internal fun setLinuxWmClass(desktopEntryName: String) {
    if (!System.getProperty("os.name").orEmpty().startsWith("Linux")) return
    runCatching {
        val toolkit = Toolkit.getDefaultToolkit()
        val field = toolkit.javaClass.getDeclaredField("awtAppClassName")
        field.isAccessible = true
        field.set(toolkit, desktopEntryName)
    }
}

/** jpackage가 만드는 `.desktop` 파일 이름: `<linux.packageName>-<packageName>` (`desktopApp/build.gradle.kts`) */
internal const val LINUX_DESKTOP_ENTRY = "tabiji-Tabiji"
