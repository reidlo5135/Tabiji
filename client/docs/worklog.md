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
| `build.sh` | ✅ BUILD SUCCESSFUL | 2026-10-14 | A3. Android 디버그 APK + jvmTest 포함 |
| Android 설치 | ⬜ Phase M | — | `<activity>` 없음 (정상). 데스크톱 우선 결정 |
| `client-ci` | ✅ 초록 (3m) | 2026-10-14 | PR #6: `.ci-enabled` 후 **첫 실제 빌드·테스트** 통과 |
| `client-release` | ⬜ 미실행 | — | A9에서 첫 실행 |
| IntelliJ Gradle 동기화 | ❌ 실패 | 2026-10-14 | "incompatible AGP 9.3.3, latest supported 9.1.0" — IntelliJ 2026.2.3 번들 Android 플러그인 한계. 명령줄 빌드는 무관 |

## 진행 중

- **A3 — 인트로 사분면 + 잉크 경계**: 완료 조건 전부 확인 → 구현 커밋 + PR #8 (브랜치 `client/feat-a3-intro-quadrants`, `main` merge로 #6과 맞춤). **남은 것: CI 확인 → 사용자 squash merge**
- **다음: A4 — 사분면 선택(`remember`, state hoisting)**. 가이드는 A4 시작 때 작성
- **데스크톱 우선** (2026-10-14 결정): Android·iOS는 Phase M. `client/feat-a2-android-entry` 원격 브랜치 삭제 여부 사용자 확인 대기
- **디자인 핸드오프 v2** (PR #7): #5 다음에 머지

### PR 현황 (2026-10-14 기준 · 최신은 `gh pr list`)

| PR | 브랜치 | 상태 | 메모 |
| --- | --- | --- | --- |
| #3 CODEOWNERS 클라이언트 담당 | `repo/chore-assign-client-owner` | 열림 | 사용자가 머지. 머지 후 `.github/`·`client/` PR은 관리자 우회("bypass rules") 필요해짐 |
| #5 디자인 핸드오프 v1 + 旅 아이콘 세트 | `design/feat-handoff-v1` | 열림 | 사용자가 머지. v2가 대체하지만 스냅샷 보존 원칙(`design/README.md`)대로 머지 권장 |
| #6 A1 단계 | `client/feat-a1-intro-window` | ✅ 머지 | `08c1383` |
| #7 디자인 핸드오프 v2 | `design/feat-handoff-v2` | 열림 | **#5 다음에 머지** (base를 `main`으로 바꾼 뒤). 루트 `.gitignore`의 인트로 webp 규칙을 `design/handoff/*/assets/intro/*.webp`로 넓힘 |
| #8 A3 단계 | `client/feat-a3-intro-quadrants` | 열림 | 로드맵 v2·데스크톱 우선 문서 포함. CI 확인 후 squash merge |

### 세션 간 로컬 메모 (레포 밖 상태 — 다른 머신에는 없음)

- `.vscode/settings.json`에 사용자 로컬 수정(`gradle.autoDetect: off`, `java.gradle.buildServer.enabled: off`)이 **커밋 안 된 채** 있음 — 사용자 결정: "일단 내비둬". 커밋·stash·브랜치 전환 시 이 파일은 제외하고 다룰 것
- `client/.mcp.json`(IntelliJ MCP 연결, `127.0.0.1:64342`)이 untracked로 있음. 머신 로컬 설정이므로 커밋하지 않음 — gitignore 추가 여부는 나중에 결정
- 인트로 일러스트 webp 4장은 gitignore라 **이 PC 로컬에만** 있음 (`design/handoff/handoff-v1/assets/intro/`, `handoff-v2/assets/intro/` — 두 벌은 sha256 동일). 다른 머신에서는 팀 보관소(`~/Downloads/handoff-v2.zip` 원본)에서 같은 경로로 복사
- `design/handoff/handoff-v1/`·`handoff-v2/`가 클라이언트 브랜치에서 **untracked 사본**으로 있음 (#5·#7 미머지라 이 브랜치엔 없음). **#5·#7 머지 후 `main`을 pull·rebase하기 전에** 두 폴더의 `assets/intro/*.webp`를 잠시 다른 곳에 옮기고 두 폴더를 지운 뒤 pull, 그다음 webp를 제자리로 — 안 그러면 git이 "untracked working tree files would be overwritten"으로 멈춘다
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
- [ ] handoff PROMPT "열린 질문" 중 개발 몫: 폰트 서브셋(v1 §9-1 — v2는 PretendardJP 4벌 22MB + Jua 2.8MB라 더 시급, A6에서), MapLibre Desktop 경로(v1 §9-3, C1에서) — 해당 단계에서 ADR로 답. v1 §9-2 Desktop 블러는 v2에서 블러 자체가 없어져 해소
- [ ] handoff-v2 PROMPT §8 팀 결정: 테마별 다크 변형 방식, v2 컴포넌트 보드 추가 제작 여부, **테마 선택 UI 디자인(B6 전에 디자인 요청 필요)**, 인트로와 테마 결합 여부
- [ ] Android API 24·25용 런처 아이콘 폴백 PNG (adaptive icon은 26+). A9 패키징 전 점검
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
