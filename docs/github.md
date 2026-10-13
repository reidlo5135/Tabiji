# GitHub 규약

Tabiji 모노레포(`yamada-studio/Tabiji`)의 브랜치·커밋·PR·이슈·태그·릴리스 규칙과, 레포 관리자가 한 번 실행할 원격 설정 명령을 모아 둔 문서다. 근거는 [ADR 0001](decisions/0001-monorepo-layout.md).

## 1. 브랜치

- **장기 브랜치는 `main` 하나.** 사람별·영역별 장기 브랜치(`client-dev` 등)는 만들지 않는다
- 모든 브랜치는 **영역 prefix로 시작한다**: `<영역>/<type>-<slug>`
  - 영역: `client | server | ai | api | design | docs | ci | repo`
    - `repo`: 어느 영역에도 속하지 않는 루트 공용 파일(`CLAUDE.md`, `README.md`, `.gitignore`, `.vscode/` …)이나 여러 영역에 걸친 변경
  - type: `feat | fix | docs | chore | refactor | test | exp`
  - slug: 소문자·숫자·하이픈만 (`[a-z0-9-]+`)
  - 예: `client/feat-a3-state-basics`, `server/feat-trip-crud`, `api/feat-leg-confidence`, `ci/fix-release-cache`, `repo/chore-bootstrap-monorepo`
  - 클라이언트 로드맵 단계는 slug 앞에 단계 번호: `client/feat-a3-...`
- 실험 브랜치도 같은 형식, type만 `exp`: `client/exp-maplibre-spike` — merge하지 않는다. PR을 열지 않으면 CI도 돌지 않는다
- 정규식 (리뷰 때 확인용):

  ```
  ^(client|server|ai|api|design|docs|ci|repo)/(feat|fix|docs|chore|refactor|test|exp)-[a-z0-9-]+$
  ```

  GitHub의 브랜치 이름 제한 ruleset은 Enterprise 플랜 전용이라 지금은 **팀 약속**으로 지킨다. 강제가 필요해지면 PR 브랜치 이름을 검사하는 워크플로를 `ci/` PR로 추가한다
- 수명 목표 **1주 이내**. 길어지면 `main`을 rebase로 따라간다

```bash
git fetch origin
git rebase origin/main
git push --force-with-lease   # 내 작업 브랜치에서만
```

## 2. 커밋

