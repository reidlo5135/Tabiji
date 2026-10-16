# B3 — UI 아이콘 22종: 벡터 리소스 · `Icon` · tint

> 브랜치: `client/feat-b3-icons` (Claude가 만들어 둠) · 커밋 footer `Refs: B3`
> 시각 기준: handoff-v2 `assets/icons/*.svg`(22종, 전부 24×24 · stroke 1.75 · round), `docs/10_intro.md`(버튼 행), `docs/11_planner.md`(컨트롤·검색·AI 버튼)
> 확인은 Desktop에서 (TOKENS 갤러리 + INTRO + PLANNER)

## 목표

핸드오프의 SVG 아이콘 22종을 **Android 벡터 리소스(XML)**로 옮겨 `TabijiIcons`로 모으고, B2까지 비워 둔 네 자리를 채운다.

| 자리 | 아이콘 | 크기 | 색 |
| --- | --- | --- | --- |
| 인트로 메뉴 버튼 왼쪽 | 시작하기 `ic_place` · 내 여행 `ic_calendar` · 설정 `ic_settings` | 22 (Phone 20) | 글자와 같음 (`onAccent`/`ink`) |
| 인트로 메뉴 버튼 오른쪽 | `ic_chevron_down`을 −90° 회전 (오른쪽 쉐브론) | 18 | 글자와 같음 |
| 지도 컨트롤 | 내 위치 `ic_my_location` · 확대 `ic_zoom_in` | 22 | `ink` |
| 검색 바 돋보기 | `ic_search` | 20 | `sub` |
| AI 버튼 스파크 | `ic_ai` | 20 | `onAccent` |

완료 확인용으로 TOKENS 화면 맨 아래에 **아이콘 갤러리**(22종, ink 색)도 추가한다.

## 먼저 읽을 것 (20분)

