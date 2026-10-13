# A0 — 개발 환경과 첫 PR

> 브랜치: 부트스트랩 PR(`repo/chore-bootstrap-monorepo`)에 이어서 작업한다 · 커밋 footer `Refs: A0`

## 목표

Ubuntu 24.04에서 **Desktop과 Android를 빌드할 수 있는 환경**을 갖추고, **GitHub PR 흐름을 한 바퀴** 돌린다.
이 단계에서는 Kotlin 코드를 쓰지 않는다. 대신 이 프로젝트가 어떻게 생겼는지(모듈·소스셋·래퍼)를 눈으로 익힌다.

## 먼저 읽을 것 (15분)

- [`client/docs/architecture.md`](../architecture.md) — "지금의 모듈 구조"까지
- [Kotlin Multiplatform 프로젝트 구조 기초](https://kotlinlang.org/docs/multiplatform/multiplatform-discover-project.html) — 타깃, 소스셋
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) — 왜 `gradle` 대신 `./gradlew`를 쓰는가

### 개념 한 장 요약

| 개념 | 한 줄 설명 | 빗대면 |
| --- | --- | --- |
| Gradle 래퍼 (`gradlew`) | 레포에 고정된 Gradle 버전(9.7.1)을 자동으로 받아 실행하는 스크립트. 누구나 같은 버전으로 빌드 | CMake 최소 버전을 강제하고, 없으면 자동 설치까지 해 주는 셈 |
| 모듈 (`shared`, `androidApp`, `desktopApp`) | Gradle 하위 프로젝트. `settings.gradle.kts`의 `include(...)` | `add_subdirectory` |
| 타깃 (`android {}`, `jvm()`) | `shared`를 어떤 플랫폼용으로 컴파일할지 | 툴체인 파일 / 크로스 컴파일 타깃 |
| 소스셋 (`commonMain`, `androidMain`, `jvmMain`) | 어떤 타깃에 들어갈 소스인지 나눈 폴더 | `if(ANDROID)` 같은 조건부 소스 목록 |
| `libs.versions.toml` | 모든 라이브러리·플러그인 버전 한 곳 | `find_package` 버전을 한 파일에 모아 둔 것 |

## 사용자가 만들 파일

| 파일 | 커밋? | 메모 |
| --- | --- | --- |
| `client/gradlew`, `client/gradlew.bat`, `client/gradle/wrapper/gradle-wrapper.jar` | **커밋한다** | 래퍼 생성으로 만들어진다. `.jar`도 커밋 대상이다 (CI가 `wrapper-validation`으로 진짜 Gradle jar인지 검증) |
| `client/local.properties` | **커밋 금지** | 내 PC의 SDK 경로. `.gitignore`에 이미 있음 |

`.kt` 파일은 이 단계에서 만들지 않는다.

---

## 1. 지금 상태 확인

```bash
cd ~/ws/src/Tabiji/client
scripts/linux/check-env.sh
```

현재 PC 기준 예상: JDK(21)는 OK, `ANDROID_HOME`·`adb`·`gh`는 FAIL. 아래 순서대로 채운다.

## 2. JDK 17 이상

이미 OpenJDK 21이 있으면 건너뛴다. 없으면:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

> CI는 Temurin 17로 빌드한다. 로컬 21은 문제없다 (Gradle 9.x는 JVM 17~27에서 실행).

## 3. Android SDK — cmdline-tools 단독 설치 (권장)

Android Studio 없이 명령줄 도구만 설치한다. (Android Studio를 이미 쓰고 있다면 SDK Manager로 SDK만 관리하고 4절의 `ANDROID_HOME`만 맞추면 된다.)

```bash
# 1) 다운로드 (2026-10-13 기준 최신. 바뀌었으면 https://developer.android.com/studio#command-line-tools-only 에서 확인)
mkdir -p ~/Android/Sdk/cmdline-tools
cd /tmp
wget https://dl.google.com/android/repository/commandlinetools-linux-15859902_latest.zip

# 2) 압축 해제 → 반드시 cmdline-tools/latest/ 구조로 둔다 (sdkmanager가 이 경로를 기대한다)
unzip -q commandlinetools-linux-15859902_latest.zip -d /tmp/cmdline-tools-unzip
mv /tmp/cmdline-tools-unzip/cmdline-tools ~/Android/Sdk/cmdline-tools/latest
```

## 4. 환경 변수 (`~/.bashrc`에 직접 추가)

```bash
# ~/.bashrc 맨 아래에 추가
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"
```

```bash
source ~/.bashrc
sdkmanager --version
```