- [Conventional Commits 1.0.0](https://www.conventionalcommits.org/ko/v1.0.0/), **영어**, **scope 필수**

```
<type>(<scope>): <summary>      ← 소문자 시작, 마침표 없음, 72자 이내

<body: 무엇을·왜>

Refs: A3                         ← 클라이언트 로드맵 단계 (있으면)
```

| type | 언제 |
| --- | --- |
| `feat` | 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서만 |
| `refactor` | 동작 변화 없는 구조 변경 |
| `test` | 테스트만 |
| `chore` | 빌드 설정, 의존성, 잡무 |
| `ci` | 워크플로·`.github/` (scope 생략 허용: `ci: ...`) |

- scope: `client`, `server`, `ai`, `api`, `design`, `docs`, `repo`. 루트 공용 파일·여러 영역에 걸친 변경은 `repo`
- 깨지는 변경은 `!`: `feat(api)!: rename leg.duration to leg.durationSec`
- **한 단계(작업 덩어리)당 커밋 1개**가 기본. 리뷰 반영 커밋은 자유롭게 쌓아도 squash merge로 하나가 된다

## 3. Pull Request

- **`main`에는 PR로만** 들어간다. 머지 방식은 **squash merge만** 허용
- **PR 제목 = squash 커밋 메시지.** 그래서 PR 제목도 Conventional Commits 형식이다
- 템플릿: [`.github/pull_request_template.md`](../.github/pull_request_template.md) — 무엇을 / 왜 / 영향 영역 / 스크린샷 / 확인 방법 / 관련
- 머지 후 작업 브랜치는 자동 삭제된다 (아래 설정)

### 리뷰 규칙

| PR이 바꾼 경로 | 리뷰 |
| --- | --- |
| 자기 영역만 (`client/`만, `server/`만 …) | CI 통과 후 **셀프 머지 가능** |
| `api/` | 클라이언트·서버 담당 **둘 중 상대방** 승인 필수 |
| `.github/`, `.vscode/`, 루트 공용 파일(`CLAUDE.md`, `README.md`, `CHANGELOG.md`, `.gitignore` …) | CODEOWNERS 승인 필수. PR 설명에 변경 이유 |
| `design/` | AI·디자인 또는 클라이언트 담당 |

### 클라이언트 PR 흐름

1. Claude가 단계 시작 때 브랜치를 만든다
2. 사용자가 구현 → Claude 진단 통과
3. Claude가 커밋 1개 → `git push` → `gh pr create`
4. **사용자가 CI 결과를 확인하고 직접 squash merge**

## 4. CODEOWNERS

[`.github/CODEOWNERS`](../.github/CODEOWNERS)가 경로별 리뷰어를 자동 지정하고, ruleset의 "코드 오너 리뷰 필수"와 결합해 승인을 강제한다.

> **주의 — 혼자 담당하는 영역의 셀프 머지**
> GitHub는 PR 작성자가 자기 PR을 승인하는 것을 허용하지 않는다. 그래서 "코드 오너 리뷰 필수"를 켜면 `/client/`처럼 오너가 한 명인 경로는 **그 오너가 연 PR이 승인을 받을 수 없어 막힌다.**
> 이 레포는 **관리자(Admin) 역할에 PR 한정 우회(bypass mode: pull_request)** 를 주는 방식으로 푼다. 관리자는 PR 머지 화면에서 "Merge without waiting for requirements to be met (bypass rules)"를 체크해 머지할 수 있다.
> **팀 약속: 우회 머지는 "자기 영역만 바꾼 PR + CI 초록"일 때만 쓴다.** `api/`, `.github/`, 루트 공용 파일 PR은 우회하지 않고 상대 승인을 받는다.
> 이를 위해 세 담당자 모두 레포 Admin 역할이 필요하다. 원치 않으면 5절의 대안을 참고.

## 5. 이슈와 라벨

- 템플릿: [`bug.yml`](../.github/ISSUE_TEMPLATE/bug.yml), [`feature.yml`](../.github/ISSUE_TEMPLATE/feature.yml), [`design-request.yml`](../.github/ISSUE_TEMPLATE/design-request.yml). 빈 이슈는 막혀 있다
- PR의 `area:*` 라벨은 [`labeler`](../.github/workflows/labeler.yml) 워크플로가 변경 경로를 보고 자동으로 붙인다 ([`.github/labeler.yml`](../.github/labeler.yml))
- `type:*`, `priority:*`, `status:*`는 사람이 붙인다

| 라벨 | 의미 |
| --- | --- |
| `area:client` `area:server` `area:ai` `area:api` `area:design` `area:ci` | 영역 |
| `type:feat` `type:fix` `type:docs` `type:chore` `type:refactor` | 작업 종류 |
| `priority:high` `priority:low` | 우선순위 (없으면 보통) |
| `status:blocked` | 다른 작업·결정을 기다림. 이유를 코멘트로 |

## 6. 태그 · 버전 · 릴리스

- 영역별 SemVer 태그: `client-vX.Y.Z`, `server-vX.Y.Z`, `ai-vX.Y.Z`
- 클라이언트 버전 규칙
  - **Phase 완료 = MINOR** (`client-v0.1.0` = Phase A 완료, `0.2.0` = Phase B …)
  - 릴리스 후 버그 수정 = PATCH (`client-v0.1.1`)
  - **1.0 전 릴리스는 GitHub에서 prerelease**로 표시된다 (Release CI가 자동 판단)
- **버전 단일 원본: `client/gradle.properties`의 `tabiji.version`.** 태그의 버전과 다르면 Release CI가 실패한다
- 변경 기록: 루트 [`CHANGELOG.md`](../CHANGELOG.md)의 `## Client` 아래 `### [X.Y.Z] - 날짜`. Release 노트는 이 섹션에서 그대로 뽑는다. 섹션이 없으면 Release CI가 실패한다

### 클라이언트 릴리스 순서

1. 릴리스 PR (`client/chore-release-0-1-0`): `tabiji.version` 올림 + `CHANGELOG.md`의 `[Unreleased]`를 `[0.1.0] - YYYY-MM-DD`로
2. CI 초록 → squash merge
3. 태그 (관리자만 가능, 아래 태그 ruleset)

```bash
git switch main && git pull --ff-only
git tag client-v0.1.0
git push origin client-v0.1.0
```

4. [`client-release`](../.github/workflows/client-release.yml)가 Desktop(`.deb`/`.msi`/`.dmg`)·Android(시크릿이 있을 때)를 만들고 Release를 공개한다
5. 같은 태그로 다시 빌드하려면: Actions → client-release → Run workflow → 태그 입력. 기존 Release의 에셋이 교체된다

## 7. CI 워크플로 명명 규칙

| 파일 | 역할 |
| --- | --- |
| `<영역>-ci.yml` | PR·main 검증. `paths: ['<영역>/**', '.github/workflows/<영역>-ci.yml']` 필터. job 이름 = `<영역>-ci` |
| `<영역>-release.yml` | `<영역>-v*` 태그로 릴리스 |
| `labeler.yml` | 경로별 `area:*` 라벨 |

- 지금 있는 것: `client-ci.yml`, `client-release.yml`, `labeler.yml`. 서버·AI는 담당자가 같은 규칙으로 추가한다
- 각 영역은 **독립 빌드 루트**다. 레포 루트에 빌드 파일을 두지 않고, CI도 영역 폴더(`working-directory: client` 등)에서만 빌드한다
- 공통: Action은 **커밋 SHA 고정 + 버전 주석**, 최상단 `permissions: contents: read`, 쓰기 권한은 필요한 job에만, `concurrency`로 이전 실행 취소
- 버전 근거: [ADR 0002](decisions/0002-toolchain-versions.md)

---

## 8. 관리자가 한 번 실행할 원격 설정

> 아래 명령은 **레포 관리자가 직접** 실행한다 (Claude는 실행하지 않는다). `gh auth login`이 끝난 상태에서, 순서대로.
> `OWNER_REPO`는 그대로 `yamada-studio/Tabiji`.

### 8-1. 머지 방식 — squash만, PR 제목을 커밋 제목으로, 머지 후 브랜치 삭제

```bash
gh api --method PATCH repos/yamada-studio/Tabiji \
  -F allow_squash_merge=true \
  -F allow_merge_commit=false \
  -F allow_rebase_merge=false \
  -f squash_merge_commit_title=PR_TITLE \
  -f squash_merge_commit_message=COMMIT_MESSAGES \
  -F delete_branch_on_merge=true
```

클릭 경로: Settings → General → Pull Requests → "Allow squash merging"만 체크, Default commit message = "Pull request title and commit details", "Automatically delete head branches" 체크.

### 8-2. Actions 권한 — 기본 `GITHUB_TOKEN` 읽기 전용

```bash
gh api --method PUT repos/yamada-studio/Tabiji/actions/permissions/workflow \
  -f default_workflow_permissions=read \
  -F can_approve_pull_request_reviews=false
```

클릭 경로: Settings → Actions → General → Workflow permissions → "Read repository contents and packages permissions", "Allow GitHub Actions to create and approve pull requests" 해제.

### 8-3. 라벨 생성

```bash
R=yamada-studio/Tabiji
gh label create "area:client"   --repo $R --color 1D76DB --description "client/" --force
gh label create "area:server"   --repo $R --color 0E8A16 --description "server/" --force
gh label create "area:ai"       --repo $R --color 5319E7 --description "ai/" --force
gh label create "area:api"      --repo $R --color FBCA04 --description "api/ (contract)" --force
gh label create "area:design"   --repo $R --color D876E3 --description "design/" --force
gh label create "area:ci"       --repo $R --color 000000 --description ".github/ workflows" --force
gh label create "type:feat"     --repo $R --color A2EEEF --description "New feature" --force
gh label create "type:fix"      --repo $R --color D73A4A --description "Bug fix" --force
gh label create "type:docs"     --repo $R --color 0075CA --description "Documentation only" --force
gh label create "type:chore"    --repo $R --color CFD3D7 --description "Build, deps, housekeeping" --force
gh label create "type:refactor" --repo $R --color C5DEF5 --description "No behavior change" --force
gh label create "priority:high" --repo $R --color B60205 --description "Do first" --force
gh label create "priority:low"  --repo $R --color C2E0C6 --description "Whenever" --force
gh label create "status:blocked" --repo $R --color E99695 --description "Waiting on something" --force
```

기본 라벨(`bug`, `enhancement` 등)은 남겨도 되고 지워도 된다 (`gh label delete <name> --repo $R --yes`).

### 8-4. `main` 브랜치 ruleset

PR 필수, squash만, force push·삭제 금지, 코드 오너 리뷰 필수. 관리자는 **PR 안에서만** 우회 가능(4절의 셀프 머지 문제 때문).

```bash
gh api --method POST repos/yamada-studio/Tabiji/rulesets --input - <<'EOF'
{
  "name": "main",
  "target": "branch",
  "enforcement": "active",
  "conditions": { "ref_name": { "include": ["~DEFAULT_BRANCH"], "exclude": [] } },
  "bypass_actors": [
    { "actor_id": 5, "actor_type": "RepositoryRole", "bypass_mode": "pull_request" }
  ],
  "rules": [
    { "type": "deletion" },
    { "type": "non_fast_forward" },
    {
      "type": "pull_request",
      "parameters": {
        "required_approving_review_count": 0,
        "dismiss_stale_reviews_on_push": true,
        "require_code_owner_review": true,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false,
        "allowed_merge_methods": ["squash"]
      }
    }
  ]
}
EOF
```

- `actor_id: 5` + `RepositoryRole` = 레포 Admin 역할
- 클릭 경로: Settings → Rules → Rulesets → New branch ruleset → Target: Default branch → "Restrict deletions", "Block force pushes", "Require a pull request before merging"(Require review from Code Owners, Allowed merge methods: Squash) → Bypass list에 Repository admin 추가 후 "For pull requests only"

#### 필수 상태 체크(`client-ci`)는 아직 걸지 않는다

`client-ci`는 `paths: ['client/**', …]` 필터가 있어 **`client/`를 안 건드리는 PR에서는 아예 실행되지 않는다.** GitHub는 경로 필터로 건너뛴 워크플로의 체크를 "Expected — Waiting for status"로 남겨 두므로, 이를 필수로 걸면 서버·AI·문서 PR이 영원히 머지되지 않는다.

지금은 "PR 머지 전 CI 초록 확인"을 팀 약속으로 지키고, 필수 체크가 필요해지면 아래 둘 중 하나를 `ci/` PR로 진행한다.

1. `client-ci`의 `paths` 필터를 빼고, job 안에서 `client/` 변경 여부를 판별해 빌드 단계만 건너뛰게 바꾼다 (체크는 항상 보고됨) → 그 뒤 아래 명령으로 필수 체크 추가
2. 영역별 CI 결과를 모아 항상 보고하는 `ci-gate` job을 하나 두고 그것만 필수로 건다

```bash
# 위 1 또는 2를 적용한 뒤에만 실행. RULESET_ID는 `gh api repos/yamada-studio/Tabiji/rulesets`로 확인
gh api --method PUT repos/yamada-studio/Tabiji/rulesets/RULESET_ID --input - <<'EOF'
{
  "rules": [
    { "type": "deletion" },
    { "type": "non_fast_forward" },
    {
      "type": "pull_request",
      "parameters": {
        "required_approving_review_count": 0,
        "dismiss_stale_reviews_on_push": true,
        "require_code_owner_review": true,
        "require_last_push_approval": false,
        "required_review_thread_resolution": false,
        "allowed_merge_methods": ["squash"]
      }
    },
    {
      "type": "required_status_checks",
      "parameters": {
        "strict_required_status_checks_policy": false,
        "do_not_enforce_on_create": false,
        "required_status_checks": [ { "context": "client-ci", "integration_id": 15368 } ]
      }
    }
  ]
}
EOF
```

(`integration_id: 15368` = GitHub Actions 앱)

#### 대안 — 관리자 우회를 쓰지 않으려면

`require_code_owner_review`를 `false`로 두고 CODEOWNERS는 "리뷰어 자동 지정" 용도로만 쓴다. `api/`·`.github/`·루트 공용 파일 PR의 상대 승인은 팀 약속으로 지킨다.

### 8-5. 태그 ruleset — `*-v*` 태그는 관리자만 생성·수정·삭제

```bash
gh api --method POST repos/yamada-studio/Tabiji/rulesets --input - <<'EOF'
{
  "name": "release-tags",
  "target": "tag",
  "enforcement": "active",
  "conditions": { "ref_name": { "include": ["refs/tags/*-v*"], "exclude": [] } },
  "bypass_actors": [
    { "actor_id": 5, "actor_type": "RepositoryRole", "bypass_mode": "always" }
  ],
  "rules": [
    { "type": "creation" },
    { "type": "update" },
    { "type": "deletion" }
  ]
}
EOF
```

클릭 경로: Settings → Rules → Rulesets → New tag ruleset → Target tags: Include by pattern `*-v*` → "Restrict creations", "Restrict updates", "Restrict deletions" → Bypass list: Repository admin (Always).

### 8-6. Release용 Secrets (Android 서명)

없어도 Release CI는 성공한다. 이 경우 Android 산출물 없이 Desktop만 올라가고 노트에 "Android 서명 빌드 미포함"이 붙는다.

키스토어 만들기 (**레포 밖**에 보관, 절대 커밋 금지):

```bash
keytool -genkeypair -v \
  -keystore ~/secure/tabiji-release.jks \
  -alias tabiji \
  -keyalg RSA -keysize 4096 -validity 10000
```

등록:

```bash
R=yamada-studio/Tabiji
base64 -w0 ~/secure/tabiji-release.jks | gh secret set TABIJI_KEYSTORE_BASE64 --repo $R
gh secret set TABIJI_KEYSTORE_PASSWORD --repo $R   # 프롬프트에 입력
gh secret set TABIJI_KEY_ALIAS         --repo $R   # 예: tabiji
gh secret set TABIJI_KEY_PASSWORD      --repo $R
gh secret list --repo $R
```

> 키스토어를 잃어버리면 같은 앱으로 업데이트를 낼 수 없다. 파일과 비밀번호를 팀 비밀 저장소(1Password 등)에 백업한다.

### 8-7. 확인

```bash
gh api repos/yamada-studio/Tabiji --jq '{squash: .allow_squash_merge, merge: .allow_merge_commit, rebase: .allow_rebase_merge, delete_branch: .delete_branch_on_merge}'
gh api repos/yamada-studio/Tabiji/actions/permissions/workflow
gh api repos/yamada-studio/Tabiji/rulesets --jq '.[] | {id, name, target, enforcement}'
gh label list --repo yamada-studio/Tabiji
```
