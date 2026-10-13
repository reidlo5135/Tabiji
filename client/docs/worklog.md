# 클라이언트 워크로그

세션을 시작할 때 Claude가 이 파일을 먼저 읽고, 작업 덩어리가 끝날 때마다 갱신한다. 여러 머신에서 이어 가기 위한 메모다.
최신 항목이 위로 온다.

## 검증 상태

마지막으로 **사용자가 직접 실행해 확인한** 결과만 적는다.

| 항목 | 상태 | 확인 날짜 | 메모 |
| --- | --- | --- | --- |
| `check-env.sh` | ✅ 모두 OK | 2026-10-13 | JDK 21 · SDK `~/Android/Sdk` · adb · gh 2.102 |
| `./gradlew --version` | ✅ 9.7.1 | 2026-10-13 | 래퍼 커밋 완료 (PR #1) |
| 에뮬레이터 | ⬜ AVD만 생성 | — | `tabiji-api37`(API 37) 생성됨, 부팅·`adb devices`는 미확인 — A2 전까지 |
| Desktop 실행 | ⬜ A1 진행 중 | — | 첫 실행에서 `LocalFontFamilyResolver not present`(Window 누락) → 수정했으나 재실행 미확인 |
| Android 설치 | ⬜ A2 전 | — | `<activity>` 없음 (정상) |
| `client-ci` | ✅ 초록 | 2026-10-13 | PR #1: 래퍼 검증 통과·빌드 건너뜀. 실제 빌드는 A1의 `.ci-enabled` 후 |
| `client-release` | ⬜ 미실행 | — | A9에서 첫 실행 |

## 진행 중

- **A1 — 인트로 바탕 창** ([guides/A1-intro-window.md](guides/A1-intro-window.md), 브랜치 `client/feat-a1-intro-window`, PR #6)
  - `App.kt`·`main.kt` 초안 작성, WIP 커밋으로 push (2026-10-13 밤). 진단 미통과 상태 — squash merge라 최종 커밋 메시지는 PR 제목으로 정리됨
  - **다음 행동: 아래 리뷰 잔여 사항 반영 → `scripts/linux/run-desktop.sh` 재실행 결과 붙여넣기 → 진단 → `--hot` → `build.sh` → `.ci-enabled` 추가 후 push**
  - 리뷰 잔여 사항 (가이드 대비)
    - `main.kt`: `Window`에 `title = "Tabiji"` 없음 (완료 조건이 제목 표시줄 "Tabiji")
    - `App.kt`: 배경 `Color(0xFFE0E0E0)` → 가이드 값 `0xFFEAF3FB`(`bg.sky.top`). 사용자가 일부러 바꾼 것인지 확인
    - `App.kt`: 글자 `"Tabiji(旅路)"` → 가이드는 `"Tabiji"` (旅路는 Hot Reload 체험용). 의도 확인
    - TODO 주석이 글자 색 줄에만 있음 → 배경색 줄에도 `// TODO(A5): TabijiTheme 토큰으로`
  - 디자인 핸드오프 v1 + 아이콘 델타 수령 → `design/handoff/handoff-v1/` (PR #5). 로드맵을 인트로 우선으로 재구성

### PR 현황 (2026-10-13 저녁 기준 · 최신은 `gh pr list`)

| PR | 브랜치 | 상태 | 메모 |
| --- | --- | --- | --- |
| #3 CODEOWNERS 클라이언트 담당 | `repo/chore-assign-client-owner` | 열림 | 사용자가 머지. 머지 후 `.github/`·`client/` PR은 관리자 우회("bypass rules") 필요해짐 |
| #5 디자인 핸드오프 v1 + 旅 아이콘 세트 | `design/feat-handoff-v1` | 열림 | 사용자가 머지. **머지 전까지 A1 브랜치 워크트리에는 핸드오프 문서가 안 보임** (GitHub에서 보거나 #5 먼저 머지) |
| #6 A1 단계 | `client/feat-a1-intro-window` | 열림 | 문서 커밋만 있음. 구현 커밋까지 담은 뒤 squash merge. #3·#5 머지 후 rebase 필요할 수 있음(충돌 없을 것) |

### 세션 간 로컬 메모 (레포 밖 상태 — 다른 머신에는 없음)

- `.vscode/settings.json`에 사용자 로컬 수정(`gradle.autoDetect: off`, `java.gradle.buildServer.enabled: off`)이 **커밋 안 된 채** 있음 — 사용자 결정: "일단 내비둬". 커밋·stash·브랜치 전환 시 이 파일은 제외하고 다룰 것
- `client/.mcp.json`(IntelliJ MCP 연결, `127.0.0.1:64342`)이 untracked로 있음. 머신 로컬 설정이므로 커밋하지 않음 — gitignore 추가 여부는 나중에 결정
- 인트로 일러스트 webp 4장은 gitignore라 **이 PC 로컬에만** 있음 (`design/handoff/handoff-v1/assets/intro/`). 다른 머신에서는 팀 보관소( `~/Downloads/handoff-v1.zip` 원본)에서 같은 경로로 복사
- 에디터를 VS Code → IntelliJ IDEA로 옮기는 중. IntelliJ에서는 `client/`를 Gradle 프로젝트로 열면 되고(루트 아님 주의 — 레포 루트에는 Gradle 빌드가 없다), `.vscode/tasks.json` 대신 `client/scripts/linux/*.sh`를 그대로 쓴다. `client/CLAUDE.md`의 "에디터 환경 (VS Code)" 절과 A0 가이드 9절의 LSP 한계 설명은 IntelliJ에서는 해당 없음 — IntelliJ는 KMP를 제대로 지원하므로 빨간 줄 기준으로 진단해도 된다 (갱신은 A1 완료 후 별도 PR로)

## 열린 일

- [ ] 팀원 GitHub 계정으로 플레이스홀더 교체: `<CLIENT_OWNER>`, `<SERVER_OWNER>`, `<AI_OWNER>` (`.github/CODEOWNERS`, 루트 `CLAUDE.md`, `server/README.md`, `ai/README.md`, ADR들)
- [ ] `docs/github.md` 8절 원격 설정 적용 (관리자)
- [ ] ruleset 결정: 코드 오너 리뷰 필수 + 관리자 PR 우회 vs 코드 오너 리뷰 미강제 (`docs/github.md` 4절)
- [ ] 필수 상태 체크(`client-ci`)는 경로 필터 문제로 보류 — 필요해지면 `ci/` PR로 job 내부 판별 방식으로 전환
- [ ] AppImage 배포는 후속 (`docs/decisions/0002-toolchain-versions.md` 3절)
- [ ] Dependabot이 AGP 9.4 / Gradle 9.8을 제안하면 Kotlin 호환성 가이드 갱신 여부 확인 후 머지 (1차: #2·#4 닫음)
- [ ] **인트로 일러스트 4장 권리 처리 또는 교체** (제3자 캐릭터 포함, 팀 판단 — handoff PROMPT §9-8). 그 전까지 레포 커밋 금지(.gitignore), 앱 리소스는 A6에서 IP 없는 플레이스홀더로
- [ ] `design/requests/0001-handoff-v1.md` — handoff README가 참조하는 요청 프롬프트 사본이 레포에 없음. 디자인 담당에게 사본 요청
- [ ] handoff PROMPT "열린 질문" 중 개발 몫: 폰트 서브셋(§9-1), Desktop 블러(§9-2), MapLibre Desktop 경로(§9-3, C1에서) — 해당 단계에서 ADR로 답

## 환경 메모

| 항목 | 값 |
| --- | --- |
| OS | Ubuntu 24.04, x86_64, `/dev/kvm` 있음 |
| JDK | OpenJDK 21.0.12 (시스템) |
| 에디터 | VS Code + Kotlin by JetBrains (Alpha, KMP 지원 제한) |
| Android SDK | 예정: `~/Android/Sdk` (cmdline-tools 단독) |
| 툴체인 | Kotlin 2.4.21 · CMP 1.12.1 · AGP 9.3.3 · Gradle 9.7.1 · compileSdk/targetSdk 37 · minSdk 24 |

## 기록

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
