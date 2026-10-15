# A9 — 잉크 마감 ② 버튼: chamfer 모양 · 메뉴 버튼 · 눌림

> 브랜치: `client/feat-a9-ink-buttons` (Claude가 만들어 둠, A8 PR #13 위에 쌓음) · 커밋 footer `Refs: A9`
> 시각 기준: handoff-v2 `docs/10_intro.md`(버튼 행 1~3), `docs/03_components.md`(버튼 표·chamfer 클립 패스), `docs/20_motion.md`(press 80ms), `source/intro_desktop.html`·`intro_phone.html`, `images/screens/intro_desktop.png`
> 확인은 **Desktop**에서 한다 (Phone 프리셋 포함)

## 목표

인트로 가운데 스택에 **메뉴 버튼 3개**를 단다. 이것으로 Phase A의 인트로 화면이 완성된다.

| 버튼 | 면 | 글자 | 모양 | 높이 Desktop / Phone |
| --- | --- | --- | --- | --- |
| **시작하기** · 새 여행 일정 만들기 | `accent` | 흰색 (부제 85%) | **chamfer**: 우상단 8dp 사선 | 58 / 56 |
| **내 여행** · 저장된 일정 보기 | `card` | 제목 `ink`, 부제 `sub` | 둥근 모서리 10 | 52 / 50 |
| **설정** · 테마 · 언어 · 데이터 | `card` | 제목 `ink`, 부제 `sub` | 둥근 모서리 10 | 52 / 50 |

공통 마감은 이렇다.
- 2dp 잉크 테두리
- 하단 **(0,4) 잉크 하드 섀도** (A8의 `inkShadow`)
- **누르면 가라앉는다**: 2dp 내려가면서 섀도가 줄어든다 (80ms, 뗄 때 120ms). 스프링·바운스·확대는 없다 (v2 원칙 7)
- 버튼을 누르면 로그만 남긴다. 실제 화면 이동은 B3(내비게이션)에서 한다

> 범위: 버튼 왼쪽 아이콘(📍·📅·⚙)과 오른쪽 쉐브론(›)은 **자리만** 둔다. 핸드오프 아이콘 22종은 SVG라서 Compose용 벡터로 바꾸는 작업이 필요하다. 그 작업은 B1(T4)에서 한꺼번에 하고, 그때 이 자리를 채운다.

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [Shape와 커스텀 모양](https://developer.android.com/develop/ui/compose/graphics/draw/shapes) | `Shape` 인터페이스와 `createOutline`. chamfer의 핵심 |
| [Interaction 다루기](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/handling-interactions) | `InteractionSource`와 "눌렸나?"를 상태로 받는 법 |
| [값 기반 애니메이션](https://developer.android.com/develop/ui/compose/animation/value-based) | `animateDpAsState` (A5의 `animateColorAsState`와 같은 가족) |
| [Slot 기반 레이아웃](https://developer.android.com/develop/ui/compose/layouts/basics#slot-based-layouts) | 껍데기 컴포넌트에 내용물을 람다로 넘기는 패턴 (1-5) |

---

## 1. 핵심 개념

### 1-1. 커스텀 `Shape` — 크기를 받아 윤곽을 돌려주는 객체

`RoundedCornerShape(10.dp)`, `RectangleShape`는 Compose가 준비해 둔 `Shape`다. `Shape`는 메서드 하나짜리 인터페이스다.

```kotlin
interface Shape {
    fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline
}
```

"이 크기(`size`)의 요소라면 윤곽은 이렇다"를 계산해 돌려주는 함수다. 버튼 크기가 바뀌어도(Desktop 58 ↔ Phone 56) 같은 규칙으로 다시 계산된다. 우리는 이 인터페이스를 구현한 **`ChamferShape`** 클래스를 만든다.

- `size`는 **픽셀**이다. 8dp·12dp 같은 값은 `density`로 픽셀로 바꿔 쓴다 (`with(density) { cut.toPx() }`). A8에서 `drawBehind`와 `LocalDensity`로 하던 것과 같은 변환이다
- 윤곽은 **`Path`** 로 그린다. SVG `<path d="…">`나 HTML Canvas의 `ctx.moveTo/lineTo/arc`와 같은 펜 그리기 API다
- CSS로 치면 `clip-path: path('…')`를 크기에 맞춰 자동으로 다시 만드는 것이다

핸드오프(`03_components.md`)의 chamfer 패스를 그대로 `Path` 호출로 옮긴다. 폭 `W`, 높이 `H`, 꼭짓점 둥글기 `r = 12`, 사선 `c = 8`이다.

| SVG 패스 조각 | 뜻 | `Path` 호출 |
| --- | --- | --- |
| `M12 0` | 왼쪽 위에서 둥글기만큼 오른쪽에서 시작 | `moveTo(r, 0f)` |
| `H(W−8)` | 위 변을 사선 시작점까지 | `lineTo(w - c, 0f)` |
| `L W 8` | **사선**으로 오른쪽 변까지 | `lineTo(w, c)` |
| `V(H−12)` | 오른쪽 변을 아래로 | `lineTo(w, h - r)` |
| `A12 12 … (W−12) H` | 오른쪽 아래 둥근 모서리 | `arcTo(Rect(w - 2r, h - 2r, w, h), 0f, 90f, false)` |
| `H12` | 아래 변을 왼쪽으로 | `lineTo(r, h)` |
| `A12 12 … 0 (H−12)` | 왼쪽 아래 둥근 모서리 | `arcTo(Rect(0f, h - 2r, 2r, h), 90f, 90f, false)` |
| `V12` | 왼쪽 변을 위로 | `lineTo(0f, r)` |
| `A12 12 … 12 0` | 왼쪽 위 둥근 모서리 | `arcTo(Rect(0f, 0f, 2r, 2r), 180f, 90f, false)` |
| `Z` | 닫기 | `close()` |

`arcTo(사각형, 시작각, 쓸어갈 각, false)`는 사각형 안에 내접한 원의 호를 그린다. 각도는 **3시 방향이 0°이고 시계 방향이 +** 다 (화면은 y가 아래로 커지므로). 그래서 오른쪽 아래 모서리는 0°에서 90°까지, 왼쪽 아래는 90°에서 180°까지, 왼쪽 위는 180°에서 270°까지다.

### 1-2. 같은 모양을 세 번 쓴다

모양 하나를 **섀도·면·테두리** 셋에 똑같이 넘겨야 버튼이 한 장의 종이처럼 보인다.

```kotlin
Modifier
    .inkShadow(offset, ink, shape)   // A8: 섀도도 chamfer 모양으로
    .background(fill, shape)         // 면
    .border(2.dp, ink, shape)        // 테두리가 사선을 따라감
```

하나라도 빠지면 각진 그림자가 삐져나오거나, 사선 부분에 테두리가 안 그려진다.

### 1-3. "눌렸나?"를 상태로 받기 — `InteractionSource`

`clickable`은 누르고 떼는 순간마다 **상호작용 이벤트**(Press, Release, Cancel)를 만든다. 그 이벤트가 흘러가는 통로가 `MutableInteractionSource`다.

```
clickable(interactionSource = source)  ──Press/Release 이벤트를 발행──▶  source
                                                                          │ 구독
                                         val pressed by source.collectIsPressedAsState()   (true/false 상태)
```

- **ROS 2로 치면** `source`는 토픽이다. `clickable`이 퍼블리셔, `collectIsPressedAsState()`가 "지금 눌린 상태인가"를 계속 갱신하는 구독자다
- **React로 치면** `onPointerDown`/`onPointerUp`에서 `setPressed(true/false)`를 하는 것을 Compose가 대신해 준다. CSS의 `:active`와 같은 정보다
- `source`는 버튼이 다시 그려져도 같은 통로여야 하니 `remember { MutableInteractionSource() }`로 만든다 (A4의 `remember`)

### 1-4. 가라앉기 — 오프셋과 섀도를 같이 움직인다

`20_motion.md`의 press는 "translateY 2dp + 하단 섀도 줄이기"다. 핵심은 **두 값을 같은 양만큼 반대로** 움직이는 것이다.

```
평소:     버튼 위치 0   + 섀도 아래로 4   → 섀도 바닥 = 4
눌림:     버튼 위치 +2  + 섀도 아래로 2   → 섀도 바닥 = 4   (그대로)
```

섀도 바닥이 제자리에 있고 버튼만 그 위로 내려앉는다. 그래서 "종이를 눌렀다"는 느낌이 난다. 값은 `animateDpAsState`로 부드럽게 바꾼다.

```kotlin
val sink by animateDpAsState(
    targetValue = if (pressed) 2.dp else 0.dp,
    animationSpec = tween(durationMillis = if (pressed) 80 else 120),   // 누를 때 80, 뗄 때 120
)
// 버튼은 sink만큼 아래로, 섀도는 (4 - sink)만큼
```

`animateDpAsState`는 A5의 `animateColorAsState`의 dp 버전이다. 목표값이 바뀌면 중간값을 프레임마다 돌려준다. CSS로 치면 `transition: transform 80ms`다.

`clickable`에 `indication = null`을 준다. 기본 눌림 효과(회색 덮개)는 v2에 없다. 눌림 피드백은 가라앉기 하나다.

### 1-5. 껍데기와 내용물을 나눈다 — Slot

버튼 3개는 **껍데기**(모양·면·테두리·섀도·눌림)가 같고 **내용물**(제목·부제·색)만 다르다. 그리고 플래너의 "AI 동선 최적화" 버튼(B단계)도 같은 껍데기를 쓴다. 그래서 둘로 나눈다.

| 컴포넌트 | 위치 | 하는 일 |
| --- | --- | --- |
| `InkButton` | `core/ui/component/` (공용) | 껍데기. 모양·면 색·섀도 깊이를 받고, **내용물은 람다로** 받는다 |
| `IntroMenuButton` | `feature/intro/` (인트로 전용) | `InkButton` 안에 제목·부제를 채운다 |

내용물을 람다로 받는 것을 **slot**이라 한다. React의 `children`과 같다.

```jsx
<InkButton fill={accent} shape={chamfer} onClick={…}>
  <Title /><Subtitle />
</InkButton>
```

```kotlin
InkButton(fill = c.accent, shape = ChamferShape(), onClick = { … }) {
    // 여기가 slot (RowScope라서 weight를 쓸 수 있다)
}
```

slot 람다의 타입을 `@Composable RowScope.() -> Unit`으로 하면, 껍데기 안쪽이 `Row`일 때 내용물에서 `Modifier.weight(1f)`를 쓸 수 있다 (A3의 스코프 원리).

### 1-6. 눌림 상태는 버튼 안에 둬도 된다

A4부터 "상태는 위로 올린다"를 지켜 왔다. 그런데 "지금 손가락이 눌려 있나"는 **그 버튼 말고는 아무도 알 필요가 없는 순간적인 UI 상태**다. `conventions.md`의 "remember는 UI 전용 일시 상태(펼침 여부, 스크롤 위치)에만"에 해당한다. 그래서 `InkButton` 안에 둔다. React에서도 `:active` 같은 상태를 부모로 올리지 않는 것과 같다.

| 상태 | 누가 알아야 하나 | 어디에 |
| --- | --- | --- |
| 선택된 계절 (A4) | 네 칸 모두 | 부모 `IntroScreen` |
| 눌렸나 (A9) | 그 버튼 자신 | `InkButton` 안 |

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| 인터페이스 구현 클래스 | `class ChamferShape(…) : Shape { override fun createOutline(…) … }` | A2(M1)에서 본 상속과 같은 `:` 문법. 인터페이스는 생성자 괄호 `()` 없이 쓴다 | `class ChamferShape implements Shape` |
| 생성자 프로퍼티 + 기본값 | `class ChamferShape(private val cut: Dp = 8.dp, …)` | 생성자 인자를 그대로 필드로. `private`이면 밖에서 안 보인다 | `constructor(private cut = 8)` |
| `apply { }` | `Path().apply { moveTo(…); lineTo(…) }` | 객체를 만들고 그 객체를 `this`로 여러 호출을 한 뒤 **그 객체를 돌려준다** | 빌더 체이닝 |
| 람다 타입의 리시버 | `content: @Composable RowScope.() -> Unit` | 람다 안의 `this`가 `RowScope`가 된다 (1-5) | — |
| `Float` 연산 | `w - 2 * r` | 픽셀 값은 `Float`. `2 * r`처럼 `Int * Float`도 된다 | — |

---

## 3. 만들 것

### 파일

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/…/core/ui/shape/ChamferShape.kt` (새 파일) | `class ChamferShape(cut, corner) : Shape` |
| `shared/…/core/ui/component/InkButton.kt` (새 파일) | 껍데기 버튼: 모양·면·테두리·섀도·눌림·slot |
| `shared/…/feature/intro/IntroMenuButton.kt` (새 파일) | 인트로 메뉴 버튼 하나 (제목 + 부제) |
| `shared/…/feature/intro/IntroScreen.kt` (수정) | 가운데 스택에 버튼 3개 |
| `shared/src/commonMain/composeResources/values/strings.xml` (수정) | 버튼 글자 6개 |
| `shared/…/core/ui/theme/TokenSwatches.kt` (수정) | chamfer 견본 하나 (디버그) |

### 인트로 가운데 스택 — 전 → 후

```
전 (A8)                                         후 (A9)
Column (가운데 스택, width 360/310,              Column (가운데 스택, width 360/310,
        spacedBy WordmarkGap)                           spacedBy StackGap 24/20)
├── OutlinedText 워드마크                        ├── Column (워드마크 블록, spacedBy WordmarkGap)
└── OutlinedText 부제                            │   ├── OutlinedText 워드마크
                                                │   └── OutlinedText 부제
                                                └── Column (메뉴, spacedBy TabijiTheme.spacing.md = 12)
                                                    ├── IntroMenuButton 시작하기  primary   height 58/56
                                                    ├── IntroMenuButton 내 여행   secondary height 52/50
                                                    └── IntroMenuButton 설정      secondary height 52/50
```

### 버튼 하나의 트리

```
IntroMenuButton(title, subtitle, primary, height, modifier, onClick)
└── InkButton(fill, shape, shadowDepth = 4dp, modifier = modifier.fillMaxWidth().height(height), onClick)
    └── Row (padding 가로 16 = spacing.lg, spacedBy 12 = spacing.md, 세로 가운데)     ← InkButton 안쪽
        ├── (아이콘 자리 — B1)
        ├── Column (Modifier.weight(1f))                                              ← slot 내용물
        │   ├── Text 제목   Jua 18/22 (primary) · 16/20 (secondary)
        │   └── Text 부제   Pretendard 11/14 w600
        └── (쉐브론 자리 — B1)
```

`InkButton` 자체의 트리:

```
InkButton(fill, modifier, shape, shadowDepth, onClick, content)
├── interactionSource = remember { MutableInteractionSource() }
├── pressed  = interactionSource.collectIsPressedAsState()
├── sink     = animateDpAsState(pressed ? 2dp : 0dp, 80ms / 120ms)
└── Row(
      modifier
        .offset(y = sink)                                         ← 버튼 전체를 내림
        .inkShadow(DpOffset(0.dp, shadowDepth - sink), ink, shape) ← 섀도는 그만큼 줄임
        .background(fill, shape)
        .border(TabijiTheme.border.panel, ink, shape)
        .clickable(interactionSource, indication = null, onClick)
        .padding(horizontal = spacing.lg),
      horizontalArrangement = spacedBy(spacing.md),
      verticalAlignment = CenterVertically,
    ) { content() }
```

---

## 4. 이름이 겹치는 것 (이번 단계)

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 펜 경로 | `androidx.compose.ui.graphics.Path` | `java.nio.file.Path`, `kotlin.io.path.*`, `org.jetbrains.skia.Path` |
| 사각형 (호의 범위) | `androidx.compose.ui.geometry.Rect` | `java.awt.Rectangle`, `androidx.compose.ui.unit.IntRect` |
| 크기 (px) | `androidx.compose.ui.geometry.Size` | `java.awt.Dimension`, `androidx.compose.ui.unit.DpSize` |
| 윤곽 | `androidx.compose.ui.graphics.Outline` | — |
| 밀도 / 방향 | `androidx.compose.ui.unit.Density`, `androidx.compose.ui.unit.LayoutDirection` | `androidx.compose.ui.text.style.TextDirection` |
| 둥근 모양 | `androidx.compose.foundation.shape.RoundedCornerShape` | `androidx.compose.material3.*Shape` 계열 |
| 상호작용 통로 | `androidx.compose.foundation.interaction.MutableInteractionSource` | — |
| 눌림 상태 | `androidx.compose.foundation.interaction.collectIsPressedAsState` | `collectIsHoveredAsState`(호버), `collectIsFocusedAsState`(포커스) |
| dp 애니메이션 | `androidx.compose.animation.core.animateDpAsState` | `androidx.compose.animation.animateContentSize` (크기 애니메이션, 다른 것) |

---

## 5. 조립 순서

`scripts/linux/run-desktop.sh --hot`. 새 파일·`strings.xml`을 바꾼 단계에서는 앱을 다시 띄운다.

### 5-1. 버튼 글자

`strings.xml`에 6개를 더한다.

| 키 | 값 |
| --- | --- |
| `intro_start_title` | 시작하기 |
| `intro_start_subtitle` | 새 여행 일정 만들기 |
| `intro_trips_title` | 내 여행 |
| `intro_trips_subtitle` | 저장된 일정 보기 |
| `intro_settings_title` | 설정 |
| `intro_settings_subtitle` | 테마 · 언어 · 데이터 |

### 5-2. `ChamferShape`

`core/ui/shape/ChamferShape.kt`:

```kotlin
class ChamferShape(
    private val cut: Dp = TabijiRadius.buttonChamfer,   // 우상단 사선 8dp
    private val corner: Dp = 12.dp,                      // 나머지 세 모서리 (03_components 클립 패스)
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val c = with(density) { cut.toPx() }
        val r = with(density) { corner.toPx() }
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(r, 0f)
            // … 1-1 표의 나머지 조각
            close()
        }
        return Outline.Generic(path)
    }
}
```

- 기본값에서 `TabijiTheme.radius.buttonChamfer`가 아니라 `TabijiRadius.buttonChamfer`를 쓴다. `TabijiTheme`은 화면 쪽 읽기 창구이고, 같은 `core/ui` 안에서는 상수 객체를 바로 써도 된다. 둘은 같은 값이다
- `12.dp`는 핸드오프 클립 패스의 꼭짓점 값이다. 토큰 표(`radius.button = 10`)와 다르다. 핸드오프가 chamfer 버튼만 12로 그렸다. 기본값 자리에 두고 주석으로 출처를 단다

import (`ChamferShape.kt`):

```kotlin
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.theme.TabijiRadius
```

**토큰 화면에 견본 하나** (`TokenSwatches`, 글꼴 구역 다음). 핸드오프 T3의 확인 크기 186×48이다.

```
Box( Modifier.size(186.dp, 48.dp)                  // 디버그 샘플
        .inkShadow(DpOffset(0.dp, 4.dp), c.ink, ChamferShape())
        .background(c.accent, ChamferShape())
        .border(TabijiTheme.border.panel, c.ink, ChamferShape()) )
```

import (`TokenSwatches.kt`에 추가): `com.yamada.studio.tabiji.core.ui.shape.ChamferShape`, `androidx.compose.ui.unit.DpOffset`

✅ 확인: 토큰 화면 아래에 `accent` 색 직사각형이 보이고, **오른쪽 위 모서리만 사선**으로 깎였다. 나머지 세 모서리는 둥글다. 테두리와 아래쪽 4dp 섀도가 사선을 그대로 따라간다.

### 5-3. `InkButton` — 껍데기와 눌림

`core/ui/component/InkButton.kt`. 3장의 `InkButton` 트리를 코드로 옮긴다.

```kotlin
@Composable
fun InkButton(
    fill: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(TabijiTheme.radius.button),
    shadowDepth: Dp = TabijiTheme.shadow.button.y,      // 토큰 기본 (0,3). 인트로는 4dp를 넘김
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
)
```

- 파라미터 순서는 컨벤션 그대로다: 필수 데이터(`fill`) → `modifier` → 선택(`shape`, `shadowDepth`) → 콜백(`onClick`) → trailing content
- 1-3의 눌림 상태와 1-4의 `sink` 계산을 함수 맨 위에 둔다
- `Row`의 modifier 순서가 중요하다. `offset`이 맨 앞이어야 섀도까지 함께 내려가고(그다음 `inkShadow`가 줄인 만큼 보정), `inkShadow`가 `background`보다 앞이어야 섀도가 면 아래에 깔린다 (A8 1-2)
- `clickable(interactionSource = …, indication = null, onClick = onClick)`. A4의 `clickable(interactionSource = null, …)`과 달리 이번에는 **우리 통로**를 넘긴다. 그래야 눌림 이벤트를 받을 수 있다
- `content`는 `Row` 블록 안에서 `content()`로 부른다

import (`InkButton.kt`):

```kotlin
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
```

✅ 확인은 5-4에서 한다.

### 5-4. `IntroMenuButton`과 스택

**`feature/intro/IntroMenuButton.kt`**: 3장의 "버튼 하나의 트리"대로 만든다.

```kotlin
@Composable
fun IntroMenuButton(
    title: String,
    subtitle: String,
    primary: Boolean,          // true = 시작하기 (accent·chamfer), false = 내 여행·설정 (card·둥근)
    height: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
```

- `primary`에 따라 고르는 값 다섯 개: 면(`accent`/`card`), 모양(`ChamferShape()`/`RoundedCornerShape(radius.button)`), 제목 색(`onAccent`/`ink`), 부제 색(`onAccent.copy(alpha = 0.85f)`/`sub`), 제목 크기(18/22 · 16/20)
- 각각 `val fill = if (primary) c.accent else c.card`처럼 `if` 식으로 함수 위쪽에 모아 둔다 (A7의 `stackWidth`와 같은 모양)
- 제목 스타일은 `TabijiTheme.typography.displayM.copy(fontSize = …, lineHeight = …)`, 부제 스타일은 `TabijiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp)`다. caption이 Pretendard SemiBold라서 크기만 바꾼다
- 섀도 깊이 4dp는 인트로 전용 값이다 (`10_intro.md` "(0,4) ink"). `IntroMenuButton.kt` 안에 `private val IntroButtonShadow = 4.dp`로 둔다
- 아이콘·쉐브론 자리에는 `// TODO(B1): 아이콘` 주석만 둔다

**`IntroScreen.kt`**: 3장의 "전 → 후" 트리대로 가운데 스택을 바꾼다.

- 값 세 개를 구간으로 고른다 (A7의 `stackWidth`와 같은 방식). 이름 붙은 상수로 둔다

  | 상수 | Desktop | Phone (`COMPACT`) | 출처 |
  | --- | --- | --- | --- |
  | 스택 간격 (워드마크 블록 ↔ 메뉴) | 24 | 20 | `10_intro.md` gap 24 / `intro_phone.html` gap 20 |
  | 시작하기 높이 | 58 | 56 | `10_intro.md` |
  | 내 여행·설정 높이 | 52 | 50 | `10_intro.md` |

- 메뉴 `Column`의 간격 12는 `TabijiTheme.spacing.md`와 같은 값이라 토큰을 쓴다
- `onClick`은 지금은 로그만 남긴다: `println("Tabiji/intro: start")`처럼 쓰고 `// TODO(B3): 내비게이션`을 단다. 공통 로거는 아직 없다 (`conventions.md`의 "`println` 남발 금지"는 B단계 로거가 생기면 바꾼다)

import (`IntroMenuButton.kt`):

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yamada.studio.tabiji.core.ui.component.InkButton
import com.yamada.studio.tabiji.core.ui.shape.ChamferShape
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
```

`IntroScreen.kt`에는 `tabiji.shared.generated.resources.*`(버튼 글자)만 더 필요하다. `IntroMenuButton`은 같은 패키지라 import가 필요 없다.

✅ 확인:
- Desktop: 워드마크 아래로 24dp 띄우고 버튼 3개가 12dp 간격으로 쌓인다. 폭은 360dp를 꽉 채운다. `intro_desktop.png`와 비교한다
- "시작하기"만 오른쪽 위가 사선이고 `accent` 색이다. "내 여행"·"설정"은 흰 바탕에 잉크 글자, 부제는 회색(`sub`)이다
- 버튼을 **누르고 있으면** 2dp 가라앉고 아래 섀도가 얇아진다. **떼면** 조금 더 천천히 올라온다. 튀거나 커지는 효과는 없다
- 버튼을 누르면 터미널에 로그가 찍힌다. 버튼을 눌러도 뒤의 사분면은 선택되지 않는다
- Phone 프리셋: 폭 310, 높이 56/50, 간격 20
- 테마를 바꾸면 버튼 색(`accent`·`card`·`ink`)이 240ms로 따라 바뀐다

### 5-5. 하드코딩 확인

```bash
grep -rnE '[0-9.]+\.dp' shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature
grep -rn 'Modifier.shadow\|elevation\|spring(' shared/src/commonMain/kotlin
```

첫째는 `private val … = …dp` 선언 줄에만, 둘째는 0줄이어야 한다.

---

## 6. 흔한 오류와 원인 읽기

| 증상 | 원인 |
| --- | --- |
| 사선이 왼쪽 위에 생김 / 모서리가 이상하게 꺾임 | `arcTo` 각도 순서가 틀림. 오른쪽 아래 0→90, 왼쪽 아래 90→180, 왼쪽 위 180→270 (1-1) |
| 모서리가 엄청 크게 둥글다 | `toPx()` 없이 dp 숫자를 픽셀로 씀 (A8의 `Stroke` 실수와 같음) |
| 섀도는 각졌는데 버튼은 사선 | `inkShadow`에 `shape`를 안 넘김 (1-2) |
| 사선 부분에 테두리가 없음 | `border`에 `shape`를 안 넘김 |
| 눌러도 안 가라앉음 | `clickable`에 우리 `interactionSource`를 안 넘김, 또는 `remember` 없이 매번 새 통로를 만듦 |
| 가라앉을 때 섀도도 같이 내려가서 바닥이 움직임 | 섀도 깊이를 `shadowDepth - sink`로 줄이지 않음 (1-4) |
| 가라앉을 때 섀도만 내려가고 버튼은 그대로 | `offset`이 `inkShadow`보다 **뒤**에 있음 |
| 눌릴 때 회색 덮개가 잠깐 뜸 | `indication = null` 빠짐 |
| `Unresolved reference: weight` (slot 안에서) | `content` 타입이 `@Composable () -> Unit` → `@Composable RowScope.() -> Unit` |
| 버튼을 누르면 뒤의 사분면도 선택됨 | 버튼에 `clickable`이 없어 클릭이 아래로 통과함 |

## 7. 완료 조건 (모두 눈으로)

- [ ] 토큰 화면: chamfer 견본 186×48, 우상단만 사선, 테두리·섀도가 사선을 따라감
- [ ] 인트로 Desktop: 버튼 3개(시작하기 chamfer `accent` / 내 여행·설정 `card`), 간격·폭이 `intro_desktop.png`와 같음
- [ ] 누르면 2dp 가라앉고 섀도가 줄어듦, 떼면 복귀 (스프링·확대 없음). 클릭 시 터미널 로그
- [ ] Phone 프리셋: 폭 310, 높이 56/50, 스택 간격 20
- [ ] 테마 전환 시 버튼 색이 따라 바뀜
- [ ] 5-5 grep 기준 통과
- [ ] PR의 `client-ci` 초록

## 8. 끝나면 붙여넣어 줄 것

1. 새 파일 셋(`ChamferShape.kt`, `InkButton.kt`, `IntroMenuButton.kt`)과 `IntroScreen.kt`·`strings.xml`·`TokenSwatches.kt` 변경분
2. 스크린샷: 인트로 Desktop·Phone 프리셋, 토큰 화면의 chamfer 견본, (가능하면) 버튼을 누르고 있는 순간

막히면 단계 번호와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): add chamfered ink menu buttons with press sink to the intro`, `Refs: A9`)과 push를 하고, #13 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A10(Phase A 릴리스 `client-v0.1.0`)으로 간다.
