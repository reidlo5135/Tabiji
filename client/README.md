# Tabiji 클라이언트

Kotlin + Compose Multiplatform 앱. 1차 타깃 **Android + Desktop(JVM)**.

> 이 영역은 담당자가 Kotlin·CMP를 손으로 익히며 만드는 학습 프로젝트이기도 하다. 진행 순서는 [docs/roadmap.md](docs/roadmap.md), Claude Code 작업 규칙은 [CLAUDE.md](CLAUDE.md).

## 다운로드

설치 파일은 [Releases](https://github.com/yamada-studio/Tabiji/releases)의 `client-v*` 항목에서 받는다.

| OS | 파일 |
| --- | --- |
| Linux (Debian/Ubuntu) | `Tabiji-<version>-linux.deb` |
| Windows | `Tabiji-<version>-windows.msi` |
| macOS | `Tabiji-<version>-macos.dmg` (미서명 — 릴리스 노트의 실행 안내 참고) |
| Android | `Tabiji-<version>-android.apk` (서명 빌드가 포함된 릴리스만) |

각 릴리스의 `SHA256SUMS.txt`로 무결성을 확인할 수 있다.

## 구조

`client/`는 **독립 Gradle 루트**다. 모든 명령은 `client/`에서 실행한다.

```
client/
├── shared/       공유 UI·로직 (KMP: android + jvm)
├── androidApp/   Android 앱 진입점
├── desktopApp/   Desktop 앱 진입점
├── gradle/libs.versions.toml   툴체인·라이브러리 버전 (단일 원본)
├── gradle.properties           tabiji.version (앱 버전 단일 원본)
└── scripts/linux/              개발 스크립트
```

자세한 건 [docs/architecture.md](docs/architecture.md).

## 빌드·실행

> ⏳ **A0 이후 채움.** 지금은 Gradle 래퍼가 없고(A0에서 생성), 앱 진입점 코드도 없다(A1·A2에서 작성). 환경 준비는 [docs/guides/A0-environment.md](docs/guides/A0-environment.md).

예정된 명령 (`client/`에서):

| 명령 | 하는 일 |
| --- | --- |
| `scripts/linux/check-env.sh` | JDK 17+, `ANDROID_HOME`, `adb`, `gh` 확인 |
| `scripts/linux/run-desktop.sh [--hot] [--log]` | Desktop 실행 (`--hot` = Compose Hot Reload) |
| `scripts/linux/install-android.sh` | 디버그 APK를 기기/에뮬레이터에 설치 |
| `scripts/linux/build.sh [--clean]` | Android 디버그 빌드 + Desktop 컴파일 + 테스트 (CI와 같음) |
| `scripts/linux/package-desktop.sh` | 현재 OS용 설치 패키지 (Release CI와 같은 태스크) |

VS Code에서는 Terminal → Run Task… 의 `client: …` 태스크로도 실행할 수 있다.

## 문서

| 문서 | 내용 |
| --- | --- |
| [docs/roadmap.md](docs/roadmap.md) | 학습 단계와 현재 위치 |
| [docs/worklog.md](docs/worklog.md) | 세션 간 작업 메모 |
| [docs/architecture.md](docs/architecture.md) | 모듈·의존 방향 |
| [docs/conventions.md](docs/conventions.md) | 코드 컨벤션 |
| [docs/decisions/](docs/decisions/) | 클라이언트 ADR (`C-NNNN`) |
| [../docs/decisions/0002-toolchain-versions.md](../docs/decisions/0002-toolchain-versions.md) | 툴체인 버전·모듈 구조 근거 |
