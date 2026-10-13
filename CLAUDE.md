# CLAUDE.md — Tabiji 팀 공통 규칙

Claude Code가 이 레포에서 작업할 때 가장 먼저 읽는 파일이다. **영역별 `CLAUDE.md`(예: `client/CLAUDE.md`)가 있으면 그 영역에서는 그것이 우선한다.**

## 레포 구조와 담당

| 경로 | 내용 | 담당 |
| --- | --- | --- |
| `client/` | Kotlin + Compose Multiplatform 앱 (Android, Desktop) | `@reidlo5135` |
| `server/` | 백엔드 API 서버 | 미정 |
| `ai/` | 동선 최적화 AI | 미정 |
| `api/` | 클라이언트↔서버 계약 (`openapi.yaml`) | 클라이언트 + 서버 공동 |
| `design/` | 디자인 요청서와 핸드오프 | AI·디자인 + 클라이언트 |
| `docs/` | 팀 공용 설계 문서, ADR, GitHub 규약 | 공동 |

## 규칙

- **자기 영역 밖 파일은 수정하지 않는다.** `api/`와 루트 공용 파일(`CLAUDE.md`, `README.md`, `CHANGELOG.md`, `.github/` 등)을 바꿀 때는 PR 설명에 이유를 쓰고 상대 담당자 리뷰를 받는다
- GitHub 규약은 [`docs/github.md`](docs/github.md)를 따른다. 요약:
  - 장기 브랜치는 `main` 하나. 모든 브랜치는 영역 prefix로 시작: `<영역>/<type>-<slug>` (예: `client/feat-a3-state-basics`, 루트 공용 작업은 `repo/…`, 실험은 `client/exp-…`)
  - 커밋은 Conventional Commits, 영어, scope 필수 (`feat(client): ...`, `ci: ...`)
  - `main`에는 PR + squash merge로만 들어간다. PR 제목이 곧 커밋 메시지
  - `api/`, `.github/`, 루트 공용 파일을 바꾼 PR은 CODEOWNERS 리뷰 필수
  - 릴리스 태그는 영역별 SemVer: `client-vX.Y.Z`, `server-vX.Y.Z`, `ai-vX.Y.Z`
- 사람이 보는 이름은 `Tabiji`(부제 旅路), 코드·빌드 식별자는 소문자 `tabiji`
- **비밀값은 커밋하지 않는다.** API 키, 키스토어(`*.jks`, `*.keystore`), `local.properties`, `.env`
- **타사 IP 에셋은 커밋하지 않는다.** 게임 캐릭터·스프라이트, 다른 회사의 로고·UI 프레임·전용 폰트
