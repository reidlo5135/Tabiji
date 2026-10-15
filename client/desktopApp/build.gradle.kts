// desktopApp — Desktop(JVM) 앱 진입점. main()(A1)과 실행·Hot Reload·설치 패키지 설정만 둔다.
// 화면·로직은 전부 :shared 에 있다.

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    // 순수 Kotlin/JVM 모듈 (멀티플랫폼일 필요 없음)
    alias(libs.plugins.kotlinJvm)
    // Compose Desktop: run, 설치 패키지(jpackage), Hot Reload(CMP 1.10+ 기본 포함)
    alias(libs.plugins.composeMultiplatform)
    // main()의 application { Window { } } 람다가 @Composable 이라 필요하다
    alias(libs.plugins.composeCompiler)
}

// 앱 버전 단일 원본(gradle.properties의 tabiji.version)
val tabijiVersion: String = providers.gradleProperty("tabiji.version").get()

dependencies {
    // 공유 UI·로직 (App() 등). :shared 의 jvm() 타깃 산출물이 연결된다
    implementation(project(":shared"))
    // 현재 OS용 Skia 네이티브 라이브러리를 포함한 Compose Desktop
    implementation(compose.desktop.currentOs)
    // Swing 이벤트 스레드를 Dispatchers.Main으로 쓰게 해 준다 (코루틴에서 UI 갱신)
    implementation(libs.kotlinx.coroutinesSwing)
}

// Desktop 앱 실행(:desktopApp:run, :desktopApp:hotRun)과 설치 패키지 설정
compose.desktop {
    application {
        // A1에서 사용자가 만들 src/main/kotlin/com/yamada/studio/tabiji/main.kt 의 main() (main.kt → MainKt)
        mainClass = "com.yamada.studio.tabiji.MainKt"

        // 설치 패키지(jpackage). 크로스 빌드가 안 돼서 각 OS에서 자기 형식만 만든다 (Release CI 매트릭스)
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Msi, TargetFormat.Dmg)
            packageName = "Tabiji"
            packageVersion = tabijiVersion
            description = "Tabiji (旅路) — travel route planner"
            vendor = "yamada-studio"
            copyright = "© 2026 yamada-studio. MIT License."

            linux {
                // deb 패키지 이름은 소문자만 허용된다
                packageName = "tabiji"
                shortcut = true
                // 앱 목록·독 아이콘. 旅 글리프, 투명 배경 (handoff-v2 04_icons_favicon: Desktop 기본)
                iconFile.set(project.file("icons/tabiji.png"))
            }
            windows {
                // MSI 업그레이드 식별자. 바꾸면 새 버전이 기존 설치를 덮어쓰지 못한다 — 절대 변경 금지
                upgradeUuid = "6ee9ef0a-cb77-4182-b044-3fc8fe6b500b"
                menu = true
                shortcut = true
                // 같은 512px 원본에서 만든 16~256px 묶음 (icons/README.md)
                iconFile.set(project.file("icons/tabiji.ico"))
            }
            macOS {
                bundleID = "com.yamada.studio.tabiji"
                iconFile.set(project.file("icons/tabiji.icns"))
                // macOS jpackage는 버전 첫 자리가 0이면 거부한다. 0.x 동안만 1.x로 올려 쓰고,
                // 파일 이름·릴리스에는 진짜 버전(tabiji.version)을 쓴다 (docs/decisions/0002)
                packageVersion = tabijiVersion.split(".").let { (major, minor, patch) ->
                    if (major == "0") "1.$minor.$patch" else tabijiVersion
                }
            }
        }
    }
}
