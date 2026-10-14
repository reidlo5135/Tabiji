# 01 · 토큰 v2 — 테마 × 역할 2차원 모델

원본: `tokens/tokens.json`. v1의 1차원 팔레트를 버리고 **역할(role) × 테마(theme)** 구조로 바꿨다. 코드는 역할만 참조하고, 테마 전환은 역할→색 매핑 테이블 교체다.

## 역할 (roles)

| 역할 | 쓰임 |
| --- | --- |
| `paper` / `stripe` | 화면 배경 종이색 / 45° 스트라이프 색 |
| `ink` | 테두리 · 본문 글자 · 하드 섀도의 잉크 |
| `card` / `soft` | 패널·카드 면 / 패널 안 리스트 바탕 |
| `accent` / `onAccent` | 주요 버튼·선택 테두리·도보 경로·선택 마커 / 그 위 글자(전 테마 #FFFFFF) |
| `sub` | 보조 글자 |
| `h1` `h2` `h3` (`h4`) | 헤더 스트립 로테이션 (korea만 h4). 스트립 글자는 전 테마 #FFFFFF |
| `shadow` | ink 22% — 패널 하드 섀도 |
| `skel` / `est` / `err` `errSoft` / `ok` | 스켈레톤 / '추정' 배지(글자 #2A1D08 고정) / 오류 / AI 이유 체크 |

경로 색은 역할을 재사용한다: **도보 = accent(점선 2 on 11 off), 대중교통 = h2, 자동차 = h3**, 미계산 45% 불투명.

## Compose 매핑

```kotlin
@Immutable
data class TabijiColors(
    val paper: Color, val stripe: Color, val ink: Color,
    val card: Color, val soft: Color,
    val accent: Color, val onAccent: Color, val sub: Color,
    val headers: List<Color>,          // [h1, h2, h3] (+h4 for korea)
    val shadow: Color, val skel: Color,
    val est: Color, val err: Color, val errSoft: Color, val ok: Color,
)

enum class TabijiTheme { Cloud, Spring, Summer, Autumn, Winter, Korea, China, Japan }

val LocalTabijiColors = staticCompositionLocalOf { cloudColors }

@Composable
fun TabijiApp(theme: TabijiTheme, content: @Composable () -> Unit) {
    // 테마 전환 = 색만 240ms 크로스페이드 (animateColorAsState 를 역할별로 감거나,
    // 스냅샷 2장 크로스페이드. 레이아웃 애니메이션 없음 — 20_motion.md)
    CompositionLocalProvider(LocalTabijiColors provides theme.colors(), content = content)
}
```

- 헤더 스트립 색은 `colors.headers[index % headers.size]` 로테이션 — 같은 리스트에서 인접 카드가 같은 색이 되지 않게 index는 카드 순서.
- tokens.json의 `themes.<name>` 객체를 그대로 codegen 하거나 수동 상수화한다. **8개 테마 모두 역할 키가 동일**하므로 (korea의 h4만 추가) 테이블 교체로 충분.

## 보드 → tokens.json 색 조정 기록 (중요)

캔버스 보드/소스 html의 CSS 값은 시안 단계 색이고, 대비 검증(흰 글자 3:1) 후 **tokens.json에서 다음 색만 어둡게 확정**했다. 구현은 tokens.json을 쓴다.

| 테마 | 역할 | 보드(시안) | tokens.json(확정) |
| --- | --- | --- | --- |
| spring | h2 | #7FA255 | **#6F9148** |
| autumn | accent·h1 | #C97A2E | **#C06F22** |
| autumn | h2 | #A8891F | **#8F741A** |
| winter | h2 | #7585C2 | **#6676B8** |
| korea | h4 | (보드 미표기) | **#A8801F** |
| china | h2 | #C2922A | **#A8791C** |
| japan | h3 | #6E7F49 | **#5F7038** |

검증: `python3 tools/contrast_check.py tokens/tokens.json` → `CONTRAST.md`, 현재 **FAIL 0** (8테마 × 16~17조합).

## 비색상 토큰 (전 테마 공통, 단위 dp/sp/ms)

- **border**: panel 2 · chip 1.5 · selected 3 · divider 2 · badgeInHeader 2
- **shadowSpec**: panel (3,3,blur0,shadow) · button (0,3,blur0,ink) · modal (5,5,blur0,#00000059)
- **stripeSpec**: 45°, Desktop 폭/간격 16, Phone 14
- **radius**: panel 10 · row 9 · chip 999 · numBadge 6 · button 10 · buttonChamfer 8 · sheetTop 18 · input 10
- **spacing**: 4 그리드 (xs4 sm8 md12 lg16 xl24) · 터치 타깃 48 · 화면 여백 Phone 14 / Desktop 16 · 일정 패널 폭 376 · 헤더 스트립 높이 Desktop 패널 52 / 카드 32 / Phone 44
- **opacity**: disabled .5 · 취소선 행 .55 · 미계산 경로 .45
- **typography / motion**: 02_typography.md / 20_motion.md
