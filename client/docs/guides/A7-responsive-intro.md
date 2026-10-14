# A7 — 반응형: 창 폭에 따라 Desktop 2×2 ↔ Phone 세로 4단

> 브랜치: `client/feat-a7-responsive-intro` (Claude가 만들어 둠, A6 PR #11 위에 쌓음) · 커밋 footer `Refs: A7`
> 시각 기준: handoff-v2 `docs/10_intro.md`(배치와 경계, 스택 폭, fit 규칙의 Phone 열), `source/intro_phone.html`(390×844), `images/screens/intro_phone.png`
> 확인은 **Desktop 창을 줄여서** 한다. 실기기 확인은 M2

## 목표

같은 인트로 화면이 **창 폭에 따라 다른 배치**로 바뀌게 한다.

| 창 폭 | 배치 | 가을·겨울 초점 | 가운데 스택 폭 | 워드마크 |
| --- | --- | --- | --- | --- |
| 600dp 이상 (Desktop) | 2×2 | +0.1 (55%) | 360dp | 58sp / 64 |
| 600dp 미만 (Phone) | **세로 4단** (봄→여름→가을→겨울) | **+0.16 (58%)** | **310dp** | **46sp / 52** |

- 경계는 둘 다 3dp ink
- 창을 줄였다 늘리면 배치가 바로 바뀐다
- 선택한 칸(A4)은 배치가 바뀌어도 그대로 선택돼 있다
- 디버그 메뉴에 **창 크기 프리셋**(Phone 390×844 / Desktop 1440×900)을 두어 핸드오프 캡처와 바로 비교한다

이번 단계에서 배우는 것은 세 가지다.

1. 지금 쓸 수 있는 폭을 **재는** 법 (`BoxWithConstraints`)
2. 그 폭을 "구간"으로 바꿔 **앱 전체에 알리는** 법 (A5의 CompositionLocal 재사용)
3. 배치가 바뀔 때 **상태가 살아남는** 조건 (Composable의 수명)

> 범위: 인트로만. 버튼 높이처럼 구간에 따라 달라지는 다른 값들은 A8(잉크 마감)에서 이번에 만든 구간을 그대로 쓴다.

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [적응형 레이아웃 기초](https://developer.android.com/develop/ui/compose/layouts/adaptive) | "창 크기에 따라 레이아웃을 고른다"는 발상과 Material 구간(600·840dp) |
| [BoxWithConstraints](https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-display-sizes#boxwithconstraints) | 부모가 준 공간의 크기를 읽는 Composable |
| [Composable 생명주기](https://developer.android.com/develop/ui/compose/lifecycle) | composition에서 나가면 `remember`가 잊힌다 (1-5) |
| [Kotlin companion object](https://kotlinlang.org/docs/object-declarations.html#companion-objects) · [`chunked`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/chunked.html) | 2장 문법 |

---

## 1. 핵심 개념

### 1-1. Compose의 반응형 = 폭을 보고 **다른 트리를 그린다**

CSS에는 미디어 쿼리가 있다. 같은 HTML에 폭별로 다른 스타일을 입힌다. Compose에는 스타일시트가 없어서 **코드에서 분기**한다. 폭을 읽고, 그 값에 따라 다른 Composable을 부른다. React의 조건부 렌더링과 같다.

```jsx
// React
return isCompact ? <QuadrantStack … /> : <QuadrantGrid … />
```

```kotlin
// Compose
when (widthClass) {
    WidthClass.COMPACT -> QuadrantStack(…)
    WidthClass.MEDIUM, WidthClass.EXPANDED -> QuadrantGrid(…)
}
```

### 1-2. 폭을 재는 법 — `BoxWithConstraints`

```kotlin
BoxWithConstraints {
    // 이 블록 안에서 maxWidth, maxHeight(Dp)를 읽을 수 있다
    if (maxWidth < 600.dp) … else …
}
```

`BoxWithConstraints`는 `Box`와 같은데, 블록 안에서 **부모가 준 공간의 크기**(`maxWidth`, `maxHeight`)를 읽을 수 있다. A3에서 `weight`가 `Row` 블록 안에서만 보였던 것처럼, `maxWidth`도 이 블록 안에서만 보인다.

- CSS로 치면 **컨테이너 쿼리**(`@container`)에 가깝다. 화면 전체가 아니라 "나에게 주어진 칸"의 크기를 본다
- 앱 루트(`App`)에서 쓰면 그 칸이 곧 창의 내용 영역이다. 그래서 창 폭을 재는 셈이 된다
- 창 크기가 바뀌면 `maxWidth`가 바뀌고, 그 값을 읽은 블록이 다시 그려진다 (A4의 "상자를 읽은 곳만 다시 그린다"와 같은 원리)

### 1-3. 폭을 "구간"으로 바꾼다 — `WidthClass` enum

화면마다 `maxWidth < 600.dp`를 직접 쓰면 기준 숫자가 여기저기 흩어진다. 그래서 폭을 **이름 붙은 구간**으로 한 번만 바꾸고, 화면은 구간만 본다. Material 디자인의 기준을 그대로 쓴다.

| 구간 | 폭 | 대표 기기 | 인트로 배치 |
| --- | --- | --- | --- |
| `COMPACT` | 600dp 미만 | 폰 세로 | 세로 4단 |
| `MEDIUM` | 600 ~ 840dp 미만 | 폴더블·태블릿 세로, 좁힌 Desktop 창 | 2×2 |
| `EXPANDED` | 840dp 이상 | 태블릿 가로, Desktop | 2×2 |

인트로는 `MEDIUM`과 `EXPANDED`를 같게 다룬다. 구간을 셋으로 둔 이유는 플래너(B1)에서 일정 패널을 옆에 둘지 아래 시트로 둘지가 `MEDIUM`부터 갈리기 때문이다. CSS 프레임워크의 `sm`/`md`/`lg` 같은 것이다.

### 1-4. 구간을 앱 전체에 알리기 — 설계 선택

| | 방법 | 장단점 |
| --- | --- | --- |
| A | 파라미터: `App`에서 재고 `IntroScreen(widthClass = …)`로 넘김 | 어디서 오는지 눈에 보인다. 대신 화면이 늘수록 모든 화면과 그 자식에게 넘겨야 한다 (prop drilling) |
| **B (추천)** | CompositionLocal: `LocalWidthClass` 통로를 만들고 `App` 루트에서 한 번 공급 | A5의 테마와 같은 방식. 앱 전체가 공유하고 **거의 안 바뀌는** 값이라 A5 1-2의 "언제 쓰나" 기준에 맞는다. 구간 경계를 넘을 때만 바뀐다 |

React로 치면 B는 `WidthClassContext`를 만들어 최상단 Provider에서 `useWindowWidth()` 결과를 넣는 것이다.

### 1-5. 배치가 바뀔 때 상태는 어디서 살아남나

`when`으로 `QuadrantGrid`에서 `QuadrantStack`으로 바뀌면, `QuadrantGrid`는 **composition에서 빠진다**(React의 unmount). 그 안에서 `remember`로 기억하던 값은 **함께 버려진다.** 새로 들어온 `QuadrantStack`은 처음부터 시작한다.

그래서 A4의 `selected`가 **`IntroScreen`에 있다는 게 여기서 효과를 낸다.** `IntroScreen`은 배치가 바뀌어도 composition에 계속 남는다. 바뀌는 건 그 아래 `when`의 가지뿐이다. 그래서 선택이 유지된다. 6장 실험에서 상태를 가지 안으로 옮겨 직접 확인한다.

```
IntroScreen          ← 계속 남음 → selected 유지
└── when (widthClass)
    ├── QuadrantGrid    ← 폭이 줄면 빠짐 (안의 remember는 버려짐)
    └── QuadrantStack   ← 대신 들어옴 (처음부터)
```

### 1-6. Desktop 창 크기도 상태다 — 프리셋 메뉴

A6에서 만든 `windowState`는 **바꿀 수 있는 상태 객체**다. `windowState.size = DpSize(390.dp, 844.dp)`라고 대입하면 창이 그 크기로 바뀐다. 디버그 메뉴에 이 대입을 걸어 두면, 손으로 창을 끌지 않고도 핸드오프의 Phone(390×844)·Desktop(1440×900) 캡처와 같은 크기로 바로 맞출 수 있다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| `companion object` | `enum class WidthClass { …; companion object { fun of(width: Dp): WidthClass = … } }` | 클래스에 붙는 정적(static) 영역. `WidthClass.of(maxWidth)`처럼 부른다 | `class WidthClass { static of(w) { … } }` |
| enum 뒤 세미콜론 | `COMPACT, MEDIUM, EXPANDED;` | enum 항목 뒤에 멤버(companion 등)를 더 쓰려면 항목 목록을 `;`로 끝낸다 | — |
| `Dp` 비교 | `width < 600.dp` | `Dp`끼리 크기 비교가 된다 | — |
| 인자 없는 `when` (복습) | `when { width < 600.dp -> COMPACT; width < 840.dp -> MEDIUM; else -> EXPANDED }` | 위에서부터 처음 참인 가지 (A4) | `if/else if` 사슬 |
| `when` 가지 묶기 | `WidthClass.MEDIUM, WidthClass.EXPANDED -> …` | 쉼표로 여러 값을 한 가지에 | `case 'md': case 'lg':` |
| `chunked(n)` | `Season.entries.chunked(2)` → `[[SPRING, SUMMER], [AUTUMN, WINTER]]` | 목록을 n개씩 자른 목록들 | lodash `_.chunk(arr, 2)` |
| `if` 식 | `val w = if (compact) 310.dp else 360.dp` | Kotlin의 `if`는 값을 돌려준다 | `compact ? 310 : 360` |

---

## 3. 완성 후의 모습

### 파일

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/…/core/ui/layout/WidthClass.kt` (새 파일) | `enum class WidthClass` + `companion object { fun of(width) }`, `LocalWidthClass`, `@Composable fun ProvideWidthClass(content)` |
| `shared/…/app/App.kt` (수정) | 테마 안쪽을 `ProvideWidthClass { … }`로 감싼다 |
| `shared/…/feature/intro/Season.kt` (수정) | `focusY` 하나 → `desktopFocusY`, `phoneFocusY` 둘 |
| `shared/…/feature/intro/IntroScreen.kt` (수정) | 사분면 부분을 `QuadrantGrid` / `QuadrantStack`으로 나누고 `when`으로 고름. 가운데 스택 폭·워드마크 크기를 구간에 맞춤 |
| `shared/…/core/ui/theme/TokenSwatches.kt` (수정, 선택) | 맨 위에 현재 구간 표시 |
| `desktopApp/…/main.kt` (수정) | 디버그 메뉴에 창 크기 프리셋 |

### 앱 전체 트리

```
main.kt  Window(state = windowState)
├── MenuBar
│   ├── Menu("Theme")  …
│   └── Menu("Debug")
│       ├── CheckboxItem("Token Page")
│       ├── Item("Phone 390×844")    → windowState.size = DpSize(390.dp, 844.dp)     ← 추가
│       └── Item("Desktop 1440×900") → windowState.size = DpSize(1440.dp, 900.dp)    ← 추가
└── App(palette, showTokens)
    └── TabijiTheme(palette)
        └── ProvideWidthClass                     ← 추가: BoxWithConstraints로 재고 LocalWidthClass 공급
            └── if (showTokens) TokenSwatches() else IntroScreen()
```

---

## 4. 이름이 겹치는 것 (이번 단계)

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 공간 크기 읽기 | `androidx.compose.foundation.layout.BoxWithConstraints` | `androidx.compose.material3.windowsizeclass.*` (Material의 `WindowSizeClass` 라이브러리. **우리 의존성에 없다**) |
| 크기 단위 | `androidx.compose.ui.unit.Dp`, `androidx.compose.ui.unit.dp` | `java.awt.Dimension` (`main.kt`의 AWT 최소 크기에만) |
| 창 크기 값 | `androidx.compose.ui.unit.DpSize` | `androidx.compose.ui.geometry.Size` (픽셀 단위 float, 그리기용) |
| 화면 정보 | 쓰지 않는다 | `androidx.compose.ui.platform.LocalWindowInfo` (창 크기를 **픽셀**로 준다. 이번엔 `BoxWithConstraints`의 Dp를 쓴다) |

---

## 5. 조립 순서

`scripts/linux/run-desktop.sh --hot`. `main.kt`나 새 파일을 추가한 단계에서는 앱을 다시 띄운다.

### 5-1. `WidthClass` — 구간 enum과 통로

`core/ui/layout/WidthClass.kt`:

```kotlin
enum class WidthClass {
    COMPACT, MEDIUM, EXPANDED;

    companion object {
        fun of(width: Dp): WidthClass = when {
            width < 600.dp -> COMPACT
            /* 840 미만 → MEDIUM */
            else -> EXPANDED
        }
    }
}

val LocalWidthClass = staticCompositionLocalOf { WidthClass.EXPANDED }   // Provider 밖 기본값: Desktop

@Composable
fun ProvideWidthClass(content: @Composable () -> Unit) {
    BoxWithConstraints {
        CompositionLocalProvider(LocalWidthClass provides WidthClass.of(maxWidth), content = content)
    }
}
```

- 600·840은 구간 정의 그 자체라 여기 한 곳에만 둔다. 화면 코드에는 숫자가 나오지 않게 한다
- `ProvideWidthClass`는 A5 `TabijiTheme` 함수와 같은 모양이다. 차이는 공급하는 값을 `BoxWithConstraints`로 잰다는 것뿐이다

import (`WidthClass.kt`):

```kotlin
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
```

**`App.kt`**: 3장 트리대로 `TabijiTheme(palette) { ProvideWidthClass { … } }`. import는 `com.yamada.studio.tabiji.core.ui.layout.ProvideWidthClass`.

**(선택) 토큰 화면에 구간 표시**: `TokenSwatches`의 맨 위 줄에 `Text("widthClass = ${LocalWidthClass.current}", color = c.ink)`를 둔다 (`// 디버그 샘플`). import는 `com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass`.

✅ 확인: 인트로는 그대로다. 토큰 화면을 켜고 창 폭을 줄였다 늘리면 `COMPACT` / `MEDIUM` / `EXPANDED`가 바뀐다.

### 5-2. 디버그 메뉴: 창 크기 프리셋

`main.kt`의 `Menu("Debug") { … }`에 항목 둘을 더한다 (3장 트리). 항목은 `Item(text, onClick = { … })`이고, `onClick`에서 `windowState.size`에 `DpSize`를 대입한다.

- 화면 높이가 900보다 작으면 Desktop 프리셋은 OS가 화면에 맞게 줄일 수 있다. 그래도 폭 1440 쪽 확인에는 충분하다
- import 추가는 없다 (`DpSize`·`dp`는 A6에서 이미 있음, `Item`은 `MenuBar` 블록 안에서 보임)

✅ 확인: 앱을 다시 띄우고 Debug ▸ Phone 390×844를 고르면 창이 폰 크기가 된다. 토큰 화면이 켜져 있다면 `COMPACT`가 보인다.

### 5-3. `Season`의 초점을 둘로

`focusY` 하나를 `desktopFocusY`와 `phoneFocusY`로 나눈다 (`10_intro.md` 표).

| 계절 | `desktopFocusY` | `phoneFocusY` |
| --- | --- | --- |
| 봄·여름 | `0f` | `0f` |
| 가을·겨울 | `0.1f` | `0.16f` |

`SeasonQuadrant`는 계절에서 초점을 직접 꺼내지 않고 **파라미터로 받는다.** 어느 초점을 쓸지는 배치(2×2냐 세로냐)가 정하기 때문이다. 시그니처가 이렇게 된다.

```kotlin
private fun SeasonQuadrant(
    season: Season,
    selected: Season?,
    focusY: Float,                  // ← 추가: 배치가 골라서 넘김
    modifier: Modifier = Modifier,
    onSelect: (Season) -> Unit,
)
```

안쪽은 `BiasAlignment(0f, season.focusY)` → `BiasAlignment(0f, focusY)` 한 곳만 바뀐다. import 변화는 없다.

### 5-4. 사분면을 두 배치로 나누기

**전 → 후 트리** (`IntroScreen.kt`):

```
전 (A6)                                          후 (A7)
IntroScreen                                      IntroScreen
├── selected, toggle                             ├── selected, toggle                    (그대로)
└── Box (fillMaxSize)                            ├── val widthClass = LocalWidthClass.current
    ├── Column (ink 배경, spacedBy 3)             └── Box (fillMaxSize)
    │   ├── Row (weight 1, spacedBy 3)               ├── when (widthClass)
    │   │   ├── SeasonQuadrant(SPRING …)             │   ├── COMPACT          → QuadrantStack(selected, toggle, Modifier.fillMaxSize())
    │   │   └── SeasonQuadrant(SUMMER …)             │   └── MEDIUM, EXPANDED → QuadrantGrid(selected, toggle, Modifier.fillMaxSize())
    │   └── Row (…)                                  └── Column (가운데 스택)  ← 5-5
    │       ├── SeasonQuadrant(AUTUMN …)
    │       └── SeasonQuadrant(WINTER …)
    └── Column (가운데 스택)
```

**두 배치 Composable** (`IntroScreen.kt` 안, `private`):

```
QuadrantGrid(selected, onSelect, modifier)             ← 기존 2×2 코드를 옮겨 온 것
└── Column (modifier · ink 배경 · spacedBy 3)
    └── Season.entries.chunked(2).forEach { row ->
            Row (weight 1 · fillMaxWidth · spacedBy 3)
            └── row.forEach { season ->
                    SeasonQuadrant(season, selected, season.desktopFocusY,
                                   Modifier.weight(1f).fillMaxSize(), onSelect)
                }
        }

QuadrantStack(selected, onSelect, modifier)             ← 새로 만드는 세로 4단
└── Column (modifier · ink 배경 · spacedBy 3)
    └── Season.entries.forEach { season ->
            SeasonQuadrant(season, selected, season.phoneFocusY,
                           Modifier.weight(1f).fillMaxWidth(), onSelect)
        }
```

- 계절 네 개를 손으로 쓰던 호출부가 **반복문**으로 바뀐다. React의 `seasons.map(s => <SeasonQuadrant … />)`와 같다. 이제 계절 이름이 호출부에 한 번도 안 나온다
- `chunked(2)`가 `[[봄, 여름], [가을, 겨울]]`을 만들어 2×2의 두 줄이 된다. enum 순서(봄→여름→가을→겨울)가 곧 배치 순서다
- 두 배치 모두 **상태가 없다.** `selected`와 `onSelect`를 받아서 칸에 그대로 넘긴다
- 세로 4단의 칸은 `weight(1f).fillMaxWidth()`다. 세로로 나누니 `weight`는 높이를, `fillMaxWidth`는 폭을 채운다 (A3의 2×2에서는 `Row` 안이라 반대였다)

import (`IntroScreen.kt`에 추가):

```kotlin
import com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass
import com.yamada.studio.tabiji.core.ui.layout.WidthClass
```

✅ 확인:
- 넓은 창: A6와 똑같은 2×2
- 창을 600dp 아래로 줄이면 **세로 4단**으로 바뀐다. 경계는 여전히 3dp
- 세로 4단에서 가을·겨울 십자선이 칸 가운데보다 **더 아래**(58%)에 있다
- 다시 넓히면 2×2로 돌아온다

### 5-5. 가운데 스택을 구간에 맞추기

```
Column (가운데 스택)
  modifier = Modifier.align(Alignment.Center).width(스택 폭)      ← 폭 추가
  horizontalAlignment = Alignment.CenterHorizontally
├── Text 워드마크  style = 워드마크 스타일                          ← COMPACT면 46sp/52
└── Text 부제      (그대로)
```

- 스택 폭: `val stackWidth = if (widthClass == WidthClass.COMPACT) 310.dp else 360.dp`. A8에서 버튼이 이 폭을 꽉 채운다. 지금은 글자만 있어서 눈에 띄는 변화는 없다. `// TODO(A8): 치수 토큰으로`
- 워드마크: `COMPACT`면 `TabijiTheme.typography.wordmark.copy(fontSize = 46.sp, lineHeight = 52.sp)`, 아니면 `wordmark` 그대로
- import: `androidx.compose.foundation.layout.width` (`layout.*`를 쓰고 있다면 필요 없음)

✅ 확인: Debug ▸ Phone 390×844에서 워드마크가 Desktop보다 작다. `intro_phone.png`와 나란히 놓고 크기와 위치를 비교한다.

---

## 6. 실험 — 상태가 가지 안에 있으면?

1-5를 직접 본다. **저장 전에 결과를 예측**하고, 끝나면 되돌린다.

1. 지금 상태에서 넓은 창으로 여름을 선택한다 → 창을 줄여 세로 4단으로 바꾼다 → 여름이 **여전히 선택**돼 있는지 본다
2. 이번에는 `selected`를 `IntroScreen`에서 지우고 **`QuadrantGrid`와 `QuadrantStack` 안에 각각** `var selected by remember { … }`를 둔다 (`toggle`도 각각). 같은 순서로 해 본다

| 실험 | 예상해 볼 것 |
| --- | --- |
| 1 | 선택이 남는가? 남는다면 `selected`가 트리의 어디에 있어서인가? |
| 2 | 배치가 바뀌는 순간 선택은? 다시 넓혔을 때 2×2의 선택은 돌아오는가? |

React로 치면 실험 2는 조건부로 렌더되는 두 자식이 각자 `useState`를 가진 경우다. 자식이 unmount되면 state도 사라진다.

두 결과와 "왜"를 한두 줄로 적어 붙여넣는다. 확인 뒤 상태를 `IntroScreen`으로 되돌린다.

---

## 7. 흔한 오류와 원인 읽기

| 증상 | 원인 |
| --- | --- |
| `Unresolved reference: maxWidth` | `BoxWithConstraints { }` 블록 **밖**에서 읽음 |
| 구간이 안 바뀜 (늘 `EXPANDED`) | `ProvideWidthClass`로 안 감쌌거나, 화면이 Provider **밖**에 있어 통로 기본값을 읽음 |
| 세로 4단인데 칸이 한 줄에 몰림 | `QuadrantStack`의 칸에 `weight(1f)`를 빠뜨림 (A3과 같은 원인) |
| 2×2 줄 순서가 이상함 | `chunked` 대신 직접 나눈 목록의 순서가 enum 순서와 다름 |
| `'when' expression must be exhaustive` | `when (widthClass)`에서 `MEDIUM`을 빠뜨림 → `MEDIUM, EXPANDED ->`로 묶기 |
| 가을·겨울 초점이 Phone에서도 55% | `QuadrantStack`이 `desktopFocusY`를 넘김 |
| 배치가 바뀔 때 선택이 풀림 | 상태가 가지(`QuadrantGrid`/`QuadrantStack`) 안에 있음 (6장 실험 2) |
| Debug 메뉴 프리셋이 안 보임 | `main.kt` 변경 → 앱을 다시 띄우지 않음 |

## 8. 완료 조건 (모두 눈으로)

- [ ] 창 폭 600dp 미만에서 세로 4단, 이상에서 2×2. 경계는 둘 다 3dp ink
- [ ] Debug ▸ Phone 390×844: 세로 4단, 가을·겨울 초점 58%, 워드마크 46sp. `intro_phone.png`와 크롭·위치 비교
- [ ] Debug ▸ Desktop 1440×900: A6와 같은 2×2
- [ ] 배치가 바뀌어도 선택 유지 (6장 실험 1). 실험 2 결과와 설명
- [ ] 코드 리뷰: 600·840 숫자는 `WidthClass.kt`에만 · 두 배치 Composable은 상태 없음 · 호출부에 계절 이름 반복 없음(반복문)
- [ ] PR의 `client-ci` 초록

## 9. 끝나면 붙여넣어 줄 것

1. `WidthClass.kt`, `IntroScreen.kt`, `Season.kt`, `App.kt`, `main.kt` 변경분
2. 스크린샷: Phone 390×844 인트로, Desktop 인트로, (선택) 토큰 화면의 구간 표시
3. 6장 실험 결과

막히면 단계 번호와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): switch intro between grid and stacked layouts by width`, `Refs: A7`)과 push를 하고, #11 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A8(잉크 마감: 치수 토큰, 워드마크 아웃라인·하드 섀도, chamfer 버튼, 눌림)로 간다.
