# 클라이언트 코드 컨벤션

기본은 [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)와 [Compose API guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md)를 따르고, 아래는 Tabiji에서 추가로 정한 것이다.

## 패키지와 파일

- 루트 패키지 `com.yamada.studio.tabiji`. 구조는 [`architecture.md`](architecture.md)의 `app / feature / core`
- 파일 하나에 공개 Composable 하나가 기본. 파일 이름 = 주 선언 이름 (`TripCard.kt`)
- 플랫폼 구현은 같은 이름 + 소스셋 접미사: `Platform.kt`(commonMain의 expect), `Platform.android.kt`(androidMain), `Platform.jvm.kt`(jvmMain)
- 앱 진입점은 `shared`가 아니라 앱 모듈에: `desktopApp/src/main/kotlin/.../main.kt`, `androidApp/src/main/kotlin/.../MainActivity.kt`

## 네이밍

| 대상 | 규칙 | 예 |
| --- | --- | --- |
| 클래스, 인터페이스, object | `PascalCase` | `TripRepository` |
| Composable (UI를 그리는 것) | `PascalCase` 명사 | `TripCard`, `PlannerScreen` |
| 값을 반환하는 Composable | `camelCase` | `rememberMapState()` |
| 함수, 변수, 프로퍼티 | `camelCase` | `loadTrips()`, `selectedVisit` |
| 상수 (`const val`, 최상위 불변) | `UPPER_SNAKE_CASE` | `MAX_VISITS_PER_DAY` |
| 화면 상태 | `<Screen>UiState` | `PlannerUiState` |
| ViewModel | `<Screen>ViewModel` | `PlannerViewModel` |
| 이벤트 콜백 파라미터 | `on` + 동사 | `onTitleChange`, `onVisitClick` |
| 불리언 | `is/has/can` 접두 | `isLoading`, `hasRoute` |

## Composable

- 파라미터 순서: **필수 데이터 → `modifier: Modifier = Modifier` → 선택 파라미터 → 콜백 → trailing content 람다**
- `modifier`는 **최상위 레이아웃 하나에만** 적용하고, 내부 자식에 재사용하지 않는다
- **상태 없는(stateless) Composable이 기본.** 값과 `onXxx` 콜백을 받는다. 상태는 호출하는 쪽(또는 ViewModel)이 가진다 (state hoisting)
- `remember`는 UI 전용 일시 상태(펼침 여부, 스크롤 위치)에만. 화면 데이터는 ViewModel
- Composable 안에서 네트워크·DB·파일 I/O 금지. 필요하면 ViewModel 이벤트로
- 미리보기용 더미 데이터는 `PreviewData.kt` 같은 별도 파일
- `LazyColumn` 항목에는 안정적인 `key`를 준다 (ID, 인덱스 금지)

## 테마와 리소스

- **색·크기·폰트를 하드코딩하지 않는다.** `TabijiTheme.colors.xxx`, `TabijiTheme.spacing.md`처럼 토큰에서 가져온다 (A5에서 만든다)
  - 예외: `0.dp`, `1.dp` 구분선 같은 의미 없는 값, 미리보기 코드
- 사용자에게 보이는 문자열은 `composeResources/values/strings.xml`
  - 기본 언어 **한국어, 해요체** ("여행을 추가해요", "경로를 다시 계산할게요")
  - 영어(`values-en/`), 일본어(`values-ja/`)는 나중에 채운다
  - 문자열 키: `<화면>_<요소>_<의미>` (`planner_title_placeholder`)
- 폰트·이미지는 재배포 가능한 라이선스만. 라이선스 파일을 같이 커밋한다

## 오류와 비동기

- 예외를 UI까지 던지지 않는다. 저장소는 `Result<T>` 또는 sealed 결과를 돌려주고, ViewModel이 UI 상태로 바꾼다
- `GlobalScope` 금지. ViewModel은 `viewModelScope`, Composable은 `rememberCoroutineScope`/`LaunchedEffect`
- 네트워크·DB는 `Dispatchers.IO`(Desktop·Android)로. UI 스레드를 막지 않는다
- `!!` 금지. null 가능성은 타입으로 드러내고 처리한다

## 로그

- 공통 로거 하나를 쓴다 (B 단계에서 도입). 태그는 `Tabiji/<layer>` — `Tabiji/ui`, `Tabiji/data`, `Tabiji/network`, `Tabiji/map`
- `println` 남발 금지. 학습 중 임시 출력은 커밋 전에 지운다
- 토큰·좌표 원본 같은 개인정보·비밀값은 로그에 남기지 않는다

## 테스트

- 도메인 규칙과 ViewModel은 `commonTest`에서 단위 테스트
- 테스트 이름은 백틱 문장: `` fun `방문지를 지우면 다음 구간이 재계산 필요가 된다`() ``
- CI에서 도는 테스트는 네트워크에 의존하지 않는다 (MockEngine, 가짜 저장소)

## Git

- 커밋은 Conventional Commits, 영어, scope `client`, footer `Refs: <단계>`

```
feat(client): add trip title input with live preview

Hoist title state to PlannerScreen and pass it down to
TitleField and TitlePreview.

Refs: A3
```

- 상세는 루트 [`docs/github.md`](../../docs/github.md)
