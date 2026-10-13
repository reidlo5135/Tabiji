// shared — Android와 Desktop이 공유하는 UI·로직 (Kotlin Multiplatform 라이브러리).
// 앱 진입점(main(), MainActivity)은 여기 두지 않는다. :androidApp, :desktopApp 이 이 모듈을 의존한다.
// CMake로 빗대면: 이 모듈은 add_library(tabiji_shared), 두 앱 모듈은 각각 add_executable + target_link_libraries.

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // KMP: 타깃(android, jvm)과 소스셋(commonMain, androidMain, jvmMain, commonTest)을 제공한다
    alias(libs.plugins.kotlinMultiplatform)
    // AGP 9의 KMP 전용 Android 라이브러리 플러그인. kotlin { android {} } 블록을 제공한다.
    // (com.android.library / com.android.application 은 AGP 9부터 KMP와 같이 쓸 수 없다)
    alias(libs.plugins.androidMultiplatformLibrary)
    // Compose Multiplatform: Compose Resources(Res 클래스 생성) 등
    alias(libs.plugins.composeMultiplatform)
    // Compose 컴파일러: @Composable 함수를 변환한다. Kotlin과 같은 버전이어야 한다
    alias(libs.plugins.composeCompiler)
}

kotlin {
    // Android 타깃. AGP 8.12+ 의 android {} 블록 (androidLibrary {} 는 AGP 9.1부터 deprecated)
    android {
        // 라이브러리 namespace. :androidApp 의 namespace(com.yamada.studio.tabiji)와 겹치면 안 된다
        namespace = "com.yamada.studio.tabiji.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        // Android 쪽 바이트코드 수준. :androidApp 의 compileOptions와 맞춘다
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        // Compose Resources(문자열·폰트·이미지)를 Android 리소스로 패키징하려면 켜야 한다 (A6)
        androidResources {
            enable = true
        }
    }

    // JVM 타깃. :desktopApp 이 이 타깃의 산출물을 쓴다. 소스셋 이름은 jvmMain / jvmTest
    jvm()

    sourceSets {
        // 모든 플랫폼이 공유하는 코드가 쓰는 의존성
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
        }
        // 공용 단위 테스트. CI에서는 JVM에서 실행한다 (:shared:jvmTest)
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