| 문서 | 왜 |
| --- | --- |
| [Compose Multiplatform 리소스 — 이미지·벡터](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-usage.html) | XML 벡터가 모든 타깃에서 동작한다는 것, `painterResource` |
| [Vector Drawable 문법](https://developer.android.com/reference/android/graphics/drawable/VectorDrawable) | `pathData`·stroke 속성 |
| [Material Icon](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Icon(androidx.compose.ui.graphics.painter.Painter,kotlin.String,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color)) | `Icon`과 `tint` |

---

## 1. 핵심 개념

### 1-1. SVG가 아니라 왜 XML 벡터인가

B2의 지도 목업은 SVG로 넣었다. SVG는 Compose Resources에서 **Desktop(JVM)에서만** 그려진다. 아이콘은 Android(Phase M)에서도 그대로 쓸 거라서, 모든 타깃에서 동작하는 **Android Vector Drawable(XML)** 형식으로 넣는다. 둘 다 `drawable/`에 두고 `painterResource`로 읽는 건 같다.

### 1-2. SVG → XML 변환은 기계적인 치환이다

| SVG | XML 벡터 |
| --- | --- |
| `<svg viewBox="0 0 24 24" …>` | `<vector android:viewportWidth="24" android:viewportHeight="24" android:width="24dp" android:height="24dp" xmlns:android="http://schemas.android.com/apk/res/android">` |
| `<path d="…"/>` | `<path android:pathData="…" …/>` |
| `stroke="currentColor"` | `android:strokeColor="#FF000000"` (아무 단색. 쓸 때 tint가 덮어쓴다) |
| `stroke-width="1.75"` | `android:strokeWidth="1.75"` |
| `stroke-linecap="round"` / `stroke-linejoin="round"` | `android:strokeLineCap="round"` / `android:strokeLineJoin="round"` |
| `fill="none"` | `android:fillColor`를 **안 쓰면** 됨 (기본이 칠 없음) |
| `<circle cx="11" cy="11" r="6.5"/>` | 원은 태그가 없어서 패스로: `M4.5,11 a6.5,6.5 0 1,0 13,0 a6.5,6.5 0 1,0 -13,0` (왼끝에서 반원 두 번) |
| `<line x1 y1 x2 y2>` | `M x1,y1 L x2,y2` |
| `<rect x y w h rx>` | 둥근 사각 패스 (길어서 직접 쓰기 번거로움 — 아래 5-3 참고) |

완성 예 — `ic_search.xml` (원 + 손잡이):

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path
        android:pathData="M4.5,11 a6.5,6.5 0 1,0 13,0 a6.5,6.5 0 1,0 -13,0"
        android:strokeColor="#FF000000" android:strokeWidth="1.75"
        android:strokeLineCap="round" android:strokeLineJoin="round" />
    <path
        android:pathData="M16,16 L20.5,20.5"
        android:strokeColor="#FF000000" android:strokeWidth="1.75"
        android:strokeLineCap="round" />
</vector>
```

### 1-3. `Icon`과 tint — 색은 그리는 쪽이 정한다

```kotlin
Icon(
    painter = painterResource(TabijiIcons.Search),
    contentDescription = null,                      // 옆에 글자가 있는 장식 아이콘이면 null
    tint = TabijiTheme.colors.sub,
    modifier = Modifier.size(SearchIconSize)
)
```

- `tint`는 벡터의 원래 색을 **통째로 덮어쓴다.** 그래서 XML의 `#FF000000`은 자리만 지키는 값이다. CSS에서 `stroke="currentColor"`로 두고 부모의 `color`를 따라가게 하는 것과 같은 구조다
- `tint`를 생략하면 `LocalContentColor.current`를 쓴다. Material 컴포넌트는 이 CompositionLocal을 깔아 주지만 우리 `InkButton`은 안 깔아 준다. **이 프로젝트에서는 tint를 항상 명시한다** (`conventions.md`의 "색은 토큰에서만"과 같은 맥락)
- `contentDescription`: 스크린리더가 읽는 글자다. 옆에 보이는 글자가 이미 있으면 `null`(중복 안내 방지), **아이콘뿐인 버튼**(지도 컨트롤)은 `strings.xml`의 글자를 넣는다

### 1-4. `TabijiIcons` — 생성된 `Res`를 한 겹 감싼다

```kotlin
package com.yamada.studio.tabiji.core.ui.icon

object TabijiIcons {
    val Search: DrawableResource = Res.drawable.ic_search
    val Place: DrawableResource = Res.drawable.ic_place
    // … 22개
    val all: List<DrawableResource> = listOf(Search, Place, /* … */)
}
```

feature 코드가 생성 코드(`tabiji.shared.generated.resources`)를 직접 import하지 않고 `TabijiIcons`만 보게 하는 어댑터다. 아이콘 교체·이름 변경이 이 파일 하나로 끝난다. `all`은 TOKENS 갤러리용이다.

### 1-5. 쉐브론은 회전으로

핸드오프 버튼 오른쪽은 **오른쪽** 쉐브론인데 아이콘 세트에는 `ic_chevron_down`(아래)만 있다. `Modifier.rotate(-90f)`로 돌려 쓴다. 원본 인트로 쉐브론은 stroke 2.5로 더 굵지만, 벡터 XML은 stroke가 고정이라 1.75로 간다 (결정: 세트 통일이 우선, 차이는 허용).

> 같은 이유로 AI 버튼 스파크는 핸드오프가 Desktop 20 · Phone 19인데 **20으로 통일**한다. 1dp는 구분이 안 보이고 파라미터만 는다.

---

## 2. 처음 보는 것

| 것 | 설명 | 빗대면 |
| --- | --- | --- |
| `DrawableResource` | 생성된 `Res.drawable.*`의 타입. 값으로 들고 다닐 수 있다 | 이미지 경로 문자열을 상수로 모아 두는 것 |
| `Modifier.rotate(deg)` | 그린 결과를 회전 | CSS `transform: rotate(-90deg)` |
| `object` 안의 `listOf` | 객체 초기화 때 한 번 만들어지는 불변 리스트 | `const ALL = [ … ]` |

## 3. 만들 것

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/src/commonMain/composeResources/drawable/ic_*.xml` ×22 | 변환한 벡터 (파일명은 핸드오프와 동일) |
| `shared/…/core/ui/icon/TabijiIcons.kt` (새 파일) | 1-4의 object |
| `shared/…/core/ui/theme/TokenSwatches.kt` | 맨 아래 아이콘 갤러리 섹션 |
| `shared/…/feature/intro/IntroMenuButton.kt` | 왼쪽 아이콘 + 오른쪽 쉐브론 (`TODO(B3)` 제거) |
| `shared/…/feature/intro/IntroScreen.kt` | 버튼 3개에 아이콘 전달 |
| `shared/…/feature/planner/PlannerParts.kt` | 돋보기·지도 컨트롤 2종·AI 스파크 |
| `strings.xml` | `planner_my_location`(내 위치) · `planner_zoom_in`(확대) — 아이콘뿐인 버튼의 contentDescription |

### 트리 — `IntroMenuButton` 전 → 후

```
전: InkButton                                 후: InkButton
    └── Column                                    ├── Icon(painterResource(leadingIcon), null,
        ├── Text(title)                           │        tint = titleColor, Modifier.size(iconSize))
        └── Text(subtitle)                        ├── Column (Modifier.weight(1f))
                                                  │   ├── Text(title)
                                                  │   └── Text(subtitle)
                                                  └── Icon(painterResource(TabijiIcons.ChevronDown), null,
                                                           tint = titleColor,
                                                           Modifier.size(ChevronSize).rotate(-90f))
```

- 파라미터 추가: `leadingIcon: DrawableResource`, `iconSize: Dp` (필수, `primary`·`height` 뒤)
- `InkButton`의 Row가 이미 가로 `spacedBy(12)` + 좌우 padding 16을 깔아 줘서 간격은 따로 안 준다
- `Column`에 `weight(1f)`를 줘야 쉐브론이 오른쪽 끝으로 밀린다 (A9의 Spacer와 같은 원리)

### 트리 — `PlannerParts` 아이콘 자리 (바뀌는 노드만)

```
PlannerSearchBar · Row                        MapControls · Column
├── Icon(Search, null, tint = c.sub,          ├── Box(buttonModifier, contentAlignment = Center)
│        Modifier.size(SearchIconSize))       │   └── Icon(MyLocation, stringResource(…my_location),
├── Text(hint …)  ← 기존                      │            tint = c.ink, Modifier.size(MapControlIconSize))
└── InkButton … ← 기존                        └── if (showZoom) Box(buttonModifier, contentAlignment = Center)
                                                  └── Icon(ZoomIn, stringResource(…zoom_in), tint = c.ink, …)

DayPanelFooter · InkButton
├── Icon(Ai, null, tint = c.onAccent, Modifier.size(AiIconSize))
└── Text(planner_ai_optimize …)  ← 기존
```

### 치수 상수 (쓰는 파일 맨 아래)

| 상수 | 값 | 파일 |
| --- | --- | --- |
| `IntroIconSizeWide` / `IntroIconSizeCompact` | `22.dp` / `20.dp` | `IntroScreen.kt` (높이들 옆) |
| `ChevronSize` | `18.dp` | `IntroMenuButton.kt` |
| `SearchIconSize` / `MapControlIconSize` / `AiIconSize` | `20.dp` / `22.dp` / `20.dp` | `PlannerParts.kt` |

## 4. 이름이 겹치는 것 (이번 단계)

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 아이콘 그리기 | `androidx.compose.material3.Icon` | `androidx.compose.material.Icon`(M2) |
| 리소스 읽기 | `org.jetbrains.compose.resources.painterResource` | `androidx.compose.ui.res.painterResource`(Android 전용) |
| 회전 | `androidx.compose.ui.draw.rotate` | `androidx.compose.ui.graphics.graphicsLayer`(이번엔 과함) |
| 리소스 타입 | `org.jetbrains.compose.resources.DrawableResource` | `androidx.compose.ui.graphics.vector.ImageVector`(머티리얼 내장 아이콘용) |

## 5. 조립 순서

`scripts/linux/run-desktop.sh --hot`. **리소스(XML)를 추가한 단계에서는 재시작**해야 `Res.drawable.*`가 생성된다.

### 5-1. 변환을 손으로 한 번 — `ic_search.xml` + 검색 바

1. 1-2의 완성 예를 **보지 말고** `assets/icons/ic_search.svg`를 직접 변환해 본 뒤 비교한다
2. `TabijiIcons.kt`를 만들고 `Search` 하나만 넣는다
3. `PlannerSearchBar` 맨 앞에 돋보기 `Icon`을 넣는다 (3장 트리)

import (`TabijiIcons.kt`):

```kotlin
import org.jetbrains.compose.resources.DrawableResource
import tabiji.shared.generated.resources.Res
import tabiji.shared.generated.resources.ic_search
```

import (`PlannerParts.kt`에 추가):

```kotlin
import androidx.compose.material3.Icon
import com.yamada.studio.tabiji.core.ui.icon.TabijiIcons
```

✅ 확인: PLANNER 검색 바 왼쪽에 돋보기가 `sub` 색으로. 테마를 바꾸면 돋보기 색도 바뀐다.

### 5-2. 지도 컨트롤 — `ic_my_location` · `ic_zoom_in`

- `ic_my_location.svg`는 원 2개 + 선 4개다. 원 → 호 2개 패스(1-2), 선 → `M…L…`
- `strings.xml`에 `planner_my_location`·`planner_zoom_in`을 넣고 contentDescription으로 전달 (아이콘뿐인 버튼)
- `MapControls`의 두 `Box`에 `contentAlignment = Alignment.Center`와 `Icon`을 넣는다

✅ 확인: 지도 우하단 버튼 두 개에 과녁·돋보기+ 아이콘이 `ink` 색으로.

### 5-3. 나머지 19종 + `TabijiIcons` 완성 + 갤러리

변환 요령은 5-1·5-2에서 익혔다. **남은 19종의 기계적 변환은 Claude에게 "나머지 변환해줘"라고 시켜도 된다** (A5 팔레트 채우기 때처럼 — 노가다는 학습이 아니다). 직접 할 거면 `ic_calendar`의 `<rect rx="3">`처럼 복잡한 것부터.

갤러리 — `TokenSwatches` Column 맨 아래에:

```
Column (기존)
└── … 기존 스와치들
    └── (새) TabijiIcons.all.chunked(8).forEach { rowIcons ->
            Row(spacedBy(TabijiTheme.spacing.md)) {
                rowIcons.forEach { Icon(painterResource(it), null, tint = c.ink, Modifier.size(24.dp)) }
            }
        }
```

✅ 확인: TOKENS 맨 아래 22종이 세 줄로, 전부 ink 색. 테마 전환 시 같이 바뀜. **깨진(비뚤어진) 아이콘이 없는지** 하나씩 본다 — 패스 변환 실수는 여기서 다 드러난다.

### 5-4. 인트로 버튼 + AI 스파크

1. `IntroMenuButton`을 3장 전 → 후 트리대로 (파라미터 2개 추가, `TODO(B3)` 주석 삭제)
2. `IntroScreen`의 호출 3곳에 `leadingIcon = TabijiIcons.Place / Calendar / Settings`, `iconSize = IntroIconSizeWide/Compact` (높이처럼 widthClass로 고른 값을 전달)
3. `DayPanelFooter`의 `InkButton` 안, `Text` 앞에 스파크 `Icon`

import (`IntroMenuButton.kt`에 추가):

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.rotate
import com.yamada.studio.tabiji.core.ui.icon.TabijiIcons
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
```

✅ 확인: INTRO가 `intro_desktop.png`와 같은 버튼 모양(왼쪽 아이콘 · 오른쪽 쉐브론). AI 버튼에 스파크.

## 6. 흔한 문제와 원인

| 증상 | 원인 |
| --- | --- |
| `Unresolved reference: ic_search` | XML 추가 후 재시작 안 함 (리소스 코드 생성은 빌드 때) |
| `Box`에 `contentAlignment`가 없다고 나옴 | 내용 람다 `{ }`가 없는 `Box(modifier)`는 다른 오버로드다. 아이콘을 `{ }` 안에 넣으면 `contentAlignment`를 받는 오버로드로 바뀐다 |
| 아이콘이 네모로 꽉 찬 검정 | `fillColor`를 넣음. stroke 아이콘은 fill을 아예 쓰지 않는다 |
| 아이콘이 안 보임 | `strokeColor` 빠짐 (칠도 선도 없으면 투명) |
| 원이 찌그러짐/안 닫힘 | 호 패스의 플래그·부호 실수. `a r,r 0 1,0 (2r),0` 두 번, 두 번째는 `-(2r)` |
| 선이 유난히 굵거나 얇음 | `strokeWidth`를 1.75가 아닌 값으로 옮김 |
| XML 파싱 오류로 빌드 실패 | `xmlns:android` 선언 빠짐, 또는 속성 이름 오타 (`strokeLinecap` ❌ → `strokeLineCap`) |
| tint를 줘도 색이 안 변함 | `android:fillColor`/`strokeColor`에 알파 없는 6자리를 씀 → 8자리(`#FF…`)로 |
| 쉐브론이 아래를 봄 | `rotate(-90f)` 빠짐 (+90은 왼쪽을 본다) |

## 7. 완료 조건 (모두 눈으로)

- [ ] TOKENS 갤러리에 22종 전부, ink 색, 깨진 것 없음
- [ ] 테마 전환 시 모든 아이콘 색이 따라감
- [ ] INTRO 버튼 3개: 왼쪽 아이콘 + 오른쪽 쉐브론, `intro_desktop.png`와 일치
- [ ] PLANNER: 돋보기(sub) · 지도 컨트롤 2종(ink) · AI 스파크(흰색)
- [ ] `grep -rn 'Res.drawable.ic_' shared/src/commonMain/kotlin --include='*.kt' | grep -v TabijiIcons` → 0줄 (feature가 생성 리소스를 직접 안 씀)
- [ ] PR `client-ci` 초록

## 8. 끝나면 붙여넣어 줄 것

1. `TabijiIcons.kt`, `IntroMenuButton.kt` 변경분, 직접 변환한 XML 한두 개
2. 스크린샷: TOKENS 갤러리 · INTRO · PLANNER(Desktop)

→ 진단 통과 후 Claude가 커밋(`feat(client): add the 22 ui icons`, `Refs: B3`)과 push·PR. 다음은 B4(방문지 카드·구간 칩·`LazyColumn`)다.
