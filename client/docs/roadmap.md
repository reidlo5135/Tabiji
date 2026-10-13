# 클라이언트 학습 로드맵

Tabiji 클라이언트를 만들며 Kotlin과 Compose Multiplatform을 기초부터 익히는 순서다.

- 한 번에 한 단계. **완료 조건을 눈으로 확인한 뒤에만** 다음 단계로 넘어간다
- 단계를 시작할 때 Claude가 `docs/guides/<step>-<slug>.md` 가이드를 **하나** 쓰고, 브랜치 `client/<type>-<step>-<slug>`를 만든다
- 단계가 끝나면 커밋 1개(`feat(client): ...`, footer `Refs: <step>`) → PR → CI 통과 → 사용자가 squash merge
- Phase가 끝나면 MINOR 버전을 올려 릴리스한다 (`client-v0.N.0`)

**현재 위치 표시**: `▶ 진행 중` / `✅ 완료 (PR #n)` / 빈칸 = 아직

## Phase A — Compose 기초 (`shared` + 진입점 `androidApp`·`desktopApp`) → `client-v0.1.0`

> 모듈 구조는 KMP 공식 권장 구조다. 화면·로직은 전부 `shared`에 쓰고, 두 앱 모듈에는 진입점(`main()`, `MainActivity`)만 둔다 ([architecture.md](architecture.md)).

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| A0 | ▶ 진행 중 | Gradle 래퍼, KMP 모듈·소스셋 구조, JDK·Android SDK, VS Code, `gh` 인증 | 개발 환경, Gradle 래퍼, GitHub 원격 설정 | `scripts/linux/check-env.sh`가 모두 OK · `./gradlew --version`이 고정 버전 출력 · 부트스트랩 PR의 `client-ci`가 "래퍼 있음, 빌드 건너뜀"으로 초록 · PR 머지 완료 |
| A1 | | `main()`, `application {}`, `Window`, `@Composable`, Compose Hot Reload, 모듈 의존(`project(":shared")`) | `shared/commonMain`에 `App()`, `desktopApp/src/main`에 `main.kt`로 "Tabiji" 창. `client/.ci-enabled` 추가로 CI 빌드 활성화 | `scripts/linux/run-desktop.sh`로 창이 뜨고 제목 표시줄에 "Tabiji" · `run-desktop.sh --hot`으로 띄운 뒤 `App()` 문구를 고쳐 저장하면 **재시작 없이** 바뀜 · PR에서 `client-ci`가 실제 빌드·테스트를 돌려 초록 |
| A2 | | `ComponentActivity`, `setContent`, 매니페스트(`<activity>`, 런처 intent-filter) | `androidApp/src/main`에 `MainActivity` + 매니페스트 `<activity>` 추가. `shared`의 `App()`을 두 플랫폼이 공유 | `install-android.sh`로 에뮬레이터/실기기에 설치, Desktop과 같은 화면 · `App()` 문구를 바꾸면 두 플랫폼 모두 바뀜 |
| A3 | | composition/recomposition, `remember`, `mutableStateOf`, state hoisting | 여행 제목 입력창 + 실시간 미리보기 | 입력하는 즉시 미리보기가 바뀜 · 상태가 부모에 있고 입력·미리보기 Composable은 상태 없음(코드 리뷰로 확인) |
| A4 | | `Row`/`Column`/`Box`, `Modifier` 체인 순서, `weight` | 지도 자리(회색 박스) + 우측 패널 정적 레이아웃 | 창 크기를 바꿔도 패널 폭 고정, 지도 영역만 늘고 줆 · padding↔background 순서를 바꿔 차이를 스크린샷으로 비교 |
| A5 | | `MaterialTheme` 커스텀, CompositionLocal | `TabijiTheme` (컬러·타이포·간격 토큰) | 화면 코드에 색·dp 리터럴 0개(grep으로 확인) · 토큰 하나를 바꾸면 전체 화면 색이 바뀜 |
| A6 | | Compose Resources (문자열·폰트·이미지) | 번들 폰트, `strings.xml` 한국어 문자열, 로고 이미지 | 두 플랫폼에서 번들 폰트로 렌더링 · 화면 코드에 하드코딩 문자열 0개 |
| A7 | | `LazyColumn`, `key`, data class | 더미 방문지 카드 + 구간 칩 목록 | 30개 더미가 부드럽게 스크롤 · 항목 하나를 지워도 나머지 상태가 유지됨(`key` 효과) |
| A8 | | WindowSizeClass, 바텀시트 | 넓은 화면 = 우측 패널 / 좁은 화면 = 바텀시트 | Desktop 창을 좁히면 바텀시트로 전환 · 폰 세로에서 바텀시트, 가로/태블릿에서 패널 |
| A9 | | 버전·CHANGELOG·태그, Release CI | **릴리스: `client-v0.1.0` 태그 → Release CI 산출물 확인** | Releases에 `client-v0.1.0` prerelease · `.deb` 설치 후 실행됨 · `SHA256SUMS.txt`로 검증 통과 |

