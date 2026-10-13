// client/ 는 독립 Gradle 루트다. 레포 루트나 server/ 와 하나의 빌드로 묶지 않는다.
// 모든 Gradle 명령은 client/ 에서 실행한다.

// 사람이 보는 이름은 "Tabiji", 빌드 식별자는 소문자 "tabiji"
rootProject.name = "tabiji"

// 플러그인(AGP, Kotlin, Compose)을 내려받을 저장소.
// google()은 androidx·com.android·com.google 그룹만 찾게 제한해 불필요한 조회를 줄인다.
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// 라이브러리 저장소는 여기 한 곳에서 정한다 (모듈에 repositories {}를 따로 쓰지 않는다)
dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// JDK 툴체인 자동 다운로드. Compose Hot Reload가 쓰는 JetBrains Runtime(JBR)을
// 시스템에 설치하지 않고 Gradle이 받아 오게 한다 (A1).
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// 모듈 구조 (Kotlin Multiplatform 공식 권장 구조 — docs/decisions/0002-toolchain-versions.md)
// shared     : 두 플랫폼이 공유하는 UI·로직 (KMP 라이브러리, Android-KMP 라이브러리 플러그인)
// androidApp : Android 앱 진입점 (APK/AAB). AGP 9부터 KMP 모듈과 분리가 필수다
// desktopApp : Desktop 앱 진입점 (main(), 실행·Hot Reload·설치 패키지)
include(":shared")
include(":androidApp")
include(":desktopApp")
