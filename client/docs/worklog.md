# 클라이언트 워크로그

세션을 시작할 때 Claude가 이 파일을 먼저 읽고, 작업 덩어리가 끝날 때마다 갱신한다. 여러 머신에서 이어 가기 위한 메모다.
최신 항목이 위로 온다.

## 검증 상태

마지막으로 **사용자가 직접 실행해 확인한** 결과만 적는다.

| 항목 | 상태 | 확인 날짜 | 메모 |
| --- | --- | --- | --- |
| `check-env.sh` | ✅ 모두 OK | 2026-10-13 | JDK 21 · SDK `~/Android/Sdk` · adb · gh 2.102 |
| `./gradlew --version` | ✅ 9.7.1 | 2026-10-13 | 래퍼 커밋 완료 (PR #1) |
| 에뮬레이터 | ⬜ 부팅 시도 | 2026-10-14 | `default_boot` 스냅샷 로드 실패 → 콜드 부트로 넘어감(정상 동작). `adb devices` 미확인. Phase M까지 보류 |
| Desktop 실행 | ✅ 인트로 2×2 + 3dp ink 경계 | 2026-10-14 | A3. 기본 크기·넓은 창 스크린샷 확인, Hot Reload로 Modifier 순서 실험 |
| `build.sh` | ✅ BUILD SUCCESSFUL | 2026-10-14 | A3. 이후로는 PR의 `client-ci`로 갈음 (사용자 요청: 매번 붙여넣기 안 함) |
| Android 설치 | ⬜ Phase M | — | `<activity>` 없음 (정상). 데스크톱 우선 결정 |
| `client-ci` | ✅ 초록 (3m) | 2026-10-14 | PR #6: `.ci-enabled` 후 **첫 실제 빌드·테스트** 통과 |
| `client-release` | ⬜ 미실행 | — | A10에서 첫 실행 |
| IntelliJ Gradle 동기화 | ❌ 실패 | 2026-10-14 | "incompatible AGP 9.3.3, latest supported 9.1.0" — IntelliJ 2026.2.3 번들 Android 플러그인 한계. 명령줄 빌드는 무관 |

## 진행 중

- **A3 — 인트로 사분면 + 잉크 경계**: 완료 조건 전부 확인 → 구현 커밋 + PR #8 (브랜치 `client/feat-a3-intro-quadrants`, `main` merge로 #6과 맞춤). **남은 것: CI 확인 → 사용자 squash merge**
- **A4**: PR #9 머지 완료
- **A5**: PR #10 머지 완료
- **A6**: PR #11 머지 완료
- **A7**: PR #12 머지 완료
- **A8**: PR #13 머지 완료
- **A9 — 잉크 마감 ② 버튼**: PR #14 (TODO(B3) 주석 커밋 추가). Phone 프리셋·눌림 사용자 확인 대기 → squash merge (bypass)
- **A10 — 릴리스 `client-v0.1.0`** ([guides/A10-release-0-1-0.md](guides/A10-release-0-1-0.md), 브랜치 `client/chore-release-0-1-0`, A9 위). Claude 준비 완료: 패키지 아이콘(`desktopApp/icons/`, PNG=핸드오프 원본, ICO·ICNS=Pillow 생성) + `iconFile`, `tabiji.version=0.1.0`, `CHANGELOG.md` `[0.1.0] - 2026-10-15` (CI awk 추출 사전 확인). **다음: 사용자 로컬 `package-desktop.sh` → `.deb` 설치 확인 → #14 머지 → 릴리스 PR → 태그 → Release CI → sha256 검증**
- **#5 → #7 → #8 → #3 전부 머지 완료** (2026-10-14). A4 브랜치에 `main` merge로 맞춤. **#3 이후 `client/`·`design/`·`.github/`·루트 공용 파일 PR은 코드 오너(본인) 리뷰 필수 → 머지할 때 관리자 bypass 필요**
- **데스크톱 우선** (2026-10-14 결정): Android·iOS는 Phase M. `client/feat-a2-android-entry` 원격 브랜치 삭제 여부 사용자 확인 대기

### PR 현황 (2026-10-14 기준 · 최신은 `gh pr list`)

열린 PR 없음. #1·#3·#5·#6·#7·#8 머지 완료 (Dependabot #2·#4는 닫음). A4 PR은 구현 커밋 후 연다.

### 세션 간 로컬 메모 (레포 밖 상태 — 다른 머신에는 없음)

- `.vscode/settings.json`에 사용자 로컬 수정(`gradle.autoDetect: off`, `java.gradle.buildServer.enabled: off`)이 **커밋 안 된 채** 있음 — 사용자 결정: "일단 내비둬". 커밋·stash·브랜치 전환 시 이 파일은 제외하고 다룰 것
- `client/.mcp.json`(IntelliJ MCP 연결, `127.0.0.1:64342`)이 untracked로 있음. 머신 로컬 설정이므로 커밋하지 않음 — gitignore 추가 여부는 나중에 결정
- `design/handoff/handoff-v1/`·`handoff-v2/`는 이제 `main`에 커밋됨 (#5·#7). 이전의 untracked 사본은 머지 전에 `/tmp/tabiji-handoff-backup/`으로 옮겨 정리함
- 인트로 일러스트 webp 4장은 gitignore라 **이 PC 로컬에만** 있음 (`design/handoff/handoff-v1/assets/intro/`, `handoff-v2/assets/intro/` — 두 벌은 sha256 동일). 다른 머신에서는 팀 보관소(`~/Downloads/handoff-v2.zip` 원본)에서 같은 경로로 복사
- 에디터를 VS Code → IntelliJ IDEA로 옮기는 중. IntelliJ에서는 `client/`를 Gradle 프로젝트로 열면 되고(루트 아님 주의 — 레포 루트에는 Gradle 빌드가 없다), `.vscode/tasks.json` 대신 `client/scripts/linux/*.sh`를 그대로 쓴다. `client/CLAUDE.md`의 "에디터 환경 (VS Code)" 절과 A0 가이드 9절의 LSP 한계 설명은 IntelliJ에서는 해당 없음 — IntelliJ는 KMP를 제대로 지원하므로 빨간 줄 기준으로 진단해도 된다 (갱신은 A1 완료 후 별도 PR로)

## 열린 일

- [ ] 서버·AI 담당 계정이 정해지면 `.github/CODEOWNERS`(목표 형태는 파일 주석), 루트 `CLAUDE.md` 담당 칸에 추가. `server/README.md`·`ai/README.md`의 플레이스홀더는 각 담당자가 교체
- [ ] `docs/github.md` 8절 원격 설정 적용 (관리자)
- [ ] ruleset 결정: 코드 오너 리뷰 필수 + 관리자 PR 우회 vs 코드 오너 리뷰 미강제 (`docs/github.md` 4절)
- [ ] 필수 상태 체크(`client-ci`)는 경로 필터 문제로 보류 — 필요해지면 `ci/` PR로 job 내부 판별 방식으로 전환
- [ ] AppImage 배포는 후속 (`docs/decisions/0002-toolchain-versions.md` 3절)
- [ ] Dependabot이 AGP 9.4 / Gradle 9.8을 제안하면 Kotlin 호환성 가이드 갱신 여부 확인 후 머지 (1차: #2·#4 닫음)
- [ ] **인트로 일러스트 4장 권리 처리 또는 교체** (제3자 캐릭터 포함, 팀 판단 — handoff-v1 PROMPT §9-8, v2 README 라이선스 절). 그 전까지 레포 커밋 금지(.gitignore), 앱 리소스는 A6에서 IP 없는 플레이스홀더로
- [ ] `design/requests/0001-handoff-v1.md` — handoff README가 참조하는 요청 프롬프트 사본이 레포에 없음. 디자인 담당에게 사본 요청
- [ ] Compose `FontFamily(Jua, PretendardJP)` 목록이 글자 단위 폴백으로 동작하는지 플랫폼별 미확인 (A6 1-6). **2026-10-14 Desktop 관찰: display 줄의 駿府城·旅路가 주변 Jua보다 가늘게 렌더 → 체인의 Pretendard SemiBold가 아니라 OS 시스템 폰트(Noto CJK)로 대체된 것으로 추정.** 즉 Desktop에서도 체인 폴백은 안 되는 듯. Linux는 시스템 Noto CJK가 있어 두부는 안 나옴. Android는 M2에서 확인, 필요하면 `AnnotatedString`으로 한자 구간만 Pretendard 지정
- [ ] handoff PROMPT "열린 질문" 중 개발 몫: 폰트 서브셋(v1 §9-1 — v2는 PretendardJP 4벌 22MB + Jua 2.8MB라 더 시급, A6에서), MapLibre Desktop 경로(v1 §9-3, C1에서) — 해당 단계에서 ADR로 답. v1 §9-2 Desktop 블러는 v2에서 블러 자체가 없어져 해소
- [ ] handoff-v2 PROMPT §8 팀 결정: 테마별 다크 변형 방식, v2 컴포넌트 보드 추가 제작 여부, **테마 선택 UI 디자인(B6 전에 디자인 요청 필요)**, 인트로와 테마 결합 여부
- [ ] Android API 24·25용 런처 아이콘 폴백 PNG (adaptive icon은 26+). A10 패키징 전 점검
- [ ] **에디터 결정**: IntelliJ 2026.2.3은 AGP 9.3.3 동기화 실패 → 코드 인사이트 없음. 선택지: Android Studio로 이사(추천) / IntelliJ Android 플러그인 갱신 대기 / 명령줄만으로 진행. 결정 후 `client/CLAUDE.md` "에디터 환경" 절을 갱신 — 별도 PR

## 환경 메모

| 항목 | 값 |
| --- | --- |
| OS | Ubuntu 24.04, x86_64, `/dev/kvm` 있음 |
| JDK | OpenJDK 21.0.12 (시스템) |
| 에디터 | IntelliJ IDEA 2026.2 (`client/`를 Gradle 프로젝트로) + Claude Code(IntelliJ MCP). 이전: VS Code + Kotlin LSP |
| Android SDK | 예정: `~/Android/Sdk` (cmdline-tools 단독) |
| 툴체인 | Kotlin 2.4.21 · CMP 1.12.1 · AGP 9.3.3 · Gradle 9.7.1 · compileSdk/targetSdk 37 · minSdk 24 |

## 기록

### 2026-10-15 (오후) — A9: 인트로 완성

- 가이드 지적 두 번: ① `18/22`·`58/56` 같은 CSS식 축약 → `fontSize = 18.sp, lineHeight = 22.sp`처럼 풀어 씀(메모리 규칙) ② Desktop/Phone과 `WidthClass.COMPACT`의 대응을 안 밝힘 → 용어 표와 값 고르는 코드 추가
- 막힌 곳: `animateDpAsState`에 ms 인자가 없다(→ `animationSpec = tween(…)`), `var … by remember { MutableInteractionSource() }`(by는 State 상자에만), slot 안에 `Row`를 또 만들고 부모 `modifier` 재사용, `Text`에 글자색 대신 `Modifier.background`, 버튼에 `Modifier.weight(1f)`(높이가 화면을 나눠 가짐), `InkButton` 체인 맨 앞 `padding`(바깥 여백), 스택 `align(Center)` 누락, 워드마크 블록 `horizontalAlignment` 누락(정렬은 손자에게 상속 안 됨)
- 결과: Desktop 인트로가 `intro_desktop.png`와 같은 구성 (버튼 아이콘은 B1)

### 2026-10-15 (낮) — A8 완료

- 막힌 곳: `OutlinedText` 본문을 비워 둔 채 "했어"(→ 워드마크가 사라짐), `Stroke(width = outlineWidth.value)`로 dp 숫자를 px로 그대로 씀(변환·2배 누락), 저장 지연 다시 발생(mtime으로 확인)
- **사용자 요청: 가이드에서 실험 항목을 빼고 개념 설명만** → `client/CLAUDE.md` 학습 루프에 반영. 이전 가이드(A3~A8)의 실험 절은 그대로 두되 앞으로 쓰지 않음
- 확인: Desktop·Phone 인트로 워드마크·부제 아웃라인 + 하드 섀도, 토큰 화면 색 상자 (3,3) 섀도, 화면 코드 `.dp`는 이름 붙은 상수만, `Modifier.shadow`·elevation 0건
- Hot Reload 중 창 테두리의 무지개색은 Compose Hot Reload 표시(코드 아님)

### 2026-10-15 — A7 완료

- 막힌 곳: `QuadrantStack`·`QuadrantGrid` 본문이 뒤바뀜(이름과 내용 불일치), `SeasonQuadrant`가 받은 `focusY`를 안쪽 `val focusY`로 가림(shadowing) → 덮어쓰기 덕분에 초점은 우연히 맞아 뒤바뀜이 덜 드러남, 두 배치가 받은 `modifier` 미사용
- 저장 타이밍: 리뷰 시점이 저장보다 37초 앞서 "안 바뀌었다"고 잘못 말함 → **리뷰 전에 파일 mtime을 현재 시각과 비교**할 것
- 실험 6-1: 결론은 맞으나 React 비유를 또 `useEffect`로 듦 → `useState`를 공통 부모에 두는 lifting state up으로 정정 (A4 때와 같은 혼동, 다음 상태 관련 단계에서 다시 짚기). **실험 6-2는 사용자가 건너뜀** → 결과(가지 안 상태는 unmount 시 사라짐)만 설명
- 확인: Desktop 1440×900 2×2, Phone 390×844 세로 4단·초점 58%·워드마크 46sp

### 2026-10-14 (밤) — A7 진행 중 마무리

- A7 가이드 5-3 "칸은 `focusY` 하나만 받는다"가 "둘로 나누라는 거 아니냐"로 읽힘 → enum에 저장 / 배치가 고름 / 칸이 씀 흐름 그림 추가. 사용자는 칸이 `LocalWidthClass`를 읽는 대안 방식을 택함 (허용)
- 다음 세션: 위 "남은 것" ①~⑦부터. PR #11(A6) 머지 여부 확인 → 머지됐으면 A7 브랜치에 `main` merge

### 2026-10-14 (저녁) — A6 완료

- 막힌 곳: `SeasonQuadrant`를 이미지로 바꾸며 A4 시그니처·덮개를 지움(가이드가 "시그니처 유지"를 안 적음 → 전/후 그림으로 보강), `stringResource(키, 스타일)`로 스타일을 format 인자에 넣음(오류 없이 무시됨), 4-5 문장 "Box 안에서"를 새 Box로 읽음 → 트리로 교체, import 혼동(skia `TextStyle`, androidx `Font`, `FontStyle.SemiBold`) → 단계별 import 블록 + 겹치는 이름 표
- **사용자 요청(규칙화)**: 가이드에 단계·파일별 import 블록, 단계별 Composable 트리 그림 (`client/CLAUDE.md` 반영)
- 관찰: display 줄의 한자가 Jua보다 가늘게 → `FontFamily` 체인 폴백이 아니라 OS 시스템 폰트 대체로 추정 (열린 일)
- Ubuntu에서 Gradle 실행 앱은 독 아이콘이 Java 기본일 수 있음. 창 아이콘 코드는 연결됨 → A10 `.deb`에서 독 아이콘 확인

### 2026-10-14 (저녁) — 창 기본 크기·반응형 순서 조정

- 사용자 요청: Desktop 기본 창 = 화면의 가로 85% × 세로 75% → A6 가이드 4-6에 추가 (`rememberWindowState` + `maximumWindowBounds`, 선택: 최소 360×600)
- 사용자 요청: 반응형을 미리 → **A7(반응형)과 A8(잉크 마감) 순서 교체**. 버튼 높이·스택 폭이 크기 구간에 따라 달라서 구간을 먼저 만든다. A5·A6 가이드의 "A7에서 잉크 마감" 참조를 A8로 고침

### 2026-10-14 (오후 늦게) — A5 완료

- 가이드 4-3 예시에 `Foo` 같은 가짜 이름을 써서 사용자가 "선언 안 된 타입"으로 읽음 → 실제 이름(`TabijiColors`, `LocalTabijiColors`)과 React Context 대응 코드로 교체. 앞으로 가이드 예시는 실제 이름으로
- 사용자 요청으로 나머지 팔레트 7개를 Claude가 `tokens.json`에서 생성 (일회용 스크립트, cloud를 같은 스크립트로 만들어 사용자 손코딩본과 글자 단위로 일치 확인). 사용자 cloud에서 `paper` hex 한 자리 누락(`0xFF2F7FC`) 버그를 사전에 잡음
- 막힌 곳: `main.kt`에 메뉴바 대신 Material2 `RadioButton` 위젯, `selected = p.equals(p)`, `onClick`에서 상태 안 바꿈, `val` 위임 / `TokenSwatches`가 리턴 없이 그린다는 개념(Compose는 JSX를 돌려주지 않고 호출로 등록) / `Column`에 `verticalAlignment`(주축 Arrangement·교차축 Alignment) / **부모 `modifier`를 안쪽 반복 자식에 재사용** → 첫 색 상자가 화면 전체를 덮음, 한 번은 반대로 바꿈 → "받은 modifier는 최상위 한 번, 안쪽은 `Modifier`로 새로" 정리
- IDE 저장 지연: 리뷰 시점에 디스크에 반영 전이라 "안 고쳐졌다"고 오판할 뻔함. 파일 mtime으로 확인
- 결과: 메뉴바로 8테마 전환 + 240ms 크로스페이드, 토큰 화면 korea 18줄이 tokens.json과 일치, 화면 코드 색 리터럴 0개

### 2026-10-14 (오후) — A4 완료

- A4 가이드 1차가 "부실·두서없음" 지적을 받음 → 개념(1장: recomposition, `mutableStateOf`와 `remember`의 역할 분리, hoisting 흐름도, 클릭 한 번의 5고리) 먼저, 단계별 확인 지점이 있는 형태로 재작성
- 2차 설계(부모가 칸마다 emphasis 계산)는 호출부에 계절을 반복하게 만들어 사용자가 이상하다고 지적 → 칸에 공통 `selected` + `onSelect: (Season) -> Unit` 하나를 넘기는 탭 패턴(`id` + `activeId`)으로 바꿈. "비교에는 두 값이 다 필요해서 어디서 비교할지만 고를 수 있다"고 설명
- 사용자가 막힌 곳: 덮개 `when`을 부모에 둠(`season` 미정의), `null -> null`, 어둡게를 흰색으로, `toggle` 해제 누락, `getValue`/`setValue` import 누락
- 실험 6-1을 `useEffect`(감시 후 람다 실행)로 이해 → `remember` 람다는 초기화 코드이고, 6-1은 다시 그려지지만 리셋(4번 고리), 6-2는 다시 그려지지 않음(3번 고리)으로 정정
- **사용자 요청: `build.sh` 결과 붙여넣기는 그만** → CI가 같은 빌드를 돌리므로 완료 조건을 "PR의 `client-ci` 초록"으로

### 2026-10-14 (낮) — A3 완료

- 사용자가 `IntroScreen.kt`를 처음 `desktopApp`에 만듦 → `shared/commonMain`으로 이동 (의존 방향 `desktopApp → shared`). Material 2 `Text` import도 `material3`로
- 막혔던 지점과 원인: 둘째 `Row`가 안 보임(`Column` 자식에 `weight` 없음 → 첫째가 남은 높이 전부 차지), 글자가 좌상단(Box가 Text 크기만 함 + Column과 형제), 덮개 Box가 사분면을 가림(불투명 배경 형제). 모두 사용자가 직접 수정
- Modifier 순서 실험: ①은 정확히 설명. ②는 "칸이 넓어진다"고 봐서 → 크기는 부모가 정하고 padding은 자식 자리만 줄인다(자식 없음 → 변화 없음)로 정정
- **사용자 정정: Qt·QML은 잘 모르고 React는 안다** → `client/CLAUDE.md` 소통 규칙을 React 비유로 변경. A1~A3 가이드의 Qt 비유는 그대로 두고 새 가이드부터 React
- `build.sh` 성공, 넓은 창에서 4등분 유지 확인 → A3 완료

### 2026-10-14 (오전) — 데스크톱 우선 전환, A3 시작

- A2 진행 중 에뮬레이터 첫 부팅: adb 데몬 미기동 경고 + `default_boot` 스냅샷 로드 실패 → 둘 다 무해(콜드 부트로 진행)라고 설명
- 사용자가 `MainActivity.kt`를 `shared/androidMain`에 만듦 → 진입점은 `androidApp/src/main/kotlin`이라고 안내
- IntelliJ "Configure Kotlin" 마법사가 루트 `build.gradle.kts`에 `kotlin("jvm")`·`jvmToolchain(8)`을 추가 → 되돌림(`git restore`). 마법사는 쓰지 말 것
- IntelliJ 동기화 실패: AGP 9.3.3 > 지원 상한 9.1.0. 사용자 결정: **데스크톱 우선**으로 로드맵 재구성 (Phase M 신설). 사용자가 "CMP가 Flutter처럼 같은 소스로 빌드만 달리하는 줄 알았다"고 해서, 실제로 그 모델이 맞고 문제는 IDE 도구 버전이라고 설명
- A3 가이드 작성, 브랜치 `client/feat-a3-intro-quadrants`

### 2026-10-14 — A1 완료, 디자인 핸드오프 v2, A2 시작

- 사용자가 A1 완료 보고: 제목 "Tabiji", 배경 `#EAF3FB`, 문구 복원, `.ci-enabled` 추가 → 완료 커밋 push (PR #6). `client-ci` 첫 실제 빌드 초록(3m)
- 디자인 핸드오프 v2 수령(`~/Downloads/handoff-v2.zip`, Claude Design 제작) → `design/handoff/handoff-v2/`에 배치, PR #7. 사용자는 `../docs/design/`을 말했지만 레포 규칙(`design/README.md`, 루트 `CLAUDE.md`)의 `design/handoff/handoff-vN/`을 따름
  - 무드 전면 교체: 글래스·실버 → "도감 같은 여행 수첩"(잉크 테두리·하드 섀도·색 머리띠·Jua), 테마 8종 × 역할 15개, 다크는 2단계
  - 인트로 webp 4장은 v1과 동일 파일(sha256 일치) → 커밋 제외 유지, ignore 규칙 일반화
- 로드맵을 v2 기준으로 다시 씀: A3 잉크 경계, A5 테마 8종·크로스페이드, A6 Jua 폴백 체인 + Desktop 창 아이콘(A2에서 이동), A7 아웃라인 워드마크·하드 섀도·pressSink, B1 스트라이프·inkPanel, B6 테마 선택, C2/C3 지도 스타일·마커 v2
- A2 가이드 작성, 브랜치 `client/feat-a2-android-entry` (A1 위)

### 2026-10-13 (밤) — A1 첫 실행·진단, WIP push

- IntelliJ 세션으로 이어 감. 사용자가 `App.kt`·`main.kt` 작성
- 첫 실행: `IllegalStateException: CompositionLocal LocalFontFamilyResolver not present` — `App()`을 `application { }` 바로 안에서 호출(Window 없음). 컴파일은 성공, 런타임 예외. CompositionLocal은 `Window`가 제공한다는 점을 설명하고 사용자가 `Window { App() }`로 수정
- 재실행 결과는 미확인. 사용자 요청으로 WIP 커밋·push만 하고 마무리 (PR #6은 이미 열려 있음)

### 2026-10-13 (저녁) — 아이콘 델타, IntelliJ 이사 준비

- `handoff-v1-delta-icons.zip` 반영 (PR #5 두 번째 커밋): 旅 글리프 아이콘 17파일 + `docs/04_icons_favicon.md` + PROMPT T5 신설. 아이콘은 IP 확인 완료(자체 글리프)
- T5를 로드맵에 배치: A2(런처·창 아이콘) / A9(패키지 아이콘) / F3(favicon)
- Dependabot #2(#material3 1.13-alpha)·#4(Gradle 9.8.1) 닫음 — ADR 0002 버전 정책대로
- 이 worklog를 IntelliJ 세션 인수인계 기준으로 갱신. 다음 세션은 `client/CLAUDE.md` → 이 파일 → `guides/A1-intro-window.md` 순으로 읽으면 이어진다

### 2026-10-13 (오후) — A0 완료, 핸드오프 v1, 인트로 우선 재구성

- A0 완료 확인: check-env 모두 OK, 래퍼 9.7.1, PR #1 머지, 원격 설정 8-1~8-5 적용. 에뮬레이터 부팅 확인만 A2 전 숙제로 남김
- 디자인 핸드오프 v1.1 수령·배치 (PR #5). **인트로 일러스트 4장은 제3자 캐릭터 포함으로 커밋 제외** — 열린 일 참조
- 사용자 결정: 첫 화면은 인트로부터. roadmap Phase A를 인트로 완성 아크로 재구성 (핸드오프 T·I 단계를 학습 순서로 재배열, P는 Phase B로)
- A1 가이드 작성 (guides/A1-intro-window.md)

### 2026-10-13 — 부트스트랩

- 모노레포 골격, GitHub 규약·워크플로, 클라이언트 빌드 설정, 학습 문서 작성
- 모듈 구조를 KMP 공식 권장 구조(`shared` + `androidApp` + `desktopApp`)로 결정 — AGP 9에서 KMP 모듈과 `com.android.application` 동시 적용 불가
- `.kt` 파일 0개. 빌드 명령은 실행하지 않음 (정적 검토만)