## Phase B — 앱 구조 → `client-v0.2.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| B1 | | ViewModel, `StateFlow`, sealed UI 상태, 단방향 데이터 흐름 | 플래너 화면 상태를 ViewModel로 이동 (Loading/Content/Error) | 화면 회전·창 크기 변경 후에도 입력 유지 · 임시 오류 토글로 Error 화면 표시 · ViewModel 단위 테스트 통과 |
| B2 | | Navigation (type-safe route) | 여행 목록 ↔ 플래너 화면 이동 | 목록에서 여행을 누르면 해당 플래너로, 뒤로 가기(Android 버튼·Desktop 단축키)로 복귀 |
| B3 | | DI, Koin 모듈 | ViewModel·저장소 주입 | 화면 코드에 `XxxRepository()` 직접 생성 0개 · 테스트에서 가짜 저장소로 교체 |
| B4 | | Gradle 멀티모듈, 의존 방향 | `shared`를 `core-*`, `feature-*`로 분리, CI·스크립트·`.vscode/tasks.json` 태스크 경로 갱신 | `./gradlew build` 통과 · feature가 다른 feature를 import하면 컴파일 실패 · CI 초록 |
| B5 | | `expect/actual` | 플랫폼 정보·파일 경로 같은 작은 플랫폼 API | 설정 화면에 "Android 15 / Linux" 같은 플랫폼 문자열 표시 |
| B6 | | SQLDelight, 코루틴 Dispatcher | 여행·방문지 로컬 저장 | 앱을 껐다 켜도 데이터 유지 · DB 접근이 UI 스레드 밖(로그로 확인) |
| B7 | | 제스처, 드래그 앤 드롭, 리스트 재정렬 | 방문지 드래그 순서 변경 | 드래그로 순서가 바뀌고 재시작 후에도 유지 |
| B8 | | 릴리스 반복 | **릴리스: `client-v0.2.0` 태그 → Release CI 산출물 확인** | Releases에 `client-v0.2.0` · 새 패키지로 업그레이드 설치 시 데이터 유지 |

## Phase C — 지도 → `client-v0.3.0`

> **게이트: C1 통과 전 C2 진입 금지.** C1에서 Desktop 지도가 안 되면 JCEF 대안 ADR을 먼저 쓴다 (`decisions/0002-free-map-stack.md`).

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| C1 | | 서드파티 KMP 라이브러리 평가, 스파이크 | MapLibre Compose 스파이크 (OpenFreeMap 타일) | Android·Desktop **둘 다**에서 지도 표시·마커·GeoJSON 라인·카메라 이동·클릭 좌표 로그 확인. 하나라도 실패하면 결과를 ADR로 남기고 대안 결정 |
| C2 | | 인터페이스로 라이브러리 감싸기 | `MapController` 인터페이스 + MapLibre 구현 | feature 코드에 MapLibre import 0개 · 가짜 `MapController`로 화면 미리보기 |
| C3 | | 상태 공유, 양방향 선택 동기화 | 패널 카드 ↔ 지도 마커 선택 동기화 | 카드를 누르면 마커 강조 + 카메라 이동, 마커를 누르면 카드로 스크롤 |
| C4 | | 권한, 플랫폼 위치 API (`expect/actual`) | 현재 위치 표시 | Android에서 권한 요청 후 내 위치 점 표시 · Desktop은 위치 없음 상태를 명확히 표시 |
| C5 | | 릴리스 반복 | **릴리스: `client-v0.3.0` 태그 → Release CI 산출물 확인** | 세 OS 패키지에서 지도 타일이 뜸 |

## Phase D — 네트워크 → `client-v0.4.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| D1 | | Ktor Client, kotlinx.serialization, MockEngine | `api/openapi.yaml` 기반 API 클라이언트 | MockEngine 테스트로 성공·4xx·5xx·타임아웃 매핑 검증 |
| D2 | | 인증 토큰, 보안 저장소 | 로그인 + 토큰 갱신 | 재시작 후 로그인 유지 · 토큰 만료 시 자동 갱신(로그 확인) |
| D3 | | 비동기 상태 표현 | 구간 소요시간 연동, "추정"·"재계산 필요" 표시 | 방문지 순서를 바꾸면 해당 구간이 "재계산 필요" → 서버 응답 후 실제 시간 |
| D4 | | 오프라인 우선, 충돌 해결(HTTP 409) | 로컬 우선 저장 + 동기화 | 비행기 모드에서 편집 → 온라인 복귀 시 동기화 · 409 충돌 시 선택 UI |
| D5 | | 릴리스 반복 | **릴리스: `client-v0.4.0` 태그 → Release CI 산출물 확인** | 패키지에서 실제 서버와 로그인·동기화 |

## Phase E — 인트로와 모션 → `client-v0.5.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| E1 | | `Canvas`, 프레임 애니메이션 | 인트로 파티클 | 60fps 근처로 부드럽게(프로파일러 확인), 화면 밖에선 멈춤 |
| E2 | | 레이어·패럴랙스, 포인터 입력 | 사계절 사분면 + 패럴랙스 | 포인터/기울기에 따라 레이어가 다른 속도로 이동 |
| E3 | | 애니메이션 스펙, 접근성 | 모션 토큰, "동작 줄이기" 설정 | 동작 줄이기를 켜면 모든 전환이 페이드/즉시로 바뀜 |
| E4 | | 릴리스 반복 | **릴리스: `client-v0.5.0` 태그 → Release CI 산출물 확인** | 패키지에서 인트로가 끊김 없이 재생 |

## Phase F — AI와 확장

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| F1 | | diff 표현, 바텀시트 상호작용 | AI 최적화 제안 diff 시트 | 제안 전/후 순서와 절약 시간이 보이고, 수락/거절이 동작 |
| F2 | | iOS 타깃, Xcode 연동 | iOS 앱 (Release CI의 iOS는 서명 정책 확정 후) | 시뮬레이터에서 플래너 화면 동작 |
| F3 | | Kotlin/Wasm | Web(Wasm) 파일럿 | 브라우저에서 여행 목록 표시 (지도 제외 가능) |
