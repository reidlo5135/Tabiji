# A1 — 첫 Compose 화면: 인트로의 바탕이 될 "Tabiji" 창

> 브랜치: `client/feat-a1-intro-window` (Claude가 만들어 둠) · 커밋 footer `Refs: A1`
> 이 단계부터 **코드는 직접 손으로 친다.** 이 가이드는 조립 설명서이고, 완성 코드는 주지 않는다.

## 목표

Desktop에서 **제목 표시줄 "Tabiji", 하늘색 배경, 중앙에 "Tabiji" 글자**가 있는 창을 띄운다. 이것이 인트로 화면의 0층(바탕)이다 — A3에서 이 바탕이 사계절 사분면으로, A6에서 일러스트로 바뀐다. 그리고 **Compose Hot Reload**로 "코드 저장 → 즉시 반영" 루프를 몸에 익힌다.

끝 그림: 하늘색(#EAF3FB) 전체 화면 가운데에 진회색 "Tabiji" 한 줄. 그게 전부다. 작지만, 이 한 화면에 Compose의 뼈대 개념이 다 들어 있다.

## 먼저 읽을 것 (20분)

| 문서 | 왜 |
| --- | --- |
| [Compose mental model](https://developer.android.com/develop/ui/compose/mental-model) | "UI를 함수로 선언한다"가 무슨 뜻인지. 오늘의 핵심 |
| [Modifier 개요](https://developer.android.com/develop/ui/compose/modifiers) | 크기·배경·여백을 주는 방법. **순서가 의미를 바꾼다**는 것만 기억 |
| [Desktop 창 관리](https://kotlinlang.org/docs/multiplatform/compose-desktop-top-level-windows-management.html) | `application {}` / `Window()` — 오늘 쓰는 Desktop 진입점 API |
| [Kotlin 기본 문법](https://kotlinlang.org/docs/basic-syntax.html) · [람다](https://kotlinlang.org/docs/lambdas.html) | 아래 "처음 보는 Kotlin 문법" 표의 원문 |

## 처음 보는 Kotlin 문법 (이번 단계에서 만나는 것만)

| 문법 | 한 줄 설명 | C++/Qt로 빗대면 |
| --- | --- | --- |
| 최상위 함수 | 함수가 클래스 밖에 그냥 있다. `fun main()`도 파일에 바로 쓴다 | 자유 함수. `int main()`과 같다 |
| 단일 표현식 함수 `fun f() = 식` | 본문이 한 식이면 `{ return … }` 대신 `=` | `auto f() { return expr; }` 축약 |
| trailing lambda | 마지막 인자가 람다면 괄호 밖으로 뺀다: `Window(...) { ... }` | `connect(btn, &clicked, [&]{ ... })`의 람다를 문법이 공식 지원한다고 생각 |
| `::exitApplication` | 함수 자체를 값으로 넘기는 참조 | `&Class::method` 멤버 함수 포인터 |
| named argument | `Window(title = "Tabiji")` — 인자 이름을 붙여 호출 | 지정 초기화자 `.title = "..."` 느낌. Compose는 인자가 많아 필수 습관 |

## Compose 핵심 개념 (오늘 분량)

**`@Composable` 함수 = UI 조각.** 함수를 호출하면 위젯이 "생성"되는 게 아니라, "이 자리에 이런 UI가 있다"고 **선언**된다. Qt Widgets의 `new QLabel(this)` + 레이아웃 조립과 달리, QML에서 `Rectangle { Text { } }`를 적는 쪽에 가깝다. 상태가 바뀌면 프레임워크가 함수를 다시 호출해(recomposition — A4에서 본격적으로) 화면을 갱신한다.

**`Modifier` 체인 = 꾸밈 명령의 순서 있는 목록.** `Modifier.fillMaxSize().background(...)`처럼 점으로 잇는다. QSS + 레이아웃 속성 역할인데, **적은 순서대로 바깥→안으로 적용**된다. A3에서 순서를 직접 바꿔 실험한다. 오늘은 "`fillMaxSize()` 다음 `background()`" 하나만 쓴다.

**Desktop 진입점.** `application { ... }`이 Qt의 `QApplication app; app.exec()` 역할(이벤트 루프 소유)이고, 그 안의 `Window(...) { ... }`가 `QMainWindow`다. `Window`의 trailing lambda 안이 창의 내용물, 즉 우리의 `App()`이다.

**왜 `App()`은 `shared`에 두나.** `App()`은 모든 플랫폼이 공유하는 화면이고(`commonMain`), `main.kt`는 Desktop만의 시동 코드다(`desktopApp`). CMake로 치면 `App()`은 공용 라이브러리 타깃의 소스, `main.kt`는 실행 파일 타깃의 `main.cpp`. A2에서 Android의 `MainActivity`가 **같은 `App()`** 을 부르는 걸 보면 이 분리의 의미가 보인다.

## 만들 파일 (직접 작성)

| 경로 | 내용 |
| --- | --- |
| `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/app/App.kt` | `@Composable fun App()` — 하늘색 배경 + 중앙 글자 |
| `desktopApp/src/main/kotlin/com/yamada/studio/tabiji/main.kt` | `fun main()` — 창을 열고 `App()` 호출 |
| `client/.ci-enabled` | 빈 파일. CI의 빌드·테스트 단계를 켠다 (마지막에) |

> 패키지 선언 주의: `App.kt`는 `package com.yamada.studio.tabiji.app`, `main.kt`는 `package com.yamada.studio.tabiji`. `main.kt`의 패키지가 다르면 빌드 설정의 `mainClass = "com.yamada.studio.tabiji.MainKt"`(파일명 main.kt → 클래스명 MainKt 규칙)와 어긋나 실행이 안 된다.

## 조립 순서

### 1. `App.kt` — 화면

구조는 세 겹이다: **`App()` 함수 → `Box`(배경·정렬) → `Text`(글자)**.

```kotlin
@Composable
fun App() {
    Box(
        modifier = /* 창을 가득 채우고, 배경색 */,
        contentAlignment = /* 내용물을 정중앙에 */,
    ) {
        Text(/* 글자, 색 */)
    }
}
```

채울 조각들:

- 창 가득: `Modifier.fillMaxSize()`, 배경: `.background(Color(0xFFEAF3FB))` — `0xAARRGGBB` 순서라 **불투명이면 FF**를 앞에 붙인다
- 정중앙: `Alignment.Center`
- 글자: `Text("Tabiji", color = Color(0xFF1C2633))`
- 두 색은 핸드오프 토큰 `bg.sky.top`·`text.primary`의 라이트 값이다. **A5에서 토큰으로 옮길 것이므로 두 줄에 `// TODO(A5): TabijiTheme 토큰으로` 주석**을 달아 둔다 (conventions의 하드코딩 금지 규칙에 대한 한시적 예외)

LSP가 자동완성을 못 해 줄 수 있으니 import를 직접 적는다:

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
```

### 2. `main.kt` — 시동

창 관리 문서의 기본 패턴 그대로다. 필요한 부품: `application { }` · `Window(onCloseRequest = ::exitApplication, title = "Tabiji") { }` · 그 안에서 `App()` 호출. `fun main()`을 단일 표현식(`=`)으로 쓰면 세 부품이 6줄 안에 들어간다.

```kotlin
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yamada.studio.tabiji.app.App
```

`onCloseRequest`는 "창의 X 버튼이 눌리면 무엇을 할까"다. `::exitApplication`(application 스코프가 주는 함수)을 넘기면 창 닫기 = 앱 종료가 된다 — Qt의 `QApplication::quit` 슬롯에 connect하는 것과 같다.

### 3. 실행

```bash
cd ~/ws/src/Tabiji/client
scripts/linux/run-desktop.sh
```

- **첫 실행은 5~10분 걸릴 수 있다** — Compose·Skia 의존성 수백 MB를 내려받는다. 두 번째부터는 수십 초
- 하늘색 창이 뜨면 성공. 안 뜨면 출력 전체를 붙여넣는다 (같이 진단)

### 4. Hot Reload 체험 (이 단계의 백미)

```bash
scripts/linux/run-desktop.sh --hot
```

- 첫 `--hot` 실행은 JetBrains Runtime(JBR)을 추가로 내려받는다 (foojay 플러그인이 자동으로)
- 앱이 뜬 상태에서 `App.kt`의 `"Tabiji"`를 `"旅路"`로 바꾸고 **저장** → 앱을 끄지 않았는데 글자가 바뀌는 것을 확인한다. QML 핫리로드처럼, 이후 모든 화면 작업의 기본 루프다
- 확인했으면 글자는 다시 `"Tabiji"`로

### 5. 테스트·빌드 확인과 CI 켜기

```bash
scripts/linux/build.sh          # Android 디버그 + Desktop 컴파일 + 테스트
touch .ci-enabled               # client/ 에서. CI 빌드 단계 활성화
```

`build.sh`의 Android 빌드가 처음이면 AGP가 platform·build-tools를 내려받는다 (A0에서 라이선스 동의를 해 둬서 자동).

## 흔한 오류와 원인 읽기

| 증상 | 원인일 가능성 |
| --- | --- |
| `Unresolved reference: App` | `main.kt`에 `App` import 누락, 또는 `App.kt` 패키지 선언 오타 |
| `Could not find or load main class ...MainKt` | `main.kt` 패키지가 `com.yamada.studio.tabiji`가 아님, 또는 파일명이 main.kt가 아님 |
| `@Composable invocations can only happen from ...` | `@Composable` 함수를 일반 함수에서 호출 — `App()`은 `Window { }` 람다 안에서만 |
| 색이 안 보이고 검정/흰 화면 | `Color(0xEAF3FB)`처럼 알파(FF) 누락 → 투명색이 됨 |
| IDE만 빨간 줄, 빌드는 성공 | Kotlin LSP의 KMP 한계 (A0 가이드 9절). **빌드 출력이 기준** |

## 완료 조건 (모두 눈으로)

- [ ] `run-desktop.sh` → 제목 표시줄 "Tabiji", 하늘색 배경, 중앙 "Tabiji" 글자
- [ ] `--hot`으로 띄운 채 문구 수정·저장 → 재시작 없이 반영
- [ ] `build.sh` 초록 (Android 디버그 빌드 포함)
- [ ] `.ci-enabled` 추가 후 PR에서 `client-ci`가 **실제 빌드·테스트까지** 초록

## 끝나면 붙여넣어 줄 것

1. `App.kt`, `main.kt` 내용 (리뷰용)
2. `run-desktop.sh` 마지막 20줄 + 창 스크린샷
3. `build.sh` 마지막 10줄

→ 진단 통과 후 Claude가 커밋(`feat(client): render intro base window on desktop`, `Refs: A1`)·push 하고, CI 확인 후 사용자가 squash merge. 그다음 A2(Android 진입점)로 간다.
