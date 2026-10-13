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
| Desktop 실행 | ⬜ A1 진행 중 | — | 사용자가 `App()`·`main.kt` 작성 중 |
| Android 설치 | ⬜ A2 전 | — | `<activity>` 없음 (정상) |
| `client-ci` | ✅ 초록 | 2026-10-13 | PR #1: 래퍼 검증 통과·빌드 건너뜀. 실제 빌드는 A1의 `.ci-enabled` 후 |
| `client-release` | ⬜ 미실행 | — | A9에서 첫 실행 |

## 진행 중

- **A1 — 인트로 바탕 창** ([guides/A1-intro-window.md](guides/A1-intro-window.md), 브랜치 `client/feat-a1-intro-window`)
  - 가이드 작성 완료. 사용자 구현 대기 (`App.kt`, `main.kt`, `.ci-enabled`)
  - 디자인 핸드오프 v1 수령 → `design/handoff/handoff-v1/` (PR #5). 로드맵을 인트로 우선으로 재구성

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

### 2026-10-13 (오후) — A0 완료, 핸드오프 v1, 인트로 우선 재구성

- A0 완료 확인: check-env 모두 OK, 래퍼 9.7.1, PR #1 머지, 원격 설정 8-1~8-5 적용. 에뮬레이터 부팅 확인만 A2 전 숙제로 남김
- 디자인 핸드오프 v1.1 수령·배치 (PR #5). **인트로 일러스트 4장은 제3자 캐릭터 포함으로 커밋 제외** — 열린 일 참조
- 사용자 결정: 첫 화면은 인트로부터. roadmap Phase A를 인트로 완성 아크로 재구성 (핸드오프 T·I 단계를 학습 순서로 재배열, P는 Phase B로)
- A1 가이드 작성 (guides/A1-intro-window.md)

### 2026-10-13 — 부트스트랩

- 모노레포 골격, GitHub 규약·워크플로, 클라이언트 빌드 설정, 학습 문서 작성
- 모듈 구조를 KMP 공식 권장 구조(`shared` + `androidApp` + `desktopApp`)로 결정 — AGP 9에서 KMP 모듈과 `com.android.application` 동시 적용 불가
- `.kt` 파일 0개. 빌드 명령은 실행하지 않음 (정적 검토만)
