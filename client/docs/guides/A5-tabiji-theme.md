# A5 — 테마: 색을 한 곳에서 내려주기 (`TabijiTheme`)

> 브랜치: `client/feat-a5-tabiji-theme` (Claude가 만들어 둠, A4 PR #9 위에 쌓음) · 커밋 footer `Refs: A5`
> 시각 기준: handoff-v2 `docs/01_tokens.md`, `tokens/tokens.json`의 `themes`, `images/screens/theme_tokens.png`
> 확인은 **Desktop**에서 한다

## 목표

지금 화면 코드에는 색이 `Color(0xFF2A3644)`처럼 **직접 박혀** 있다. 이번 단계에서 이걸 전부 **테마**에서 꺼내 쓰게 바꾼다.

- 핸드오프 v2의 **테마 8종**(cloud · spring · summer · autumn · winter · korea · china · japan)을 코드로 옮긴다. 각 테마는 같은 **색 역할 15개 + 머리띠 색 목록**을 가진다
- 화면 코드는 `TabijiTheme.colors.ink`처럼 **역할 이름**으로만 색을 쓴다
- Desktop 메뉴바에서 테마를 고르면 앱 전체 색이 **240ms 동안 부드럽게** 바뀐다
- 디버그 메뉴로 "토큰 화면"을 켜면 현재 테마의 역할별 색이 이름·hex와 함께 나온다

끝나면 화면 코드(`feature/…`)에 `Color(0x…)` 리터럴이 하나도 없다.

이번 단계에서 새로 배우는 핵심은 하나다. **값을 깊은 자식까지 파라미터 없이 내려주는 방법(CompositionLocal)** 이다. React의 Context와 같다.

> 범위: **색만** 다룬다. 테두리 두께·모서리·간격 같은 치수 토큰은 실제로 쓰는 A8(버튼·패널)에서 같은 방식으로 추가한다. 다크 모드는 v2 범위 밖이다.

## 먼저 읽을 것 (40분)

| 문서 | 왜 |
| --- | --- |
| [CompositionLocal](https://developer.android.com/develop/ui/compose/compositionlocal) | 오늘의 핵심. "언제 쓰고 언제 쓰지 말아야 하나"까지 |
| [Compose의 커스텀 디자인 시스템](https://developer.android.com/develop/ui/compose/designsystems/custom) | `MaterialTheme` 없이 자기 테마를 만드는 공식 패턴. 우리가 만드는 것과 거의 같다 |
| [값 기반 애니메이션 `animate*AsState`](https://developer.android.com/develop/ui/compose/animation/value-based) | 마지막 단계의 크로스페이드 |
| [Kotlin data class](https://kotlinlang.org/docs/data-classes.html) · [object](https://kotlinlang.org/docs/object-declarations.html) · [enum 생성자](https://kotlinlang.org/docs/enum-classes.html#working-with-enum-constants) | 2장 문법의 원문 |
| 핸드오프 `docs/01_tokens.md` | 역할 15개의 뜻, Compose 매핑 예시, **보드 → tokens.json 색 조정 7건** |

---

## 1. 핵심 개념

### 1-1. 왜 테마가 필요한가 — 색에 "이름"을 붙인다

v2 디자인은 색을 **역할(role)** 로 정의한다. `ink`(테두리·글자), `paper`(바탕), `accent`(주요 버튼) 같은 것이다. 테마 8종은 **같은 역할에 다른 색**을 넣은 표다.

| 역할 | cloud | spring | … |
| --- | --- | --- | --- |
| `ink` | `#2A3644` | `#43313A` | … |
| `accent` | `#3E8FC7` | `#C95E7E` | … |

화면 코드가 `Color(0xFF2A3644)`를 직접 쓰면, 테마를 바꿀 때 그 숫자를 찾아 전부 고쳐야 한다. `colors.ink`라고 **역할로** 쓰면 표만 바꿔 끼우면 된다. CSS로 치면 색을 `var(--ink)`로 쓰고, 테마 전환은 `:root`의 변수 값만 바꾸는 것과 같다.

### 1-2. 그 표를 어떻게 깊은 자식까지 전달하나 — CompositionLocal

`IntroScreen` → `SeasonQuadrant`처럼 Composable은 깊게 중첩된다. 맨 위에서 고른 테마 색을 맨 아래까지 **파라미터로 일일이 넘기면** 모든 함수가 `colors` 파라미터를 가져야 한다. React에서 말하는 **prop drilling**이다.

React는 이걸 Context로 푼다. Compose에는 **CompositionLocal**이 있고, 대응이 거의 1:1이다.

| React | Compose | 하는 일 |
| --- | --- | --- |
| `const ThemeCtx = createContext(cloud)` | `val LocalTabijiColors = staticCompositionLocalOf { cloud색 }` | 통로를 만든다. `{ }`는 Provider 밖에서 읽을 때의 기본값 |
| `<ThemeCtx.Provider value={c}>…</ThemeCtx.Provider>` | `CompositionLocalProvider(LocalTabijiColors provides c) { … }` | 이 블록 안의 모든 자손에게 `c`를 공급한다 |
| `useContext(ThemeCtx)` | `LocalTabijiColors.current` | 가장 가까운 Provider의 값을 읽는다 |

A1에서 본 오류 `CompositionLocal LocalFontFamilyResolver not present`를 기억하자. 그것도 같은 장치였다. `Window`가 폰트 리졸버를 Provider로 공급하는데, `App()`을 `Window` 밖에서 불러서 못 찾은 것이다.

**주의: 아무 데나 쓰지 않는다.** CompositionLocal은 "보이지 않는 파라미터"라서 남용하면 어디서 값이 오는지 추적하기 어렵다. 테마·폰트·밀도처럼 **앱 전체가 공유하고 거의 안 바뀌는 것**에만 쓴다. A4의 `selected` 같은 화면 상태는 지금처럼 파라미터로 넘긴다.

### 1-3. 우리 테마의 세 부품 — `MaterialTheme`을 그대로 흉내 낸다

Compose의 `MaterialTheme`은 이름이 같은 두 가지로 되어 있다.

- 함수 `MaterialTheme(colorScheme) { content }`: 공급자(Provider)
- 객체 `MaterialTheme.colorScheme`: 읽는 쪽

우리도 똑같이 만든다.

```
TabijiPalette          enum — 테마 8종. 각 항목이 자기 TabijiColors를 들고 있음 (데이터)
TabijiColors           data class — 역할 15개 + headers (한 테마의 색 표)
LocalTabijiColors      CompositionLocal — 통로

TabijiTheme(palette) { … }   함수 — palette.colors를 통로에 공급 (Provider)
TabijiTheme.colors           객체의 프로퍼티 — 통로에서 현재 색을 읽음 (useContext)
```

> 이름: 핸드오프 `01_tokens.md`는 enum을 `TabijiTheme`, 공급 함수를 `TabijiApp`이라 불렀다. 여기서는 `conventions.md`의 `TabijiTheme.colors.xxx` 표기와 `MaterialTheme` 관례에 맞추려고 enum을 **`TabijiPalette`** 로 바꿨다. 같은 이름의 함수 `TabijiTheme`과 객체 `TabijiTheme`이 공존하는 건 Kotlin에서 허용된다 (`MaterialTheme`이 그렇게 되어 있다).

### 1-4. 테마는 누가 고르나 — 상태는 맨 위에

"현재 테마"도 A4의 `selected`처럼 **상태**다. 이 상태를 바꾸는 UI는 Desktop 메뉴바이고, 메뉴바는 `Window` 안(`main.kt`)에 있다. 그래서 상태도 `main.kt`에 두고 `App`에 파라미터로 내려준다.

```
main.kt   ── var palette (상태), var showTokens (상태)
  │ MenuBar: 라디오 항목 클릭 → palette = …     (이벤트가 상태를 바꿈)
  ▼
App(palette, showTokens)
  └── TabijiTheme(palette) {                     (Provider)
          if (showTokens) TokenSwatches() else IntroScreen()
      }                                           ↑ 둘 다 TabijiTheme.colors로 읽음
```

A4와 같은 단방향 흐름이 한 층 위에서 반복된다. `App`의 파라미터에는 기본값(`TabijiPalette.CLOUD`, `false`)을 준다. 나중에 Android(Phase M)에서는 메뉴바가 없으니 `App()`만 불러도 되게 하려는 것이다.

### 1-5. 240ms 크로스페이드 — 목표 색을 바꾸면 알아서 따라간다

`animateColorAsState(목표색, tween(240))`은 **목표색이 바뀌면 이전 색에서 새 색으로 240ms 동안 보간한 값**을 프레임마다 돌려준다. CSS의 `transition: color 240ms`와 같은 발상이다. "목표만 선언하면 중간값은 알아서" 계산된다.

테마 전환은 "역할 15개 각각에 이걸 감싸는 것"이다. `TabijiTheme` 함수 안에서 `palette.colors`를 바로 공급하지 않고, 역할마다 애니메이션된 색으로 새 `TabijiColors`를 만들어 공급한다. 화면 코드는 아무것도 몰라도 된다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| `data class` | `data class P(val x: Int, val y: Int)` | 값을 담는 클래스. `==`가 **내용 비교**가 되고, `copy`·`toString`이 자동으로 생긴다 | 값 비교가 되는 `type P = { x: number; y: number }` |
| `val` 프로퍼티 | `val x: Int` | 생성 후 바꿀 수 없음 (읽기 전용) | `readonly x: number` |
| enum 생성자 | `enum class Size(val px: Int) { S(8), M(12) }` | enum 항목마다 값을 붙인다. `Size.M.px == 12` | `const Size = { S: { px: 8 }, M: { px: 12 } } as const` |
| `object` | `object Theme { val name = "x" }` | 인스턴스가 하나뿐인 싱글턴. `Theme.name`으로 바로 쓴다 | 모듈 수준 `export const Theme = { … }` |
| 커스텀 getter | `val colors: TabijiColors @Composable get() = …` | 읽을 때마다 `get()`이 실행되는 프로퍼티. `@Composable`을 붙이면 Composable 안에서만 읽을 수 있다 | `get colors() { … }` |
| `listOf`, `Pair`와 `to` | `listOf("ink" to c.ink, "paper" to c.paper)` | `a to b`는 `Pair(a, b)`를 만드는 중위 함수다 | `[["ink", c.ink], ["paper", c.paper]]` |
| `forEach` · `mapIndexed` | `list.forEach { (name, color) -> … }` | 람다로 반복한다. `(a, b)`는 Pair를 풀어 받는 구조 분해 | `list.forEach(([name, color]) => …)` |
| 문자열 템플릿 | `"h${i + 1}"` | `$이름` 또는 `${식}`을 문자열에 넣는다 | `` `h${i + 1}` `` |

---

## 3. 만들 것

### 파일

모두 `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/` 아래다. 테마는 `architecture.md`의 패키지 구조대로 **`core/ui/theme/`** 에 둔다. 나중에 B8에서 `core-ui` 모듈로 옮길 자리다.

| 경로 | 들어갈 것 |
| --- | --- |
| `core/ui/theme/TabijiColors.kt` | `@Immutable data class TabijiColors` |
| `core/ui/theme/TabijiPalette.kt` | `enum class TabijiPalette(val colors: TabijiColors)` 8종 |
| `core/ui/theme/TabijiTheme.kt` | `LocalTabijiColors`, 함수 `TabijiTheme`, 객체 `TabijiTheme`, 크로스페이드 |
| `core/ui/theme/TokenSwatches.kt` | 디버그 화면: 현재 테마의 역할별 색 목록 |
| `app/App.kt` (수정) | `App(palette, showTokens)`, `TabijiTheme`으로 감싸기 |
| `feature/intro/IntroScreen.kt` (수정) | 리터럴 대신 `TabijiTheme.colors` |
| `desktopApp/…/main.kt` (수정) | 테마·디버그 상태 + `MenuBar` |

### `TabijiColors`의 모양

역할 15개와 머리띠 목록이다. 이름과 순서는 핸드오프 `01_tokens.md`의 매핑을 그대로 쓴다.

```
paper  stripe  ink
card   soft
accent onAccent sub
headers: List<Color>      ← [h1, h2, h3] (korea만 h4까지 4개)
shadow skel
est    err  errSoft  ok
```

---

## 4. 조립 순서

단계마다 실행해서 확인한다. `scripts/linux/run-desktop.sh --hot`을 띄워 두면 빠르다. 단, **4-4의 `main.kt` 수정은 Hot Reload가 반영하지 못할 수 있으니** 그때는 앱을 다시 띄운다.

### 4-1. `TabijiColors` — 색 표의 틀

- 위 모양대로 `data class`를 만든다. 모든 필드는 `val`, 타입은 `Color` (`headers`만 `List<Color>`)
- 클래스 위에 `@Immutable`을 붙인다 (`androidx.compose.runtime.Immutable`)

**`@Immutable`은 Compose에게 하는 약속이다.** "이 객체는 만들어진 뒤 내용이 절대 안 바뀐다." A4에서 본 "넘긴 값이 같으면 다시 그리기 건너뛰기(skip)"에서 Compose는 "같은가"를 두 방식으로 판단한다.
- 안정(stable)하다고 확신하는 타입은 **내용**으로 비교한다 (`==`)
- 그렇지 않은 타입은 **같은 객체인지**로만 비교한다 (`===`)

`TabijiColors`는 `List`(인터페이스라 겉보기로는 바뀔 수 있는지 모름)를 품고 있어서 그냥 두면 후자다. 그러면 내용이 같은 새 객체가 올 때마다 다시 그린다. 4-6의 크로스페이드는 매 프레임 새 `TabijiColors`를 만들기 때문에 이 차이가 실제로 생긴다. React로 치면 `React.memo`가 얕은 비교 대신 깊은 값 비교를 해도 된다고 보증하는 셈이다.

✅ 확인: 빌드만 된다 (아직 아무도 안 씀).

### 4-2. `TabijiPalette` — 먼저 cloud 하나만 손으로

```kotlin
enum class TabijiPalette(val colors: TabijiColors) {
    CLOUD(
        TabijiColors(
            paper = Color(0xFFF2F7FC),
            stripe = /* … */,
            // … 나머지 역할을 이름 붙여서
        ),
    ),
}
```

- 이름은 A4의 `Season`과 같은 스타일(`UPPER_SNAKE_CASE`)로 맞춘다
- 값은 **`tokens/tokens.json`의 `themes.cloud`** 에서 옮긴다. 보드 html의 CSS 값이 아니다 (`01_tokens.md` 조정 7건)
- **이름 붙인 인자(named argument)로** 쓴다. 역할이 15개라 위치 인자로 쓰면 순서를 틀려도 컴파일러가 못 잡는다

**hex 변환 함정 두 가지**:

| tokens.json | Compose | 이유 |
| --- | --- | --- |
| `"#2A3644"` (6자리) | `Color(0xFF2A3644)` | 앞에 불투명 `FF`를 붙인다 (A1에서 본 규칙) |
| `"#2A364438"` (8자리, `shadow`) | `Color(0x382A3644)` | tokens.json은 CSS식 **RRGGBB·AA**(알파가 뒤), Compose는 **AA·RRGGBB**(알파가 앞)이다. 뒤 두 자리를 앞으로 옮긴다 |

✅ 확인: 빌드만 된다.

> **나머지 7개 테마는 4-4에서 채운다.** 136개 hex를 옮겨 적는 작업이라 배울 거리는 cloud 하나로 충분하다. 직접 해도 되고, "나머지 팔레트는 네가 채워줘"라고 하면 Claude가 tokens.json에서 생성한다. 어느 쪽이든 4-5의 토큰 화면으로 값을 검증한다.

### 4-3. 통로·공급자·읽기 — 그리고 첫 사용

`TabijiTheme.kt`에 세 가지를 만든다.

```kotlin
val LocalTabijiColors = staticCompositionLocalOf { TabijiPalette.CLOUD.colors }   // 통로 (기본값 cloud)

@Composable
fun TabijiTheme(palette: TabijiPalette, content: @Composable () -> Unit) {         // 공급자
    CompositionLocalProvider(LocalTabijiColors provides palette.colors, content = content)
}

object TabijiTheme {                                                               // 읽기
    val colors: TabijiColors
        @Composable get() = LocalTabijiColors.current
}
```

React로 옮기면 이렇다.

```jsx
const TabijiColorsCtx = createContext(CLOUD.colors)                    // LocalTabijiColors
const TabijiTheme = ({ palette, children }) =>
  <TabijiColorsCtx.Provider value={palette.colors}>{children}</TabijiColorsCtx.Provider>
const useTabijiColors = () => useContext(TabijiColorsCtx)              // TabijiTheme.colors
```

- 함수 `TabijiTheme`과 객체 `TabijiTheme`은 이름이 같아도 공존한다. 괄호로 부르면 함수(`TabijiTheme(…) { }`), 점으로 접근하면 객체(`TabijiTheme.colors`)다
- `content: @Composable () -> Unit`은 "Composable 람다를 받는다"는 뜻이다. 그래서 `TabijiTheme(…) { IntroScreen() }`처럼 trailing lambda로 쓸 수 있다. React의 `children`과 같은 역할이다
- 크로스페이드는 4-6에서 `palette.colors` 자리를 바꿔서 넣는다

그다음 연결한다.

- `App()`: 안의 내용을 `TabijiTheme(TabijiPalette.CLOUD) { … }`로 감싼다 (4-4에서 파라미터로 바꿈)
- `IntroScreen`: ink 리터럴 `Color(0xFF2A3644)`를 `TabijiTheme.colors.ink`로 바꾸고 `TODO(A5)` 주석을 지운다

✅ 확인: 화면이 **전과 똑같다.** 같은 색을 이제 테마에서 꺼내 쓸 뿐이다. 이게 맞는지 보려면 공급하는 값을 잠깐 `palette.colors.copy(ink = Color.Red)`로 바꿔 본다. 경계선이 빨개지면 통로가 제대로 연결된 것이다 (확인 뒤 되돌림).

### 4-4. 테마 8종과 메뉴바

**① 나머지 팔레트 7개**를 채운다 (직접 또는 Claude에게 요청). korea만 `headers`가 4개다.

**② 계절 임시 색도 테마에서.** A4의 `seasonColor()` 리터럴 4개는 사실 계절 테마들의 `accent`와 같은 값이다. `TabijiPalette.SPRING.colors.accent`처럼 바꾸면 `feature/intro`에서 리터럴이 사라진다. `TODO(A6)`는 그대로 둔다 (A6에서 이미지로 바뀜).

**③ `App`에 파라미터 추가**: `App(palette: TabijiPalette = TabijiPalette.CLOUD, showTokens: Boolean = false)`. 감싸는 `TabijiTheme`에 `palette`를 넘긴다. `showTokens`는 4-5에서 쓴다.

**④ `main.kt`: 상태 + 메뉴바.** `application { }` 람다 안은 Composable이라 A4처럼 `remember`를 쓸 수 있다.

```kotlin
var palette by remember { mutableStateOf(TabijiPalette.CLOUD) }
```

메뉴바는 `Window { }` 안에서 `MenuBar { Menu("테마") { … } }`로 만든다. 항목은 `RadioButtonItem(text, selected, onClick)`이다. 8개를 손으로 쓰지 말고 enum의 모든 항목 목록 `TabijiPalette.entries`를 `forEach`로 돌린다.

```kotlin
TabijiPalette.entries.forEach { p ->
    RadioButtonItem(p.name, selected = /* 현재 것인가 */, onClick = { /* 상태 바꾸기 */ })
}
```

- import: `androidx.compose.ui.window.MenuBar` (`Menu`·`RadioButtonItem`은 블록 안에서 저절로 보인다. A3의 `weight`와 같은 스코프 원리)
- `p.name`은 enum 항목 이름 문자열(`"CLOUD"`)이다. 디버그 메뉴라 그대로 쓴다
- 메뉴바 전체가 **디버그용**이다. 사용자용 테마 선택은 B9 설정 화면에서 만든다. 그래서 메뉴 글자는 하드코딩하고 `// 디버그 메뉴 — B9 설정 화면이 생기면 정리` 주석을 단다
- `App(palette = palette)`로 내려준다

✅ 확인: 메뉴에서 테마를 바꾸면 **인트로의 잉크 경계선 색이 바뀐다** (아직 즉시 바뀜). cloud의 남색 잉크와 spring의 자주빛 잉크처럼 차이가 미묘하니 잘 본다. 4-5의 토큰 화면에서는 확실히 보인다.

### 4-5. 토큰 화면 — 현재 테마의 색을 눈으로 검증

`TokenSwatches.kt`에 `@Composable fun TokenSwatches(modifier: Modifier = Modifier)`를 만든다.

- `val c = TabijiTheme.colors`로 현재 색을 읽는다
- `(이름, 색)` 목록을 만든다: `listOf("paper" to c.paper, "stripe" to c.stripe, …)`. 머리띠는 `c.headers.mapIndexed { i, color -> "h${i + 1}" to color }`로 만들어 이어 붙인다 (`+`로 리스트를 합칠 수 있다)
- 목록을 `forEach`로 돌며 한 줄씩 그린다. 한 줄 = `Row`: 색 상자(32dp 정사각형, 2dp `ink` 테두리) · 역할 이름 · hex
- 화면 바탕은 `c.paper`, 글자는 `c.ink`로 칠한다. 토큰 화면도 토큰을 쓴다
- 줄이 17~18개라 창보다 길 수 있다. 바깥 `Column`에 `Modifier.verticalScroll(rememberScrollState())`를 붙인다. `rememberScrollState()`도 `remember`로 기억되는 상태다(A4). 그래서 다시 그려져도 스크롤 위치가 유지된다

hex 문자열은 이렇게 만든다 (`toArgb` import: `androidx.compose.ui.graphics.toArgb`):

```kotlin
"#" + color.toArgb().toUInt().toString(16).padStart(8, '0').uppercase()   // "#FF2A3644"
```

Kotlin 공통 코드에는 `String.format`이 없어서 이렇게 직접 만든다. 결과는 Compose 순서(**AARRGGBB**)라 tokens.json과 비교할 때 알파 위치에 주의한다.

연결:

- `main.kt`: `var showTokens by remember { mutableStateOf(false) }` + `Menu("디버그") { CheckboxItem("토큰 화면", checked = showTokens, onCheckedChange = { showTokens = it }) }`
- `App`: `if (showTokens) TokenSwatches() else IntroScreen()`

✅ 확인: 디버그 ▸ 토큰 화면을 켜고 테마를 바꿔 가며 본다.
- 값이 `tokens.json`과 일치한다. 최소 cloud와 korea 두 테마는 전 항목 대조
- korea에서만 `h4`가 한 줄 더 나온다
- `images/screens/theme_tokens.png`와 색감이 같다

### 4-6. 240ms 크로스페이드

`TabijiTheme` 공급자가 `palette.colors`를 그대로 공급하지 않고, **역할마다 애니메이션된 색**으로 새 `TabijiColors`를 만들어 공급하게 바꾼다.

- 한 색을 애니메이션하는 작은 함수부터 만든다. `animateColorAsState(target, tween(240, easing = …))`의 `.value`를 돌려주는 `@Composable private fun`
  - import: `androidx.compose.animation.animateColorAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.animation.core.CubicBezierEasing`
  - 이징은 핸드오프 `20_motion.md` 값 `CubicBezierEasing(0.2f, 0f, 0f, 1f)`
- 그 함수로 15개 역할을 각각 감싸 새 `TabijiColors(paper = …, …)`를 만든다. 이것도 `TabijiColors`의 **확장 함수**(A3에서 본 문법)로 만들면 `palette.colors.animated()`처럼 읽힌다
- `headers`는 `map { 한 색 애니메이션(it) }`로 감싼다. korea로 바뀔 때 4번째 색이 새로 생기는데, 목록 맨 끝에 추가되는 거라 문제없다

✅ 확인: 테마를 바꾸면 토큰 화면의 색 상자들이 **툭 바뀌지 않고 부드럽게** 넘어간다. 레이아웃(위치·크기)은 하나도 안 움직인다 (v2 원칙 8: "테마는 색만 바꾼다").

### 4-7. 리터럴 0개 확인

```bash
grep -rn "Color(0x" shared/src/commonMain/kotlin --include=*.kt
```

결과가 **`core/ui/theme/TabijiPalette.kt` 안에만** 나와야 한다. `Color.White`·`Color.Black`·`Color.Transparent`(A4 덮개, 워드마크 흰 글자)는 핸드오프가 "전 테마 고정"으로 정한 값이라 그대로 둔다.

---

## 5. import 목록

```kotlin
// TabijiColors.kt
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// TabijiTheme.kt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue      // by animateColorAsState(…)로 쓸 경우

// TokenSwatches.kt
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.toArgb

// main.kt
import androidx.compose.ui.window.MenuBar
```

## 6. 흔한 오류와 원인 읽기

| 증상 | 원인 |
| --- | --- |
| `@Composable invocations can only happen from the context of a @Composable function` (`TabijiTheme.colors`에서) | getter에 `@Composable`이 없음, 또는 일반 함수에서 읽음 |
| 테마를 바꿔도 아무것도 안 바뀜 | `App`이 `palette`를 `TabijiTheme`에 안 넘김, 또는 화면이 아직 리터럴을 씀 |
| 4-3에서 빨간 경계가 안 나옴 | `IntroScreen`이 `TabijiTheme { }` 블록 **밖**에서 불림 → 통로 기본값(cloud)을 읽음. React에서 Provider 밖의 `useContext`가 기본값을 받는 것과 같다 |
| `shadow` 색이 진하게 보임 | `#2A364438`을 `0xFF2A364438`처럼 옮김 → 알파 위치 (4-2 함정 표) |
| `Unresolved reference: MenuBar` 또는 `RadioButtonItem` | `MenuBar`를 `Window { }` 밖에서 부름. 메뉴바는 창에 붙는 것이라 창 블록 안에서만 쓸 수 있다 |
| 메뉴를 바꿨는데 Hot Reload가 반영 안 함 | `main.kt`(Desktop 진입점) 변경. 앱을 다시 띄운다 |
| 크로스페이드 없이 툭 바뀜 | 공급자가 아직 `palette.colors`를 그대로 공급함 |
| `No value passed for parameter 'xxx'` (`TabijiColors(…)`) | 팔레트 하나에서 역할을 빠뜨림. named argument라 어느 역할인지 메시지에 나온다 |

## 7. 완료 조건 (모두 눈으로)

- [ ] 메뉴바 ▸ 테마에서 8종을 고를 수 있고, 고르면 색이 240ms 크로스페이드로 바뀜 (레이아웃은 그대로)
- [ ] 디버그 ▸ 토큰 화면: 역할 이름과 hex가 tokens.json과 일치 (cloud·korea 전 항목 대조), korea만 h4
- [ ] 인트로 화면은 cloud에서 A4와 똑같이 보이고, 선택 동작도 그대로
- [ ] `grep "Color(0x"` 결과가 `TabijiPalette.kt` 안에만 있음
- [ ] 코드 리뷰: `TabijiColors`는 `@Immutable data class` · 테마 상태는 `main.kt`에, `App`은 파라미터로 받음 · 화면 코드는 `TabijiTheme.colors`로만 색을 읽음
- [ ] PR의 `client-ci` 초록

## 8. 끝나면 붙여넣어 줄 것

1. `core/ui/theme/` 네 파일, `App.kt`, `main.kt`, `IntroScreen.kt` 변경분 (`TabijiPalette.kt`는 cloud 부분만이어도 된다)
2. 스크린샷: 토큰 화면 cloud와 korea 각 한 장, 메뉴바 펼친 모습 한 장
3. `grep` 결과

중간에 막히면 단계 번호(예: "4-3에서 빨간 경계가 안 나옴")와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): add TabijiTheme with eight palettes and crossfade`, `Refs: A5`)과 push를 하고, #9 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A6(Compose Resources: 계절 이미지·폰트·문자열)로 간다.
