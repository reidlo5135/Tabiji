# A4 — 상태 기초: 누른 사분면만 밝게

> 브랜치: `client/feat-a4-intro-selection` (Claude가 만들어 둠, A3 PR #8 위에 쌓음) · 커밋 footer `Refs: A4`
> 시각 기준: handoff-v2 `docs/10_intro.md` "파티클·패럴랙스·시퀀스" 절의 마지막 줄: *"호버/터치한 사분면 밝기 +8%, 나머지 −6%"*
> 확인은 **Desktop**에서 한다

## 목표

A3의 사분면을 **눌러서 고를 수 있게** 만든다.
- 한 칸을 클릭하면 그 칸은 살짝 밝아지고(+8%) 나머지 세 칸은 살짝 어두워진다(−6%)
- 같은 칸을 다시 누르면 선택이 풀린다
- 다른 칸을 누르면 선택이 그쪽으로 옮겨 간다

이번 단계의 핵심은 화면이 아니라 **상태(state)가 어디에 사는가**다.
- "어느 칸이 선택됐나"는 `IntroScreen` 한 곳에만 둔다
- 칸(`SeasonQuadrant`)은 상태 없이, 받은 값대로 그리고 클릭을 위로 알리기만 한다

> 범위: **클릭만** 다룬다. 핸드오프의 "호버 시 밝기"와 120ms 애니메이션은 E2(입장 시퀀스·포인터 입력)에서 한다. 그래서 로드맵 A4 행에서 호버를 뺐다.

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [State와 Compose](https://developer.android.com/develop/ui/compose/state) | `remember`, `mutableStateOf`, state hoisting. 오늘의 전부 |
| [Compose 생명주기(recomposition)](https://developer.android.com/develop/ui/compose/lifecycle) | "상태가 바뀌면 함수가 다시 불린다"의 정확한 의미 |
| [Kotlin enum class](https://kotlinlang.org/docs/enum-classes.html) · [null 안전성](https://kotlinlang.org/docs/null-safety.html) · [`when`](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements) · [위임 프로퍼티 `by`](https://kotlinlang.org/docs/delegated-properties.html) | 아래 문법 표의 원문 |

## React로 먼저 보기

이번 단계는 React에서 하던 것과 거의 같다. 대응표부터 본다.

| Compose | React |
| --- | --- |
| `@Composable` 함수가 불림 (composition) | 함수 컴포넌트 렌더 |
| recomposition | 리렌더 |
| `var selected by remember { mutableStateOf<Season?>(null) }` | `const [selected, setSelected] = useState<Season \| null>(null)` |
| `selected = Season.Spring` | `setSelected("spring")` |
| state hoisting | lifting state up |
| `onClick: () -> Unit` 파라미터 | `onClick` prop 콜백 |
| 값은 아래로, 이벤트는 위로 | 단방향 데이터 흐름 (props down, callbacks up) |

차이도 두 가지 있다.
- **대입이 곧 setState다.** `mutableStateOf`가 만든 값은 관찰되는 상자(`MutableState`)다. 거기에 값을 넣으면, 그 값을 **읽은** Composable만 다시 불린다. setter 함수가 따로 없다.
- **더 잘게 다시 그린다.** React는 상태를 가진 컴포넌트를 통째로 다시 실행한다. Compose는 바뀐 값을 실제로 읽은 함수만 다시 부르고, 파라미터가 같은 자식은 건너뛴다(skip).

## 처음 보는 Kotlin 문법 (이번 단계에서 만나는 것만)

| 문법 | 한 줄 설명 | TypeScript로 빗대면 |
| --- | --- | --- |
| `enum class Season { Spring, Summer, Autumn, Winter }` | 정해진 값 몇 개만 갖는 타입 | `enum Season { … }` 또는 `'spring' \| 'summer' \| …` |
| `Season?` | null일 수 있는 `Season`. `Season`과 `Season?`는 **다른 타입**이다 | `Season \| null` (strictNullChecks 켠 상태) |
| `when (x) { A -> …; B -> … }` | 값을 돌려주는 switch. enum의 모든 경우를 다루지 않으면 **컴파일 오류**가 난다 | `switch`에 exhaustive 검사가 붙은 것 |
| `var x by remember { … }` | `by`는 위임이다. `x`를 읽고 쓰면 상자의 `.value`를 읽고 쓴다 | `useState`의 `[value, setValue]`를 변수 하나로 합친 것 |
| `Color.White.copy(alpha = 0.08f)` | 일부 값만 바꾼 복사본 | `{ ...white, alpha: 0.08 }` |
| `() -> Unit` | 인자 없고 반환값 없는 함수 타입 | `() => void` |
| trailing lambda | 마지막 인자가 함수면 괄호 밖으로 뺀다: `SeasonQuadrant(…) { onSelect(…) }` | `onClick={() => …}`를 태그 밖에 적는 셈 |

## 설계 선택: 칸에 무엇을 넘길까

칸은 "선택됨 / 다른 칸이 선택돼서 어두움 / 아무것도 선택 안 됨" 셋 중 하나로 그려진다. 넘기는 방법은 두 가지다.

| | 방법 | 장단점 |
| --- | --- | --- |
| A | `selected: Boolean, dimmed: Boolean` | 쉽다. 하지만 `selected = true, dimmed = true` 같은 **말이 안 되는 조합**도 타입상 가능하다 |
| B (추천) | `enum class Emphasis { Normal, Selected, Dimmed }` 하나 | 세 경우만 표현 가능하고 `when`이 빠짐없이 검사해 준다. "불가능한 상태를 타입으로 막기" |

TypeScript에서 boolean 두 개 대신 `'normal' | 'selected' | 'dimmed'` 유니언을 쓰는 것과 같은 이유다. B로 하되, A로 하고 싶으면 그렇게 해도 된다. 리뷰에서 같이 본다.

## 만들 파일 (직접 작성)

| 경로 | 내용 |
| --- | --- |
| `shared/src/commonMain/kotlin/com/yamada/studio/tabiji/feature/intro/Season.kt` (새 파일) | `enum class Season`. A6에서 이미지·초점 정보가 붙는다 |
| `…/feature/intro/IntroScreen.kt` (수정) | 선택 상태, 칸에 넘길 값 계산, 클릭 처리, `SeasonQuadrant` 시그니처 변경, `Emphasis`(B안이면) |

## 조립 순서

### 1. `Season` enum

봄·여름·가을·겨울 네 값. 색 리터럴 4개는 `IntroScreen.kt` 안의 `private fun seasonColor(season: Season): Color = when (season) { … }` 하나로 모은다. 호출부에서 색 대신 `Season.Spring` 같은 값을 넘기게 된다. `// TODO(A6)`는 이 함수에 한 번만 단다.

### 2. 상태는 `IntroScreen`에 하나만

```kotlin
var selected by remember { mutableStateOf<Season?>(null) }   // null = 아무것도 선택 안 됨
```

- 필요한 import: `androidx.compose.runtime.remember`, `mutableStateOf`, **`getValue`, `setValue`**. 마지막 둘은 `by`가 쓰는 것이다. 빠뜨리면 아래 "흔한 오류"의 첫 줄이 뜬다
- 이 한 줄 외에 **어디에도 `remember`/`mutableStateOf`가 없어야 한다.** 칸 안에는 없다

### 3. 칸에 넘길 값과 클릭 처리 — 부모가 계산한다

부모가 할 일은 두 가지다.

- **값 내려보내기**: 각 칸의 `Emphasis` = (아무것도 선택 안 됨 → `Normal`) / (이 칸이 선택됨 → `Selected`) / (그 밖 → `Dimmed`). `when { 조건 -> 값 … }`처럼 인자 없는 `when`으로 쓸 수 있다
- **이벤트 받기**: 칸이 클릭됐다고 알리면 `selected`를 바꾼다. 같은 칸이면 `null`(해제), 다른 칸이면 그 칸으로 옮긴다

React로 치면 `selected`가 부모의 `useState`이고, `emphasis`가 자식 prop, `onClick`이 `() => setSelected(...)`다.

### 4. `SeasonQuadrant` — 여전히 상태 없음

시그니처는 컨벤션 순서(필수 데이터 → `modifier` → 콜백)를 따른다.

```kotlin
@Composable
private fun SeasonQuadrant(
    season: Season,
    emphasis: Emphasis,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
```

마지막 파라미터가 함수라서, 부르는 쪽에서 trailing lambda로 쓸 수 있다: `SeasonQuadrant(Season.Spring, …, Modifier.weight(1f)) { … }`

칸 안에서 할 일:

- **덮개 색 정하기**: `Selected` → 흰색 8%, `Dimmed` → 검정 6%, `Normal` → `Color.Transparent`. 핸드오프의 "밝기 +8% / −6%"를 반투명 덮개로 근사한 것이다
- **두 번 칠하기**: `.background(계절색).background(덮개색)`. A3에서 본 규칙 그대로, 체인의 뒤쪽이 안쪽이자 위층이다. 두 번째 `background`가 첫 번째 위에 덮여 그려진다
- **클릭 받기**: `.clickable(interactionSource = null, indication = null) { onClick() }`
  - import: `androidx.compose.foundation.clickable`
  - `indication = null`은 기본 눌림 효과(회색 덮개)를 끈다. 우리 덮개만 보이게 하려는 것이다. v2 원칙상 인트로 사분면의 반응은 밝기 변화뿐이다

> 8%·6%는 미세하다. 처음엔 잘 안 보일 수 있으니 **잠깐 30% 정도로 키워 동작을 확인**하고, 확인 뒤 핸드오프 값으로 되돌린다. Hot Reload로 하면 금방이다.

### 5. 실행

```bash
scripts/linux/run-desktop.sh --hot
```

클릭해 보는 순서: 봄 → 봄(해제) → 여름 → 겨울 → 창 크기 바꾸기. 마지막에 선택이 유지되는지 본다.

### 6. 실험 — `remember`를 빼면?

`remember { … }`를 지우고 `var selected by mutableStateOf<Season?>(null)`만 남긴 채 저장한다. 그리고 클릭해 본다.

- 저장하기 **전에** 결과를 예측해 본다
- 결과를 보고 왜 그런지 한두 줄로 설명한다. 힌트: 클릭 → 상태 변경 → recomposition → `IntroScreen`이 **다시 불린다**. 그때 이 줄은 어떻게 실행될까? React에서 `useState` 대신 함수 안에 `let selected = null`을 쓰면 어떻게 되던가?
- 확인 뒤 `remember`를 되돌린다

### 7. 빌드 확인

```bash
scripts/linux/build.sh
```

## 흔한 오류와 원인 읽기

| 증상 | 원인일 가능성 |
| --- | --- |
| `Property delegate must have a 'getValue(Nothing?, KProperty<*>)' method` | `androidx.compose.runtime.getValue`/`setValue` import 누락 |
| `'when' expression must be exhaustive` | `when (season)`에서 계절 하나를 빠뜨림. 컴파일러가 잡아 주는 게 enum의 장점이다 |
| `Assignment type mismatch: actual type is 'Season', but 'Nothing?' was expected` (문구는 Kotlin 버전마다 조금 다름) | `mutableStateOf(null)`처럼 타입 없이 써서 상자 타입이 `Nothing?`(항상 null)로 추론됨. `mutableStateOf<Season?>(null)`로 타입을 적는다 |
| 클릭해도 아무 변화 없음 | `remember` 없음(실험 6), `onClick`을 부르지 않음, 또는 덮개 색이 너무 옅음 |
| 클릭하면 회색 덮개가 잠깐 뜸 | `clickable`의 기본 indication. `indication = null` 버전을 쓴다 |
| 여러 칸이 동시에 선택됨 | 칸마다 각자 상태를 가짐. 상태가 부모에 하나만 있어야 한다 |
| `Unresolved reference: clickable` | `androidx.compose.foundation.clickable` import 누락 |

## 완료 조건 (모두 눈으로)

- [ ] 한 칸을 클릭하면 그 칸만 밝고 나머지 셋은 어두워짐 (핸드오프 값 8%/6%로)
- [ ] 같은 칸 다시 클릭 → 해제, 다른 칸 클릭 → 선택 이동
- [ ] 선택한 채 창 크기를 바꿔도 선택 유지
- [ ] `remember` 실험 결과와 설명
- [ ] 코드 리뷰: 상태는 `IntroScreen`의 한 줄뿐, `SeasonQuadrant`에는 `remember` 없음, 파라미터 순서가 컨벤션대로, 계절색은 `when` 하나에
- [ ] `build.sh` 초록

## 끝나면 붙여넣어 줄 것

1. `Season.kt`, `IntroScreen.kt` 내용
2. 스크린샷: 한 칸 선택 상태 (30%로 키운 확인용 한 장이 있으면 함께)
3. `remember` 실험 결과와 설명
4. `build.sh` 마지막 10줄

→ 진단을 통과하면 Claude가 커밋(`feat(client): select an intro season quadrant on click`, `Refs: A4`)과 push를 하고, #8 머지 뒤 `main`과 맞춰 PR을 연다. 그다음 A5(`TabijiTheme`: 테마 8종 × 색 역할)로 간다.
