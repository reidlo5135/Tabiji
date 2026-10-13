# 클라이언트 학습 로드맵

Tabiji 클라이언트를 만들며 Kotlin과 Compose Multiplatform을 기초부터 익히는 순서다.

- 한 번에 한 단계. **완료 조건을 눈으로 확인한 뒤에만** 다음 단계로 넘어간다
- 단계를 시작할 때 Claude가 `docs/guides/<step>-<slug>.md` 가이드를 **하나** 쓰고, 브랜치 `client/<type>-<step>-<slug>`를 만든다
- 단계가 끝나면 커밋(`feat(client): ...`, footer `Refs: <step>`) → PR → CI 통과 → 사용자가 squash merge
- Phase가 끝나면 MINOR 버전을 올려 릴리스한다 (`client-v0.N.0`)

**현재 위치 표시**: `▶ 진행 중` / `✅ 완료 (PR #n)` / 빈칸 = 아직

## 디자인 핸드오프와의 대응

시각 기준은 [`design/handoff/handoff-v1/`](../../design/handoff/handoff-v1/)이다. 핸드오프 `PROMPT.md`의 단계(T·P·I·L·A·S)는 구현 효율 순서라서, **학습 순서(쉬운 개념 → 어려운 개념)로 재배열**했다. 첫 화면은 사용자가 정한 대로 **인트로**부터 올린다 — 화면마다 배경 → 골격 → 컴포넌트 → 상호작용 → 모션 순서는 유지한다.

| 핸드오프 단계 | 로드맵 위치 |
| --- | --- |
| T 토큰·폰트·도형 | A5 (T1 테마) · A6 (T2 폰트) · A7 (T3 도형 일부) · B1 (T4 아이콘) |
| I 인트로 | **A3~A8 (정적 화면)** · E1~E3 (파티클·시퀀스·패럴랙스) |
| P 플래너 | B1~B2 (골격·컴포넌트·상태) · B8 (드래그) · C (지도) |
| L 여행 목록 | B3 (내비게이션과 함께) |
| A AI 시트 | F1 |
| S 공통 상태 | D3~D4 (네트워크 상태와 함께) |

> ⚠ **인트로 일러스트 4장**(`assets/intro/*.webp`)에는 제3자 캐릭터가 들어 있어 레포에 커밋하지 않는다(루트 `CLAUDE.md`). 로컬에서는 팀 보관소 사본을 같은 경로에 두고 쓰고, **앱 리소스로 커밋하는 것은 IP 없는 플레이스홀더 이미지**다 (A6 참조). 권리 처리된 교체본이 오면 파일만 바꾼다.

## Phase A — Compose 기초: 인트로 화면 완성 → `client-v0.1.0`

