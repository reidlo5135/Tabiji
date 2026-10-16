# 클라이언트 아키텍처

## 한 줄 요약

**진입점(app) → 기능(feature) → 핵심(core)**, 의존은 이 한 방향으로만 흐른다. 외부와의 연결은 **우리 서버 REST**와 **지도 타일** 두 갈래뿐이다.

## 외부 연결

```
                 ┌──────────────────────────┐
                 │   Tabiji 클라이언트       │
                 └──────┬─────────────┬─────┘
       REST (api/openapi.yaml)        │ 벡터 타일 (HTTPS)
                        ▼             ▼
               ┌───────────────┐  ┌────────────────┐
               │  Tabiji 서버   │  │  OpenFreeMap   │
               └───┬───────┬───┘  └────────────────┘
       경로·교통 API     AI (ai/)
```

- 경로·교통·AI 결과는 **전부 서버를 거쳐서** 받는다. 클라이언트에 외부 API 키를 두지 않는다
- 지도 타일은 OpenFreeMap(OSM 기반, 키 없음)을 직접 받는다. 근거: [`decisions/0002-free-map-stack.md`](decisions/0002-free-map-stack.md)

## 빌드 경계

- **`client/`는 독립 Gradle 루트다.** 레포 루트에는 Gradle 파일이 없고, `server/`와 하나의 빌드로 묶지 않는다 (루트 빌드·루트 `includeBuild`·일괄 스크립트 금지)
- 모든 클라이언트 명령은 `client/`에서 실행한다

## 지금의 모듈 구조 (Phase A~B7)

Kotlin Multiplatform **공식 권장 구조**를 따른다: 앱 진입점마다 별도 모듈, 공유 코드는 하나의 모듈. AGP 9부터는 Android 앱 진입점을 공유 모듈에서 분리하는 것이 **필수**다. 근거: [`docs/decisions/0002-toolchain-versions.md`](../../docs/decisions/0002-toolchain-versions.md)

```
client/
├── androidApp/   com.android.application (+ Compose 컴파일러)
│                 MainActivity(A2), AndroidManifest.xml, 앱 버전·서명
├── desktopApp/   org.jetbrains.kotlin.jvm + Compose Multiplatform
│                 main.kt(A1), 실행·Hot Reload·설치 패키지(nativeDistributions)
└── shared/       org.jetbrains.kotlin.multiplatform + com.android.kotlin.multiplatform.library
                  모든 화면·로직. 타깃: android {}, jvm()

androidApp ──┐
             ├──► shared
desktopApp ──┘
```

| 모듈 | 플러그인 | 둘 것 | 두지 않을 것 |
| --- | --- | --- | --- |
| `shared` | KMP, Android-KMP 라이브러리, CMP, Compose 컴파일러 | 화면(Composable), 상태, 도메인, 리소스(`composeResources/`), `expect/actual` | `main()`, `Activity`, 앱 버전·서명 |
| `androidApp` | Android 앱, Compose 컴파일러 (AGP 내장 Kotlin, `kotlin-android` 미적용) | `MainActivity`, 매니페스트, `versionName/Code`, 서명 | 화면 코드 |
| `desktopApp` | Kotlin JVM, CMP, Compose 컴파일러 | `main()`, 창 설정, 패키징 | 화면 코드 |

CMake로 빗대면 `shared`는 `add_library(tabiji_shared)`, 두 앱은 `add_executable` + `target_link_libraries(... tabiji_shared)`다.

### `shared` 소스셋 (KMP)

| 소스셋 | 무엇을 두나 | CMake 비유 |
| --- | --- | --- |
| `commonMain` | 공용 UI·로직. 대부분의 코드 | 모든 타깃이 공유하는 소스 |
| `androidMain` | Android 전용 `actual` (예: 플랫폼 이름, 위치) | `if(ANDROID)` 블록 |
| `jvmMain` | Desktop(JVM) 전용 `actual` | `if(NOT ANDROID)` 블록 |
| `commonTest` | 공용 단위 테스트 (CI에서 JVM으로 실행: `:shared:jvmTest`) | `add_test` |

