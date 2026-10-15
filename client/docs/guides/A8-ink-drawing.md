# A8 — 잉크 마감 ① 그리기: 치수 토큰 · 하드 섀도 · 아웃라인 워드마크

> 브랜치: `client/feat-a8-ink-drawing` (Claude가 만들어 둠, A7 PR #12 위에 쌓음) · 커밋 footer `Refs: A8`
> 시각 기준: handoff-v2 `docs/00_principles.md`(원칙 1·2: 잉크 테두리, 블러 0 하드 섀도), `docs/01_tokens.md`·`tokens/tokens.json`(비색상 토큰), `docs/10_intro.md`(워드마크·부제 마감), `images/screens/intro_desktop.png`
> 확인은 **Desktop**에서 한다 (Phone 프리셋 포함)

## 목표

v2 디자인의 질감 두 가지를 코드로 만든다.

- **하드 섀도**: 블러 없이 **오프셋만 있는 단색 그림자**. 도감 카드처럼 종이를 한 장 겹친 느낌이다
- **잉크 아웃라인 글자**: 흰 글자에 잉크색 테두리를 두르고, 그 뒤에 하드 섀도를 깐다

그리고 이 둘을 인트로 **워드마크·부제**에 입힌다. 지금 흰 글자는 밝은 이미지 위에서 묻히는데, 아웃라인과 섀도가 있으면 **어떤 배경 위에서도 또렷하다** (`10_intro.md`의 "로고 가독성은 아웃라인+섀도가 보장").

함께 **치수 토큰**(테두리 두께·모서리·섀도 오프셋·간격)을 테마에 넣고, 화면 코드의 이름 없는 `.dp` 숫자를 없앤다.

> 범위: 메뉴 버튼 3개(chamfer 모양·눌림)는 **A9**에서 한다. 이번 단계에서 만드는 `inkShadow`와 치수 토큰을 A9 버튼이 그대로 쓴다.

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [그래픽 Modifier — drawBehind](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers) | 내용 **뒤에** 직접 그리는 법. 하드 섀도의 핵심 |
| [커스텀 Modifier 만들기](https://developer.android.com/develop/ui/compose/custom-modifiers) | `fun Modifier.inkShadow(…)`처럼 재사용 가능한 Modifier를 만드는 법 |
| [텍스트 스타일 — drawStyle](https://developer.android.com/develop/ui/compose/text/style-text#text-drawstyle) | `Stroke`로 글자 윤곽선만 그리기 |
| 핸드오프 `docs/00_principles.md` 원칙 1·2·3 | "elevation·블러 금지, 섀도는 오프셋 라운드렉트를 먼저 그린다" |

---

## 1. 핵심 개념

### 1-1. 치수 토큰은 상수 `object`면 충분하다

A5의 색은 **테마마다 다르고 바뀌는 값**이라 CompositionLocal로 내려줬다. 테두리 두께·모서리·간격은 v2 원칙 8("테마는 색만 바꾼다")대로 **8테마 모두 같은 고정값**이다. 바뀌지 않는 값은 통로가 필요 없다. 그냥 상수를 모은 `object`로 둔다.

| 값 | 성격 | 담는 방법 |
| --- | --- | --- |
| 색 (`TabijiTheme.colors`) | 테마에 따라 바뀜, 240ms 애니메이션 | CompositionLocal (A5) |
| 글꼴 (`TabijiTheme.typography`) | 안 바뀌지만 `Font()`가 Composable이라 Composable 안에서 만들어야 함 | CompositionLocal (A6) |
| 너비 구간 (`LocalWidthClass`) | 창 크기에 따라 바뀜 | CompositionLocal (A7) |
| **치수 (`TabijiTheme.border` 등)** | **절대 안 바뀜** | **상수 `object`** |

A5 1-2의 "CompositionLocal을 아무 데나 쓰지 않는다"를 적용하는 경우다. React로 치면 Context 없이 `export const border = { panel: 2 }` 모듈 상수를 import하는 것이다.

읽는 쪽 표기는 색과 맞춘다 (`conventions.md`의 `TabijiTheme.spacing.md`). `object TabijiTheme` 안에 `val border = TabijiBorder`처럼 상수 객체를 가리키는 프로퍼티를 두면 `TabijiTheme.border.panel`로 읽힌다. Composable이 아닌 일반 프로퍼티라서 어디서든 읽을 수 있다.

### 1-2. 하드 섀도 = 같은 모양을 오프셋해서 **먼저** 그리기

CSS의 `box-shadow: 3px 3px 0 ink`(블러 0)와 같은 효과를 Compose에서는 이렇게 만든다.

```
1. 이 요소의 모양(Shape)을 실제 크기의 윤곽(Outline)으로 바꾼다
2. 오른쪽으로 3dp, 아래로 3dp 옮긴 자리에 그 윤곽을 잉크색으로 칠한다     ← 섀도
3. 그 위에 원래 내용(배경·테두리·글자)을 그린다                         ← 본체
```

2번을 "내용보다 먼저" 그리게 해 주는 것이 **`Modifier.drawBehind { … }`** 다. 블록 안은 `DrawScope`(그리기 도구 상자)다. `size`(이 요소의 크기), `drawOutline(…)`, `translate(…) { … }` 같은 그리기 함수를 쓸 수 있다. CSS로 치면 `::before` 가상 요소를 `z-index: -1`로 깔아 그리는 것과 비슷하다.

**왜 `Modifier.shadow`(elevation)를 안 쓰나?** 그건 Material의 **블러 그림자**다. v2 원칙 2가 금지한다. 하드 섀도는 항상 `drawBehind`로 직접 그린다.

**Modifier 순서가 다시 중요하다** (A3). `inkShadow`는 체인의 **바깥(앞)** 에 둔다.

```kotlin
Modifier
    .size(32.dp)
    .inkShadow(…)        // ← 먼저: 섀도를 가장 뒤에 깐다
    .background(color)   // 그 위에 본체 면
    .border(…)           // 그 위에 테두리
```

### 1-3. 재사용 Modifier = 확장 함수

A3에서 `Modifier.padding`이 확장 함수라고 했다. 우리도 똑같이 만들 수 있다.

```kotlin
fun Modifier.inkShadow(offset: DpOffset, color: Color, shape: Shape = RectangleShape): Modifier =
    this.drawBehind { /* 1-2의 1·2번 */ }
```

이렇게 만들면 패널·카드·버튼 어디서든 `.inkShadow(TabijiTheme.shadow.panel, c.shadow)` 한 줄로 같은 질감이 생긴다. CSS의 재사용 클래스(`.ink-shadow`)나 Sass mixin과 같은 역할이다.

### 1-4. 아웃라인 글자 = 같은 글자를 세 번 겹쳐 그리기

Compose `Text`에는 "테두리 두께" 옵션이 없다. 대신 **같은 글자를 세 겹**으로 쌓는다 (`10_intro.md`의 구현 방식).

```
Box  (겹쳐 쌓기, A3)
├── Text  잉크색 채움, (3,3)만큼 옮김         ← 맨 아래: 하드 섀도
├── Text  잉크색 윤곽선(Stroke) 4dp           ← 가운데: 아웃라인
└── Text  흰색 채움                           ← 맨 위: 본체
```

- 윤곽선은 `TextStyle(drawStyle = Stroke(width = …))`로 그린다. `Stroke`는 글자 테두리를 **선 가운데 기준**으로 그려서 절반은 글자 안쪽, 절반은 바깥에 걸친다. 안쪽 절반은 맨 위 흰 글자가 덮는다. 그래서 **바깥으로 2dp**가 보이게 하려면 선 굵기를 **4dp**로 준다
- CSS로 치면 `-webkit-text-stroke` + `text-shadow: 3px 3px 0 ink`이다. 핸드오프 html은 4방향 `text-shadow`로 흉내 냈다

이 세 겹을 매번 쓰지 않도록 **`OutlinedText`** 라는 작은 Composable로 만든다. 지금까지 만든 `SeasonQuadrant`처럼 받은 값대로만 그리는 상태 없는 부품이다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| 상수 `object` | `object TabijiBorder { val panel = 2.dp }` | A5에서 본 `object`(싱글턴)에 값만 모은 것 | `export const border = { panel: 2 } as const` |
| 객체를 가리키는 프로퍼티 | `object TabijiTheme { val border = TabijiBorder }` | `TabijiTheme.border.panel`로 읽게 하는 별칭 | `export const theme = { border }` |
| `Modifier` 확장 함수 만들기 | `fun Modifier.inkShadow(…): Modifier = this.drawBehind { … }` | `this`는 점 앞의 기존 체인. 거기에 이어 붙인 새 체인을 돌려준다 | 메서드 체이닝 빌더에 메서드 추가 |
| 람다 안의 리시버 | `drawBehind { size.width }` | 블록 안의 `this`가 `DrawScope`라서 `size`·`drawOutline`을 이름만으로 쓴다 (A3의 `RowScope`와 같은 원리) | — |
| `with(…) { }` | `with(LocalDensity.current) { 4.dp.toPx() }` | 블록 안에서 그 객체를 `this`로 쓴다. dp → 픽셀 변환에 쓴다 | — |
| `Dp` 곱셈 | `outlineWidth * 2` | `Dp`에 숫자를 곱하면 `Dp` | — |

---

## 3. 만들 것

### 파일

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/…/core/ui/theme/TabijiDimens.kt` (새 파일) | `TabijiBorder`, `TabijiRadius`, `TabijiShadow`, `TabijiSpacing` 상수 `object` 넷 |
| `shared/…/core/ui/theme/TabijiTheme.kt` (수정) | `object TabijiTheme`에 `border`·`radius`·`shadow`·`spacing` 프로퍼티 |
| `shared/…/core/ui/modifier/InkShadow.kt` (새 파일) | `fun Modifier.inkShadow(offset, color, shape)` |
| `shared/…/core/ui/component/OutlinedText.kt` (새 파일) | `@Composable fun OutlinedText(…)` |
| `shared/…/core/ui/theme/TokenSwatches.kt` (수정) | 색 상자에 하드 섀도, 리터럴 → 토큰 |
| `shared/…/feature/intro/IntroScreen.kt` (수정) | 워드마크·부제를 `OutlinedText`로, 이름 없는 `.dp` 정리 |

`core/ui/` 아래 `theme`·`modifier`·`component`로 나누는 건 `architecture.md`의 `core-ui`(테마, 공용 컴포넌트) 자리다. B5에서 모듈로 분리할 때 그대로 옮긴다.

### 치수 토큰 값 (`tokens.json`)

| `object` | 프로퍼티 | 값 |
| --- | --- | --- |
| `TabijiBorder` | `panel` · `chip` · `selected` · `divider` | 2 · 1.5 · 3 · 2 dp |
| `TabijiRadius` | `panel` · `row` · `numBadge` · `button` · `buttonChamfer` · `sheetTop` · `input` | 10 · 9 · 6 · 10 · 8 · 18 · 10 dp |
| `TabijiShadow` | `panel` · `button` · `modal` (`DpOffset`) | (3,3) · (0,3) · (5,5) dp |
| `TabijiSpacing` | `xs` · `sm` · `md` · `lg` · `xl` | 4 · 8 · 12 · 16 · 24 dp |

- `chip` 모서리(999 = 완전 둥근 알약)는 dp 값이 아니라 모양(`RoundedCornerShape(50)`)으로 쓸 것이라 B1에서 다룬다
- 섀도 **색**은 토큰이 아니라 테마 색이다: 패널은 `colors.shadow`(ink 22%), 버튼은 `colors.ink`

---

## 4. 이름이 겹치는 것 (이번 단계)

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 하드 섀도 그리기 | `androidx.compose.ui.draw.drawBehind` (직접 그림) | `androidx.compose.ui.draw.shadow` (`Modifier.shadow` = elevation **블러**, v2 금지) |
| 글자 윤곽선 | `androidx.compose.ui.graphics.drawscope.Stroke` | `androidx.compose.foundation.BorderStroke` (상자 테두리용) |
| 오프셋 (dp) | `androidx.compose.ui.unit.DpOffset` | `androidx.compose.ui.geometry.Offset` (px, 그리기 안에서만) |
| 옮겨 그리기 | `androidx.compose.ui.graphics.drawscope.translate` | `androidx.compose.ui.graphics.drawscope.DrawTransform.translate` 을 직접 import |
| 윤곽 칠하기 | `androidx.compose.ui.graphics.drawOutline` | — |
| 모양 | `androidx.compose.ui.graphics.Shape`, `RectangleShape` | `java.awt.Shape` |
| 텍스트 그림자 (선택 6장) | `androidx.compose.ui.graphics.Shadow` | `org.jetbrains.skia.paragraph.Shadow` |

---

## 5. 조립 순서

`scripts/linux/run-desktop.sh --hot`. 새 파일을 만든 단계에서는 앱을 다시 띄운다.

### 5-1. 치수 토큰

`core/ui/theme/TabijiDimens.kt`:

```kotlin
object TabijiBorder {
    val panel = 2.dp
    val chip = 1.5.dp
    // selected · divider
}

object TabijiShadow {
    val panel = DpOffset(3.dp, 3.dp)
    // button · modal
}
// TabijiRadius · TabijiSpacing 도 3장 표대로
```

`TabijiTheme.kt`의 `object TabijiTheme` 안에 네 줄을 더한다 (`colors`·`typography` 옆).

```kotlin
val border = TabijiBorder
val radius = TabijiRadius
val shadow = TabijiShadow
val spacing = TabijiSpacing
```

`@Composable get()`이 **필요 없다.** CompositionLocal을 읽지 않는 상수라서다 (1-1).

import (`TabijiDimens.kt`):

```kotlin
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
```

✅ 확인: 빌드만 된다 (아직 아무도 안 씀).

### 5-2. `Modifier.inkShadow`

`core/ui/modifier/InkShadow.kt`:

```kotlin
fun Modifier.inkShadow(
    offset: DpOffset,
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.drawBehind {
    val outline = shape.createOutline(size, layoutDirection, this)   // ① 실제 크기의 윤곽
    translate(left = /* offset.x를 px로 */, top = /* offset.y를 px로 */) {
        drawOutline(outline, color)                                   // ② 옮긴 자리에 칠하기
    }
}
```

- `drawBehind` 블록 안의 `this`는 `DrawScope`다. `DrawScope`는 화면 밀도(Density)도 알고 있어서 `offset.x.toPx()`로 dp를 픽셀로 바꿀 수 있다. `createOutline`의 세 번째 인자에 `this`를 넘기는 것도 그래서다
- `shape` 기본값이 `RectangleShape`(직각 사각형)이다. 둥근 모서리 패널이면 `RoundedCornerShape(TabijiTheme.radius.panel)`을 넘긴다. A9의 chamfer 버튼은 `ChamferShape`를 넘긴다. 섀도는 항상 **본체와 같은 모양**이어야 한다

import (`InkShadow.kt`):

```kotlin
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.DpOffset
```

**토큰 화면에서 바로 시험한다** (`TokenSwatches`의 색 상자). 전 → 후:

```
전                                                  후
Box( Modifier.size(32.dp)                          Box( Modifier.size(32.dp)
        .background(color)                                 .inkShadow(TabijiTheme.shadow.panel, c.shadow)   ← 추가 (맨 앞)
        .border(2.dp, c.ink) )                             .background(color)
                                                           .border(TabijiTheme.border.panel, c.ink) )        ← 토큰
```

같은 김에 `TokenSwatches`의 `spacedBy(4.dp)` → `TabijiTheme.spacing.xs`, `spacedBy(12.dp)` → `TabijiTheme.spacing.md`로 바꾼다. `32.dp`(디버그 상자 크기)는 토큰이 아니니 그대로 두고 `// 디버그 샘플` 주석 범위에 넣는다.

import (`TokenSwatches.kt`에 추가): `com.yamada.studio.tabiji.core.ui.modifier.inkShadow`

✅ 확인: 토큰 화면의 색 상자마다 오른쪽 아래로 **3dp 단색 그림자**가 생긴다. 흐릿함(블러)은 없다. 테마를 바꾸면 그림자 색도 그 테마의 `shadow`로 바뀐다. 실험으로 `.inkShadow(…)`를 체인 **맨 뒤**로 옮겨 보고 무엇이 달라지는지 본다(1-2). 확인 뒤 되돌린다.

### 5-3. `OutlinedText`

`core/ui/component/OutlinedText.kt`. 1-4의 세 겹 트리를 그대로 코드로 옮긴다.

```
OutlinedText(text, style, modifier, fillColor, outlineColor, outlineWidth, shadowOffset)
└── Box (modifier)
    ├── Text(text, style, color = outlineColor, Modifier.offset(shadowOffset.x, shadowOffset.y))   ← 하드 섀도
    ├── Text(text, style.copy(drawStyle = Stroke(width = 선 굵기 px, join = StrokeJoin.Round)),
    │        color = outlineColor)                                                                 ← 아웃라인
    └── Text(text, style, color = fillColor)                                                       ← 본체
```

시그니처 (컨벤션: 필수 데이터 → `modifier` → 선택 파라미터):

```kotlin
@Composable
fun OutlinedText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,                    // 핸드오프: 전 테마 흰 글자
    outlineColor: Color = TabijiTheme.colors.ink,      // 기본값에 Composable 읽기를 써도 된다
    outlineWidth: Dp = TabijiTheme.border.panel,       // 바깥으로 보이는 아웃라인 두께 (2dp)
    shadowOffset: DpOffset = TabijiTheme.shadow.panel, // (3,3)
)
```

- 선 굵기 px: `val strokePx = with(LocalDensity.current) { (outlineWidth * 2).toPx() }`. 2배인 이유는 1-4 참고
- `join = StrokeJoin.Round`: 획이 꺾이는 모서리를 둥글게 이어서 뾰족한 돌기가 안 생기게 한다
- 세 `Text`의 `style`은 같은 것을 쓴다. 크기·자간이 같아야 세 겹이 정확히 포개진다

import (`OutlinedText.kt`):

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
```

✅ 확인: 아직 아무도 안 쓰니 5-4에서 본다.

### 5-4. 워드마크·부제에 입히기

**전 → 후** (`IntroScreen`의 가운데 스택):

```
전                                              후
Column (가운데 스택)                             Column (가운데 스택)
├── Text  워드마크, color = White                ├── OutlinedText  워드마크
│                                               │     outlineWidth = 2dp (기본), shadowOffset = (3,3) (기본)
└── Text  부제, color = White                    └── OutlinedText  부제
                                                      outlineWidth = 1.5dp, shadowOffset = (2,2)
```

- 부제 값은 `10_intro.md`의 "같은 처리의 축소판 (아웃라인 1.5dp, 섀도 (2,2) ink)"이다. 1.5dp는 `TabijiTheme.border.chip`과 같은 값이지만 **뜻이 다르니** 빌려 쓰지 않는다. 대신 `IntroScreen.kt` 안에 이름 붙은 상수로 둔다 (5-5)
- 워드마크와 부제 사이 간격 6dp(`intro_desktop.html`의 `gap: 6px`)도 `verticalArrangement = Arrangement.spacedBy(…)`로 넣는다

import (`IntroScreen.kt`에 추가): `com.yamada.studio.tabiji.core.ui.component.OutlinedText`, `androidx.compose.ui.unit.DpOffset`

✅ 확인:
- 네 계절 칸 어디 위에서도 "Tabiji"와 부제가 또렷하다. 흰 글자 둘레에 잉크 테두리, 오른쪽 아래로 잉크 그림자가 있다
- Phone 프리셋에서도 같다 (워드마크 46sp에 같은 두께)
- 테마를 바꾸면 아웃라인·그림자 색이 그 테마의 `ink`로 부드럽게 바뀐다
- (권장) 실제 일러스트로 확인: A6 4-3과 같은 방법으로 잠깐 덮어쓰고, 확인 뒤 반드시 `git restore`

### 5-5. 이름 없는 `.dp` 정리

이번 단계의 규칙: **화면 코드에 이름 없는 dp 숫자를 남기지 않는다.** 토큰에 있는 값은 토큰으로, 인트로에만 있는 값은 `IntroScreen.kt` 맨 아래에 **이름 붙은 `private val` 상수**로 둔다.

| 지금 | 바꿀 것 | 근거 |
| --- | --- | --- |
| 사분면 `spacedBy(3.dp)` ×3 | `private val QuadrantInkGap = 3.dp` | `10_intro.md` "grid gap 3" (인트로 전용, 토큰에 없음) |
| `310.dp` / `360.dp` | `private val StackWidthCompact = 310.dp`, `StackWidthWide = 360.dp` | `10_intro.md` 스택 폭 |
| 부제 아웃라인 1.5 · 섀도 (2,2) | `private val SubtitleOutline = 1.5.dp`, `SubtitleShadow = DpOffset(2.dp, 2.dp)` | `10_intro.md` 부제 마감 |
| 워드마크·부제 간격 6 | `private val WordmarkGap = 6.dp` | `intro_desktop.html` |

- 상수 이름은 `conventions.md`의 `UPPER_SNAKE_CASE`를 따라도 된다 (`QUADRANT_INK_GAP`). Compose 관례로는 `PascalCase`도 흔하다. 한 가지로 통일한다
- `46.sp`·`52.sp`·`17.sp`는 글꼴 크기라 이번 규칙 대상이 아니다. 같은 방식으로 이름을 붙여도 좋다

✅ 확인:

```bash
grep -rnE '[0-9.]+\.dp' shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature
```

결과가 `private val … = …dp` 선언 줄에만 나온다.

---

## 6. (선택) 워드마크 아래 부드러운 그림자

`10_intro.md`는 워드마크에 한해 **딱 한 장의 블러 그림자**를 허용한다: `(0, 10, 24, #000 35%)`. 가독성 보조용이다. `OutlinedText`의 **맨 아래 섀도 `Text`** 스타일에 `shadow = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(0f, 10dp를 px로), blurRadius = 24dp를 px로)`를 더하면 된다. 이게 v2에서 유일한 블러다. 하지 않아도 완료 조건에는 영향이 없다.

---

## 7. 흔한 오류와 원인 읽기

| 증상 | 원인 |
| --- | --- |
| 그림자가 흐릿하다 | `Modifier.shadow(…)`(elevation)를 씀 → `inkShadow`로 |
| 그림자가 본체 위를 덮어 상자 일부가 잉크색으로 칠해진다 | `inkShadow`를 `background` **뒤**에 둠 → `background`가 먼저 면을 칠하고, 그다음에 그림자가 그 **위**에 그려짐. 체인 맨 앞으로 (1-2, 5-2 실험) |
| 그림자가 각진데 본체는 둥글다 | `inkShadow`에 `shape`를 안 넘김 (기본 `RectangleShape`) |
| `Unresolved reference: toPx` | `drawBehind` 블록 **밖**에서 씀. 밖에서는 `with(LocalDensity.current) { … }` |
| 아웃라인이 너무 얇다 | `Stroke` 굵기를 2dp로 줌 → 바깥으로는 1dp만 보임. 4dp(2배)로 |
| 세 겹이 어긋나 보인다 | 세 `Text`의 `style`이 다르다 (자간·크기 한쪽만 바꿈) |
| 아웃라인 모서리에 뾰족한 가시 | `StrokeJoin.Round` 빠짐 |
| `Overload resolution ambiguity: Stroke` | `drawscope.Stroke`와 다른 `Stroke`를 둘 다 import |
| `TabijiTheme.border`가 `@Composable` 오류 | getter에 `@Composable`을 붙임 → 상수라 필요 없다 (5-1) |

## 8. 완료 조건 (모두 눈으로)

- [ ] 토큰 화면: 색 상자마다 (3,3) 단색 하드 섀도, 테마 전환 시 색도 따라 바뀜
- [ ] 인트로: 네 계절 위 어디서도 워드마크·부제가 또렷함 (흰 글자 + 잉크 아웃라인 + 하드 섀도). Desktop·Phone 프리셋 둘 다
- [ ] (권장) 실제 일러스트로 로컬 확인 → 되돌린 뒤 `git status`에 `intro_*.webp` 없음
- [ ] `grep 'Modifier.shadow\|elevation'` 0건, 5-5의 `.dp` grep이 상수 선언 줄에만
- [ ] 코드 리뷰: 치수는 상수 `object`(CompositionLocal 아님) · `inkShadow`는 체인 맨 앞 · `OutlinedText`는 상태 없음
- [ ] PR의 `client-ci` 초록

## 9. 끝나면 붙여넣어 줄 것

1. 새 파일 셋(`TabijiDimens.kt`, `InkShadow.kt`, `OutlinedText.kt`)과 `IntroScreen.kt`·`TokenSwatches.kt`·`TabijiTheme.kt` 변경분
2. 스크린샷: 인트로 Desktop·Phone 프리셋, 토큰 화면 색 상자 부분
3. 5-2 실험(`inkShadow`를 맨 뒤로 옮겼을 때) 결과 한 줄

막히면 단계 번호와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): add ink drawing primitives and outline the intro wordmark`, `Refs: A8`)과 push를 하고, #12 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A9(잉크 마감 ②: chamfer 메뉴 버튼과 눌림)로 간다.
