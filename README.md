# Tabiji (旅路)

[![client-ci](https://github.com/yamada-studio/Tabiji/actions/workflows/client-ci.yml/badge.svg?branch=main)](https://github.com/yamada-studio/Tabiji/actions/workflows/client-ci.yml)
[![client-release](https://github.com/yamada-studio/Tabiji/actions/workflows/client-release.yml/badge.svg)](https://github.com/yamada-studio/Tabiji/actions/workflows/client-release.yml)
[![Latest release](https://img.shields.io/github/v/release/yamada-studio/Tabiji?include_prereleases&filter=client-v*&label=client)](https://github.com/yamada-studio/Tabiji/releases)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

지도 위에서 여행 일정과 동선을 만들고, 실제 이동시간을 근거로 AI가 동선을 다듬어 주는 여행 계획 앱.

## 영역

| 영역 | 설명 | 문서 |
| --- | --- | --- |
| [`client/`](client/) | Kotlin + Compose Multiplatform 앱. 1차 Android + Desktop | [client/README.md](client/README.md) |
| [`server/`](server/) | 백엔드 API 서버 | [server/README.md](server/README.md) |
| [`ai/`](ai/) | 동선 최적화 AI | [ai/README.md](ai/README.md) |
| [`api/`](api/) | 클라이언트↔서버 계약 (OpenAPI) | [api/README.md](api/README.md) |
| [`design/`](design/) | 디자인 요청과 핸드오프 | [design/README.md](design/README.md) |
| [`docs/`](docs/) | 설계 문서, ADR, GitHub 규약 | [docs/github.md](docs/github.md) |

## 다운로드

클라이언트 설치 파일(Linux `.deb`, Windows `.msi`, macOS `.dmg`, Android `.apk`)은 [Releases](https://github.com/yamada-studio/Tabiji/releases)에서 받는다. 1.0 전 릴리스는 모두 prerelease다.

## 기여

- 팀 공통 규칙: [CLAUDE.md](CLAUDE.md)
- 브랜치·커밋·PR·태그 규약: [docs/github.md](docs/github.md)
- 변경 기록: [CHANGELOG.md](CHANGELOG.md)

## 라이선스

[MIT](LICENSE) © yamada-studio