## 5. SDK 패키지와 라이선스

```bash
# 라이선스 동의 (모두 y). 동의해 두면 Gradle이 빌드 때 필요한 platform·build-tools를 스스로 받는다
sdkmanager --licenses

# 기기 연결 도구, 에뮬레이터, 에뮬레이터용 시스템 이미지 (Android 17 = API 37, x86_64)
sdkmanager "platform-tools" "emulator" "system-images;android-37.0;google_apis;x86_64"
```

`compileSdk = 37`에 필요한 `platforms;…`·`build-tools;…`는 직접 고르지 않아도 된다. 첫 빌드에서 AGP가 맞는 패키지를 내려받는다 (라이선스 동의가 돼 있을 때).

## 6. 에뮬레이터(AVD) 또는 실기기

### 에뮬레이터

```bash
# KVM(하드웨어 가속) 권한 확인. "OK"가 아니면 아래 usermod 후 재로그인
[ -r /dev/kvm ] && [ -w /dev/kvm ] && echo OK || echo "no access"
# 필요할 때만:
sudo usermod -aG kvm "$USER"

# AVD 생성 (이름: tabiji-api37). "custom hardware profile?" 질문에는 Enter(no)
avdmanager create avd -n tabiji-api37 -k "system-images;android-37.0;google_apis;x86_64"
# 특정 기기 모양을 원하면: avdmanager list device 로 id를 찾아 -d <id> 를 붙인다

# 실행 (별도 터미널, 창이 뜨고 부팅까지 1~2분)
emulator -avd tabiji-api37
```

### 실기기

기기에서 개발자 옵션 → USB 디버깅을 켜고 연결한 뒤 기기에 뜨는 허용 창을 승인한다.

```bash
adb devices   # "device" 상태로 한 줄 보이면 OK
```

## 7. GitHub CLI