진입점 코드(`main.kt`, `MainActivity.kt`)는 소스셋이 아니라 각 앱 모듈의 `src/main/kotlin`에 둔다.

### 패키지 (분리 전에도 최종 구조를 따른다)

`shared` 안에서도 패키지는 최종 모듈 구조를 따른다. 그래야 B8에서 폴더를 옮기기만 하면 된다.

```
com.yamada.studio.tabiji
├── app/              # App() 루트 Composable, (나중) Navigation
├── feature/
│   ├── intro/
│   ├── planner/
│   ├── trips/
│   └── auth/
└── core/
    ├── ui/           # TabijiTheme (A5~)
    ├── map/
    ├── data/
    ├── network/
    └── domain/
```

## 최종 모듈 구조 (B8 이후)

B8에서 `shared`를 기능·핵심 모듈로 나눈다. 진입점 모듈(`androidApp`, `desktopApp`)은 그대로다.

```
androidApp ─┐                         ┌─► core-ui       (TabijiTheme, 공용 컴포넌트)
desktopApp ─┼─► feature-intro    ─────┼─► core-map      (MapController 인터페이스 + MapLibre 구현)
            ├─► feature-planner  ─────┼─► core-data     (저장소, SQLDelight, 동기화)
            ├─► feature-trips    ─────┼─► core-network  (Ktor 클라이언트, DTO)
            └─► feature-auth     ─────┴─► core-domain   (모델·규칙, 순수 Kotlin)
```

| 모듈 | 책임 | 알아도 되는 것 | 몰라야 하는 것 |
| --- | --- | --- | --- |
| `androidApp` / `desktopApp` | 플랫폼 진입점, DI 조립, 창·Activity | 모든 feature, core | — |
| `feature-*` | 화면 하나의 UI + ViewModel | core-* | **다른 feature** (화면 간 이동은 app의 Navigation이 연결) |
| `core-ui` | 테마 토큰, 공용 Composable | core-domain | 네트워크·DB |
| `core-map` | `MapController` 인터페이스와 구현 | core-domain | feature, 네트워크 |
| `core-data` | 저장소(Repository), 로컬 DB, 동기화 | core-network, core-domain | Compose, feature |
| `core-network` | HTTP 클라이언트, DTO, 오류 매핑 | core-domain | Compose, DB |
| `core-domain` | 여행·방문지·구간 모델, 규칙 | **Kotlin 표준 라이브러리와 코루틴뿐** | Compose, 플랫폼 API, 직렬화 라이브러리 세부 |

## 화면의 데이터 흐름 (B5 이후)

```
 사용자 입력 ──event──► ViewModel ──► Repository ──► (DB / 서버)
      ▲                    │
      └── StateFlow<UiState> ┘   (UI는 상태를 그리기만 한다)
```

- 화면 상태는 ViewModel의 `StateFlow<XxxUiState>` **하나**로 노출한다
- UI 상태는 sealed 계층: `Loading` / `Content(...)` / `Error(...)`
- Qt로 빗대면: ViewModel이 모델, Composable이 QML 뷰, `StateFlow` 구독이 프로퍼티 바인딩, 이벤트 함수 호출이 시그널 → 슬롯

## 서버와의 계약 공유 (Phase D에서 ADR로 결정)

- **기본안**: `api/openapi.yaml`에서 클라이언트 DTO를 **코드 생성**한다. 계약의 원본은 OpenAPI 하나
- **비교안** (서버가 Kotlin일 때): 서버 쪽 계약 모듈(DTO·검증 규칙)만 `client/settings.gradle.kts`에서 `includeBuild`로 참조한다
- **어느 쪽이든 client와 server를 하나의 빌드로 묶지 않는다.** 레포 루트 빌드나 루트 `includeBuild`는 만들지 않는다
- 결정 전까지는 `api/openapi.yaml`이 유일한 계약이다
