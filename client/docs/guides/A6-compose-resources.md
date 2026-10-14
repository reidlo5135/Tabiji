# A6 — 리소스: 계절 이미지·폰트·문자열·창 아이콘

> 브랜치: `client/feat-a6-compose-resources` (Claude가 만들어 둠, A5 PR #10 위에 쌓음) · 커밋 footer `Refs: A6`
> 시각 기준: handoff-v2 `docs/10_intro.md`(이미지 fit 규칙·워드마크), `docs/02_typography.md`(폰트·스타일 9종), `docs/04_icons_favicon.md`(Desktop 창 아이콘)
> 확인은 **Desktop**에서 한다

## 목표

지금까지는 코드만 있었다. 이번 단계에서 처음으로 **파일(이미지·폰트·글자)을 앱에 넣고 코드에서 꺼내 쓴다.**

- 사분면의 임시 단색 → **계절 이미지**. 창 비율이 바뀌어도 찌그러지지 않고 꽉 채운다
- 가운데 "Tabiji" → **Sora 굵은 워드마크** + 그 아래 **Jua 부제 "여행(旅路) 도우미"**
- 앱 전체가 쓸 **글꼴 스타일 9종**을 테마에 추가한다 (`TabijiTheme.typography`)
- 화면에 보이는 글자는 코드가 아니라 **`strings.xml`** 에서 온다
- Desktop 창(작업 전환기·작업표시줄)에 **旅 아이콘**이 붙고, 처음 뜰 때 **화면의 가로 85% × 세로 75%** 크기로 가운데에 열린다

끝 그림은 핸드오프 `images/screens/intro_desktop.png`에서 버튼만 뺀 모습이다. 버튼과 워드마크의 잉크 테두리는 A8에서 한다.

> **인트로 일러스트는 커밋하지 않는다.** 제3자 캐릭터가 들어 있어서다 (루트 `CLAUDE.md`, roadmap ⚠). 그래서 앱에는 Claude가 만든 **같은 비율의 플레이스홀더**를 넣는다. 실제 일러스트는 로컬에서 잠깐 바꿔 끼워 확인만 한다 (4-3).

## 먼저 읽을 것 (40분)

| 문서 | 왜 |
| --- | --- |
| [Compose Multiplatform 리소스 개요](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources.html) | 폴더 구조와 `Res` 클래스. 오늘의 뼈대 |
| [리소스 사용법](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html) | `painterResource` · `stringResource` · `Font`의 실제 쓰는 법 |
| [이미지 다루기 — ContentScale](https://developer.android.com/develop/ui/compose/graphics/images/customize#content-scale) | `Crop`이 무엇을 자르는지 그림으로 |
| [글꼴과 텍스트 스타일](https://developer.android.com/develop/ui/compose/text/fonts) | `FontFamily` · `FontWeight` · `TextStyle` |

---

## 1. 핵심 개념

### 1-1. Compose Resources — 파일을 넣으면 `Res`가 생긴다

`shared/src/commonMain/composeResources/` 아래에 정해진 폴더가 있다.

| 폴더 | 넣는 것 | 코드에서 |
| --- | --- | --- |
| `drawable/` | 이미지 (png·webp·jpg, Android 벡터 xml) | `Res.drawable.파일이름` |
| `font/` | 글꼴 (ttf·otf) | `Res.font.파일이름` |
| `values/strings.xml` | 화면에 보이는 문자열 | `Res.string.키` |
| `files/` | 그 밖의 원본 파일 (이번엔 폰트 라이선스) | `Res.readBytes("files/…")` |

빌드할 때 Gradle이 이 폴더를 훑어서 **`Res`라는 Kotlin 객체를 자동으로 만든다.** 파일 `intro_spring.webp`를 넣으면 코드에서 `Res.drawable.intro_spring`이 생긴다. 이름이 틀리면 **컴파일 오류**로 잡힌다.

React로 치면 이렇다.

| React (번들러) | Compose Resources |
| --- | --- |
| `import springImg from './assets/intro_spring.webp'` | `Res.drawable.intro_spring` |
| `<img src={springImg} />` | `Image(painterResource(Res.drawable.intro_spring), …)` |
| i18n의 `t('intro_subtitle')` | `stringResource(Res.string.intro_subtitle)` |
| CSS `@font-face { src: url(jua.ttf) }` | `Font(Res.font.jua_regular)` |

**이 프로젝트에서 알아야 할 두 가지**

- **`Res`의 패키지는 `tabiji.shared.generated.resources`다.** 우리 코드 패키지(`com.yamada…`)와 다르다. 그래서 import가 필요하다
- **리소스 하나마다 import가 하나 필요하다.** `Res.drawable.intro_spring`의 `intro_spring` 부분이 확장 프로퍼티라서 그렇다. IDE 동기화가 안 되는 지금은 자동 import가 없으니 직접 적는다

```kotlin
import tabiji.shared.generated.resources.Res
import tabiji.shared.generated.resources.intro_spring     // 쓰는 리소스마다 한 줄
import tabiji.shared.generated.resources.intro_subtitle
// 또는 한 줄로 전부:
import tabiji.shared.generated.resources.*
```

리소스를 쓰는 파일마다 와일드카드 한 줄(`.*`)을 두는 게 가장 편하다.

**파일 이름 규칙**: 소문자·숫자·밑줄만 쓴다 (`intro_spring.webp`, `jua_regular.ttf`). 파일 이름이 그대로 Kotlin 이름(`Res.font.jua_regular`)이 되기 때문이다. 하이픈(`-`)이나 대문자가 섞이면 이름이 바뀌어 생성되거나 빌드가 실패할 수 있다. 핸드오프의 `Jua-Regular.ttf`는 복사하면서 이름을 바꾼다.

**`Res`는 `shared` 모듈 안에서만 보인다** (`internal`). 그래서 `desktopApp`의 `main.kt`는 `Res`를 직접 못 쓴다. 창 아이콘처럼 `main.kt`에 필요한 리소스는 `shared`에 작은 함수를 만들어 꺼내 준다 (4-7).

### 1-2. `painterResource` · `stringResource` · `Font`도 Composable이다

셋 다 `@Composable` 함수다. 리소스를 읽는 건 파일 입출력이라, Compose가 읽어 오고 캐시하는 과정을 관리한다. 그래서 **Composable 안에서만** 부를 수 있다. enum이나 일반 함수에서는 `Res.drawable.xxx`(리소스를 가리키는 **표지**)만 들고 있다가, 화면을 그릴 때 `painterResource(표지)`로 실제 이미지를 꺼낸다.

```kotlin
enum class Season(val image: DrawableResource) {    // 표지만 보관 (Composable 아님)
    SPRING(Res.drawable.intro_spring),
}
// 그릴 때:
Image(painter = painterResource(season.image), …)    // 실제 이미지 (Composable)
```

React로 치면 enum에는 `import`한 경로만 두고, 렌더할 때 `<img src={…}>`로 쓰는 것과 같다.

### 1-3. 비율이 다른 이미지를 칸에 꽉 채우기 — `Crop` + `BiasAlignment`

계절 이미지는 비율이 둘이다. 봄·여름은 1.79:1(가로로 김), 가을·겨울은 1.34:1이다. 칸의 비율은 창 크기에 따라 계속 바뀐다. 핸드오프 규칙(`10_intro.md`)은 하나다. **찌그러뜨리지 말고, 남는 부분은 잘라라.**

| Compose | CSS | 뜻 |
| --- | --- | --- |
| `contentScale = ContentScale.Crop` | `object-fit: cover` | 비율을 유지한 채 칸을 **빈틈없이** 덮을 때까지 키운다. 넘치는 쪽은 잘린다 |
| `alignment = BiasAlignment(0f, 0.1f)` | `object-position: 50% 55%` | 잘릴 때 **어느 쪽을 남길지**. −1 = 위(왼쪽) 끝, 0 = 가운데, +1 = 아래(오른쪽) 끝 |

환산은 `bias = (y% − 50) / 50`이다. 가을·겨울은 인물이 약간 아래에 있어서 초점이 55%이고, `+0.1f`다.

| 계절 | verticalBias (Desktop) |
| --- | --- |
| 봄·여름 | `0f` |
| 가을·겨울 | `0.1f` |

플레이스홀더에는 **10% 격자, 초점 십자선, 1.6:1 안전 영역**이 그려져 있다. 창 크기를 바꿀 때 무엇이 잘리고 십자선이 어디 남는지 눈으로 볼 수 있다.

### 1-4. 이미지 위에 덮개 — 겹쳐 그리기

A4의 밝기 덮개는 지금 `background(계절색).background(덮개)`다. 이제 계절색이 **자식 `Image`** 로 바뀐다. 그런데 `background`는 내용물(자식) **밑에** 칠해진다. 그래서 덮개를 `background`로 두면 이미지에 가려진다.

A3에서 본 `Box`의 겹쳐 쌓기로 푼다. 칸을 `Box`로 두고, 자식을 **이미지 → 덮개** 순서로 넣는다. 나중에 선언한 덮개가 위층이다.

```
SeasonQuadrant(season, selected, modifier, onSelect)        ← A4 시그니처 그대로
└── Box (modifier · clickable)
    ├── Image            칸 가득, Crop, BiasAlignment     ← 아래층
    └── Box (덮개)        Modifier.matchParentSize().background(overlay)   ← 위층
```

`matchParentSize()`는 **`Box` 안에서만** 쓸 수 있는 크기 지정이다 (A3의 `weight`처럼 스코프 전용). "부모 `Box`와 같은 크기로"라는 뜻이다. `fillMaxSize()`와 달리 부모의 크기 결정에 끼어들지 않는다. CSS의 `position: absolute; inset: 0`과 같다.

### 1-5. 글꼴: `FontFamily`와 `TextStyle`, 그리고 테마에 넣기

| Compose | CSS |
| --- | --- |
| `Font(Res.font.sora_bold, FontWeight.Bold)` | `@font-face { font-family: Sora; font-weight: 700; src: url(…) }` |
| `FontFamily(Font(…), Font(…))` | 같은 `font-family` 이름에 굵기별 파일 여러 개 |
| `TextStyle(fontFamily = …, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = …)` | `.display-xl { font: 26px/32px …; }` 같은 클래스 하나 |
| `Text(…, style = TabijiTheme.typography.displayXL)` | `<p class="display-xl">` |

`sp`는 글자 크기 단위다. `dp`와 같지만 사용자의 "글자 크기" 설정을 따라 커진다.

글꼴 스타일 9종은 A5의 색과 **똑같은 방식으로** 테마에 넣는다. `TabijiTypography` data class, `LocalTabijiTypography` 통로, `TabijiTheme.typography` 읽기다. 한 가지 다른 점이 있다. `Font(Res.font.…)`가 Composable이라서 스타일 표를 **Composable 안에서** 만들어야 한다. 그래서 enum 상수처럼 미리 만들어 둘 수 없고, `TabijiTheme` 함수 안에서 만들어 공급한다.

### 1-6. Jua에는 한자가 없다 — 폴백

Jua는 한글·라틴 글자만 있다. "여행(旅路) 도우미"를 Jua로 쓰면 **旅路** 두 글자를 그릴 글리프가 없다. 핸드오프 규칙은 "디스플레이 스타일은 `FontFamily(Jua, PretendardJP)` 체인으로 선언해 글자 단위 폴백"이다.

CSS의 `font-family: Jua, "Pretendard JP"`는 브라우저가 글자마다 폴백한다. 하지만 **Compose의 `FontFamily(…)` 목록이 글자 단위 폴백으로 동작하는지는 플랫폼마다 다르다.** 목록을 "굵기별 후보"로만 쓰고, 없는 글자는 OS 시스템 폰트로 대체할 수도 있다. 이번 단계에서는 핸드오프대로 체인을 선언하고, 완료 조건은 **두부(□)가 안 보이는 것**으로 확인한다. Linux에는 한자 시스템 폰트(Noto CJK)가 있어서 어느 쪽이든 두부는 안 나올 가능성이 크다. Android에서 같은지는 Phase M(M2)에서 본다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| enum 생성자 여러 값 | `SPRING(Res.drawable.intro_spring, Res.string.season_spring, 0f)` | A5의 `TabijiPalette(val colors)`에 값이 여러 개로 늘어난 것 | `{ image, label, focusY }` 객체를 항목마다 |
| `Float` 리터럴 | `0.1f` | 끝의 `f`가 Float(32비트)라는 표시. `0.1`만 쓰면 Double이라 타입 오류 | — |
| 단위 확장 | `26.sp`, `(-0.02).em` | A3의 `3.dp`와 같은 확장 프로퍼티. `em`은 글자 크기에 비례하는 단위 | CSS `26px`, `-0.02em` |
| `vararg` 넘기기 | `CompositionLocalProvider(A provides a, B provides b) { … }` | 같은 종류 인자를 몇 개든 쉼표로 | `(...args)` |
| `error("…")` | `staticCompositionLocalOf<T> { error("TabijiTheme 밖") }` | 예외를 던진다. "여기 오면 안 됨"을 표시 | `throw new Error("…")` |

---

## 3. 준비물

### Claude가 해 둔 것

- `shared/src/commonMain/composeResources/drawable/intro_{spring,summer,autumn,winter}.webp`: **IP 없는 플레이스홀더**. 원본과 같은 비율(2048×1143 = 1.79:1, 2048×1529 = 1.34:1)이고, 계절 테마 `accent` 색에 격자·초점 십자선(Desktop 초점 y%)·1.6:1 안전 영역이 그려져 있다. 권리 처리된 실제 일러스트가 오면 같은 이름으로 바꿔 끼운다

### 직접 복사할 것 (`client/`에서)

```bash
R=shared/src/commonMain/composeResources
H=../design/handoff/handoff-v2/assets

# 글꼴 5개 — 쓰는 굵기만 (02_typography의 9 스타일 기준)
cp $H/fonts/Jua-Regular.ttf          $R/font/jua_regular.ttf
cp $H/fonts/PretendardJP-SemiBold.ttf $R/font/pretendardjp_semibold.ttf
cp $H/fonts/PretendardJP-Bold.ttf     $R/font/pretendardjp_bold.ttf
cp $H/fonts/Sora-SemiBold.ttf         $R/font/sora_semibold.ttf
cp $H/fonts/Sora-Bold.ttf             $R/font/sora_bold.ttf

# 글꼴 라이선스 — OFL은 글꼴을 앱에 넣어 배포할 때 라이선스 전문을 함께 두라고 요구한다
mkdir -p $R/files/licenses
cp $H/fonts/OFL-Jua.txt $H/fonts/OFL-PretendardJP.txt $H/fonts/OFL-Sora.txt $R/files/licenses/

# Desktop 창 아이콘 — 旅 글리프, 투명 배경 (04_icons_favicon: Desktop 기본)
cp $H/logo/icon/desktop-window-512.png $R/drawable/app_icon.png
```

- PretendardJP는 굵기 하나가 5MB가 넘는다. 9 스타일이 쓰는 600(SemiBold)·700(Bold)만 넣는다. 크기를 줄이는 서브셋은 worklog 열린 일로 남아 있다
- 넣은 뒤 `composeResources/font/.gitkeep`과 `drawable/.gitkeep`은 지워도 된다 (빈 폴더 표시용이었다)

### import — 이름이 겹치는 것 (이번 단계 공통)

같은 이름의 클래스가 여러 패키지에 있다. 자동완성이 엉뚱한 쪽을 고르기 쉬우니 이 표대로 고른다.

| 쓰려는 것 | 맞는 import | 고르면 안 되는 것 |
| --- | --- | --- |
| 글자 스타일 | `androidx.compose.ui.text.TextStyle` | `org.jetbrains.skia.paragraph.TextStyle` |
| 리소스 폰트 | `org.jetbrains.compose.resources.Font` | `androidx.compose.ui.text.font.Font` (Android `R.font`·파일 경로용) |
| 굵기 | `androidx.compose.ui.text.font.FontWeight` | `FontStyle`(기울임), `org.jetbrains.skia.FontWeight` |
| 이미지 꺼내기 | `org.jetbrains.compose.resources.painterResource` | `androidx.compose.ui.res.painterResource` |
| 문자열 꺼내기 | `org.jetbrains.compose.resources.stringResource` | `androidx.compose.ui.res.stringResource` (Android `R.string`용) |
| 이미지 Composable | `androidx.compose.foundation.Image` | `org.jetbrains.skia.Image` |
| 색 | `androidx.compose.ui.graphics.Color` | `java.awt.Color` |
| 글자 | `androidx.compose.material3.Text` | `androidx.compose.material.Text` (Material 2) |

- `org.jetbrains.skia.*`는 그리기 엔진 내부다. 화면 코드에서 쓸 일이 없다
- `java.awt.*`는 Desktop 창 설정(`main.kt`)에서만 쓴다
- `org.jetbrains.compose.resources.*`는 `Res`와 짝으로 쓰는 것들이다

---

## 4. 조립 순서

`scripts/linux/run-desktop.sh --hot`을 띄워 두고 단계마다 본다. 단, **새 리소스 파일을 추가하거나 `strings.xml`을 바꾼 직후에는 `Res`를 다시 만들어야** 하니 앱을 다시 띄운다. Hot Reload는 Kotlin 코드 변경만 반영한다.

### 4-1. `strings.xml` — 화면 글자를 파일로

`composeResources/values/strings.xml`을 만든다.

```xml
<resources>
    <string name="app_name">Tabiji</string>
    <string name="intro_wordmark">Tabiji</string>
    <string name="intro_subtitle">여행(旅路) 도우미</string>
    <string name="season_spring">봄</string>
    <!-- season_summer · season_autumn · season_winter -->
</resources>
```

- `app_name`(창 제목)과 `intro_wordmark`(화면 로고)는 지금은 같은 글자지만 **용도가 달라서** 따로 둔다. 나중에 창 제목이 "Tabiji — 내 여행"처럼 바뀌어도 로고는 그대로여야 한다
- `IntroScreen`의 워드마크 `Text`에서 `text` 값만 바꾼다

  ```kotlin
  Text(
      modifier = Modifier.align(Alignment.Center),
      text = stringResource(Res.string.intro_wordmark),   // 전: "Tabiji"
      color = Color.White,
  )
  ```

  `stringResource(키)`는 `strings.xml`에서 그 키의 값을 읽어 `String`으로 돌려준다 (React i18n의 `t('intro_wordmark')`). import는 `org.jetbrains.compose.resources.stringResource`와 `tabiji.shared.generated.resources.*`

import (`IntroScreen.kt`에 추가):

```kotlin
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.*
```

✅ 확인: 앱을 다시 띄워도 **화면이 그대로**다. 일부러 `strings.xml`의 `Tabiji`를 바꿔 보면 화면 글자가 따라 바뀐다 (확인 뒤 되돌림).

### 4-2. 계절 이미지

**`Season.kt`**: 계절마다 이미지·이름·초점을 붙인다.

```kotlin
enum class Season(
    val image: DrawableResource,   // org.jetbrains.compose.resources.DrawableResource
    val label: StringResource,     // org.jetbrains.compose.resources.StringResource
    val focusY: Float,             // BiasAlignment의 verticalBias (1-3 표)
) {
    SPRING(Res.drawable.intro_spring, Res.string.season_spring, 0f),
    // SUMMER · AUTUMN · WINTER
}
```

**`IntroScreen.kt`의 `SeasonQuadrant`**: **시그니처(`season, selected, modifier, onSelect`)와 호출부 네 곳은 A4 그대로 둔다.** 바뀌는 건 칸 **안쪽**뿐이다. 계절색 `background`가 `Image`로 바뀌고, 덮개 `background`가 자식 `Box`로 옮겨 간다.

```
전 (A4~A5)                                    후 (A6)
Box(                                          Box(
  modifier                                      modifier
    .background(seasonColor(season))  ──┐         .clickable(…) { onSelect(season) }
    .background(overlay)              ─┐│     ) {
    .clickable(…) { onSelect(season) } ││       Image(…)                                   ← 계절색 자리
)                                      │└──→    Box(Modifier.matchParentSize()
                                       └───→        .background(overlay))                ← 덮개 자리
                                              }
```

- 덮개를 `background`로 두면 안 되는 이유: `background`는 자식(`Image`) **밑에** 칠해져서 이미지에 가려진다 (1-4)
- `overlay`를 정하는 `when (selected)`는 A4 코드 그대로
- `Image`는 핸드오프 `10_intro.md`의 코드 그대로다

  ```kotlin
  Image(
      painter = painterResource(season.image),
      contentDescription = stringResource(season.label),   // 화면 낭독기가 읽는 설명 (img의 alt)
      contentScale = ContentScale.Crop,
      alignment = BiasAlignment(0f, season.focusY),
      modifier = Modifier.fillMaxSize(),
  )
  ```

- A4~A5의 `seasonColor()` 함수는 이제 쓰이지 않으니 지운다 (`TabijiPalette` import도)

import:

```kotlin
// Season.kt
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import tabiji.shared.generated.resources.*

// IntroScreen.kt (4-1에 더해)
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
// 지울 것: com.yamada.studio.tabiji.core.ui.theme.TabijiPalette (seasonColor 삭제로 안 씀)
```

`Season.kt`의 `focusY`는 1-3 표대로 **봄·여름 `0f`, 가을·겨울 `0.1f`** 다.

✅ 확인 (앱 다시 띄우기):
- 네 칸에 플레이스홀더가 보인다. 찌그러짐(격자가 직사각형으로 늘어남)이 없다
- 창을 **아주 넓게** 만들면 위아래가 잘리고, **아주 길쭉하게** 만들면 좌우가 잘린다. 어느 쪽이든 **십자선은 칸 안에 남는다**
- 가을·겨울은 십자선이 칸 가운데보다 살짝 아래에 있다 (초점 55%)
- A4의 클릭 선택(밝게/어둡게)이 이미지 위에서도 그대로 동작한다
- 한 줄에 두 칸이 반씩 보인다. 한 칸이 줄을 다 차지하면 호출부의 `Modifier.weight(1f)`가 빠진 것이다

### 4-3. 실제 일러스트로 로컬 확인 (커밋 금지)

플레이스홀더 자리에 실제 일러스트를 **잠깐** 덮어써서 본다.

```bash
R=shared/src/commonMain/composeResources/drawable
I=../design/handoff/handoff-v2/assets/intro          # gitignore라 이 PC에만 있음
for s in spring summer autumn winter; do cp $I/$s.webp $R/intro_$s.webp; done
```

✅ 확인: 창 비율을 바꿔도 가을·겨울 인물의 머리·발이 잘리지 않는다. `intro_desktop.png`와 비교한다.

**확인이 끝나면 반드시 되돌린다.**

```bash
git restore shared/src/commonMain/composeResources/drawable/intro_*.webp
git status --short    # intro_*.webp가 목록에 없어야 한다
```

IDE가 바뀐 파일을 자동으로 `git add` 해 두는 경우가 있다. `git status`에서 `M`이 보이면 `git restore --staged --worktree …`로 되돌린다. Claude도 커밋 전에 이 파일들이 플레이스홀더인지 확인한다.

### 4-4. 글꼴 스타일 9종을 테마에

`core/ui/theme/TabijiTypography.kt`를 만든다.

**① 틀**: `@Immutable data class TabijiTypography(val displayXL: TextStyle, … 9개)`

| 이름 | 글꼴 | 크기/줄높이 (sp) | 굵기 |
| --- | --- | --- | --- |
| `displayXL` | 디스플레이 체인 | 26 / 32 | — |
| `displayL` | 디스플레이 체인 | 20 / 26 | — |
| `displayM` | 디스플레이 체인 | 16 / 22 | — |
| `displayS` | 디스플레이 체인 | 14 / 18 | — |
| `body` | Pretendard JP | 14 / 20 | SemiBold |
| `caption` | Pretendard JP | 12 / 16 | SemiBold |
| `badge` | Pretendard JP | 11 / 14 | Bold |
| `numeric` | Sora | 12 / 16 | SemiBold |
| `wordmark` | Sora | 58 / 64, 자간 `(-0.02).em` | Bold |

**② 만드는 함수**: `@Composable fun tabijiTypography(): TabijiTypography`. 안에서 글꼴 가족 셋을 먼저 만들고 9개 스타일을 채운다.

```kotlin
val pretendard = FontFamily(
    Font(Res.font.pretendardjp_semibold, FontWeight.SemiBold),
    Font(Res.font.pretendardjp_bold, FontWeight.Bold),
)
val sora = FontFamily(/* sora_semibold, sora_bold — 위와 같은 모양 */)
val display = FontFamily(
    Font(Res.font.jua_regular),                               // 먼저 Jua
    Font(Res.font.pretendardjp_semibold, FontWeight.SemiBold), // 한자·가나 폴백 후보 (1-6)
)
```

- 함수 이름이 소문자로 시작하는 건 Compose 관례다. 화면을 그리지 않고 **값을 돌려주는** Composable은 소문자로 쓴다 (`remember…`, `painterResource`처럼)
- `Font`는 `org.jetbrains.compose.resources.Font`다. `androidx.compose.ui.text.font.Font`와 이름이 같으니 import를 정확히 고른다
- data class 필드에 `= TextStyle()` 같은 기본값을 **주지 않는다.** 하나를 빠뜨리면 컴파일 오류로 잡혀야 한다 (`TabijiColors`와 같은 이유)

import (`TabijiTypography.kt`):

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle              // skia 것 아님
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight        // FontStyle(기울임) 아님
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font            // androidx 것 아님
import tabiji.shared.generated.resources.*
```

`TabijiTheme.kt`·`TokenSwatches.kt`는 A5의 import로 충분하다 (`error(…)`는 Kotlin 기본 함수).

**③ 테마에 연결** (`TabijiTheme.kt`, A5와 같은 패턴):
- 통로: `val LocalTabijiTypography = staticCompositionLocalOf<TabijiTypography> { error("TabijiTheme 밖에서 typography를 읽었어요") }`. 색과 달리 기본값을 만들 수 없다(`Font`가 Composable이라서). 그래서 "여기 오면 버그"라고 표시한다
- 공급: `CompositionLocalProvider(LocalTabijiColors provides …, LocalTabijiTypography provides tabijiTypography(), content = content)`
- 읽기: `object TabijiTheme`에 `val typography: TabijiTypography`를 `colors`와 같은 모양으로 추가한다

**④ 토큰 화면에 글꼴 구역 추가** (`TokenSwatches`): 색 목록 아래에 `(이름, 스타일)` 목록을 같은 방식(A5의 `rows`)으로 만들고, 줄마다 샘플 문장을 그 스타일로 그린다.

```kotlin
Text("displayXL  Day 2 · 시즈오카 센겐 신사 · 駿府城 · 旅路 1.8km", style = t.displayXL, color = c.ink)
```

샘플 문장은 핸드오프 T2의 확인 문장이다. 디버그 화면이라 하드코딩하고 `// 디버그 샘플` 주석을 단다.

✅ 확인 (앱 다시 띄우기 → 토큰 화면):
- display 줄은 둥근 Jua 글씨, body/caption/badge는 Pretendard, numeric·wordmark는 Sora다
- 어느 줄에도 **두부(□)가 없다**. 특히 display 줄의 駿府城·旅路
- 크기 차이가 표대로 보인다 (wordmark가 가장 크고, badge가 가장 작다)

### 4-5. 워드마크와 부제

`IntroScreen`의 가운데 `Text` 하나를 **두 줄짜리 `Column`** 으로 바꾼다. **새 `Box`는 넣지 않는다.** 이미 있는 바깥 `Box` 안에 놓이니 `Modifier.align`을 쓸 수 있다.

```
IntroScreen
└── Box (이미 있는 바깥 Box, fillMaxSize)
    ├── Column (사분면 2×2)                         ← 그대로
    └── Column (가운데 스택)                         ← 원래 Text 한 개 자리
          modifier = Modifier.align(Alignment.Center)
          horizontalAlignment = Alignment.CenterHorizontally
        ├── Text 워드마크
        └── Text 부제
```

`Column` 안에 `Box`를 또 두면 두 `Text`가 위아래가 아니라 **겹쳐** 쌓인다 (A3).

| 줄 | 문자열 | 스타일 |
| --- | --- | --- |
| 워드마크 | `Res.string.intro_wordmark` | `TabijiTheme.typography.wordmark`, 흰색 |
| 부제 | `Res.string.intro_subtitle` | `displayM.copy(fontSize = 17.sp, letterSpacing = 0.1.em)`, 흰색 (`10_intro.md`: Jua 17sp, 자간 0.1em) |

- 스타일·색은 `Text`의 파라미터다: `Text(text = stringResource(…), style = TabijiTheme.typography.wordmark, color = Color.White)`. `stringResource(키, …)`의 두 번째 인자부터는 문자열 안 `%1$s` 자리에 끼울 값(React i18n의 `t('key', { name })`)이라, 스타일을 넣으면 **오류 없이 무시된다**
- 스타일은 4-4 ③에서 연결한 `TabijiTheme.typography`로 읽는다. `tabijiTypography()`를 화면에서 직접 부르지 않는다 (색을 `TabijiTheme.colors`로 읽는 것과 같은 이유)
- `copy(…)`는 A4에서 본 data class 복사다. `TextStyle`도 일부 값만 바꾼 복사본을 만들 수 있다
- 흰 글자는 밝은 이미지 위에서 잘 안 읽힐 수 있다. **잉크 아웃라인과 하드 섀도는 A8에서** 입힌다

import (`IntroScreen.kt`에 추가):

```kotlin
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
// Column·Alignment는 이미 있음 (layout.* · ui.Alignment)
```

✅ 확인: 가운데에 큰 Sora "Tabiji", 그 아래 Jua "여행(旅路) 도우미". 旅路에 두부가 없다.

### 4-6. 창 아이콘·제목·기본 크기

**① 아이콘과 제목.** `main.kt`는 `Res`를 못 쓴다(1-1). 그래서 `shared`에 꺼내 주는 함수를 만든다.

`app/AppResources.kt`:

```kotlin
@Composable
fun appIcon(): Painter = painterResource(Res.drawable.app_icon)     // androidx.compose.ui.graphics.painter.Painter

@Composable
fun appTitle(): String = stringResource(Res.string.app_name)
```

`main.kt`: `Window(…, title = appTitle(), icon = appIcon())`. `application { }` 안은 Composable이라 두 함수를 부를 수 있다. import는 `com.yamada.studio.tabiji.app.appIcon`, `appTitle`이다.

**② 기본 창 크기: 화면의 가로 85% × 세로 75%, 가운데.** 지금은 Compose Desktop 기본값(800×600)으로 뜬다. 창의 크기와 위치는 **`WindowState`** 라는 상태 객체가 들고 있다. A4의 `remember { mutableStateOf(…) }`처럼 `rememberWindowState(…)`로 만들어 `Window(state = …)`에 넘긴다.

```kotlin
val screen = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds   // 작업표시줄을 뺀 화면 영역
val windowState = rememberWindowState(
    position = WindowPosition(Alignment.Center),
    size = DpSize((screen.width * 0.85f).dp, (screen.height * 0.75f).dp),
)
Window(state = windowState, …)
```

- **화면 크기는 플랫폼 API로 읽는다.** `java.awt.GraphicsEnvironment`는 JVM(Desktop)에만 있는 API라서 `commonMain`이 아니라 `desktopApp`의 `main.kt`에서만 쓸 수 있다. Android에는 "창 크기"라는 개념이 없다 (앱이 화면을 차지한다)
- `maximumWindowBounds`는 작업표시줄·상단 바를 **뺀** 영역이다. 전체 화면 크기(`Toolkit.getDefaultToolkit().screenSize`)로 계산하면 창 아래가 작업표시줄에 가릴 수 있다
- **단위**: AWT가 돌려주는 값은 배율(HiDPI)이 적용된 논리 픽셀이고, Compose Desktop에서는 이 값이 곧 `dp`다. 그래서 그대로 `.dp`를 붙인다
- CSS로 치면 `width: 85vw; height: 75vh`를 창 자체에 거는 셈이다. 다만 **처음 열릴 때 한 번만** 정해진다. 사용자가 창 크기를 바꾸면 그 값이 `windowState`에 들어간다
- import는 아래 블록에 모았다

**③ 최소 크기 (선택).** 너무 작게 줄이면 레이아웃이 깨지니 바닥을 둔다. `Window { }` 블록 안에서는 실제 AWT 창 객체가 `window`로 보인다. 그 객체에 직접 설정한다.

```kotlin
Window(…) {
    window.minimumSize = java.awt.Dimension(360, 600)   // A7에서 폰 크기(390×844)까지 줄여 볼 수 있게 360
    …
}
```

import:

```kotlin
// app/AppResources.kt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tabiji.shared.generated.resources.*

// main.kt (기존에 더해)
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.yamada.studio.tabiji.app.appIcon
import com.yamada.studio.tabiji.app.appTitle
import java.awt.Dimension                 // ③ 최소 크기 (AWT 창 설정이라 java.awt가 맞다)
import java.awt.GraphicsEnvironment
```

✅ 확인:
- `Alt+Tab` 전환기와 창 목록에 旅 아이콘이 보인다. GNOME 독(dock)에는 Gradle로 띄운 앱이 Java 기본 아이콘으로 나올 수 있다. 설치 패키지(A9)에서는 `.desktop` 파일 아이콘이 쓰인다
- 앱을 띄우면 화면 가운데에 화면의 85%×75% 크기로 열린다
- (③을 했다면) 창을 끝까지 줄여도 360×600 아래로는 안 줄어든다

### 4-7. 하드코딩 확인

```bash
grep -rn 'Text("' shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature
grep -rn 'Color(0x' shared/src/commonMain/kotlin --include=*.kt | grep -v TabijiPalette.kt
git status --short shared/src/commonMain/composeResources/drawable   # intro_*.webp가 M이면 4-3 되돌리기
```

첫째·둘째 결과는 0줄이어야 한다. 토큰 화면(`core/ui/theme`)의 디버그 샘플은 대상이 아니다.

---

## 5. 흔한 오류와 원인 읽기

| 증상 | 원인 |
| --- | --- |
| `Unresolved reference: intro_spring` (그런데 `Res`는 됨) | 리소스별 import 누락 (1-1). 또는 파일을 넣은 뒤 앱을 다시 안 띄워서 `Res`가 갱신 전 |
| `Unresolved reference: Res` | `import tabiji.shared.generated.resources.Res` 누락, 또는 `desktopApp`에서 쓰려 함 (`internal`) |
| 리소스 이름이 이상하게 생성되거나 빌드 오류 | 파일 이름에 하이픈·대문자 (`Jua-Regular.ttf` 그대로 복사) |
| `@Composable invocations can only happen from …` (`painterResource`나 `Font`에서) | enum 생성자나 일반 함수 안에서 부름. 거기엔 `Res.drawable.xxx`(표지)만 둔다 |
| 이미지가 찌그러짐 | `contentScale` 기본값(`Fit`)이나 `FillBounds` 사용 → `ContentScale.Crop` |
| 위아래에 빈 띠(레터박스) | `ContentScale.Fit` 사용 |
| 덮개(선택 밝기)가 안 보임 | 덮개를 `background`로 둬서 `Image` 아래에 깔림 → 자식 `Box`로, `Image` 다음에 |
| 칸 크기가 이상하게 커지거나 작아짐 | 덮개에 `fillMaxSize()` → `matchParentSize()` |
| 4-4에서 `IllegalStateException: TabijiTheme 밖에서…` | `TabijiTheme { }` 밖에서 `TabijiTheme.typography`를 읽음, 또는 공급자에 `LocalTabijiTypography provides …`를 빠뜨림 |
| 글자가 두부 □ | 폴백이 안 되는 글꼴로만 그림. display 체인에 Pretendard가 빠졌는지 확인 |
| `Overload resolution ambiguity: Font` | `androidx…font.Font`와 `org.jetbrains…resources.Font`를 둘 다 import함 |
| 창 아이콘이 안 바뀜 | `app_icon.png`를 넣은 뒤 앱을 다시 안 띄움 |

## 6. 완료 조건 (모두 눈으로)

- [ ] 네 칸에 플레이스홀더 이미지. 창을 넓게·길쭉하게 바꿔도 찌그러짐·빈 띠 없음, 십자선이 칸 안에 남음, 가을·겨울 초점이 약간 아래
- [ ] 4-3: 실제 일러스트로 바꿔 봤을 때 인물이 안 잘림. **되돌린 뒤** `git status`에 `intro_*.webp` 변경 없음
- [ ] 토큰 화면 글꼴 구역: 9 스타일이 표의 글꼴·크기로 보이고, 두부 없음
- [ ] 인트로 가운데: Sora "Tabiji" + Jua "여행(旅路) 도우미"
- [ ] `Alt+Tab`에 旅 창 아이콘, 창 제목은 `strings.xml`에서, 처음 열릴 때 화면 85%×75% 가운데
- [ ] 4-7의 grep 0줄
- [ ] A4 선택 동작, A5 테마 전환이 그대로 동작
- [ ] PR의 `client-ci` 초록

## 7. 끝나면 붙여넣어 줄 것

1. 바뀐 Kotlin 파일 (`Season.kt`, `IntroScreen.kt`, `TabijiTypography.kt`, `TabijiTheme.kt`, `TokenSwatches.kt`, `AppResources.kt`, `main.kt`)과 `strings.xml`
2. 스크린샷: 인트로 기본 크기 + 아주 넓은 창, 토큰 화면 글꼴 구역, (가능하면) 실제 일러스트로 본 한 장
3. `git status --short` 결과

중간에 막히면 단계 번호와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): load intro images, fonts and strings from compose resources`, `Refs: A6`)과 push를 하고, #10 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A7(반응형: Desktop 2×2 ↔ Phone 세로 4단)로 간다. 잉크 마감(워드마크 아웃라인·하드 섀도·chamfer 버튼·눌림)은 A8이다.
