# A10 — Phase A 릴리스: `client-v0.1.0`

> 브랜치: `client/chore-release-0-1-0` (Claude가 만들어 둠, A9 PR #14 위에 쌓음) · 커밋 footer `Refs: A10`
> 절차 원본: 루트 `docs/github.md` 6절 "태그 · 버전 · 릴리스", 워크플로 `.github/workflows/client-release.yml`
> 이번 단계는 Kotlin 코드를 쓰지 않는다. **설치 파일을 만들고, 설치해 보고, 태그로 공개하는** 단계다

## 목표

Phase A(인트로 화면)를 첫 버전 **0.1.0**으로 공개한다.

- GitHub **Releases**에 `client-v0.1.0`이 prerelease로 올라간다
- 첨부 파일은 Linux `.deb`, Windows `.msi`, macOS `.dmg`, 그리고 `SHA256SUMS.txt`(무결성 확인용)다
- 내려받은 `.deb`을 Ubuntu에 **설치해서 앱 목록에서 실행**하면 인트로가 뜬다

Android 설치 파일은 이번에 포함되지 않는다. 서명 키가 아직 없고, Android 실행 확인은 Phase M에서 하기 때문이다. Release CI는 이 경우에도 실패하지 않고 노트에 "Android 서명 빌드 미포함"을 붙인다.

## 먼저 읽을 것 (20분)

| 문서 | 왜 |
| --- | --- |
| 루트 `docs/github.md` 6절 | 우리 레포의 릴리스 규칙. 이 가이드의 원본 |
| [Semantic Versioning](https://semver.org/lang/ko/) | `MAJOR.MINOR.PATCH`의 뜻, 1.0 전 버전의 의미 |
| [Keep a Changelog](https://keepachangelog.com/ko/1.1.0/) | `CHANGELOG.md` 형식 |
| [Compose Desktop 배포](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html) | `nativeDistributions`로 `.deb`·`.msi`·`.dmg`를 만드는 원리 |

---

## 1. 핵심 개념

### 1-1. 버전 번호 — 왜 0.1.0인가

`MAJOR.MINOR.PATCH` 세 숫자다. 이 프로젝트의 규칙(`docs/github.md`)은 이렇다.

| 숫자 | 언제 올리나 | 예 |
| --- | --- | --- |
| MINOR | **Phase 하나를 마칠 때** | Phase A → `0.1.0`, Phase B → `0.2.0` |
| PATCH | 릴리스 후 버그만 고칠 때 | `0.1.1` |
| MAJOR | 1.0 = 실제 사용자에게 내놓을 첫 정식판 | 아직 없음 |

`0.x`는 "아직 개발 중"이라는 뜻이라 GitHub에서 **prerelease**로 표시된다. Release CI가 첫 숫자가 0인지 보고 자동으로 정한다. npm 패키지의 `0.x` 버전과 같은 관례다.

### 1-2. 버전은 한 곳에만 있다 — `tabiji.version`

앱 버전은 `client/gradle.properties`의 `tabiji.version` **한 줄**에서만 정한다. 나머지는 전부 여기서 계산된다.

```
client/gradle.properties   tabiji.version=0.1.0
  ├── Desktop 설치 파일 버전 (desktopApp/build.gradle.kts → packageVersion)
  │     └── macOS만 1.1.0으로 바꿔 씀 (jpackage가 0으로 시작하는 버전을 거부, ADR 0002)
  ├── Android versionName = 0.1.0, versionCode = 100 (androidApp/build.gradle.kts)
  └── Release CI가 태그 client-v0.1.0과 같은지 검사 → 다르면 실패
```

React 프로젝트에서 `package.json`의 `"version"`이 단일 원본인 것과 같다. 태그와 이 값이 어긋나면 CI가 막아 준다.

### 1-3. 릴리스 노트는 `CHANGELOG.md`에서 나온다

루트 `CHANGELOG.md`의 `## Client` 아래 `### [0.1.0] - 날짜` 섹션 내용이 **GitHub Release 본문**으로 그대로 들어간다. 그래서 형식이 정해져 있다 (`CHANGELOG.md` 맨 위 "작성 규칙"). 섹션 제목을 바꾸거나 비워 두면 Release CI가 실패한다.

### 1-4. 태그 하나가 릴리스를 시작한다

`client-v0.1.0` 태그를 GitHub에 push하면 `client-release` 워크플로가 돈다.

```
git push origin client-v0.1.0
        │
        ▼
client-release
├── verify   태그 형식 · tabiji.version 일치 · CHANGELOG 섹션 추출 (실패하면 여기서 멈춤)
├── desktop  Ubuntu · Windows · macOS 러너가 각자 자기 설치 파일을 만듦 (동시에)
│            → Tabiji-0.1.0-linux.deb / -windows.msi / -macos.dmg
├── android  서명 키 시크릿이 없으면 건너뜀 (실패 아님)
└── publish  파일을 모아 SHA256SUMS.txt를 만들고 GitHub Release를 공개 (prerelease)
```

설치 파일은 **각 OS에서만** 만들 수 있다 (jpackage가 크로스 빌드를 못 한다). 그래서 CI가 OS 세 대를 동시에 띄운다. 로컬 Ubuntu에서는 `.deb`만 만들 수 있다.

태그는 **관리자만** 만들 수 있게 막혀 있다 (`docs/github.md` 8-5). 레포 관리자인 본인이 직접 push한다. Claude는 명령만 안내한다.

### 1-5. release 빌드는 코드를 줄인다 — 그래서 로컬에서 먼저 설치해 본다

설치 파일용 빌드(`packageReleaseDistributionForCurrentOS`)는 **ProGuard**로 쓰이지 않는 코드를 지우고 이름을 줄인다. 웹 번들러의 tree-shaking·minify와 같은 일이다. 지금까지 `run-desktop.sh`로 띄운 건 이 과정을 거치지 않은 개발 빌드였다.

그래서 개발 빌드에서는 멀쩡해도 release 빌드에서만 리소스를 못 찾거나 실행 직후 꺼지는 일이 생길 수 있다. **태그를 찍기 전에 로컬에서 `.deb`을 만들어 설치해 보는 것**이 이번 단계의 핵심 확인이다. 태그 후에 문제가 드러나면 태그를 지우고 다시 찍어야 해서 번거롭다.

### 1-6. 패키지 아이콘

창 안의 아이콘(A6 `Window(icon = …)`)과 **설치 패키지 아이콘**은 다르다.

| 아이콘 | 어디에 보이나 | 파일 |
| --- | --- | --- |
| 창 아이콘 | `Alt+Tab`, 창 목록 | `shared/…/composeResources/drawable/app_icon.png` (A6) |
| 패키지 아이콘 | 앱 목록(Activities), 독, 설치 관리자 | `desktopApp/icons/tabiji.png` · `.ico` · `.icns` (이번에 Claude가 추가, handoff-v3 한지 타일) |

A6에서 GNOME 독에 Java 기본 아이콘이 보였던 건, Gradle로 띄운 앱에는 `.desktop` 파일이 없기 때문이었다. 설치한 `.deb`에는 jpackage가 만든 `.desktop` 파일이 들어가서 앱 목록에 旅가 보여야 한다.

---

## 2. Claude가 준비해 둔 것 (이 브랜치)

| 커밋 | 내용 |
| --- | --- |
| `chore(client): add desktop package icons …` | `desktopApp/icons/` (PNG는 핸드오프 원본, ICO·ICNS는 거기서 생성) + `build.gradle.kts`의 OS별 `iconFile` |
| `chore(client): release 0.1.0` | `tabiji.version=0.1.0`, 루트 `CHANGELOG.md`의 `### [0.1.0] - 2026-10-15` |

`CHANGELOG.md`는 루트 공용 파일이라 PR 설명에 이유를 적고 코드 오너 리뷰(관리자 bypass)로 머지한다. 날짜는 실제로 태그를 찍는 날과 다르면 PR에서 고친다.

**PR은 A9(#14)이 머지된 뒤에 연다.** 이 브랜치가 A9 위에 쌓여 있어서다.

---

## 3. 순서 (명령은 직접 실행)

### 3-1. 로컬에서 `.deb` 만들기

```bash
cd ~/ws/src/Tabiji/client
git switch client/chore-release-0-1-0
scripts/linux/package-desktop.sh
```

- 처음에는 ProGuard·jpackage 단계 때문에 몇 분 걸린다
- 끝나면 `.deb` 경로가 출력된다. `desktopApp/build/compose/binaries/main-release/deb/` 아래에 `tabiji_0.1.0-1_amd64.deb` 같은 이름으로 생긴다
- `fakeroot`·`dpkg-deb`가 필요하다. 이 PC에는 이미 있다
- 실패하면 출력 마지막 40줄을 붙여넣는다. ProGuard 경고·오류가 가장 흔하다

### 3-2. 설치하고 실행해 보기

```bash
sudo apt install ./desktopApp/build/compose/binaries/main-release/deb/tabiji_*.deb
```

- 앱 목록(Activities, `Super` 키)에서 **Tabiji**를 찾아 실행한다. 터미널에서도 실행할 수 있다. jpackage는 `/opt/tabiji/` 아래에 설치하니 `ls /opt/tabiji/bin/`으로 실행 파일 이름을 확인한다 (보통 `Tabiji`)
- 확인할 것:
  1. 인트로가 뜬다 (사분면 · 워드마크 · 버튼 3개). 개발 빌드와 똑같아야 한다
  2. 메뉴바 Theme·Debug가 동작한다
  3. **앱 목록과 독에 旅 아이콘**이 보인다
  4. 버튼을 누르면 가라앉는다 (release 빌드에서도 애니메이션이 정상인지)
- 지울 때: `sudo apt remove tabiji`

> **2026-10-15 갱신**: 처음 만든 `.deb`의 아이콘(투명 배경 + 진한 고딕 旅)이 어두운 독에서 거의 안 보여서, 사용자 선택으로 **한지 타일 위 붓글씨 旅(handoff-v3, 디자인 PR #15)** 로 바꿨다. 창 아이콘(`app_icon.png`)과 패키지 아이콘(`desktopApp/icons/`)을 둘 다 교체했으니 3-1·3-2를 **다시** 한다. 아래 표의 두 번째 줄(안 보임)은 이걸로 해결됐다.

**아이콘 관련 결정 두 가지** (보고 알려 주면 된다):

| 보이는 모습 | 원인 | 처리 |
| --- | --- | --- |
| 앱 목록엔 旅인데 **실행 중 독**에는 Java 아이콘 | GNOME이 실행 중인 창과 `.desktop` 파일을 짝짓지 못함 (창의 WM_CLASS가 `.desktop` 이름과 다름) | 0.1.0은 그대로 내고, worklog 열린 일로 남겨 다음 PATCH에서 다룬다 |
| 어두운 독에서 진한 旅가 **잘 안 보임** | 핸드오프 기본 아이콘이 투명 배경 + 진한 글자 | 핸드오프의 대안 `desktop-window-512-halo.png`(흰 테두리)로 바꿀지 결정 → 바꾸면 Claude가 아이콘을 다시 만든다 |

### 3-3. PR 머지

1. A9 PR **#14**를 머지한다 (CI 초록 확인, 관리자 bypass)
2. Claude가 이 브랜치에 `main`을 합치고 **릴리스 PR**을 연다 (제목 `chore(client): release 0.1.0`)
3. 릴리스 PR의 CI 초록을 확인하고 squash merge한다

### 3-4. 태그 찍기 (관리자)

릴리스 PR이 머지된 `main`에서 찍는다.

```bash
cd ~/ws/src/Tabiji
git switch main && git pull --ff-only
git log --oneline -1                       # 맨 위가 "chore(client): release 0.1.0 (#…)"인지 확인
git tag client-v0.1.0
git push origin client-v0.1.0
```

### 3-5. Release CI 지켜보기

```bash
gh run list --workflow client-release --limit 1
gh run watch                                # 방금 시작된 실행을 고르면 단계별 진행이 보인다
```

또는 GitHub 웹의 Actions 탭 → `client-release`. Windows·macOS 러너까지 돌아서 20~40분쯤 걸린다. 실패하면 `gh run view --log-failed` 출력을 붙여넣는다.

### 3-6. 공개된 파일 검증하고 설치하기

```bash
mkdir -p ~/Downloads/tabiji-0.1.0 && cd ~/Downloads/tabiji-0.1.0
gh release download client-v0.1.0 --repo yamada-studio/Tabiji -p '*.deb' -p 'SHA256SUMS.txt'
sha256sum -c SHA256SUMS.txt --ignore-missing      # "Tabiji-0.1.0-linux.deb: OK"가 나와야 한다

sudo apt remove tabiji                             # 3-2에서 설치한 로컬 빌드를 지우고
sudo apt install ./Tabiji-0.1.0-linux.deb          # 공개된 파일로 다시 설치
```

`SHA256SUMS.txt`는 CI가 만든 파일들의 지문이다. 내려받은 파일의 지문이 같으면 전송 중에 깨지거나 바뀌지 않았다는 뜻이다.

---

## 4. 흔한 실패와 원인

| 증상 | 원인 | 처리 |
| --- | --- | --- |
| 3-1에서 ProGuard 오류·경고로 실패 | release 빌드의 코드 축소가 필요한 클래스를 지움 | 출력을 붙여넣으면 Claude가 `proguard-rules.pro` 규칙을 제안한다 (빌드 설정이라 원하면 Claude가 수정) |
| 3-2에서 설치는 되는데 실행하면 바로 꺼짐 | 위와 같은 원인이 실행 시점에 드러남 | 터미널에서 `/opt/tabiji/bin/` 아래 실행 파일을 직접 실행해 오류 출력을 붙여넣는다 |
| 3-2에서 글꼴·이미지가 안 나옴 | release 빌드에서 Compose Resources를 못 찾음 | 위와 같음 |
| CI `verify`: "tabiji.version(…)이 태그 버전(…)과 달라요" | 릴리스 PR이 머지되기 전 커밋에 태그를 찍음 | 태그를 지우고(먼저 Claude에게 확인) 머지된 `main`에서 다시 찍는다 |
| CI `verify`: "'### [0.1.0]' 섹션이 없어요" | `CHANGELOG.md` 제목 형식이 바뀜 | 릴리스 PR에서 형식을 고친다 |
| CI `desktop (macos)`만 실패 | macOS jpackage 제약 (버전·아이콘 형식) | 로그를 붙여넣는다. Linux `.deb`만 먼저 공개할지 결정한다 |
| `git push origin client-v0.1.0`가 거절됨 | 태그 ruleset(관리자 전용) | 관리자 계정으로 push했는지 확인한다 |

태그를 지우는 건 기록을 지우는 일이라 **먼저 Claude와 확인한 뒤에** 한다 (`client/CLAUDE.md` 6장).

---

## 5. 완료 조건

- [ ] 3-1: 로컬 `.deb` 생성 성공
- [ ] 3-2: 설치한 앱이 앱 목록에서 실행되고 인트로·메뉴·버튼 눌림이 개발 빌드와 같음. 아이콘 상태 보고 (앱 목록 / 실행 중 독)
- [ ] 3-3: #14와 릴리스 PR 머지
- [ ] 3-4·3-5: `client-release` 성공, Releases에 `client-v0.1.0` (prerelease) + `.deb`·`.msi`·`.dmg`·`SHA256SUMS.txt`
- [ ] 3-6: `sha256sum -c` 통과, 공개된 `.deb`으로 설치·실행

## 6. 끝나면 붙여넣어 줄 것

1. 3-1 출력 마지막 10줄 (`.deb` 경로)
2. 3-2 스크린샷: 설치한 앱의 인트로, 앱 목록 아이콘, (실행 중) 독
3. 3-5 Release 페이지 링크 또는 `gh release view client-v0.1.0` 출력
4. 3-6 `sha256sum -c` 출력

→ 끝나면 Claude가 roadmap·worklog를 갱신한다 (A10 ✅, Phase A 완료). 그다음은 **Phase B — 앱 구조와 플래너**의 B1(플래너 정적 화면, 아이콘 22종)이다.
