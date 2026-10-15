# B2 — 플래너 배경과 골격: 줄무늬 종이 · 지도 자리 · 검색 바 · 일정 패널

> 브랜치: `client/feat-b2-planner-skeleton` (Claude가 만들어 둠, B1 PR #22 위에 쌓음) · 커밋 footer `Refs: B2`
> 시각 기준: handoff-v2 `docs/11_planner.md`, `docs/03_components.md`("패널"), `source/planner_desktop.html`(1440×900)·`planner_phone.html`(390×844), `images/screens/planner_desktop.png`·`planner_phone.png`
> 확인은 **Desktop**에서 한다 (Desktop 1440×900 · Phone 390×844 프리셋)

## 목표

앱의 메인 화면인 **플래너**의 뼈대를 세운다. 이번 단계는 **배경과 큰 상자들의 자리**까지다.

```
Desktop 1440×900                                    Phone 390×844
┌───────────────────────────────────────────┐       ┌──────────────┐
│[검색 바 480×48]                ┌─────────┐│       │[검색 바 h44] │
│┌──────────────────────────────┐│Day 2 …  ││       │┌────────────┐│
││                              │├─────────┤│       ││  지도 자리  ││
││        지도 자리 (잉크 프레임)  ││ 목록 자리 ││       ││         [◎]││
││                              ││  (soft) ││       │└────────────┘│
││                          [◎] │├─────────┤│       │╭────────────╮│
││                          [+] ││[AI 버튼] ││       ││ Day 2 …    ││ ← 하단 시트 384
│└──────────────────────────────┘└─────────┘│       ││ 목록 자리   ││
└───────────────────────────────────────────┘       ││ [AI 버튼]  ││
   ↖ 배경: 종이색 + 45° 줄무늬                      └╰────────────╯┘
```

- 배경은 **종이색 위에 45° 줄무늬**다 (v2 원칙 4)
- 지도·검색 바·일정 패널은 전부 **잉크 패널**이다: `card` 면 + 2dp 잉크 테두리 + 모서리 10 + (3,3) 하드 섀도
- 방문지 카드·구간 칩은 B4, 아이콘(돋보기·내 위치·확대·스파크)은 B3에서 채운다. 이번엔 **자리만** 둔다
- 디버그 메뉴에서 인트로·플래너·토큰 화면을 골라 볼 수 있게 한다 (화면 이동은 B6)

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [DrawScope로 그리기](https://developer.android.com/develop/ui/compose/graphics/draw/overview) | `drawLine`·`drawRect`·`clipRect`. 줄무늬를 직접 그린다 |
| [Box와 정렬](https://developer.android.com/develop/ui/compose/layouts/basics#box) | 지도·검색 바·패널을 겹쳐 놓는 법 |
| 핸드오프 `docs/11_planner.md` | 영역별 배치와 치수의 원본 |

---

## 1. 핵심 개념

### 1-1. 줄무늬 배경 = 굵은 사선을 일정 간격으로 반복해서 긋기

핸드오프 CSS는 `repeating-linear-gradient(45deg, transparent 0 16px, stripe 16px 32px)`이다. 폭 16dp 띠와 16dp 틈이 45° 방향으로 반복된다는 뜻이다. Compose에는 반복 그라데이션이 없어서 **굵은 선을 반복해서 긋는다.**

```
↘ 방향 선을 왼쪽 밖에서부터 오른쪽 끝까지 일정 간격으로
    ╲   ╲   ╲   ╲   ╲      선 굵기 = 띠 폭 16dp
     ╲   ╲   ╲   ╲   ╲     선과 선 사이 (띠 + 틈) = 32dp  ← 선에 수직으로 잰 거리
      ╲   ╲   ╲   ╲   ╲    가로로 잰 간격 = 32dp × √2 (45°로 기울어서)
```

```kotlin
fun Modifier.paperStripes(paper: Color, stripe: Color, band: Dp): Modifier = this.drawBehind {
    drawRect(paper)                                   // ① 종이색으로 전부 칠하고
    val b = band.toPx()
    val step = 2 * b * sqrt(2f)                       // 띠 + 틈을 가로 간격으로 환산
    clipRect {                                        // ② 이 영역 밖으로 선이 삐져나가지 않게
        var x = -size.height                          // 왼쪽 밖에서 시작해야 왼쪽 아래 모서리까지 덮인다
        while (x < size.width) {
            drawLine(stripe, start = Offset(x, 0f), end = Offset(x + size.height, size.height), strokeWidth = b)
            x += step
        }
    }
}
```

- `drawBehind` 블록은 이 요소의 영역 밖에도 그릴 수 있다. 사선 끝이 바깥으로 나가므로 `clipRect { }`로 자른다. CSS의 `overflow: hidden`이다
- `while`은 Kotlin의 반복문이다 (C++·JS와 같다). `var x`는 바뀌는 변수다
- 띠 폭은 Desktop 16dp, Phone 14dp다 (`tokens.json` `stripeSpec`). 너비 구간으로 고른다

### 1-2. 잉크 패널 = Modifier 네 개를 하나로 묶기

지도·검색 바·일정 패널 모두 같은 마감이다. A8·A9에서 만든 부품을 **한 줄로 묶는 확장 함수**를 만든다. CSS로 치면 여러 속성을 묶은 `.ink-panel` 클래스다.

```kotlin
fun Modifier.inkPanel(colors: TabijiColors, shape: Shape = RoundedCornerShape(TabijiRadius.panel)): Modifier =
    this.inkShadow(TabijiShadow.panel, colors.shadow, shape)    // 섀도 (맨 아래)
        .background(colors.card, shape)                        // 면
        .border(TabijiBorder.panel, colors.ink, shape)         // 테두리
        .clip(shape)                                           // 안쪽 내용(지도 그림·헤더)이 둥근 모서리 밖으로 안 나가게
```

**색을 파라미터로 받는 이유**: `Modifier` 확장 함수는 `@Composable`이 아니라서 `TabijiTheme.colors`(CompositionLocal)를 직접 읽을 수 없다. 부르는 쪽(Composable 안)에서 `Modifier.inkPanel(TabijiTheme.colors)`처럼 넘긴다. Compose 공식 가이드도 Modifier 팩토리를 `@Composable`로 만들지 말라고 권한다.

### 1-3. 겹쳐 놓기 = `Box` + 바깥 여백

핸드오프 CSS는 전부 `position: absolute`다. Compose에서는 `Box` 안에 자식을 겹쳐 놓고, **바깥 여백(padding)과 정렬(align)로 자리를 잡는다.**

| CSS | Compose (`Box` 안의 자식) |
| --- | --- |
| `left: 16px; top: 76px; right: 408px; bottom: 16px` | `Modifier.padding(start = 16.dp, top = 76.dp, end = 408.dp, bottom = 16.dp).fillMaxSize()` |
| `right: 16px; top: 16px; bottom: 16px; width: 376px` | `Modifier.align(Alignment.TopEnd).padding(16.dp).width(376.dp).fillMaxHeight()` |
| `left: 16px; top: 16px; width: 480px; height: 48px` | `Modifier.padding(16.dp).size(480.dp, 48.dp)` (기본 정렬이 TopStart) |
| `left: 0; right: 0; bottom: 0; height: 384px` | `Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(384.dp)` |

A3의 규칙 그대로, **`padding`이 앞에 오면 바깥 여백**이다. 그다음의 `fillMaxSize`는 여백을 뺀 나머지를 채운다. 지도 프레임 오른쪽 `408 = 패널 폭 376 + 여백 16 + 틈 16`이라, 창 폭이 바뀌어도 지도와 패널 사이가 16dp로 유지된다.

### 1-4. 너비 구간마다 다른 골격

A7과 같다. `COMPACT`면 `PhonePlanner`, 아니면 `DesktopPlanner`를 그린다. 두 골격은 같은 부품(`MapFrame`, `PlannerSearchBar`, `DayPanel`의 내용)을 **다른 자리에** 놓는다.

### 1-5. 디버그 화면 고르기 — Boolean 대신 enum

지금 `main.kt`는 `showTokens: Boolean`으로 토큰 화면을 켠다. 화면이 셋(인트로·플래너·토큰)이 되면 Boolean 두 개로는 "둘 다 켜짐" 같은 말이 안 되는 조합이 생긴다 (A4의 `Emphasis` 고민과 같다). 그래서 `enum class DebugScreen { INTRO, PLANNER, TOKENS }` 하나로 바꾼다. 메뉴는 A5의 테마 메뉴처럼 `RadioButtonItem`으로 만든다.

### 1-6. 지도 자리 그림은 SVG

핸드오프의 지도 목업 `assets/map/map_light.svg`를 그대로 `composeResources/drawable/planner_map_placeholder.svg`로 쓴다. Compose Resources는 **Desktop에서 SVG를 그대로** 그린다. Android는 SVG를 못 읽지만, 이 자리는 C2에서 실제 지도(MapLibre)로 바뀌고 Android 확인은 Phase M이라 지금은 상관없다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| `while` | `while (x < w) { … }` | 조건이 참인 동안 반복 | `while (x < w) { … }` |
| `var` 지역 변수 | `var x = -size.height` | 다시 대입할 수 있는 변수 (`val`은 못 함) | `let` |
| `sqrt` | `sqrt(2f)` | `kotlin.math.sqrt`. Float 버전을 쓰려고 `2f` | `Math.sqrt(2)` |
| `RoundedCornerShape(topStart = …, topEnd = …)` | 위 두 모서리만 둥글게 | 이름 붙인 인자로 모서리를 골라 줌 | `border-radius: 18px 18px 0 0` |

---

## 3. 만들 것

### 파일

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/…/core/ui/modifier/PaperStripes.kt` (새 파일) | `fun Modifier.paperStripes(paper, stripe, band)` |
| `shared/…/core/ui/modifier/InkPanel.kt` (새 파일) | `fun Modifier.inkPanel(colors, shape)` |
| `shared/…/feature/planner/PlannerScreen.kt` (새 파일) | `PlannerScreen` (구간으로 골격 선택), `DesktopPlanner`, `PhonePlanner` |
| `shared/…/feature/planner/PlannerParts.kt` (새 파일) | `MapFrame`, `PlannerSearchBar`, `DayPanelHeader`, `DayPanelFooter`, `MapControls` |
| `shared/…/feature/planner/PlannerDummy.kt` (새 파일) | 헤더용 더미 값 (B4에서 data class로 바뀜) |
| `shared/src/commonMain/composeResources/drawable/planner_map_placeholder.svg` | 핸드오프 `map_light.svg` 복사 |
| `shared/src/commonMain/composeResources/values/strings.xml` | 검색 안내·검색·AI 버튼 글자 |
| `shared/…/app/App.kt` · `desktopApp/…/main.kt` | 디버그 화면 enum과 메뉴 |

### Desktop 트리 (`DesktopPlanner`)

```
Box (modifier.fillMaxSize().paperStripes(paper, stripe, band = 16.dp))
├── MapFrame          Modifier.padding(start = 16, top = 76, end = 408, bottom = 16).fillMaxSize()
│   └── (MapFrame 안) Box(modifier.inkPanel(c))
│       ├── Image(planner_map_placeholder, ContentScale.Crop, fillMaxSize)
│       └── MapControls  Modifier.align(BottomEnd).padding(12.dp)     ← 46×46 ×2, 간격 8
├── PlannerSearchBar  Modifier.padding(16.dp).size(480.dp, 48.dp)
└── Column (일정 패널)  Modifier.align(TopEnd).padding(16.dp).width(376.dp).fillMaxHeight().inkPanel(c)
    ├── DayPanelHeader   height 52, 배경 headers[0](h1), 아래 2dp 잉크 선
    ├── Box (목록 자리)   Modifier.weight(1f).fillMaxWidth().background(c.soft)   ← B4에서 LazyColumn
    └── DayPanelFooter   위 2dp 잉크 선, card 면, AI 버튼 348×50 가운데
```

### Phone 트리 (`PhonePlanner`)

```
Box (modifier.fillMaxSize().paperStripes(paper, stripe, band = 14.dp))
├── PlannerSearchBar  Modifier.padding(start = 12, top = 10, end = 12).fillMaxWidth().height(44.dp)
├── MapFrame          Modifier.padding(start = 12, top = 64, end = 12, bottom = 396).fillMaxSize()
│                     (MapControls: 44×44 하나, 안쪽 여백 10)
└── Column (하단 시트) Modifier.align(BottomCenter).fillMaxWidth().height(384.dp)
                          .inkShadow(DpOffset(0.dp, (-4).dp), c.shadow, sheetShape)
                          .background(c.card, sheetShape).border(2.dp 토큰, c.ink, sheetShape).clip(sheetShape)
    │                  sheetShape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    ├── Box (그랩바 자리)   높이 16, 가운데에 36×4 막대 (ink 30%)
    ├── DayPanelHeader     height 44
    ├── Box (목록 자리)     weight(1f), soft
    └── DayPanelFooter     AI 버튼 폭 = 시트 폭 − 24, 높이 50
```

시트 섀도는 위로 `(0, −4)`다. 아래가 화면 끝이라 섀도가 위쪽에 깔린다.

### 부품 트리

```
PlannerSearchBar(modifier)                          ← 정적. 입력은 나중 단계
└── Row (modifier.inkPanel(c), 세로 가운데, padding start 14 / end 8, spacedBy 10)
    ├── (돋보기 아이콘 자리 20dp — B3)
    ├── Text(검색 안내 글자, style = typography.body, color = c.sub, Modifier.weight(1f))
    └── InkButton(fill = c.accent, height 34, shape = RoundedCornerShape(8.dp), shadowDepth = 2.dp) { Text("검색", displayS, onAccent) }

DayPanelHeader(height, modifier)
└── Row (fillMaxWidth().height(height).background(c.headers[0]), 세로 가운데, padding 가로 14, spacedBy 10)
    ├── Text(DUMMY_DAY_TITLE "Day 2",            style = typography.displayL, color = Color.White)
    ├── Text(DUMMY_DAY_DATE "9월 20일 (토) · 시즈오카", style = typography.body.copy(fontSize = 13.sp), color = Color.White.copy(alpha = 0.92f))
    ├── Spacer(Modifier.weight(1f))
    └── Text(DUMMY_DAY_MOVE "이동 1시간 12분", style = typography.badge.copy(fontSize = 12.sp),
             Modifier.background(Color.White, CircleShape).border(2.dp 토큰, c.ink, CircleShape).padding(horizontal = 10.dp, vertical = 2.dp))
(아래 2dp 잉크 선: 헤더 Row 다음에 Box(fillMaxWidth().height(2.dp 토큰).background(c.ink)))
```

`CircleShape`(완전 둥근 알약)는 `androidx.compose.foundation.shape.CircleShape`다.

### 치수 상수 (`PlannerParts.kt`·`PlannerScreen.kt` 맨 아래 `private val`)

| 상수 | 값 | 출처 |
| --- | --- | --- |
| `DayPanelWidth` | `376.dp` | `11_planner.md` |
| `MapFrameTop` / `MapFrameEndDesktop` | `76.dp` / `408.dp` | `planner_desktop.html` |
| `SearchBarSize` | `DpSize(480.dp, 48.dp)` | 같은 곳 |
| `DayHeaderHeightDesktop` / `Phone` | `52.dp` / `44.dp` | `03_components.md` |
| `AiButtonSizeDesktop` | `DpSize(348.dp, 50.dp)` | `11_planner.md` |
| `SheetHeight` / `SheetCorner` | `384.dp` / `18.dp` | `planner_phone.html`, 토큰 `radius.sheetTop` |
| `MapFrameTopPhone` / `MapFrameBottomPhone` | `64.dp` / `396.dp` | `planner_phone.html` |
| `StripeBandDesktop` / `Phone` | `16.dp` / `14.dp` | `tokens.json` `stripeSpec` |

여백 12·16, 간격 8·10은 토큰(`spacing.md`, `spacing.lg`, `spacing.sm`)을 쓴다. 토큰에 없는 10·14는 이름 붙인 상수로 둔다.

---

## 4. 이름이 겹치는 것 (이번 단계)

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 좌표 (px) | `androidx.compose.ui.geometry.Offset` | `androidx.compose.ui.unit.DpOffset` (dp, 그리기 밖에서) |
| 잘라 그리기 | `androidx.compose.ui.graphics.drawscope.clipRect` | `androidx.compose.ui.draw.clip` (Modifier, 다른 것) |
| 제곱근 | `kotlin.math.sqrt` | `java.lang.Math.sqrt` (Double 전용, 공통 코드 아님) |
| 알약 모양 | `androidx.compose.foundation.shape.CircleShape` | `androidx.compose.material3.*` 모양 |
| 빈 칸 | `androidx.compose.foundation.layout.Spacer` | — |

---

## 5. 조립 순서

`scripts/linux/run-desktop.sh --hot`. 새 파일·리소스·`main.kt`를 바꾼 단계에서는 재시작한다. **Hot Reload로 띄운 창은 모니터를 옮기지 않는다** (worklog 열린 일: JBR에서 메뉴바가 사라지는 증상).

### 5-1. 디버그 화면 고르기 (먼저 해 두면 5-2부터 플래너를 바로 본다)

**전 → 후**

```
전 (main.kt)                                       후
var showTokens by remember { … false }             var screen by remember { mutableStateOf(DebugScreen.INTRO) }
Menu("Debug")                                      Menu("Screen")                                   ← 새 메뉴
├── CheckboxItem("Token Page", …)                  │   └── DebugScreen.entries.forEach { s -> RadioButtonItem(s.name, selected = s == screen, onClick = { screen = s }) }
├── Item("Phone 390×844")                          Menu("Debug")
└── Item("Desktop 1440×900")                       ├── Item("Phone 390×844")
                                                   └── Item("Desktop 1440×900")
App(palette, showTokens)                           App(palette, screen)

전 (App.kt)                                        후
if (showTokens) TokenSwatches(…) else IntroScreen()  when (screen) { INTRO -> IntroScreen(); PLANNER -> PlannerScreen(); TOKENS -> TokenSwatches(Modifier.fillMaxSize()) }
```

- `enum class DebugScreen { INTRO, PLANNER, TOKENS }`은 `app/DebugScreen.kt`에 둔다 (`shared`). `main.kt`가 쓰려면 공개(`public`, 기본값)여야 한다
- `App(palette: TabijiPalette = TabijiPalette.CLOUD, screen: DebugScreen = DebugScreen.INTRO)`. Android(Phase M)에서는 인자 없이 부른다
- `PlannerScreen`은 5-4에서 만든다. 지금은 빈 `@Composable fun PlannerScreen(modifier: Modifier = Modifier) {}`만 두면 메뉴부터 동작한다

✅ 확인: 메뉴바에 Screen이 생기고 INTRO·PLANNER·TOKENS가 전환된다 (PLANNER는 아직 빈 화면).

### 5-2. `paperStripes`

1-1의 코드를 `PaperStripes.kt`에 만들고, 빈 `PlannerScreen`에 `Box(modifier.fillMaxSize().paperStripes(c.paper, c.stripe, 16.dp))`만 그린다.

import (`PaperStripes.kt`):

```kotlin
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import kotlin.math.sqrt
```

✅ 확인: PLANNER 화면 전체가 연한 종이색에 ↘ 방향 줄무늬. `planner_desktop.png`의 바탕과 비교한다. 테마를 바꾸면 종이·줄무늬 색이 함께 바뀐다.

### 5-3. `inkPanel`

1-2의 코드를 `InkPanel.kt`에 만든다.

import (`InkPanel.kt`):

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.yamada.studio.tabiji.core.ui.theme.TabijiBorder
import com.yamada.studio.tabiji.core.ui.theme.TabijiColors
import com.yamada.studio.tabiji.core.ui.theme.TabijiRadius
import com.yamada.studio.tabiji.core.ui.theme.TabijiShadow
```

`inkShadow`는 같은 패키지(`core.ui.modifier`)라 import가 필요 없다.

### 5-4. Desktop 골격

1. 지도 그림 복사:
   ```bash
   cp ../design/handoff/handoff-v2/assets/map/map_light.svg shared/src/commonMain/composeResources/drawable/planner_map_placeholder.svg
   ```
2. `strings.xml`에 세 개 추가: `planner_search_hint` = 장소, 주소, 역 이름 검색 / `planner_search` = 검색 / `planner_ai_optimize` = AI 동선 최적화
3. `PlannerDummy.kt`: `internal const val DUMMY_DAY_TITLE = "Day 2"`, `DUMMY_DAY_DATE`, `DUMMY_DAY_MOVE`. 더미 데이터라 `strings.xml`이 아니라 별도 파일이다 (`conventions.md` "미리보기용 더미 데이터는 별도 파일")
4. `PlannerParts.kt`의 부품과 `PlannerScreen.kt`의 `DesktopPlanner`를 3장 트리대로 만든다
5. `PlannerScreen`: `when (LocalWidthClass.current) { COMPACT -> PhonePlanner(modifier); else -> DesktopPlanner(modifier) }` (5-5 전까지는 둘 다 `DesktopPlanner`여도 된다)

import (`PlannerParts.kt`·`PlannerScreen.kt` 공통으로 쓰게 되는 것):

```kotlin
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yamada.studio.tabiji.core.ui.component.InkButton
import com.yamada.studio.tabiji.core.ui.layout.LocalWidthClass
import com.yamada.studio.tabiji.core.ui.layout.WidthClass
import com.yamada.studio.tabiji.core.ui.modifier.inkPanel
import com.yamada.studio.tabiji.core.ui.modifier.inkShadow
import com.yamada.studio.tabiji.core.ui.modifier.paperStripes
import com.yamada.studio.tabiji.core.ui.shape.ChamferShape
import com.yamada.studio.tabiji.core.ui.theme.TabijiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.*
```

✅ 확인 (Debug ▸ Desktop 1440×900, Screen ▸ PLANNER):
- 줄무늬 바탕 위에 왼쪽 위 검색 바, 왼쪽 큰 지도 프레임, 오른쪽 일정 패널
- 지도 프레임 모서리가 둥글고, 지도 그림이 그 모서리 밖으로 안 나간다 (`clip`)
- 일정 패널: 파란 머리(h1) "Day 2 · 9월 20일 (토) · 시즈오카 · [이동 1시간 12분]", 연한 목록 자리, 아래 AI 버튼(chamfer)
- `planner_desktop.png`와 **외곽**(상자 위치·크기)이 같다. 안쪽 카드는 아직 없다

### 5-5. Phone 골격

3장 Phone 트리대로 `PhonePlanner`를 만든다. 같은 부품을 쓰고, 자리·크기만 다르다.

✅ 확인 (Debug ▸ Phone 390×844): 위 검색 바, 가운데 지도 프레임, 아래 위쪽만 둥근 시트(그랩바·Day 헤더·목록 자리·AI 버튼). `planner_phone.png`와 외곽 비교. 창 폭을 600dp 위아래로 넘나들면 Desktop ↔ Phone 골격이 바뀐다.

### 5-6. 하드코딩 확인

```bash
grep -rnE '[0-9.]+\.dp' shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature/planner | grep -v 'private val'
grep -rn 'Text("' shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature/planner
```

둘 다 0줄이어야 한다. 더미 값은 `PlannerDummy.kt`의 상수로만 쓴다.

---

## 6. 흔한 문제와 원인

| 증상 | 원인 |
| --- | --- |
| 줄무늬가 왼쪽 아래 삼각형에 비어 있음 | `x`를 `0`부터 시작함. `-size.height`부터 시작해야 왼쪽 아래까지 덮인다 |
| 줄무늬가 화면 밖(다른 영역)까지 그려짐 | `clipRect { }` 빠짐 |
| 줄무늬 방향이 ↗ | 선 끝점을 `Offset(x + h, 0f)`처럼 씀. ↘는 `(x, 0) → (x + h, h)` |
| 지도 그림이 둥근 모서리 밖으로 삐져나옴 | `inkPanel`에 `clip(shape)` 빠짐 |
| 패널 섀도가 안 보임 | `inkShadow`가 `background` 뒤에 있음 (A8 1-2) |
| 지도와 패널이 겹침 | 지도 `end` 여백을 `16.dp`로 줌. `408.dp`(= 376 + 16 + 16) |
| 일정 패널이 화면 높이를 다 안 채움 | `fillMaxHeight()` 빠짐, 또는 `padding`을 `fillMaxHeight` 뒤에 둠 |
| `Unresolved reference: planner_map_placeholder` | SVG를 넣고 앱을 재시작하지 않음 (리소스 변경) |
| `when (screen)` 'must be exhaustive' | `DebugScreen` 항목 하나를 빠뜨림 |

## 7. 완료 조건 (모두 눈으로)

- [ ] Screen 메뉴로 INTRO·PLANNER·TOKENS 전환
- [ ] Desktop 1440×900: `planner_desktop.png`와 외곽 일치 (줄무늬 · 검색 바 · 지도 프레임 · 일정 패널 헤더/목록 자리/AI 버튼 · 지도 컨트롤 자리)
- [ ] Phone 390×844: `planner_phone.png`와 외곽 일치 (검색 바 · 지도 프레임 · 하단 시트)
- [ ] 8테마 전환 시 색만 바뀜 (종이·줄무늬·패널·머리띠)
- [ ] 5-6 grep 0줄
- [ ] PR의 `client-ci` 초록

## 8. 끝나면 붙여넣어 줄 것

1. 새 파일 다섯(`PaperStripes.kt`, `InkPanel.kt`, `PlannerScreen.kt`, `PlannerParts.kt`, `PlannerDummy.kt`)과 `App.kt`·`main.kt` 변경분
2. 스크린샷: PLANNER Desktop 1440×900, Phone 390×844, 테마 하나 바꾼 것

막히면 단계 번호와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): lay out the planner background and panels`, `Refs: B2`)과 push를 하고, #22 머지 뒤 PR을 연다. 그다음은 B3(아이콘 22종)이다.
