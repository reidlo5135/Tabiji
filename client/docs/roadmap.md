# 클라이언트 학습 로드맵

Tabiji 클라이언트를 만들며 Kotlin과 Compose Multiplatform을 기초부터 익히는 순서다.

- 한 번에 한 단계. **완료 조건을 눈으로 확인한 뒤에만** 다음 단계로 넘어간다
- 단계를 시작할 때 Claude가 `docs/guides/<step>-<slug>.md` 가이드를 **하나** 쓰고, 브랜치 `client/<type>-<step>-<slug>`를 만든다
- 단계가 끝나면 커밋(`feat(client): ...`, footer `Refs: <step>`) → PR → CI 통과 → 사용자가 squash merge
- Phase가 끝나면 MINOR 버전을 올려 릴리스한다 (`client-v0.N.0`)

**현재 위치 표시**: `▶ 진행 중` / `✅ 완료 (PR #n)` / `⏭ 이동` / 빈칸 = 아직

## 데스크톱 우선 (2026-10-14 사용자 결정)

- Phase A~F의 완료 조건은 **Desktop에서** 눈으로 확인한다. Android·iOS 실행 확인은 [Phase M](#phase-m--모바일-android--ios)으로 모았다
- 화면·로직은 그대로 `shared/commonMain`에 쓴다. 플랫폼만 미룰 뿐 코드 구조는 멀티플랫폼이다. Android 컴파일은 `build.sh`와 `client-ci`가 계속 검증하므로, Android에서만 깨지는 코드는 그때그때 드러난다
- 계기: IntelliJ IDEA 2026.2.3의 Android 플러그인이 AGP 9.1.0까지만 지원해 Gradle 동기화가 실패했다 (프로젝트는 AGP 9.3.3, ADR 0002). Android 작업은 IDE 문제를 정리한 뒤 Phase M에서 한다

## 디자인 핸드오프와의 대응

시각 기준은 [`design/handoff/handoff-v2/`](../../design/handoff/handoff-v2/)다 (2026-10-14부터). v2는 v1의 글래스·실버 무드를 버리고 **"도감 같은 여행 수첩"** 으로 바꿨다. 2dp 잉크 테두리, 블러 없는 하드 섀도, 색 머리띠, Jua 디스플레이 폰트를 쓰고 **사용자 선택 테마 8종 × 색 역할** 구조를 갖는다. 다크 모드는 v2 범위 밖이라 라이트로 고정한다. v1([`handoff-v1/`](../../design/handoff/handoff-v1/))은 v2가 "v1과 동일"이라고 밝힌 부분(인트로 이미지 fit 규칙, 旅 아이콘 세트, 지도 생성 도구)만 참고한다.

수치의 정답 순서는 `tokens/tokens.json` → `source/*.html`(1px = 1dp) → `images/screens/`다. 보드 CSS 색과 `tokens.json`이 다르면 **`tokens.json`이 정답**이다 (`docs/01_tokens.md`의 조정 7건).

핸드오프 `PROMPT.md`의 단계(T·P·I·L·A·S)는 구현 효율 순서라서, **학습 순서(쉬운 개념 → 어려운 개념)로 재배열**했다. 첫 화면은 사용자가 정한 대로 **인트로**부터 올린다. 화면마다 배경 → 골격 → 컴포넌트 → 상호작용 → 모션 순서는 유지한다.

| 핸드오프 v2 단계 | 로드맵 위치 |
| --- | --- |
| T1 테마×역할 색 (8종) | A5 |
| T2 폰트·타이포 (Jua 폴백 체인) | A6 |
| T3 도감 표면 (`inkPanel` · `pressSink` · `chamferShape` · `paperStripes`) | A8 (하드 섀도) · A9 (chamfer·눌림) · B1 (`inkPanel`·`paperStripes`) |
| T4 UI 아이콘 22종 | B1 (인트로 메뉴 버튼 아이콘 포함) |
| T5 앱 아이콘·파비콘 (`04_icons_favicon`) | A6 (Desktop 창 아이콘, Compose Resources로) · A10 (설치 패키지 아이콘) · M1 (Android adaptive) · F3 (favicon·manifest) |
| I 인트로 | **A3~A8 (정적 화면)** · E1~E3 (파티클·시퀀스·패럴랙스) |
| P 플래너 | B1~B2 (배경·골격·컴포넌트·상태) · B8 (드래그) · C2~C3 (지도·마커·경로) |
| L 여행 목록 | B3 (내비게이션과 함께) |
| A AI 시트 | F1 |
| S 공통 상태 | D3~D4 (네트워크 상태와 함께) |
| 테마 선택 UI (PROMPT §8-3, 미설계) | B6 (설정 화면) |

> ⚠ **인트로 일러스트 4장**(`assets/intro/*.webp`, v1·v2 같은 파일)에는 제3자 캐릭터가 들어 있어 레포에 커밋하지 않는다(루트 `CLAUDE.md`). 로컬에서는 팀 보관소 사본을 같은 경로에 두고 쓰고, **앱 리소스로 커밋하는 것은 IP 없는 플레이스홀더 이미지**다 (A6 참조). 권리 처리된 교체본이 오면 파일만 바꾼다.

## Phase A — Compose 기초: 인트로 화면 완성 → `client-v0.1.0`

> 모듈 구조는 KMP 공식 권장 구조다. 화면·로직은 전부 `shared`에 쓰고, 두 앱 모듈에는 진입점(`main()`, `MainActivity`)만 둔다 ([architecture.md](architecture.md)).
> 2026-10-15: 잉크 마감을 A8(그리기)·A9(버튼·눌림)으로 나누고 릴리스를 A10으로 옮겼다.
> 2026-10-14: 반응형(원래 A8)을 잉크 마감(원래 A7)보다 앞으로 옮겼다. 버튼 높이·스택 폭이 크기 구간에 따라 달라서 구간을 먼저 만든다.
> A5 전까지는 색·크기 리터럴에 `// TODO(A5): 토큰으로` 주석을 달아 두고, A5에서 전부 `TabijiTheme` 토큰으로 옮긴다.

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| A0 | ✅ 완료 (PR #1) | Gradle 래퍼, KMP 모듈·소스셋 구조, JDK·Android SDK, VS Code, `gh` 인증 | 개발 환경, Gradle 래퍼, GitHub 원격 설정 | `check-env.sh` 모두 OK · `./gradlew --version` 9.7.1 · 부트스트랩 PR의 `client-ci` 초록 · 머지 완료 |
| A1 | ✅ 완료 (PR #6) | `main()`, `application {}`, `Window`, `@Composable`, `Modifier`, Compose Hot Reload, 모듈 의존(`project(":shared")`) | `shared`에 `App()`: 하늘색 배경 + 중앙 "Tabiji" 글자 (v1 색. v2에는 하늘색 배경이 없고 A3에서 사분면으로 바뀐다). `desktopApp`에 `main.kt`. `client/.ci-enabled` 추가 | `run-desktop.sh`로 제목 "Tabiji" 창 + 하늘색 화면 · `--hot`으로 문구를 고쳐 저장하면 **재시작 없이** 바뀜 · PR에서 `client-ci`가 실제 빌드·테스트까지 초록 |
| A2 | ⏭ M1로 이동 | — | Android 진입점 + 旅 런처 아이콘. 데스크톱 우선 결정으로 Phase M으로 옮겼다 (가이드는 `guides/M1-android-entry.md`로 보존) | — |
| A3 | ✅ 완료 (PR #8) | `Row`/`Column`/`Box`, `weight`, `Arrangement.spacedBy`, `Modifier` 체인 순서 | 인트로 배경 골격: 2×2 사분면(봄·여름·가을·겨울 임시 단색). **셀 사이 3dp 잉크 경계**는 루트 배경을 ink로 깔고 간격 3dp로 만든다 (`10_intro`) | 창 크기를 바꿔도 정확히 4등분 + 경계 3dp 균일 · padding↔background 순서를 바꾼 차이를 스크린샷으로 비교 |
| A4 | ✅ 완료 (PR #9) | `enum class`, nullable 타입, `when`, `remember`, `mutableStateOf`, `by` 위임, state hoisting, `clickable` | 사분면 선택: 클릭한 칸 +8% 밝게·나머지 −6%, 다시 클릭하면 해제 (호버와 120ms 애니메이션은 E2) | 클릭한 칸만 강조, 해제·이동 동작 · 창 크기 변경 후에도 선택 유지 · 선택 상태가 부모 Composable에 하나만 있고 사분면 Composable은 상태 없음(코드 리뷰) · `remember`·`mutableStateOf`를 하나씩 뺀 실험 설명 |
| A5 | ✅ 완료 (PR #10) | `data class`, `@Immutable`, enum 생성자, `object`·커스텀 getter, `CompositionLocal`(`staticCompositionLocalOf`, React Context 대응), `animateColorAsState`, Desktop `MenuBar` | `core/ui/theme/`: `TabijiColors`(역할 15개 + `headers`) × `enum TabijiPalette` 8종, `LocalTabijiColors`, 공급 함수 `TabijiTheme(palette)` + 읽기 `TabijiTheme.colors`, 240ms 크로스페이드. 값은 **`tokens.json`의 `themes.*`** (핸드오프 T1). Desktop 메뉴바로 테마 전환·토큰 화면 토글(디버그). 화면 코드의 색 리터럴 제거. **치수 토큰(테두리·모서리·간격)은 A8로** | 메뉴로 8테마 전환 시 240ms 크로스페이드, 레이아웃 불변 · 토큰 화면의 역할·hex가 tokens.json과 일치(korea만 h4) · `Color(0x` grep이 `TabijiPalette.kt`에만 · `theme_tokens.png`와 색감 대조 |
| A6 | ✅ 완료 (PR #11) | Compose Resources(이미지·폰트·문자열), `FontFamily` 폴백 체인, `TextStyle` | 계절 이미지 4장 fit-in-view(`ContentScale.Crop` + `BiasAlignment`), 폰트 Jua·Pretendard JP·Sora와 타이포 9스타일, 워드마크 "Tabiji" + 부제 "여행(旅路) 도우미"(마감 없이 글자만), Desktop 창 아이콘 `Window(icon = …)`, Desktop 기본 창 크기(화면의 가로 85% × 세로 75%, 가운데) (핸드오프 I1 + T2 + T5 일부) | 창 비율을 바꿔도 왜곡·레터박스 없음, 가을·겨울 인물 안 잘림 · 디버그 샘플 "Day 2 · 시즈오카 센겐 신사 · 駿府城 · 旅路 1.8km"에서 Day 2는 Jua, 한자는 Pretendard JP 폴백(두부 □ 없음), 숫자는 Sora · 화면 코드에 하드코딩 문자열 0개 · Desktop 작업표시줄에 旅 · **커밋 이미지는 IP 없는 플레이스홀더**(위 ⚠). 로컬에서 실제 일러스트로 교체해 확인 |
| A7 | ✅ 완료 (PR #12) | 반응형: `BoxWithConstraints`(CSS 미디어 쿼리 대응), 크기 구간 enum(Material 기준 Compact <600dp · Medium · Expanded ≥840dp), 구간을 화면에 내려주기(파라미터 또는 CompositionLocal), Desktop 최소 창 크기 | Desktop 2×2 ↔ Phone 세로 4단 전환(칸 높이 ≈211dp, 가을·겨울 bias +0.16f), 가운데 스택 폭 360↔310 (`10_intro.md`) | Desktop 창을 좁히면(600dp 미만) 세로 4단으로 전환 · 창을 390×844로 맞췄을 때 `source/intro_phone.html`·`intro_phone.png`와 크롭 일치 · 넓히면 다시 2×2 · 실기기 확인은 M2 |
| A8 | ✅ 완료 (PR #13) | 상수 `object`(토큰이 모두 CompositionLocal일 필요는 없다), 커스텀 Modifier 확장 함수, `drawBehind`·`Shape.createOutline`·`translate`, `DrawStyle`(`Stroke`) | 잉크 마감 ① 그리기: 치수 토큰 `TabijiDimens`(테두리·모서리·섀도·간격, `tokens.json`) + `Modifier.inkShadow`(블러 0 하드 섀도) + `OutlinedText`(흰 글자 + 잉크 아웃라인 + (3,3) 하드 섀도)를 워드마크·부제에 적용 (핸드오프 I2 일부, T3 일부) | 네 계절 이미지 어디 위에서도 워드마크·부제가 또렷함(실제 일러스트로 로컬 확인) · 토큰 화면 색 상자에 (3,3) 하드 섀도 · `elevation`·`Modifier.shadow` 0건 · 화면 코드에 이름 없는 `.dp` 숫자 0개(토큰 또는 이름 붙은 상수) |
| A9 | ✅ 완료 (PR 대기) | 커스텀 `Shape`(`Path`·`arcTo`), `InteractionSource`·`collectIsPressedAsState`, `animateDpAsState`, 재사용 컴포넌트 설계 | 잉크 마감 ② 버튼: `ChamferShape`(우상단 8dp 사선) + 메뉴 버튼 3개(시작하기 `accent`·chamfer / 내 여행·설정 `card`), 하단 (0,4) 잉크 섀도, **눌림 = 가라앉기**(2dp, 80ms), Desktop 58/52 · Phone 56/50 높이, 스택 gap 24/20 (핸드오프 I2, T3 나머지). 버튼 아이콘 자리만 두고 아이콘은 B1(T4) | 버튼을 누르면 2dp 가라앉고 섀도가 줄어듦(스프링·스케일 없음) · chamfer가 `intro_desktop.png`와 같은 모양 · 버튼 클릭 시 로그(이동은 B3) · Phone 프리셋에서 버튼 높이·스택 폭 일치 |
| A10 | ▶ 다음 | 버전·CHANGELOG·태그, Release CI | **릴리스: `client-v0.1.0` 태그 → Release CI 산출물 확인** (설치 패키지 아이콘 포함) | Releases에 `client-v0.1.0` prerelease · `.deb` 설치 후 인트로가 뜸 · `SHA256SUMS.txt` 검증 통과 |

## Phase B — 앱 구조와 플래너 → `client-v0.2.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| B1 | | `LazyColumn`, `key`, data class, `drawBehind` 반복 그리기 | 플래너 정적 화면: **종이+45° 스트라이프 배경**(`paperStripes`), 잉크 프레임 지도 자리(`map_light.svg` Crop), `inkPanel` 일정 패널 + `DayHeader` + 머리띠 로테이션 `VisitCard` + `SegmentChip` + 아이콘 22종 (핸드오프 P1~P4, T3 나머지, T4) | 더미 4곳+3구간이 `planner_desktop.png`·`source/planner_desktop.html`과 외곽·색 일치 (머리띠 h1→h2→accent→h3, '추정' 배지, dashed 칩, 고정 시계) · 8테마 전환 시 색만 바뀜 · 30개 더미로 부드럽게 스크롤 · 항목 삭제에도 나머지 상태 유지(`key`) |
| B2 | | ViewModel, `StateFlow`, sealed UI 상태, 단방향 흐름 | 플래너 상태를 ViewModel로 (Loading/Content/Error), 카드↔선택 상호작용 (P6 일부) | 창 크기 변경 후 상태 유지(회전은 M2) · 오류 토글 시 Error 화면 · ViewModel 단위 테스트 통과 |
| B3 | | Navigation (type-safe route) | 여행 목록 화면(핸드오프 L: 머리띠 40 카드·D-day 배지·dashed 새 여행 타일) + 인트로 "시작하기"→플래너, "내 여행"→목록 연결 | 인트로 버튼으로 이동하고 뒤로 가기(버튼·단축키)로 복귀 · `trips_*.png`와 일치, 인접 카드 머리띠 색 중복 없음 |
| B4 | | DI, Koin 모듈 | ViewModel·저장소 주입 (`FakeTripRepository`) | 화면 코드에 저장소 직접 생성 0개 · 테스트에서 가짜 저장소 교체 |
| B5 | | Gradle 멀티모듈, 의존 방향 | `shared`를 `core-*`, `feature-*`로 분리, CI·스크립트·`.vscode/tasks.json` 태스크 경로 갱신 | `./gradlew build` 통과 · feature 간 import 시 컴파일 실패 · CI 초록 |
| B6 | | `expect/actual` | 설정 화면: 플랫폼 정보 같은 작은 플랫폼 API + **테마 선택**(A5 디버그 토글 대체. 피커 디자인은 미설계라 디자인 요청 먼저) | 설정 화면에 "Android 17 / Linux" 같은 플랫폼 문자열 · 테마를 고르면 앱 전체 색이 바뀜 |
| B7 | | SQLDelight, 코루틴 Dispatcher | 여행·방문지 로컬 저장 + 선택 테마 저장 | 재시작 후 데이터·테마 유지 · DB 접근이 UI 스레드 밖(로그) |
| B8 | | 제스처, 드래그 앤 드롭 | 방문지 드래그 순서 변경 (P6 나머지) | 드래그로 순서 변경, 재시작 후 유지 |
| B9 | | 릴리스 반복 | **릴리스: `client-v0.2.0`** | Releases에 `client-v0.2.0` · 업그레이드 설치 시 데이터 유지 |

## Phase C — 지도 → `client-v0.3.0`

> **게이트: C1 통과 전 C2 진입 금지.** C1에서 Desktop 지도가 안 되면 JCEF 대안 ADR을 먼저 쓴다 (`decisions/0002-free-map-stack.md`).

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| C1 | | 서드파티 KMP 라이브러리 평가, 스파이크 | MapLibre Compose 스파이크 (OpenFreeMap 타일) | **Desktop**에서 지도·마커·GeoJSON 라인·카메라 이동·클릭 좌표 확인. 실패 시 ADR + 대안 결정. Android 확인은 M2 (MapLibre Android는 성숙해서 위험이 Desktop 쪽에 몰려 있다) |
| C2 | | 인터페이스로 라이브러리 감싸기 | `MapController` + MapLibre 구현, 지도 스타일(핸드오프 `30_map_style`: 테마 불변 중립 라이트 타일), 경로선(도보 accent 점선·대중교통 h2·자동차 h3) | feature 코드에 MapLibre import 0개 · 잉크 프레임 안 중립 타일이 `planner_desktop.png` 분위기 · 테마를 바꿔도 타일은 그대로, 경로선만 바뀜 |
| C3 | | 상태 공유, 양방향 선택 동기화 | 물방울 마커(로테이션 채움 + 2.5dp ink 외곽 + ink 번호 Jua, P5) + 패널 카드 ↔ 마커 선택 동기화, 카메라 600ms 이동 | 카드 탭 → 마커 확대(44×54) + 카메라, 마커 탭 → 카드 스크롤 · 경로색이 칩 커넥터와 일치 |
| C4 | | `expect/actual`로 플랫폼 기능 감추기 | 현재 위치 인터페이스 + Desktop 구현(위치 없음 상태 명시) | Desktop에서 '위치 없음' 상태가 UI에 표시 · Android 권한·실제 위치는 M2 |
| C5 | | 릴리스 반복 | **릴리스: `client-v0.3.0`** | 세 OS 패키지에서 지도 타일이 뜸 |

## Phase D — 네트워크 → `client-v0.4.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| D1 | | Ktor Client, kotlinx.serialization, MockEngine | `api/openapi.yaml` 기반 API 클라이언트 | MockEngine 테스트로 성공·4xx·5xx·타임아웃 매핑 검증 |
| D2 | | 인증 토큰, 보안 저장소 | 로그인 + 토큰 갱신 | 재시작 후 로그인 유지 · 만료 시 자동 갱신(로그) |
| D3 | | 비동기 상태 표현 | 구간 소요시간 연동 + 공통 상태: 스켈레톤(잉크 테두리 유지, 펄스만)·"추정"·"재계산 필요"·오류 배너 (핸드오프 S) | 순서를 바꾸면 해당 구간 "재계산 필요" → 응답 후 실제 시간 · `states_*.png`와 일치 · 스켈레톤에 시머 없음 |
| D4 | | 오프라인 우선, 충돌 해결(HTTP 409) | 로컬 우선 저장 + 동기화 + 오프라인 필(지도 saturate 55%) (핸드오프 S) | 비행기 모드 편집 → 복귀 시 동기화 · 409 선택 UI |
| D5 | | 릴리스 반복 | **릴리스: `client-v0.4.0`** | 패키지에서 실제 서버와 로그인·동기화 |

## Phase E — 인트로 모션 → `client-v0.5.0`

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| E1 | | `Canvas`, `withFrameNanos`, 프레임 루프 | 계절 파티클 오버레이: 벚꽃 8·반짝임 6·낙엽 8·눈 12, seed 고정 (핸드오프 I3) | 지정 속도·방향으로 루프(`10_intro` 표) · 60fps 근처(프로파일러) · 화면 밖에선 멈춤 |
| E2 | | 시퀀스 애니메이션, 포인터 입력 | 0–1.2s 입장 시퀀스 + 호버 밝기 ± + 패럴랙스 2단 (I3) | 타임라인(사분면 stagger 150 → 스택 상승 600~1200ms) 일치 · 호버 사분면 +8%/나머지 −6% · 이미지 ±4dp, 파티클 ±8dp |
| E3 | | 모션 토큰, 접근성 | `motion` 토큰(`20_motion`: micro 120 · press 80 · transition 240 · themeSwitch 240 · camera 600) 적용 + "동작 줄이기" | 동작 줄이기 켜면 전환 0ms·파티클 정지 프레임, press의 가라앉기는 유지 · 스프링·스케일 팝 0건 |
| E4 | | 릴리스 반복 | **릴리스: `client-v0.5.0`** | 패키지에서 인트로가 끊김 없이 재생 |

## Phase F — AI와 확장

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| F1 | | diff 표현, 모달·시트 | AI 제안 시트 (핸드오프 A: Desktop 780 다이얼로그 + (5,5) 모달 섀도 / Phone 바텀시트) | 체크 해제 시 절약 배지 숫자 갱신 · 수락 시 240ms 닫힘 + 리스트 재정렬 · `ai_sheet_*.png` 일치 |
| F2 | ⏭ M3로 이동 | — | iOS 앱 | — |
| F3 | | Kotlin/Wasm | Web(Wasm) 파일럿 + favicon·manifest (T5) | 브라우저에서 여행 목록 표시 (지도 제외 가능) · 탭에 旅 favicon |

## Phase M — 모바일 (Android → iOS)

> **시점은 사용자가 정한다.** 기본 제안은 Phase B 이후다 (인트로·플래너·목록이 얼추 있을 때). 시작 전에 IDE 문제(IntelliJ의 AGP 지원 범위)부터 정리한다: Android Studio로 옮기거나 IntelliJ 플러그인 갱신을 확인한다. Phase M을 마치면 그 시점의 다음 MINOR로 릴리스한다.

| 단계 | 현재 위치 | 배우는 개념 | 만드는 것 | 완료 조건 (눈으로 확인) |
| --- | --- | --- | --- | --- |
| M1 | | 클래스·상속·`override`, `ComponentActivity`, `setContent`, 매니페스트(`<activity>`, 런처 intent-filter), Android 리소스(VectorDrawable, adaptive icon), 에뮬레이터 | `androidApp`에 `MainActivity` + 매니페스트 `<activity>` + 旅 글리프 adaptive 런처 아이콘(전경·흰 배경·monochrome). 가이드 초안: [`guides/M1-android-entry.md`](guides/M1-android-entry.md) | 에뮬레이터(tabiji-api37)에 설치, Desktop과 같은 화면 · `App()` 문구를 바꾸면 두 플랫폼 모두 바뀜 · 런처(원형 마스크)와 13+ 테마 아이콘에 旅 |
| M2 | | 창 insets(edge-to-edge), 회전·구성 변경, Android 권한 | 그때까지 만든 화면의 Android 점검: 시스템 바 insets, Phone 레이아웃 실기기 확인(A8), 회전 후 상태 유지(B2), 지도(C1~C3), 위치 권한·현재 위치(C4) | 에뮬레이터에서 각 단계 완료 조건 중 "M2"로 미룬 항목 전부 통과 |
| M3 | | iOS 타깃, Xcode 연동 | iOS 앱 (Release CI의 iOS는 서명 정책 확정 후) | 시뮬레이터에서 인트로·플래너 동작 |
| M4 | | 릴리스 반복 | **릴리스: 그 시점의 다음 MINOR** (APK 포함) | Releases에 APK · 에뮬레이터 설치 후 실행 |