공식 저장소에서 설치한다 ([cli/cli 설치 문서](https://github.com/cli/cli/blob/trunk/docs/install_linux.md)):

```bash
(type -p wget >/dev/null || (sudo apt update && sudo apt install wget -y)) \
  && sudo mkdir -p -m 755 /etc/apt/keyrings \
  && out=$(mktemp) && wget -nv -O$out https://cli.github.com/packages/githubcli-archive-keyring.gpg \
  && cat $out | sudo tee /etc/apt/keyrings/githubcli-archive-keyring.gpg > /dev/null \
  && sudo chmod go+r /etc/apt/keyrings/githubcli-archive-keyring.gpg \
  && sudo mkdir -p -m 755 /etc/apt/sources.list.d \
  && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null \
  && sudo apt update \
  && sudo apt install gh -y

gh auth login          # GitHub.com → HTTPS → 브라우저 로그인. "Authenticate Git with your GitHub credentials?" → Yes
gh auth status
```

`gh auth login`에서 Git 자격 증명 연동을 켜면 `git push`도 같은 인증을 쓴다. (현재 이 PC는 GitHub에 SSH 키도 HTTPS 자격 증명도 없어 push가 안 되는 상태다.)

## 8. 다시 환경 확인

```bash
cd ~/ws/src/Tabiji/client
scripts/linux/check-env.sh    # 네 줄 모두 [ OK ], 마지막 "모두 OK"
```

## 9. VS Code

1. 레포 **루트**(`~/ws/src/Tabiji`)를 연다: `code ~/ws/src/Tabiji`
2. 오른쪽 아래 "권장 확장 설치" 알림 → 설치. 또는 확장 패널에서 `@recommended` 검색 ([`.vscode/extensions.json`](../../../.vscode/extensions.json))
   - Kotlin by JetBrains (`JetBrains.kotlin-server`), Gradle for Java, EditorConfig, GitHub Actions, YAML
3. Kotlin 확장이 `client/settings.gradle.kts`를 감지해 Gradle 프로젝트를 가져오는지 본다 (상태 표시줄·출력 패널 "Kotlin"). 래퍼 생성(10절) 후에 가져오기가 성공한다
4. Terminal → Run Task… 에 `client: …` 태스크들이 보이면 OK ([`.vscode/tasks.json`](../../../.vscode/tasks.json))

### 미리 알아 둘 한계

- Kotlin 확장(Kotlin LSP)은 **Alpha**이고, 공식 README에 "**KMP 프로젝트 지원은 향후 릴리스 예정**"이라고 적혀 있다 ([Kotlin/kotlin-lsp](https://github.com/Kotlin/kotlin-lsp))
- 그래서 `shared/src/commonMain`에서 빨간 줄·자동완성 누락·정의 이동 실패가 생길 수 있다
- **판단 기준은 Gradle 빌드 결과다.** 에디터에만 빨간 줄이 있고 `scripts/linux/build.sh`가 통과하면 코드 문제가 아니다
- Compose Preview는 VS Code에 없다. A1부터 Desktop 실행 + **Compose Hot Reload**(`run-desktop.sh --hot`)로 화면을 본다

## 10. Gradle 래퍼 생성 (`client/`)

래퍼를 만들려면 Gradle이 한 번 설치돼 있어야 한다. SDKMAN으로 **사용자 홈에만** 설치한다 (시스템 전역 설치 아님).

```bash
# SDKMAN 설치 (처음 한 번. ~/.sdkman 에 설치되고 ~/.bashrc 에 초기화 줄이 추가된다)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install gradle 9.7.1
gradle --version          # Gradle 9.7.1
```

```bash
cd ~/ws/src/Tabiji/client
git switch repo/chore-bootstrap-monorepo

gradle wrapper \
  --gradle-version 9.7.1 \
  --distribution-type bin \
  --gradle-distribution-sha256-sum acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a
```

- 이 명령은 `client/` 빌드 설정을 한 번 읽는다(플러그인 다운로드 때문에 처음엔 몇 분 걸린다). `ANDROID_HOME`이 없으면 여기서 실패한다
- `git diff gradle/wrapper/gradle-wrapper.properties`로 확인: 버전·체크섬이 같으면 그대로 둔다 (줄 몇 개가 추가되는 정도는 정상)
- 설정 단계에서 실패하면 **로그를 그대로 붙여넣어 주세요**. 원인을 같이 본다

```bash
./gradlew --version       # "Gradle 9.7.1" 이 나오면 래퍼 OK
```

### `local.properties` (선택, 커밋 금지)

AGP는 `ANDROID_HOME`을 읽으므로 없어도 된다. IDE가 만들거나 경로를 명시하고 싶으면:

```bash
echo "sdk.dir=$HOME/Android/Sdk" > ~/ws/src/Tabiji/client/local.properties
```

**커밋하지 않는 이유**: 내 PC의 절대 경로라 다른 사람·CI에서는 틀린 값이다. 이미 `.gitignore`에 있으므로 `git status`에 보이면 안 된다.

## 11. 래퍼를 부트스트랩 PR에 올리기

```bash
cd ~/ws/src/Tabiji
git status                                   # gradlew, gradlew.bat, gradle-wrapper.jar 가 보여야 한다. local.properties 는 안 보여야 한다
git add client/gradlew client/gradlew.bat client/gradle/wrapper/
git update-index --chmod=+x client/gradlew   # 실행 권한 기록 (이미 있으면 변화 없음)
git commit -m "chore(client): add Gradle wrapper 9.7.1" -m "Refs: A0"
git push
```

- (부트스트랩 PR이 아직 안 열렸다면: 최종 응답의 "push·PR 열기" 순서를 먼저 따른다)
- PR의 Checks 탭에서 `client-ci`가 **"래퍼 있음, 빌드 건너뜀"** 안내와 함께 초록이 되는지 본다. `Validate Gradle wrapper` 단계가 통과해야 한다
- `labeler`는 `area:*` 라벨이 없으면 PR에 붙이면서 **자동으로 만든다**(색은 기본 회색, 설명 없음). 12절의 라벨 생성 명령은 `--force`라서 이미 있는 라벨의 색·설명을 덮어써 정리해 준다

## 12. GitHub 원격 설정 적용

[`docs/github.md`](../../../docs/github.md) 8절을 순서대로 실행한다 (레포 관리자 권한 필요):

1. 8-1 머지 방식 (squash만)
2. 8-2 Actions 권한 (읽기 전용)
3. 8-3 라벨 생성
4. 8-4 `main` ruleset (코드 오너 리뷰 + 관리자 PR 우회)
5. 8-5 태그 ruleset
6. 8-6 Android 서명 시크릿 — **지금은 건너뛰어도 된다** (A9 릴리스 전까지)
7. 8-7 확인 명령

그다음 부트스트랩 PR을 **squash merge**한다. PR 제목이 `main`의 첫 커밋 메시지가 된다.

## 13. (선택) Claude Code Kotlin LSP 플러그인

Claude Code가 Kotlin 진단·정의 이동을 쓰게 하는 **공식 플러그인**이 있다 ([Claude Code — code intelligence](https://code.claude.com/docs/en/plugins/code-intelligence.md)).

```bash
# 1) kotlin-lsp 바이너리 설치 (플러그인에 포함돼 있지 않다). https://github.com/Kotlin/kotlin-lsp/releases 에서 standalone zip 다운로드 후
unzip kotlin-lsp-*.zip -d ~/opt/kotlin-lsp
chmod +x ~/opt/kotlin-lsp/kotlin-lsp.sh
mkdir -p ~/.local/bin && ln -s ~/opt/kotlin-lsp/kotlin-lsp.sh ~/.local/bin/kotlin-lsp
which kotlin-lsp
```

2) 플러그인 설치 — **어디서 Claude Code를 쓰는지에 따라 다르다**

| 환경 | 명령 |
| --- | --- |
| **VS Code 확장** (이 프로젝트 기본) | `/plugin` 슬래시 명령을 지원하지 않는다("/plugin isn't available in this environment"). **VS Code 터미널에서 CLI로** 설치한다 ↓ |
| 터미널의 `claude` | 대화창에서 `/plugin install kotlin-lsp@claude-plugins-official` |

```bash
# VS Code 터미널에서 (사용자 범위 ~/.claude 에 설치된다)
claude plugin marketplace list                              # claude-plugins-official 이 있는지 확인
# 없으면: claude plugin marketplace add anthropics/claude-plugins-official
claude plugin install kotlin-lsp@claude-plugins-official
claude plugin list                                          # kotlin-lsp 가 보이면 OK
```

3) **Claude Code 세션을 새로 시작**해야 플러그인이 로드된다 (VS Code의 Claude 패널에서 새 대화)

- `--scope project`로 설치하지 않는다. 레포의 `.claude/settings.json`에 들어가 팀원 전원에게 적용되는데, 각자 바이너리를 설치해야 하므로 맞지 않다
- 동작 확인은 `.kt` 파일이 생기는 **A1부터** 가능하다 (지금은 진단할 Kotlin 파일이 없다)
- 같은 Kotlin LSP라서 **KMP 한계도 같다** (`commonMain` 진단이 불완전할 수 있음). 마켓플레이스 설명도 "JVM 전용 Kotlin Gradle 프로젝트에 최적화"
- 메모리를 많이 쓰면 `claude plugin disable kotlin-lsp@claude-plugins-official`
- 없어도 학습 루프에는 지장이 없다. 진단 기준은 언제나 Gradle 빌드 출력이다

---

## 완료 조건 (모두 눈으로 확인)

- [ ] `scripts/linux/check-env.sh` → 네 줄 모두 `[ OK ]`, "모두 OK"
- [ ] `client/`에서 `./gradlew --version` → **Gradle 9.7.1**
- [ ] `adb devices`에 에뮬레이터 또는 실기기가 `device`로 보임
- [ ] VS Code에 권장 확장 설치, Run Task에 `client: …` 태스크가 보임
- [ ] 부트스트랩 PR에서 `client-ci`가 "래퍼 있음, 빌드 건너뜀"으로 **초록**, `labeler`가 `area:*` 라벨을 붙임
- [ ] `docs/github.md` 8-1~8-5 적용, `8-7` 확인 명령 출력 붙여넣기
- [ ] 부트스트랩 PR **squash merge 완료**

## 앱 실행은 아직 실패하는 게 정상

A0에서 `scripts/linux/run-desktop.sh`나 `install-android.sh`를 돌려 보면 실패하거나 아무것도 안 뜬다. 고장이 아니다.

- **Desktop**: `desktopApp/build.gradle.kts`의 `mainClass = "com.yamada.studio.tabiji.MainKt"`는 **예약만** 해 둔 이름이다. 그 클래스를 만드는 `main.kt`는 A1에서 직접 쓴다. 지금 실행하면 "Could not find or load main class com.yamada.studio.tabiji.MainKt" 류의 오류가 난다
- **Android**: `androidApp/src/main/AndroidManifest.xml`에 `<activity>`가 없다 (A2에서 `MainActivity`와 함께 추가). APK는 만들어져도 런처 아이콘이 없고 실행할 화면이 없다
- **CI**: 같은 이유로 `client/.ci-enabled`가 없어서 빌드 단계를 건너뛴다. **A1을 마칠 때 이 파일을 추가**해 CI 빌드·테스트를 켠다

## 끝나면 붙여넣어 줄 것

1. `scripts/linux/check-env.sh` 출력
2. `./gradlew --version` 출력
3. `adb devices` 출력
4. `docs/github.md` 8-7 확인 명령 출력
5. 부트스트랩 PR 링크 (머지 후)

→ 확인되면 `roadmap.md`의 A0를 완료로 표시하고 A1 가이드를 쓴다.
