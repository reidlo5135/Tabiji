// androidApp — Android 앱 진입점. MainActivity(A2)와 매니페스트, 앱 버전, 서명 설정만 둔다.
// 화면·로직은 전부 :shared 에 있다.

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Android 앱(APK/AAB). AGP 9는 Kotlin 지원이 내장(built-in Kotlin)이라
    // org.jetbrains.kotlin.android 플러그인은 적용하지 않는다
    alias(libs.plugins.androidApplication)
    // MainActivity의 setContent { } 람다가 @Composable 이라 Compose 컴파일러가 필요하다
    alias(libs.plugins.composeCompiler)
}

// ── 앱 버전: gradle.properties의 tabiji.version 하나에서 계산 ──
val tabijiVersion: String = providers.gradleProperty("tabiji.version").get()
val (versionMajor, versionMinor, versionPatch) = tabijiVersion.split(".").map { it.toInt() }
require(versionMinor in 0..99 && versionPatch in 0..99) {
    "tabiji.version의 MINOR·PATCH는 0~99여야 한다 (versionCode 계산식 때문): $tabijiVersion"
}

// ── release 서명: 네 환경변수가 모두 있을 때만 적용 (Release CI의 android job이 설정) ──
// 로컬에서는 보통 없으므로 release 빌드는 미서명 APK가 된다. 키스토어·비밀번호는 절대 커밋하지 않는다
val keystorePath = providers.environmentVariable("TABIJI_KEYSTORE_PATH").orNull
val keystorePassword = providers.environmentVariable("TABIJI_KEYSTORE_PASSWORD").orNull
val keyAliasName = providers.environmentVariable("TABIJI_KEY_ALIAS").orNull
val keyPasswordValue = providers.environmentVariable("TABIJI_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(keystorePath, keystorePassword, keyAliasName, keyPasswordValue)
    .all { !it.isNullOrBlank() }

// AGP 내장 Kotlin의 컴파일 옵션. compileOptions의 Java 버전과 맞춘다
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // 공유 UI·로직 (App() 등)
    implementation(project(":shared"))
    // ComponentActivity.setContent { } — Android에서 Compose 화면을 띄우는 진입점 API
    implementation(libs.androidx.activity.compose)
}

android {
    // R 클래스·BuildConfig 패키지
    namespace = "com.yamada.studio.tabiji"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        // 스토어·기기에서 앱을 구분하는 ID. 한 번 배포하면 바꾸지 않는다
        applicationId = "com.yamada.studio.tabiji"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // 사람이 보는 버전 = tabiji.version
        versionName = tabijiVersion
        // 기기가 업그레이드 여부를 판단하는 정수. MAJOR*10000 + MINOR*100 + PATCH (0.1.0 → 100).
        // 0.0.0은 0이 되는데 versionCode는 1 이상이어야 하므로 최소 1
        versionCode = (versionMajor * 10_000 + versionMinor * 100 + versionPatch).coerceAtLeast(1)
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(checkNotNull(keystorePath))
                storePassword = keystorePassword
                keyAlias = keyAliasName
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            // 학습 단계에서는 코드 축소(R8)를 끈다. 켜면 keep 규칙 관리가 필요하다
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        // Compose UI를 쓰는 모듈임을 AGP에 알린다
        compose = true
    }

    packaging {
        resources {
            // 여러 라이브러리에 같은 라이선스 파일이 있어 APK 패키징이 충돌하는 것을 막는다
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