> 모듈 구조는 KMP 공식 권장 구조다. 화면·로직은 전부 `shared`에 쓰고, 두 앱 모듈에는 진입점(`main()`, `MainActivity`)만 둔다 ([architecture.md](architecture.md)).
> A5 전까지는 색·크기 리터럴에 `// TODO(A5): 토큰으로` 주석을 달아 두고, A5에서 전부 `TabijiTheme` 토큰으로 옮긴다.

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| A0 | ✅ 완료 (PR #1) | Gradle 래퍼, KMP 모듈·소스셋 구조, JDK·Android SDK, VS Code, `gh` 인증 | 개발 환경, Gradle 래퍼, GitHub 원격 설정 | `check-env.sh` 모두 OK · `./gradlew --version` 9.7.1 · 부트스트랩 PR의 `client-ci` 초록 · 머지 완료 |
| A1 | ▶ 진행 중 | `main()`, `application {}`, `Window`, `@Composable`, `Modifier`, Compose Hot Reload, 모듈 의존(`project(":shared")`) | `shared`에 `App()` — 하늘색 배경 + 중앙 "Tabiji" 글자. `desktopApp`에 `main.kt`. `client/.ci-enabled` 추가 | `run-desktop.sh`로 제목 "Tabiji" 창 + 하늘색 화면 · `--hot`으로 문구를 고쳐 저장하면 **재시작 없이** 바뀜 · PR에서 `client-ci`가 실제 빌드·테스트까지 초록 |
| A2 | | `ComponentActivity`, `setContent`, 매니페스트(`<activity>`, 런처 intent-filter) | `androidApp`에 `MainActivity` + 매니페스트 `<activity>`. `shared`의 `App()` 공유 | `install-android.sh`로 에뮬레이터(tabiji-api37)에 설치, Desktop과 같은 화면 · `App()` 문구를 바꾸면 두 플랫폼 모두 바뀜 |
| A3 | | `Row`/`Column`/`Box`, `weight`, `Modifier` 체인 순서 | 인트로 배경 골격 — 2×2 사분면(봄·여름·가을·겨울 임시 단색, 하드 엣지) | 창 크기를 바꿔도 정확히 4등분 유지 · padding↔background 순서를 바꾼 차이를 스크린샷으로 비교 |
| A4 | | `remember`, `mutableStateOf`, state hoisting, 클릭·호버 입력 | 사분면 선택 — 누른(올린) 사분면만 밝아지는 오버레이 | 클릭·호버한 사분면만 강조 · 선택 상태가 부모 Composable에 있고 사분면 Composable은 상태 없음(코드 리뷰) |
| A5 | | `MaterialTheme` 커스텀, `CompositionLocal` | `TabijiTheme` — `tokens.json`의 색·간격·모서리 토큰(라이트/다크 + 디버그 토글), 임시 리터럴 제거 (핸드오프 T1) | 디버그 스와치 화면에 색 토큰 32종이 이름과 함께, 토글 시 전부 전환 · 화면 코드에 색 리터럴 0개(grep) |
| A6 | | Compose Resources (이미지·폰트·문자열) | 계절 이미지 4장 fit-in-view(`ContentScale.Crop` + `BiasAlignment`), Sora·Pretendard JP 폰트, 워드마크 "Tabiji" + 부제 "여행(旅路) 도우미" (핸드오프 I1 + T2) | 창 비율을 바꿔도 왜곡·레터박스 없음, 가을·겨울 인물 안 잘림 · 旅路가 Pretendard JP로 렌더 · 화면 코드에 하드코딩 문자열 0개 · **커밋 이미지는 IP 없는 플레이스홀더**(위 ⚠) — 로컬에서 실제 일러스트로 교체해 확인 |
| A7 | | 커스텀 `Shape`, 그림자, 레이어 겹침 | 로고·메뉴 스택 — chamfer "시작하기" 버튼 + 글래스 행 "내 여행"·"설정" (핸드오프 I2, T3 일부) | 네 이미지 어느 위에서도 그림자 덕에 글자가 읽힘 · 버튼 클릭 시 로그 출력(이동은 B3) |
| A8 | | `WindowSizeClass` | Desktop 2×2 ↔ Phone 세로 4단 전환 | Desktop 창을 좁히면 세로 4단으로 전환 · 폰 세로에서 4단, `intro_phone_light.html`과 크롭 일치 |
| A9 | | 버전·CHANGELOG·태그, Release CI | **릴리스: `client-v0.1.0` 태그 → Release CI 산출물 확인** | Releases에 `client-v0.1.0` prerelease · `.deb` 설치 후 인트로가 뜸 · `SHA256SUMS.txt` 검증 통과 |

## Phase B — 앱 구조와 플래너 → `client-v0.2.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| B1 | | `LazyColumn`, `key`, data class | 플래너 정적 화면 — 지도 플레이스홀더 + 패널 골격 + 더미 방문지 카드·구간 칩 + 아이콘 22종 (핸드오프 P1~P4, T4) | 더미 5곳+4구간이 `planner_desktop_light.html`과 외곽·색 일치 · 30개 더미로 부드럽게 스크롤 · 항목 삭제에도 나머지 상태 유지(`key`) |
| B2 | | ViewModel, `StateFlow`, sealed UI 상태, 단방향 흐름 | 플래너 상태를 ViewModel로 (Loading/Content/Error), 카드↔선택 상호작용 (P6 일부) | 회전·창 크기 변경 후 상태 유지 · 오류 토글 시 Error 화면 · ViewModel 단위 테스트 통과 |
| B3 | | Navigation (type-safe route) | 여행 목록 화면(핸드오프 L) + 인트로 "시작하기"→플래너, "내 여행"→목록 연결 | 인트로 버튼으로 이동하고 뒤로 가기(버튼·단축키)로 복귀 · `trips_*_light.html`과 일치 |
| B4 | | DI, Koin 모듈 | ViewModel·저장소 주입 (`FakeTripRepository`) | 화면 코드에 저장소 직접 생성 0개 · 테스트에서 가짜 저장소 교체 |
| B5 | | Gradle 멀티모듈, 의존 방향 | `shared`를 `core-*`, `feature-*`로 분리, CI·스크립트·`.vscode/tasks.json` 태스크 경로 갱신 | `./gradlew build` 통과 · feature 간 import 시 컴파일 실패 · CI 초록 |
| B6 | | `expect/actual` | 플랫폼 정보·파일 경로 같은 작은 플랫폼 API | 설정 화면에 "Android 17 / Linux" 같은 플랫폼 문자열 |
| B7 | | SQLDelight, 코루틴 Dispatcher | 여행·방문지 로컬 저장 | 재시작 후 데이터 유지 · DB 접근이 UI 스레드 밖(로그) |
| B8 | | 제스처, 드래그 앤 드롭 | 방문지 드래그 순서 변경 (P6 나머지) | 드래그로 순서 변경, 재시작 후 유지 |
| B9 | | 릴리스 반복 | **릴리스: `client-v0.2.0`** | Releases에 `client-v0.2.0` · 업그레이드 설치 시 데이터 유지 |

## Phase C — 지도 → `client-v0.3.0`

> **게이트: C1 통과 전 C2 진입 금지.** C1에서 Desktop 지도가 안 되면 JCEF 대안 ADR을 먼저 쓴다 (`decisions/0002-free-map-stack.md`).

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| C1 | | 서드파티 KMP 라이브러리 평가, 스파이크 | MapLibre Compose 스파이크 (OpenFreeMap 타일) | Android·Desktop **둘 다**에서 지도·마커·GeoJSON 라인·카메라 이동·클릭 좌표 확인. 실패 시 ADR + 대안 결정 |
| C2 | | 인터페이스로 라이브러리 감싸기 | `MapController` + MapLibre 구현, 지도 스타일(핸드오프 30_map_style) | feature 코드에 MapLibre import 0개 · 실버 톤 지도 위 `planner_desktop_light.html` 분위기 재현 |
| C3 | | 상태 공유, 양방향 선택 동기화 | 패널 카드 ↔ 지도 마커 선택 동기화, 카메라 600ms 이동 | 카드 탭 → 마커 강조 + 카메라, 마커 탭 → 카드 스크롤 |
| C4 | | 권한, 플랫폼 위치 API | 현재 위치 표시 | Android 권한 요청 후 내 위치 점 · Desktop은 위치 없음 상태 명시 |
| C5 | | 릴리스 반복 | **릴리스: `client-v0.3.0`** | 세 OS 패키지에서 지도 타일이 뜸 |

## Phase D — 네트워크 → `client-v0.4.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| D1 | | Ktor Client, kotlinx.serialization, MockEngine | `api/openapi.yaml` 기반 API 클라이언트 | MockEngine 테스트로 성공·4xx·5xx·타임아웃 매핑 검증 |
| D2 | | 인증 토큰, 보안 저장소 | 로그인 + 토큰 갱신 | 재시작 후 로그인 유지 · 만료 시 자동 갱신(로그) |
| D3 | | 비동기 상태 표현 | 구간 소요시간 연동 + 공통 상태 — 스켈레톤·"추정"·"재계산 필요" (핸드오프 S1~S2) | 순서를 바꾸면 해당 구간 "재계산 필요" → 응답 후 실제 시간 · `states_*_light.html`과 일치 |
| D4 | | 오프라인 우선, 충돌 해결(HTTP 409) | 로컬 우선 저장 + 동기화 + 오프라인 필 (S3~S4) | 비행기 모드 편집 → 복귀 시 동기화 · 409 선택 UI |
| D5 | | 릴리스 반복 | **릴리스: `client-v0.4.0`** | 패키지에서 실제 서버와 로그인·동기화 |

## Phase E — 인트로 모션 → `client-v0.5.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| E1 | | `Canvas`, `withFrameNanos`, 프레임 루프 | 계절 파티클 오버레이 — 벚꽃 8·반짝임 6·낙엽 8·눈 12, seed 고정 (핸드오프 I3) | 지정 속도·방향으로 루프(10_intro 표) · 60fps 근처(프로파일러) · 화면 밖에선 멈춤 |
| E2 | | 시퀀스 애니메이션, 포인터 입력 | 0–1.2s 입장 시퀀스 + 호버 밝기 ± + 패럴랙스 2단 (I4) | 타임라인(0/150/300/450/600/1200ms) 일치 · 호버 사분면 +8%/나머지 −6% · 이미지 ±4dp, 파티클 ±8dp |
| E3 | | 모션 토큰, 접근성 | `motion` 토큰(20_motion) 적용 + "동작 줄이기" | 동작 줄이기 켜면 전환 0ms·파티클 정지 프레임 |
| E4 | | 릴리스 반복 | **릴리스: `client-v0.5.0`** | 패키지에서 인트로가 끊김 없이 재생 |

## Phase F — AI와 확장

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| F1 | | diff 표현, 모달·시트 | AI 제안 시트 (핸드오프 A1~A3) | 체크 해제 시 절약 시간 재계산 · 수락 시 리스트 240ms 재배치 · `ai_sheet_*_light.html` 일치 |
| F2 | | iOS 타깃, Xcode 연동 | iOS 앱 (Release CI의 iOS는 서명 정책 확정 후) | 시뮬레이터에서 인트로·플래너 동작 |
| F3 | | Kotlin/Wasm | Web(Wasm) 파일럿 | 브라우저에서 여행 목록 표시 (지도 제외 가능) |
