# Changelog

이 레포의 모든 주목할 만한 변경을 영역별로 기록한다.
형식은 [Keep a Changelog 1.1.0](https://keepachangelog.com/ko/1.1.0/), 버전은 [Semantic Versioning](https://semver.org/lang/ko/)을 따른다.

## 작성 규칙

- 영역마다 `## <영역>` 섹션이 있고, 그 아래 버전은 `### [X.Y.Z] - YYYY-MM-DD`, 분류는 `#### Added / Changed / Fixed / Removed`
- 작업 중 변경은 `### [Unreleased]`에 쌓고, 릴리스 PR에서 버전 제목으로 바꾼다
- **Release CI는 `## Client` 섹션의 `### [X.Y.Z]` 아래 내용을 릴리스 노트로 그대로 쓴다.** 제목 형식을 바꾸면 CI가 실패한다
- 태그: Client `client-vX.Y.Z`, Server `server-vX.Y.Z`, AI `ai-vX.Y.Z`

## Client

### [Unreleased]

#### Added

- 모노레포 골격, 클라이언트 빌드 설정, CI·Release 워크플로, 학습 로드맵

## Server

### [Unreleased]

## AI

### [Unreleased]
