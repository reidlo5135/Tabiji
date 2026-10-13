# 클라이언트 워크로그

세션을 시작할 때 Claude가 이 파일을 먼저 읽고, 작업 덩어리가 끝날 때마다 갱신한다. 여러 머신에서 이어 가기 위한 메모다.
최신 항목이 위로 온다.

## 검증 상태

마지막으로 **사용자가 직접 실행해 확인한** 결과만 적는다.

| 항목 | 상태 | 확인 날짜 | 메모 |
| --- | --- | --- | --- |
| `check-env.sh` | ⬜ 미확인 | — | 2026-10-13 정적 확인: JDK 21 있음, Android SDK·`adb`·`gh` 없음 |
| `./gradlew --version` | ⬜ 래퍼 없음 | — | A0에서 생성 |
| Desktop 실행 | ⬜ A1 전 | — | `main.kt` 없음 (정상) |
| Android 설치 | ⬜ A2 전 | — | `<activity>` 없음 (정상) |
| `client-ci` | ⬜ 미실행 | — | 부트스트랩 PR에서 첫 실행 |
| `client-release` | ⬜ 미실행 | — | A9에서 첫 실행 |

## 진행 중

- **A0 — 개발 환경과 첫 PR** ([guides/A0-environment.md](guides/A0-environment.md))
  - 부트스트랩 커밋 완료(브랜치 `repo/chore-bootstrap-monorepo`). push·PR은 이 PC의 GitHub 인증이 없어 사용자가 진행

## 열린 일

- [ ] 팀원 GitHub 계정으로 플레이스홀더 교체: `<CLIENT_OWNER>`, `<SERVER_OWNER>`, `<AI_OWNER>` (`.github/CODEOWNERS`, 루트 `CLAUDE.md`, `server/README.md`, `ai/README.md`, ADR들)
- [ ] `docs/github.md` 8절 원격 설정 적용 (관리자)
- [ ] ruleset 결정: 코드 오너 리뷰 필수 + 관리자 PR 우회 vs 코드 오너 리뷰 미강제 (`docs/github.md` 4절)
- [ ] 필수 상태 체크(`client-ci`)는 경로 필터 문제로 보류 — 필요해지면 `ci/` PR로 job 내부 판별 방식으로 전환
- [ ] AppImage 배포는 후속 (`docs/decisions/0002-toolchain-versions.md` 3절)
- [ ] Dependabot이 AGP 9.4 / Gradle 9.8을 제안하면 Kotlin 호환성 가이드 갱신 여부 확인 후 머지

## 환경 메모

| 항목 | 값 |
| --- | --- |
| OS | Ubuntu 24.04, x86_64, `/dev/kvm` 있음 |
| JDK | OpenJDK 21.0.12 (시스템) |
| 에디터 | VS Code + Kotlin by JetBrains (Alpha, KMP 지원 제한) |
| Android SDK | 예정: `~/Android/Sdk` (cmdline-tools 단독) |
| 툴체인 | Kotlin 2.4.21 · CMP 1.12.1 · AGP 9.3.3 · Gradle 9.7.1 · compileSdk/targetSdk 37 · minSdk 24 |

## 기록

### 2026-10-13 — 부트스트랩

- 모노레포 골격, GitHub 규약·워크플로, 클라이언트 빌드 설정, 학습 문서 작성
- 모듈 구조를 KMP 공식 권장 구조(`shared` + `androidApp` + `desktopApp`)로 결정 — AGP 9에서 KMP 모듈과 `com.android.application` 동시 적용 불가
- `.kt` 파일 0개. 빌드 명령은 실행하지 않음 (정적 검토만)
