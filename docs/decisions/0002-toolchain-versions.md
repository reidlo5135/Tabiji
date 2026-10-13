# 0002. 클라이언트 툴체인·모듈 구조·CI Action 버전

- 상태: 채택
- 날짜: 2026-10-13 (모든 버전은 이 날짜에 공식 저장소·문서에서 확인)
- 결정한 사람: @<CLIENT_OWNER>
- 영역: client, ci

## 맥락

- 클라이언트는 Kotlin + Compose Multiplatform(CMP), 1차 타깃 Android + Desktop(JVM)
- 버전은 기억이 아니라 **확인 당일의 공식 안정 버전**으로 고정하고, 출처를 남긴다
- GitHub Action은 태그가 아니라 **커밋 SHA로 고정**(태그는 옮겨질 수 있다)하고 옆에 버전 주석을 단다. Dependabot이 SHA와 주석을 같이 올린다

## 1. 클라이언트 툴체인

| 항목 | 버전 | 근거·출처 |
| --- | --- | --- |
| Kotlin | **2.4.21** (2026-10-08) | [Kotlin releases](https://kotlinlang.org/docs/releases.html), [Maven Central metadata](https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/maven-metadata.xml). 2.5.0은 Beta |
| Compose Multiplatform | **1.12.1** (2026-09-22) | [CMP releases](https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.12.1). 1.13.0은 alpha |
| CMP material3 | **1.12.0-alpha03** ⚠ | [Maven metadata](https://repo1.maven.org/maven2/org/jetbrains/compose/material3/material3/maven-metadata.xml). CMP 1.12와 짝인 안정판이 없다. 공식 위저드도 이 버전을 쓴다. 마지막 안정판 1.9.0은 CMP 1.12와의 호환이 확인되지 않아 쓰지 않는다 → **안정 버전 원칙의 유일한 예외** |
| Android Gradle Plugin | **9.3.3** | [AGP maven metadata](https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/maven-metadata.xml), [AGP 릴리스 노트](https://developer.android.com/build/releases/gradle-plugin). 아래 2절 |
| Gradle | **9.7.1** (2026-08-19), `-bin` + SHA-256 고정 | [services.gradle.org](https://services.gradle.org/versions/all), [체크섬](https://services.gradle.org/distributions/gradle-9.7.1-bin.zip.sha256) `acd53f1e…f804d20a`. AGP 9.3 최소 Gradle 9.5.0 ([AGP 호환표](https://developer.android.com/build/releases/about-agp)) |
| androidx.activity:activity-compose | **1.13.0** | [AndroidX activity releases](https://developer.android.com/jetpack/androidx/releases/activity). 1.14.0은 alpha |
| kotlinx-coroutines-swing | **1.11.0** | [Maven metadata](https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-swing/maven-metadata.xml). `desktopApp`의 `Dispatchers.Main`, 공식 권장 구조·위저드 기본 포함 |
| foojay-resolver-convention | **1.0.0** | [Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.gradle.toolchains.foojay-resolver-convention). Hot Reload용 JetBrains Runtime 자동 다운로드. kotlinlang Hot Reload 문서에 1.2.0이 적혀 있지만 포털에 없다(2026-10-13) |
| compileSdk / targetSdk | **37** (Android 17) | [Android 17 안정판 발표](https://android-developers.googleblog.com/2026/06/Android-17.html). CMP 1.12.1의 `androidx.compose.ui` AAR이 `minCompileSdk=37`, `minAndroidGradlePluginVersion=9.1.0`을 요구 |
| minSdk | **24** | 프로젝트 고정값 |
| JDK | 17 이상 (CI: Temurin 17) | AGP 9.x 최소 JDK 17, Gradle 9.x 실행 JVM 17~27 ([Gradle 호환성](https://docs.gradle.org/current/userguide/compatibility.html)), jpackage 최소 JDK 17 |

### Kotlin 테스트 범위와의 관계

[KMP 호환성 가이드](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html)에 따르면 Kotlin 2.4.20~2.4.21의 테스트 범위는 **Gradle 7.6.3–9.7.0, AGP 8.5.2–9.3.1**이다.

- AGP는 테스트된 **9.3 라인의 최신 패치 9.3.3**을 쓴다. 최신 9.4.1은 Kotlin 테스트 범위 밖이라 보류
- Gradle은 테스트된 **9.7 라인의 패치 9.7.1**을 쓴다. 최신 9.8.1은 범위 밖이라 보류
- Dependabot이 AGP 9.4·Gradle 9.8을 제안하면, Kotlin 호환성 가이드가 갱신된 뒤 올린다

## 2. 모듈 구조 — `shared` + `androidApp` + `desktopApp`

### 문제

초기 계획은 `composeApp` 한 모듈에 `org.jetbrains.kotlin.multiplatform` + `com.android.application` + `androidTarget()`을 두는 구조였다. 현재 안정 스택에서는 이것이 불가능하거나 곧 불가능해진다.

- CMP 1.12.x는 **AGP 9.1 이상**을 요구한다 (위 AAR 메타데이터)
- AGP 9부터 KMP 모듈에 `com.android.application`/`com.android.library`를 함께 쓸 수 없다. Kotlin 2.4.21은 이 조합에 치명 오류 `KMPIsIncompatibleWithTheNewAgpDsl`을 낸다 ([KGP 진단 소스](https://github.com/JetBrains/kotlin/blob/v2.4.21/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/plugin/diagnostics/KotlinToolingDiagnostics.kt))
- 우회 플래그(`android.newDsl=false`, `android.builtInKotlin=false`)는 **AGP 10(2026년 하반기)에서 제거**된다 ([AGP 로드맵](https://developer.android.com/build/releases/gradle-plugin-roadmap), [built-in Kotlin 마이그레이션](https://developer.android.com/build/migrate-to-built-in-kotlin))
- 구버전(CMP 1.11.1 + AGP 8.13.2)에 머물면 시작부터 한 세대 뒤처지고, 업그레이드하려면 어차피 분리해야 한다

### 결정

Kotlin Multiplatform **공식 권장 구조**를 따른다. "앱 진입점마다 별도 모듈, 공유 코드는 하나의 모듈"이며, "AGP 9 이상을 쓰려면 Android 앱 진입점을 공통 코드에서 분리하는 것이 필수"다.

| 모듈 | 플러그인 | 내용 |
| --- | --- | --- |
| `shared` | `kotlin.multiplatform` + `com.android.kotlin.multiplatform.library` + `org.jetbrains.compose` + `kotlin.plugin.compose` | 모든 화면·로직. 타깃 `kotlin { android {} ; jvm() }`, 소스셋 `commonMain/androidMain/jvmMain/commonTest` |
| `androidApp` | `com.android.application` + `kotlin.plugin.compose` | `MainActivity`(A2), 매니페스트, `versionName/Code`, release 서명. **`org.jetbrains.kotlin.android`는 적용하지 않는다** (AGP 9 내장 Kotlin) |
| `desktopApp` | `kotlin.jvm` + `org.jetbrains.compose` + `kotlin.plugin.compose` | `main()`(A1), `compose.desktop.application`, `nativeDistributions`, Hot Reload |

출처:

- [Recommended KMP project structure](https://kotlinlang.org/docs/multiplatform/multiplatform-project-recommended-structure.html)
- [Updating multiplatform projects to support AGP 9](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html)
- [Android: Set up the Android Gradle Library Plugin for KMP](https://developer.android.com/kotlin/multiplatform/plugin) (2026-10-01 갱신)
- 공식 KMP 위저드 출력 (<https://kmp.jetbrains.com>, Android + Desktop 선택, 2026-10-13 생성)

### 공식 문서·위저드와 다른 점 (그리고 왜)

| 항목 | kotlinlang 가이드 | Android 문서 | 위저드 | Tabiji | 이유 |
| --- | --- | --- | --- | --- | --- |
| 공유 모듈 Android 블록 | `kotlin { androidLibrary {} }` | `kotlin { android {} }` — `androidLibrary {}`는 **AGP 9.1.0-alpha09부터 deprecated** | `android {}` | `android {}` | 두 공식 문서가 다르다. AGP 제공자인 Android 문서가 더 최신(2026-10-01)이고 위저드도 같음 |
| `androidApp` 플러그인 | `androidApplication` + `composeMultiplatform` + `composeCompiler` (`kotlinAndroid`는 AGP 9에서 제거) | — | `androidApplication` + `composeCompiler` | 위저드와 같음 | `MainActivity`의 `setContent {}` 람다가 `@Composable`이라 Compose 컴파일러는 필요. `compose.*` 접근자·리소스를 쓰지 않으므로 `composeMultiplatform`은 불필요 |
| `androidApp` Kotlin 옵션 | `kotlin { target { compilerOptions {} } }` | — | `kotlin { compilerOptions {} }` | 위저드와 같음 | 같은 효과, 더 짧은 쪽 |
| JVM 타깃 이름 | `jvm()` | — | `jvm()` | `jvm()` | 초기 계획의 `jvm("desktop")`/`desktopMain` 대신 공식 이름. 진입점이 `desktopApp`으로 나가서 `shared`의 JVM 소스셋은 "Desktop 앱"이 아니라 "JVM 플랫폼 코드"다 |
| 버전 | 예시 AGP 9.0.0 / Gradle 9.1.0 | — | AGP 9.1.1 / Gradle 9.5.1 / Kotlin 2.4.20 | AGP 9.3.3 / Gradle 9.7.1 / Kotlin 2.4.21 | 1절 — Kotlin 테스트 범위 안의 최신 |
| `gradle/gradle-daemon-jvm.properties` | — | — | 있음 (JBR 21 자동 다운로드) | 없음 | 파일 목록 최소화. 로컬 JDK 21, CI Temurin 17로 충분. Hot Reload용 JBR은 foojay가 받는다 |
| `shared`의 Android 호스트/디바이스 테스트, `ui-tooling` | — | — | 있음 | 없음 | 지금은 `commonTest`를 JVM에서만 돌린다. VS Code에는 Compose Preview가 없다. 필요한 단계에서 추가 |
| `desktopApp` `ui-tooling-preview` | — | — | 있음 | 없음 | 위와 같음 |

### 결과

- 쉬워지는 것: AGP 10이 나와도 구조 변경 없이 올릴 수 있다. B4 모듈 분리 때 진입점 모듈은 그대로 두고 `shared`만 나눈다
- 감수할 것: Phase A부터 모듈이 3개다. A1(Desktop)과 A2(Android) 가이드에서 "진입점은 앱 모듈, 화면은 `shared`"를 먼저 설명한다
- Gradle 태스크 경로: `:desktopApp:run`, `:desktopApp:hotRun`, `:desktopApp:packageReleaseDistributionForCurrentOS`, `:androidApp:assembleDebug`/`installDebug`/`assembleRelease`/`bundleRelease`, `:shared:jvmTest`

## 3. Desktop 패키징

- 태스크: `:desktopApp:packageReleaseDistributionForCurrentOS` (CMP 1.12.1, [Native distributions 문서](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html)). release는 ProGuard를 거친다. 크로스 빌드 불가 → Release CI가 OS별 러너에서 만든다
- 산출물: `desktopApp/build/compose/binaries/main-release/{deb,msi,dmg}/`
- **macOS 버전 제약**: macOS jpackage(JDK 17/21)는 버전 첫 자리 0을 거부한다 ("The first number in an app-version cannot be zero or negative", [jdk21u MacResources](https://github.com/openjdk/jdk21u/blob/master/src/jdk.jpackage/macosx/classes/jdk/jpackage/internal/resources/MacResources.properties)). 0.x 동안은 `macOS { packageVersion }`을 `1.MINOR.PATCH`로 바꿔 쓰고, 파일 이름과 Release에는 진짜 버전을 쓴다. 1.0부터는 같아진다
- MSI: `MAJOR.MINOR.BUILD`가 각각 0–255/0–255/0–65535 범위면 되므로 0.x도 가능. `upgradeUuid`를 고정해 업그레이드 설치가 되게 한다
- deb: 패키지 이름은 소문자 `tabiji`
- **AppImage → 후속**: CMP 1.12.1의 `TargetFormat.AppImage`는 jpackage `app-image`(디렉터리)를 만들 뿐 단일 `.AppImage` 파일이 아니다(플러그인 소스: "cannot have a file extension"). 배포 가능한 AppImage는 `appimagetool` 후처리가 필요하므로 Phase A 이후 별도 `ci/` PR로 검토한다
- Compose Hot Reload: CMP 1.10부터 기본 포함. Kotlin/JVM 모듈 태스크는 `:desktopApp:hotRun`, 자동 리로드는 `--auto` ([Compose Hot Reload README](https://github.com/JetBrains/compose-hot-reload), [kotlinlang 문서](https://kotlinlang.org/docs/multiplatform/compose-hot-reload.html)). JetBrains Runtime이 필요하며 foojay 플러그인으로 자동 다운로드한다

## 4. GitHub Actions (SHA 고정)

각 저장소의 `releases/latest`와 `git ls-remote`로 확인 (2026-10-13).

| Action | 버전 | 커밋 SHA | 릴리스 날짜 |
| --- | --- | --- | --- |
| [actions/checkout](https://github.com/actions/checkout/releases/tag/v7.0.1) | v7.0.1 | `3d3c42e5aac5ba805825da76410c181273ba90b1` | 2026-07-20 |
| [actions/setup-java](https://github.com/actions/setup-java/releases/tag/v6.0.1) | v6.0.1 | `de7274f081f381c8f8158605e0321c36c376e2e6` | 2026-09-09 |
| [gradle/actions](https://github.com/gradle/actions/releases/tag/v6.4.0) (`setup-gradle`, `wrapper-validation`) | v6.4.0 | `3f5f9adaf7d9fecd50b5935e54106014257a94e6` | 2026-09-28 |
| [actions/upload-artifact](https://github.com/actions/upload-artifact/releases/tag/v7.0.2) | v7.0.2 | `cf430e030ddbb5b0abf93d22962f4752f3646cd9` | 2026-10-07 |
| [actions/download-artifact](https://github.com/actions/download-artifact/releases/tag/v8.0.2) | v8.0.2 | `9000827ccba6bdab643e8b6fd33ac0654aef8333` | 2026-10-07 |
| [actions/labeler](https://github.com/actions/labeler/releases/tag/v7.0.0) | v7.0.0 | `bf12e9b00b37c5c0ca2b87b79b2daf7891dbda13` | 2026-07-21 |

- **GitHub Release 생성은 서드파티 Action 대신 러너에 기본 설치된 `gh` CLI**를 쓴다 (공급망 표면 축소)
- **setup-gradle 캐시 제공자 `basic`**: v6의 기본 `enhanced` 캐시는 독점 라이브러리이며 비공개 레포에서는 "Free Preview"다. MIT 라이선스인 `basic`(`actions/cache` 래퍼)을 명시한다 ([setup-gradle 문서](https://github.com/gradle/actions/blob/v6.4.0/docs/setup-gradle.md))
- checkout v7: `pull_request_target`/`workflow_run`에서 포크 코드 체크아웃을 기본 거부한다. 우리는 그 트리거를 쓰지 않는다

### labeler 트리거: `pull_request` (not `pull_request_target`)

- `pull_request_target`은 base 브랜치 권한·시크릿으로 실행되어, 잘못 쓰면 PR 작성자가 쓰기 권한·시크릿을 얻는 "pwn request" 위험이 있다 ([labeler README](https://github.com/actions/labeler/blob/v7.0.0/README.md#notes-regarding-pull_request_target-event))
- 우리 팀은 모두 **같은 레포의 브랜치**로 PR을 연다 → `pull_request`에서도 job 단위 `pull-requests: write`가 동작한다
- 포크 PR은 토큰이 읽기 전용이라 라벨을 못 붙이므로 job 조건으로 건너뛴다 (실패로 남기지 않음)
- `issues: write`는 주지 않는다. 없는 라벨은 PR에 붙일 때 자동 생성된다(2026-10-13 PR #1에서 확인, 기본 색). 색·설명은 `docs/github.md` 8-3 명령으로 맞춘다
- 덤: `pull_request`라서 이 부트스트랩 PR에서 labeler 자체를 바로 검증할 수 있다

## 5. 에디터 (VS Code 확장 ID)

[VS Code Marketplace](https://marketplace.visualstudio.com/vscode)에서 확인.

| 확장 | ID | 버전 (2026-10-13) |
| --- | --- | --- |
| Kotlin by JetBrains | `JetBrains.kotlin-server` | 0.0.13 |
| Gradle for Java | `vscjava.vscode-gradle` | 3.18.x |
| EditorConfig | `EditorConfig.EditorConfig` | 0.18.2 |
| GitHub Actions | `GitHub.vscode-github-actions` | 0.32.3 |
| YAML | `redhat.vscode-yaml` | 1.25.x |

- Kotlin LSP는 Alpha이고 "KMP 지원은 향후 릴리스 예정" ([Kotlin/kotlin-lsp README](https://github.com/Kotlin/kotlin-lsp)). 진단 기준은 Gradle 빌드 결과다 (`client/CLAUDE.md`)

## 다시 검토할 조건

- Kotlin 호환성 가이드가 AGP 9.4 / Gradle 9.8을 포함하도록 갱신될 때
- CMP material3 안정판이 나올 때 (alpha 예외 해소)
- AGP 10 출시 때 (구조는 이미 대응됨, 버전만 확인)
