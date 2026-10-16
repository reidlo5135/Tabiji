# A3 — 레이아웃 기초: 인트로 사계절 사분면과 잉크 경계

> 브랜치: `client/feat-a3-intro-quadrants` (Claude가 만들어 둠) · 커밋 footer `Refs: A3`
> 시각 기준: `design/handoff/handoff-v2/docs/10_intro.md`의 "배치와 경계" 절, `images/screens/intro_desktop.png`
> 확인은 **Desktop**에서만 한다 (로드맵 "데스크톱 우선")

## 목표

A1의 하늘색 바탕을 **인트로 화면의 뼈대**로 바꾼다.
- 창을 2×2로 나눠 봄·여름·가을·겨울 네 칸을 만든다. 이미지는 A6에서 넣으니 지금은 임시 단색이다
- 칸 사이에 **3dp 잉크(ink) 경계선**을 둔다
- 그 위 정중앙에 "Tabiji"를 띄운다

끝 그림은 아래와 같다. 창 크기를 어떻게 바꿔도 네 칸이 똑같이 나뉘고, 십자 경계선 두께는 그대로다.

```
┌────────────┬────────────┐
│  봄 (분홍)   ┃  여름 (청록)  │
│            ┃            │
┣━━━━━━━━  Tabiji  ━━━━━━━━┫   ← 글자는 정중앙에 겹쳐 뜸
│            ┃            │
│  가을 (주황)  ┃  겨울 (남색)  │
└────────────┴────────────┘
  ┃ ━ = 3dp ink 경계 (칸 사이에만, 바깥 테두리 없음)
```

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [Compose 레이아웃 기초](https://developer.android.com/develop/ui/compose/layouts/basics) | `Row`·`Column`·`Box` 세 가지가 레이아웃의 전부다 |
| [Modifier 순서가 중요한 이유](https://developer.android.com/develop/ui/compose/modifiers#order-modifier-matters) | 오늘의 실험 주제 |
| [Kotlin 확장 함수](https://kotlinlang.org/docs/extensions.html) · [기본 인자](https://kotlinlang.org/docs/functions.html#default-arguments) · [리시버가 있는 람다](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver) | 아래 문법 표의 원문 |
| 핸드오프 `docs/10_intro.md` "배치와 경계" | 2×2 배치와 3dp ink 경계의 원본 규칙 |

## 처음 보는 Kotlin 문법 (이번 단계에서 만나는 것만)

| 문법 | 한 줄 설명 | C++로 빗대면 |
| --- | --- | --- |
| 확장 함수 `fun Modifier.padding(...)` | 남의 타입에 메서드를 **밖에서 덧붙인** 것. `Modifier.padding()`도, `.background()`도 전부 확장 함수다 | 자유 함수 `padding(modifier, …)`를 점 문법으로 부를 수 있게 한 것 |
| 확장 프로퍼티 `3.dp` | `Int`에 `dp`라는 속성을 덧붙였다. 숫자 뒤에 단위를 붙이는 문법처럼 읽힌다 | 사용자 정의 리터럴 `3_dp`(`operator""_dp`) |
| 기본 인자 `modifier: Modifier = Modifier` | 안 넘기면 기본값을 쓴다. 이름 붙여 넘길 수도 있다(A1의 named argument) | C++ 기본 인자. 다만 중간 인자를 건너뛸 수 있다 |
| 리시버가 있는 람다 | `Row { … }` 중괄호 안에서는 `this`가 `RowScope`다. 그래서 **`Row` 안에서만** `Modifier.weight()`가 보인다 | 람다 안에서만 특정 객체의 멤버가 이름만으로 보이는 것. Qt로 치면 `QHBoxLayout` 안에서만 `addStretch()`가 의미 있는 것과 비슷하다 |
| `private fun` | 같은 파일 안에서만 보이는 함수 | `.cpp` 안의 `static` 함수 |

## Compose 핵심 개념 (오늘 분량)

**`Row` / `Column` / `Box` = 세 가지 레이아웃.** Qt에 그대로 대응된다.

| Compose | Qt Widgets | QML |
| --- | --- | --- |
| `Row` | `QHBoxLayout` | `RowLayout` |
| `Column` | `QVBoxLayout` | `ColumnLayout` |
| `Box` | `QStackedLayout`(StackAll) | 자식이 겹쳐 쌓이는 `Item` |
| `Modifier.weight(1f)` | `addWidget(w, /*stretch=*/1)` | `Layout.fillWidth: true` + 비율 |
| `Arrangement.spacedBy(3.dp)` | `layout->setSpacing(3)` | `spacing: 3` |
| `Modifier.align(Alignment.Center)` (Box 안) | — | `anchors.centerIn: parent` |

**`weight`는 남는 공간을 비율로 나눈다.** 형제 둘이 `weight(1f)`면 1:1이다. `spacedBy`로 뺀 3dp를 제외한 나머지를 반씩 가져간다. 그래서 창 크기가 바뀌어도 4등분이 유지되고, 경계 두께는 3dp로 고정된다.

**잉크 경계는 선을 그리는 게 아니라 "틈"이다.** 핸드오프의 방법은 이렇다: *루트 배경을 ink로 깔고, 칸 사이를 3dp 띄운다.* 칸들이 바닥을 덮고, 그 틈으로 보이는 바닥색이 곧 경계선이 된다. 선 그리기 API 없이 레이아웃만으로 만드는 것이다. 바깥 테두리는 없다. 틈은 칸 **사이**에만 생긴다.

**`Modifier` 체인은 바깥에서 안으로 적용된다.** `Modifier.padding(12.dp).background(c)`는 "12dp 들여 놓고, **그 안쪽**을 칠한다"는 뜻이다. 반대로 `Modifier.background(c).padding(12.dp)`는 "전체를 칠하고, 내용물만 12dp 들인다"는 뜻이다. Qt로 치면 앞쪽은 레이아웃 margin, 뒤쪽은 스타일시트의 padding이다. 둘이 다른 결과를 낸다는 것을 오늘 **눈으로** 확인한다.

**상태 없는 Composable + `modifier` 파라미터.** 칸 하나를 `SeasonQuadrant(color, modifier)`로 뽑아낸다. 이 칸은 자기가 `Row` 안에 있는지 모른다. 크기 규칙(`weight`)은 **부르는 쪽이 `modifier`로 넣어 준다.** 그래서 컨벤션이 "첫 번째 선택 파라미터는 `modifier: Modifier = Modifier`"다. A4에서 "선택된 칸" 상태를 부모에 두는 것(state hoisting)도 이 구조 위에서 한다.

## 만들 파일 (직접 작성)

| 경로 | 내용 |
| --- | --- |
| `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature/intro/IntroScreen.kt` (새 파일) | `IntroScreen(modifier)`(공개)와 `SeasonQuadrant(color, modifier)`(`private`) |
| `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/app/App.kt` (수정) | 하늘색 `Box`를 지우고 `IntroScreen()`만 부른다 |

> 패키지: `IntroScreen.kt`는 `package com.yamada.studio.tabiji.feature.intro`다. `architecture.md`의 패키지 구조(`app / feature / core`)를 따른다. 그래야 B8에서 모듈을 나눌 때 폴더를 옮기기만 하면 된다. `App.kt`에서는 `com.yamada.studio.tabiji.feature.intro.IntroScreen`을 import한다.

## 조립 순서

### 1. 구조 — 트리로 먼저 그린다

```
IntroScreen                Box — 창 가득 (겹쳐 쌓기용)
├── Column                 가득 · 배경 ink · 세로 간격 3dp
│   ├── Row                weight 1 · 가로 가득 · 가로 간격 3dp
│   │   ├── SeasonQuadrant(봄)    weight 1
│   │   └── SeasonQuadrant(여름)  weight 1
│   └── Row                (위와 같음)
│       ├── SeasonQuadrant(가을)
│       └── SeasonQuadrant(겨울)
└── Text "Tabiji"          Box 정중앙
```

`Box`의 자식은 **적은 순서대로 아래부터 쌓인다.** `Column`(사분면)이 먼저, `Text`가 나중이라 글자가 위에 뜬다. QML에서 나중에 선언한 형제가 위에 그려지는 것과 같다.

패턴은 이것 하나다. 칸 수와 색만 바꿔 위 트리에 맞추면 된다.

```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
) {
    Box(Modifier.weight(1f))   // 남는 폭을 형제끼리 1:1로
    Box(Modifier.weight(1f))
}
```

- 세로로 나누는 `Column`에서는 `verticalArrangement = Arrangement.spacedBy(…)`이고, 자식 `Row`에 `Modifier.weight(1f)`를 주면 높이를 1:1로 나눈다
- `weight`는 **import하지 않는다.** `RowScope`·`ColumnScope`의 멤버라서 해당 중괄호 안에서 저절로 보인다. IDE가 다른 `weight`를 import하라고 하면 무시한다

### 2. `SeasonQuadrant` — 상태 없는 칸

```kotlin
@Composable
private fun SeasonQuadrant(color: Color, modifier: Modifier = Modifier) {
    Box(/* 받은 modifier에 이어서: 가득 채우고 color로 칠하기 */)
}
```

**받은 `modifier`(소문자)에 이어 붙인다.** `Modifier`(대문자)로 새로 시작하면 부모가 준 `weight`가 버려진다. 초보 때 가장 흔한 실수다.

### 3. 임시 색 — 핸드오프 계절 테마의 `accent`

A6에서 이미지로 바뀔 자리라 의미 있는 임시 색을 쓴다. 값은 `tokens/tokens.json`의 계절 테마 `accent`와 cloud 테마 `ink`다.

| 자리 | 색 | 출처 |
| --- | --- | --- |
| 봄 (좌상) | `0xFFC95E7E` | `themes.spring.accent` |
| 여름 (우상) | `0xFF1F96B8` | `themes.summer.accent` |
| 가을 (좌하) | `0xFFC06F22` | `themes.autumn.accent` |
| 겨울 (우하) | `0xFF5674AE` | `themes.winter.accent` |
| 경계 (루트 배경) | `0xFF2A3644` | `themes.cloud.ink` |

- 칸 색 4줄에는 `// TODO(A6): 계절 이미지로`, ink에는 `// TODO(A5): TabijiTheme 토큰으로`를 단다
- 가운데 "Tabiji" 글자는 색 칸과 잉크 십자 위에 걸쳐 있어서, 진회색이면 잘 안 보일 수 있다. 지금은 `Color.White`로 바꿔 둔다. A7에서 흰 글자 + 잉크 아웃라인으로 마감한다

### 4. `App.kt` 정리

`App()` 안의 하늘색 `Box`와 `Text`를 지우고 `IntroScreen()` 한 줄만 남긴다. 화면의 실체는 `feature/intro`로 가고, `App()`은 "앱의 루트"라는 자리만 지킨다. B6에서 여기에 내비게이션이 들어온다. 쓰지 않게 된 import도 지운다.

### 5. 실행

```bash
cd ~/ws/src/Tabiji/client
scripts/linux/run-desktop.sh --hot
```

창 모서리를 끌어 아주 넓게, 아주 길쭉하게 바꿔 본다. 네 칸이 항상 같은 크기인지, 십자 경계 두께가 변하지 않는지 본다.

### 6. 실험 — Modifier 순서 (Hot Reload로)

`SeasonQuadrant`의 체인에 `padding(12.dp)`를 넣는다. 위치를 두 가지로 바꿔 가며 저장하고 각각 스크린샷을 찍는다.

| | 체인 | 예상해 볼 것 |
| --- | --- | --- |
| ① | `fillMaxSize()` → **`padding(12.dp)`** → `background(color)` | 칸 둘레에 무엇이 보일까? 경계는 몇 dp처럼 보일까? |
| ② | `fillMaxSize()` → `background(color)` → **`padding(12.dp)`** | ①과 무엇이 다를까? 왜 그럴까? |

저장하기 **전에** 결과를 먼저 예측해 본다. 두 스크린샷과 함께 "왜 다른지"를 한두 줄로 적어 붙여넣는다. 확인이 끝나면 `padding`은 지운다. 최종 화면에는 여백이 없다.

`padding` import: `androidx.compose.foundation.layout.padding`

### 7. 빌드 확인

```bash
scripts/linux/build.sh
```

Android 빌드도 함께 돈다. 실행 확인은 안 하지만, `commonMain` 코드가 Android에서도 컴파일되는지는 계속 검증된다.

## import 목록

IDE 동기화가 실패하는 동안에는 자동완성이 약할 수 있다. 그래서 import를 직접 적는다.

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
```

## 흔한 오류와 원인 읽기

| 증상 | 원인일 가능성 |
| --- | --- |
| `Unresolved reference: weight` | `Row`/`Column`의 중괄호 **밖**에서 `weight`를 씀. 예: `SeasonQuadrant` 본문 안. `weight`는 부르는 쪽에서 넘긴다 |
| `Unresolved reference: dp` | `androidx.compose.ui.unit.dp` import 누락 |
| 한 칸이 줄을 다 차지하고 나머지는 안 보임 | 칸에 `weight` 없이 `fillMaxSize()`만 있음. 첫째가 가능한 만큼 다 가져간다 |
| `weight`를 넘겼는데 무시됨 | `SeasonQuadrant` 안에서 받은 `modifier` 대신 `Modifier`로 새로 시작함 |
| 경계선이 안 보임 | `Column`에 ink 배경이 없거나 `spacedBy`를 빠뜨림 |
| 위아래 두 줄 높이가 다름 | 두 `Row` 중 하나에 `weight(1f)` 누락 |
| 글자가 안 보임 | `Text`가 `Column`보다 먼저 선언돼 아래에 깔림, 또는 색 대비 문제 |
| `Unresolved reference: IntroScreen` (App.kt) | import 누락이나 패키지 선언 오타 |

## 완료 조건 (모두 눈으로)

- [ ] `run-desktop.sh` → 2×2 네 칸(좌상 봄 분홍 · 우상 여름 청록 · 좌하 가을 주황 · 우하 겨울 남색) + 3dp 잉크 십자, 바깥 테두리 없음
- [ ] 창을 아주 넓게·길쭉하게 바꿔도 네 칸이 같은 크기, 경계 두께 그대로
- [ ] 정중앙에 흰 "Tabiji"
- [ ] Modifier 순서 실험 스크린샷 두 장과 차이 설명
- [ ] 코드 리뷰: `App()`은 `IntroScreen()`만 부름, `SeasonQuadrant`는 상태 없고 `modifier`를 받아 이어 씀
- [ ] `build.sh` 초록

## 끝나면 붙여넣어 줄 것

1. `IntroScreen.kt`, `App.kt` 내용
2. 창 스크린샷 (기본 크기 + 아주 넓거나 길쭉한 것 하나)
3. Modifier 순서 실험 스크린샷 ①② + 설명
4. `build.sh` 마지막 10줄

→ 진단을 통과하면 Claude가 커밋(`feat(client): lay out intro season quadrants with ink borders`, `Refs: A3`)과 push를 하고 PR을 연다. CI를 확인한 뒤 사용자가 squash merge한다. 그다음 A4(사분면 선택 — `remember`와 state hoisting)로 간다.
