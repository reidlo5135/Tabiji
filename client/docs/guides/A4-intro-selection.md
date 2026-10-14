# A4 — 상태 기초: 누른 사분면만 밝게

> 브랜치: `client/feat-a4-intro-selection` (Claude가 만들어 둠, A3 PR #8 위에 쌓음) · 커밋 footer `Refs: A4`
> 시각 기준: handoff-v2 `docs/10_intro.md` "파티클·패럴랙스·시퀀스" 절의 마지막 줄: *"호버/터치한 사분면 밝기 +8%, 나머지 −6%"*
> 확인은 **Desktop**에서 한다

## 목표

A3의 사분면을 **클릭해서 고를 수 있게** 만든다.

- 한 칸을 클릭하면 그 칸은 살짝 밝아지고(+8%) 나머지 세 칸은 살짝 어두워진다(−6%)
- 같은 칸을 다시 누르면 선택이 풀려 네 칸 모두 원래 색으로 돌아간다
- 다른 칸을 누르면 선택이 그쪽으로 옮겨 간다

화면 변화는 작지만, 이번 단계는 Compose에서 가장 중요한 질문 세 개를 다룬다.

1. 값이 바뀌면 화면은 **어떻게** 다시 그려지나 (recomposition)
2. 그 값은 **어디에** 저장되나 (`remember` + `mutableStateOf`)
3. 그 값은 **누가** 가져야 하나 (state hoisting)

A5 이후의 테마 전환, B2의 ViewModel이 전부 이 세 가지 위에 쌓인다.

> 범위: **클릭만** 다룬다. 핸드오프의 "호버 시 밝기"와 120ms 애니메이션은 E2(포인터 입력·시퀀스)에서 한다.

## 먼저 읽을 것 (40분)

| 문서 | 왜 |
| --- | --- |
| [State와 Compose](https://developer.android.com/develop/ui/compose/state) | `remember`, `mutableStateOf`, state hoisting. 오늘의 본문 |
| [Composable의 생명주기](https://developer.android.com/develop/ui/compose/lifecycle) | composition·recomposition이 정확히 무엇인지 |
| [Kotlin enum class](https://kotlinlang.org/docs/enum-classes.html) · [null 안전성](https://kotlinlang.org/docs/null-safety.html) · [`when`](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements) | 2장 문법의 원문 |

---

## 1. 핵심 개념: 상태가 화면을 바꾸는 원리

### 1-1. 화면을 바꾸는 방법은 "함수를 다시 부르기"뿐이다

A1부터 써 온 `@Composable` 함수는 **화면을 그리는 함수**다. 처음 한 번 불려서 화면이 만들어진다. 이걸 **composition**이라 한다. 이미 그려진 글자나 색을 나중에 "수정"하는 API는 없다. 화면을 바꾸려면 **함수를 새 값으로 다시 불러야** 한다. 이걸 **recomposition**이라 한다.

React와 같은 모델이다. React 함수 컴포넌트도 DOM을 직접 고치지 않고, 상태가 바뀌면 컴포넌트 함수를 다시 실행(리렌더)해서 새 결과를 만든다.

그러면 문제는 두 가지로 좁혀진다.

- **누가** "다시 불러라"를 결정하나?
- 다시 불릴 때 함수 안의 지역 변수는 처음부터 다시 실행된다. 그럼 **이전 값은 어디에** 남아 있나?

### 1-2. 일반 변수로는 안 되는 이유와, 그걸 고치는 두 도구

```kotlin
@Composable
fun Counter() {
    var count = 0                                // ✗ 일반 변수
    Text("$count", Modifier.clickable { count++ })
}
```

이 카운터는 눌러도 0에서 안 바뀐다. 이유가 **두 개**다.

| 문제 | 설명 | 고치는 도구 |
| --- | --- | --- |
| ① 아무도 모른다 | `count++`는 그냥 메모리 값만 바꾼다. Compose는 이 값이 바뀐 걸 알 방법이 없으니 함수를 다시 부르지 않는다 | **`mutableStateOf(초깃값)`**: Compose가 **지켜보는 상자**(`MutableState`). 값을 넣으면, 그 상자를 **읽은** Composable이 다시 불리도록 예약된다 |
| ② 다시 불리면 리셋된다 | 혹시 다시 불리더라도 첫 줄 `var count = 0`이 또 실행되니 다시 0이다 | **`remember { … }`**: 처음 composition 때만 `{ }` 안을 실행해 결과를 저장한다. 다시 불릴 때는 **저장해 둔 그 객체를 그대로** 돌려준다 |

그래서 둘을 겹쳐 쓴다.

```kotlin
var count by remember { mutableStateOf(0) }      // ✓ 지켜보는 상자를, 다시 불려도 같은 것으로
```

**React와의 차이**: React의 `useState`는 이 두 일을 한 번에 한다. 리렌더를 일으키는 setter와, 리렌더 사이에 값 유지를 함께 맡는다. Compose는 이걸 **두 부품으로 나눠 놓았다.** 하나만 쓰면 어떻게 되는지는 6장 실험에서 직접 본다.

| 쓴 것 | 클릭하면 |
| --- | --- |
| 일반 `var` | 화면 안 바뀜 (①·② 둘 다 문제) |
| `mutableStateOf`만 | ? (실험 6-1) |
| `remember { mutableStateOf(…) }` | 정상 |

### 1-3. `by` — 상자의 `.value`를 생략하는 문법

`mutableStateOf`가 돌려주는 건 상자라서, 원래는 `count.value`로 읽고 `count.value = 1`로 써야 한다. `by`(위임 프로퍼티)를 쓰면 `count`를 평범한 변수처럼 읽고 쓰고, 뒤에서 `.value`가 불린다.

```kotlin
val box = remember { mutableStateOf(0) }    // box.value 로 읽고 쓰기
var count by remember { mutableStateOf(0) } // count 로 읽고 쓰기 (같은 동작)
```

`by`를 쓰려면 `androidx.compose.runtime.getValue`, `setValue` 두 import가 필요하다. 이 둘이 "상자를 변수처럼 쓰는 법"을 Kotlin에 알려 준다. IDE가 자동으로 넣어 주지 않으면 이 import 누락이 가장 흔한 오류다.

**대입이 곧 setState다.** React처럼 `setCount(…)`를 따로 부르지 않는다. 상자에 값을 넣는 순간 Compose가 알아챈다.

### 1-4. 상태는 누가 가져야 하나 — state hoisting

"선택된 칸"이라는 상태를 각 칸이 스스로 가진다고 해 보자. 봄 칸은 자기가 눌렸는지 알 수 있다. 하지만 **"다른 칸이 선택돼서 나는 어두워야 한다"는 알 수 없다.** 그 정보는 칸 바깥에 있기 때문이다. 또 네 칸이 각자 상태를 가지면 두 칸이 동시에 "선택됨"이 되는 것도 막을 수 없다.

해결은 상태를 **네 칸 모두를 아는 가장 가까운 부모**, 즉 `IntroScreen`으로 올리는 것이다. 이것이 state hoisting이고, React의 "lifting state up"과 이유도 방법도 같다.

올린 뒤의 데이터 흐름은 한 방향이다.

```
            IntroScreen   ── selected: Season?  (상태는 여기 하나뿐)
               │    ▲
  값이 내려감  │    │  이벤트가 올라옴
   emphasis    │    │  onClick()
               ▼    │
      SeasonQuadrant × 4   (상태 없음: 받은 값대로 그리고, 눌리면 알리기만)
```

- **아래로**: 부모가 각 칸에 "너는 밝게 / 어둡게 / 보통으로"를 값으로 넘긴다 (React의 props)
- **위로**: 칸은 상태를 직접 바꾸지 않고 "나 눌렸어"라는 함수(`onClick`)만 부른다 (React의 콜백 prop)
- 칸은 자기가 왜 밝은지 모른다. 그래서 테스트하기 쉽고, A6에서 이미지로 바뀌어도 이 구조는 그대로다

### 1-5. 클릭 한 번에 일어나는 일 (순서대로)

1. 사용자가 여름 칸을 클릭한다 → 칸의 `clickable`이 받은 `onClick()`을 부른다
2. `onClick`은 부모가 넘긴 람다라서, 실제로는 부모 쪽 코드 `selected = Season.Summer`가 실행된다
3. `selected`는 `mutableStateOf` 상자라서, 이걸 **읽은** `IntroScreen`이 "다시 불러야 함"으로 표시된다
4. 다음 프레임에 `IntroScreen`이 다시 불린다. `remember`는 같은 상자를 돌려주고, 상자 안에는 이제 `Summer`가 있다
5. 각 칸의 강조 값을 새로 계산해 `SeasonQuadrant`를 다시 부른다. **넘긴 값이 지난번과 같은 칸은 Compose가 건너뛴다(skip).** React보다 더 잘게 다시 그리는 부분이다

이 다섯 줄이 이번 단계의 전부다. 막히면 여기로 돌아와 몇 번째 고리가 끊겼는지 본다.

---

## 2. 처음 보는 Kotlin 문법

| 문법 | 예 | 설명 | TypeScript로 빗대면 |
| --- | --- | --- | --- |
| `enum class` | `enum class Dir { Up, Down }` | 정해진 값 몇 개만 갖는 타입. `Dir.Up`처럼 쓴다 | `enum Dir { Up, Down }` |
| nullable 타입 | `val d: Dir? = null` | `?`가 붙으면 null도 담을 수 있다. `Dir`과 `Dir?`는 **다른 타입**이라, `Dir` 자리에 null을 넣으면 컴파일 오류 | `Dir \| null` (strictNullChecks) |
| `when` (인자 있음) | `when (d) { Dir.Up -> 1; Dir.Down -> -1 }` | 값을 돌려주는 switch. enum의 경우를 하나라도 빠뜨리면 **컴파일 오류** | exhaustive 검사가 붙은 `switch` |
| `when` (인자 없음) | `when { x < 0 -> "음수"; x == 0 -> "영"; else -> "양수" }` | 위에서부터 처음 참인 조건의 값. `if-else if` 사슬을 식으로 쓴 것 | 삼항 연산자 사슬 |
| `copy` | `Color.White.copy(alpha = 0.5f)` | 일부 속성만 바꾼 복사본 | `{ ...white, alpha: 0.5 }` |
| 함수 타입 | `onClick: () -> Unit` | 인자 없고 반환값 없는 함수를 받는 파라미터 | `onClick: () => void` |
| trailing lambda | `Button(…) { doIt() }` | 마지막 파라미터가 함수면 괄호 밖 `{ }`로 넘길 수 있다 | — |

---

## 3. 설계 선택 두 가지

### 3-1. 칸에 무엇을 넘길까

칸이 그려질 모습은 "보통 / 선택됨 / 어두움" 셋 중 하나다.

| | 방법 | 장단점 |
| --- | --- | --- |
| A | `selected: Boolean, dimmed: Boolean` | 익숙하고 쉽다. 하지만 `true, true` 같은 **말이 안 되는 조합**도 타입상 넘길 수 있다 |
| **B (추천)** | `Emphasis` enum 하나: `Normal`, `Selected`, `Dimmed` | 가능한 세 경우만 표현된다. `when`이 빠짐없이 검사해 준다 |

TypeScript에서 boolean 두 개 대신 `'normal' | 'selected' | 'dimmed'` 유니언을 쓰는 것과 같은 이유다. "불가능한 상태를 타입으로 막는다." A로 해도 되고, 리뷰에서 같이 본다.

### 3-2. 밝기를 어떻게 바꿀까

핸드오프의 "밝기 +8% / −6%"는 **반투명 덮개**로 근사한다.

| Emphasis | 덮개 |
| --- | --- |
| `Selected` | 흰색, 불투명도 8% |
| `Dimmed` | 검정, 불투명도 6% |
| `Normal` | 투명 (`Color.Transparent`) |

칠하는 방법은 `background`를 **두 번** 거는 것이다: 계절색 다음에 덮개색. A3에서 본 규칙대로 체인의 뒤쪽이 안쪽이고 위층이라, 두 번째 색이 첫 번째 위에 덮인다. CSS로 치면 `background: linear-gradient(덮개, 덮개), 계절색`처럼 겹쳐 칠하는 것이다.

`clickable`은 기본으로 누를 때 회색 덮개 효과(indication)를 보여 준다. 그러면 우리 덮개와 섞이니 끈다. v2 원칙상 인트로 사분면의 반응은 밝기 변화 하나뿐이다.

---

## 4. 완성 후의 모습

### 파일

| 경로 | 들어갈 것 |
| --- | --- |
| `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature/intro/Season.kt` (새 파일) | `enum class Season` (봄·여름·가을·겨울). 공개. A6에서 이미지·초점 정보가 여기에 붙는다 |
| `…/feature/intro/IntroScreen.kt` (수정) | `Emphasis` enum(`private`), `seasonColor()`(`private`), `IntroScreen`(상태와 연결), `SeasonQuadrant`(새 시그니처) |

`Emphasis`는 인트로 화면 안에서만 쓰는 표현 방식이라 `IntroScreen.kt` 안에 `private`으로 둔다. `Season`은 다른 곳(A6 이미지, 나중의 테마)에서도 쓰니 자기 파일로 뺀다.

### `IntroScreen.kt` 안의 구조

```
IntroScreen(modifier)
├── selected 상태 (remember + mutableStateOf, null = 선택 없음)
├── emphasisOf(season)        ← selected를 보고 Normal/Selected/Dimmed 계산 (지역 함수 또는 when 식)
└── Box ─ Column ─ Row ─ SeasonQuadrant(Season.Spring, emphasisOf(Spring), Modifier.weight(1f)) { 클릭 처리 }
                         … × 4
     └── Text "Tabiji"

SeasonQuadrant(season, emphasis, modifier, onClick)     ← 상태 없음
└── Box: modifier → 계절색 → 덮개색 → clickable

seasonColor(season): Color = when (season) { … }        ← 색 리터럴 4개는 여기 한 곳에, TODO(A6)
```

---

## 5. 조립 순서

**한 번에 다 고치지 않는다.** 단계마다 실행해서 화면이 그대로인지(또는 기대대로 바뀌는지) 확인하고 넘어간다. Hot Reload로 띄워 놓고 저장할 때마다 보면 빠르다.

```bash
scripts/linux/run-desktop.sh --hot
```

### 5-1. `Season` enum과 색 모으기

- `Season.kt`에 `enum class Season`을 만든다. 값은 `Spring`, `Summer`, `Autumn`, `Winter`
- `IntroScreen.kt`에 `private fun seasonColor(season: Season): Color = when (season) { … }`를 만든다. A3의 색 리터럴 4개가 여기로 온다. `// TODO(A6): 계절 이미지로`는 이 함수에 한 번만 단다
- `SeasonQuadrant`의 첫 파라미터를 `color: Color`에서 `season: Season`으로 바꾼다. 칸 안에서 `seasonColor(season)`으로 색을 얻는다. 호출부 네 곳은 `Season.Spring` 같은 값을 넘긴다

✅ 확인: 화면이 A3과 **똑같다.** 구조만 바꿨기 때문이다. 일부러 `when`에서 한 계절을 지워 보면 컴파일 오류가 나는 것도 확인할 수 있다(확인 뒤 되돌린다).

### 5-2. `Emphasis`와 덮개 — 아직 상태 없이

- `IntroScreen.kt`에 `private enum class Emphasis { Normal, Selected, Dimmed }` (3-1에서 A안을 골랐다면 boolean 두 개)
- `SeasonQuadrant`에 `emphasis: Emphasis` 파라미터를 추가한다. 순서는 컨벤션대로 `season`, `emphasis`, `modifier`
- 칸 안에서 `when (emphasis)`로 덮개색을 정하고, 체인을 `modifier → background(계절색) → background(덮개색)`로 만든다
- 호출부는 임시로 **손으로 값을 넣는다**: 봄 `Emphasis.Selected`, 나머지 셋 `Emphasis.Dimmed`

✅ 확인: 봄만 밝고 나머지가 어둡게 고정돼 보인다. 8%·6%는 미세하니 **잠깐 30%로 키워서** 눈으로 확인하고 다시 8%·6%로 돌린다.

이 시점의 `SeasonQuadrant`는 "값을 받아 그리기만 하는" 완성된 상태 없는 Composable이다. 상태를 붙이기 전에 그리기부터 검증해 두면, 다음 단계에서 문제가 생겼을 때 원인을 상태 쪽으로 좁힐 수 있다.

### 5-3. 클릭을 위로 알리기

- `SeasonQuadrant`의 **마지막** 파라미터로 `onClick: () -> Unit`을 추가한다 (`modifier` 뒤 — 컨벤션의 "콜백" 자리)
- 칸의 체인 끝에 `clickable(interactionSource = null, indication = null) { onClick() }`를 붙인다. import는 `androidx.compose.foundation.clickable`
- 호출부는 마지막 인자가 함수라서 trailing lambda로 넘길 수 있다. 임시로 `{ println("봄 클릭") }`처럼 넣어 둔다. 이 `println`은 동작 확인용이라 5-4에서 바로 지운다

✅ 확인: 칸을 클릭하면 `run-desktop.sh`를 띄운 터미널에 글자가 찍힌다. 화면은 아직 안 바뀐다. 상태가 없으니 당연하다.

### 5-4. 상태를 부모에 두고 연결하기

`IntroScreen` 맨 위에 상태 한 줄을 둔다 (1-2의 패턴을 `Season?`에 적용).

```kotlin
var selected by remember { mutableStateOf<Season?>(null) }   // null = 아무것도 선택 안 됨
```

- `mutableStateOf<Season?>(null)`의 `<Season?>`을 빼면 안 된다. `null`만 보고는 무슨 타입의 상자인지 알 수 없어서 "항상 null만 담는 상자"로 추론된다
- **내려보내기**: 5-2에서 손으로 넣은 `Emphasis` 값을 `selected`로부터 계산한 값으로 바꾼다. 규칙은 이렇다: 선택 없음이면 `Normal`, 이 칸이 선택됐으면 `Selected`, 그 밖은 `Dimmed`. 인자 없는 `when`이 잘 맞는다. 칸 네 개에 같은 계산을 반복하지 않게 `IntroScreen` 안에 작은 지역 함수로 만들어도 된다 (Kotlin은 함수 안에 함수를 둘 수 있다)
- **올려 받기**: 각 칸의 클릭 람다에서 `selected`를 바꾼다. 이미 선택된 칸이면 `null`(해제), 아니면 그 칸
- 5-3의 `println`을 지운다

✅ 확인: 이제 클릭에 따라 화면이 바뀐다.

**이 단계가 끝나면 `remember`/`mutableStateOf`는 파일 전체에 이 한 줄뿐이어야 한다.** `SeasonQuadrant` 안에는 없다.

### 5-5. 동작 확인

클릭 순서: 봄 → 봄(해제) → 여름 → 겨울 → (겨울 선택 상태로) 창 크기 바꾸기.

- 봄: 봄만 밝고 셋은 어둡다
- 봄 다시: 네 칸 모두 원래 색
- 여름 → 겨울: 선택이 옮겨 간다
- 창 크기 변경: 겨울 선택이 그대로다. 크기 변경은 recomposition이 아니라 **배치(layout) 단계만** 다시 돈다. 함수를 다시 부르지 않고 이미 만든 화면의 크기·위치만 다시 계산한다. 그래서 상태와 무관하게 유지된다. 반대로 클릭할 때마다 일어나는 recomposition에서 값이 유지되는 건 `remember` 덕분이다 (6-1에서 확인)

---

## 6. 실험 — 두 부품을 하나씩 빼 보기

1-2에서 "`mutableStateOf`는 알리는 일, `remember`는 기억하는 일"이라고 했다. 하나씩 빼서 확인한다. 각 실험은 **저장하기 전에 결과를 예측**하고, 끝나면 원래대로 되돌린다.

| 실험 | 바꾸는 줄 | 생각해 볼 것 |
| --- | --- | --- |
| 6-1 | `var selected by mutableStateOf<Season?>(null)` (`remember` 제거) | 클릭 → 상태 변경 → `IntroScreen`이 다시 불린다. 그때 이 줄이 다시 실행되면 상자는? (1-5의 4번 고리) |
| 6-2 | `var selected: Season? = null` (둘 다 제거한 일반 변수) | 클릭 람다가 이 변수를 바꾸긴 한다. 그런데 Compose가 다시 부를까? (1-5의 3번 고리) |

둘 다 겉보기 결과는 "안 바뀐다"로 같을 수 있다. 하지만 **끊어진 고리가 다르다.** 각각 1-5의 몇 번 고리가 끊겼는지 한두 줄로 적어 붙여넣는다.

React로 치면 6-2는 컴포넌트 안에 `let selected = null`을 쓴 경우다. 6-1은 React에는 딱 맞는 대응이 없다. `useState`는 두 일을 한꺼번에 하기 때문이다. 그래서 이 실험이 Compose를 이해하는 데 특히 중요하다.

---

## 7. 빌드 확인

```bash
scripts/linux/build.sh
```

## import 목록

IDE 동기화가 실패하는 동안은 자동완성이 약하니, A3에서 쓰던 것에 아래를 더한다.

```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
```

`Season`은 같은 패키지(`feature.intro`)라서 `IntroScreen.kt`에서 import가 필요 없다.

## 흔한 오류와 원인 읽기

| 증상 | 원인 | 1-5의 몇 번 고리 |
| --- | --- | --- |
| `Property delegate must have a 'getValue(...)' method` | `getValue`·`setValue` import 누락 | 컴파일 단계 |
| `'when' expression must be exhaustive` | `when (season)`이나 `when (emphasis)`에서 경우 하나를 빠뜨림 | 컴파일 단계 |
| `... actual type is 'Season', but 'Nothing?' was expected` (문구는 버전마다 조금 다름) | `mutableStateOf(null)`처럼 타입 없이 씀 → `mutableStateOf<Season?>(null)` | 컴파일 단계 |
| `No value passed for parameter 'onClick'` | 호출부 중 한 곳에 클릭 람다를 안 넘김 | 컴파일 단계 |
| 클릭해도 터미널에 아무것도 안 찍힘 (5-3) | `clickable`을 안 붙였거나, 칸 안에서 `onClick()`을 부르지 않음 | 1번 |
| 찍히는데 화면이 안 바뀜 (5-4) | `remember` 또는 `mutableStateOf` 누락, 또는 클릭 람다가 `selected`를 안 바꿈 | 2~4번 |
| 바뀌는데 엉뚱한 칸이 밝아짐 | 호출부에서 `Season` 값과 `emphasis` 계산에 넘긴 계절이 서로 다름 | 5번 |
| 클릭하면 회색 덮개가 잠깐 뜸 | `clickable`의 기본 indication → `indication = null` 버전 | — |
| 두 칸이 동시에 선택됨 | 칸마다 각자 상태를 가짐 → 상태는 부모 한 곳에 | 설계 |

## 완료 조건 (모두 눈으로)

- [ ] 한 칸을 클릭하면 그 칸만 밝고 나머지 셋은 어두워짐 (최종값 8%·6%)
- [ ] 같은 칸 다시 클릭 → 해제, 다른 칸 클릭 → 선택 이동
- [ ] 선택한 채 창 크기를 바꿔도 선택 유지
- [ ] 실험 6-1·6-2의 결과와 "몇 번 고리가 끊겼나" 설명
- [ ] 코드 리뷰: `remember`/`mutableStateOf`는 `IntroScreen`의 한 줄뿐 · `SeasonQuadrant`는 상태 없음 · 파라미터 순서 `season, emphasis, modifier, onClick` · 색 리터럴은 `seasonColor` 한 곳
- [ ] `build.sh` 초록

## 끝나면 붙여넣어 줄 것

1. `Season.kt`, `IntroScreen.kt` 내용
2. 스크린샷: 한 칸 선택 상태 (30%로 키운 확인용이 있으면 함께)
3. 실험 6-1·6-2 결과와 설명
4. `build.sh` 마지막 10줄

중간에 막히면 그 단계 번호(예: "5-3에서 안 찍힘")와 코드·출력을 붙여넣으면 된다.

→ 진단을 통과하면 Claude가 커밋(`feat(client): select an intro season quadrant on click`, `Refs: A4`)과 push를 하고, #8 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A5(`TabijiTheme`: 테마 8종 × 색 역할)로 간다.
