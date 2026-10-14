# 01 · 토큰 — 색 · 간격 · 모서리 · 그림자 · Kotlin 이름 매핑

기계가 읽는 원본은 `tokens/tokens.json` 이다. 이 문서는 사람이 읽는 표와 변경 기록이다. 두 문서가 다르면 **tokens.json이 정답**이다.

## Kotlin 매핑 규칙

- `TabijiTheme.colors.<camelCase>` — 점(`.`)을 지우고 다음 단어 첫 글자를 대문자로. `accent.sky → accentSky`, `surface.glass.solid → surfaceGlassSolid`.
- `TabijiTheme.spacing.<key>`, `TabijiTheme.radius.<key>`, `TabijiTheme.typography.<key>`, `TabijiTheme.motion.<key>` 도 같은 규칙.
- `#RRGGBBAA` 표기는 **뒤 두 자리가 알파**다. Compose `Color(0xAARRGGBB)` 로 옮길 때 순서를 바꾼다. 예: `#FFFFFFB8` → `Color(0xB8FFFFFF)`.

```kotlin
@Immutable
data class TabijiColors(
    val bgSkyTop: Color, val bgSkyBottom: Color,
    val surfaceGlass: Color, val surfaceGlassSolid: Color, val surfaceCard: Color, val surfaceChip: Color,
    val surfaceSilverFrom: Color, val surfaceSilverTo: Color,
    val surfaceSilverPressedFrom: Color, val surfaceSilverPressedTo: Color,
    val surfaceSkeleton: Color, val surfaceSkeletonHi: Color, val surfaceScrim: Color,
    val lineHighlight: Color, val lineSubtle: Color,
    val textPrimary: Color, val textSecondary: Color,
    val accentSky: Color, val accentPressed: Color, val accentText: Color, val accentLine: Color, val onAccent: Color, val accentSoft: Color,
    val routeWalk: Color, val routeTransit: Color, val routeDrive: Color,
    val statusEstimated: Color, val statusError: Color, val statusErrorText: Color, val statusErrorSoft: Color,
    val markerStroke: Color, val markerFace: Color,
)
```

## 색

| 토큰 | 라이트 | 다크 | Kotlin | 용도 |
| --- | --- | --- | --- | --- |
| bg.sky.top | #EAF3FB | #0E1621 | bgSkyTop | 배경 그라데이션 위 |
| bg.sky.bottom | #F7F9FC | #18212D | bgSkyBottom | 배경 그라데이션 아래 |
| surface.glass | #FFFFFF @72% | #1E2733 @72% | surfaceGlass | 지도 위 패널·시트·검색 (블러 16–24) |
| surface.glass.solid | #FFFFFF @92% | #1E2733 @92% | surfaceGlassSolid | 블러 불가 시 대체 표면 |
| surface.card | #FFFFFF @82% | #1E2733 @82% | surfaceCard | 패널 안 카드 |
| surface.chip | #FFFFFF @90% | #242E3C @90% | surfaceChip | 구간 칩 |
| surface.silver.from | #F4F6F9 | #2A3441 | surfaceSilverFrom | 실버 그라데이션 시작 |
| surface.silver.to | #C9D1DB | #3A4655 | surfaceSilverTo | 실버 그라데이션 끝 |
| surface.silver.pressed.from / to | #DDE3EA / #B9C3CF | #222B36 / #2F3947 | surfaceSilverPressed* | 실버 버튼 눌림 |
| surface.skeleton / .hi | #E6EBF1 / #F3F6F9 | #2A3441 / #354253 | surfaceSkeleton* | 스켈레톤 + 시머 |
| surface.scrim | #1C2633 @32% | #000000 @50% | surfaceScrim | 모달 뒤 |
| line.highlight | #FFFFFF @80% | #FFFFFF @14% | lineHighlight | 패널·버튼 상단 1dp |
| line.subtle | #D5DCE4 | #334051 | lineSubtle | 구분선·테두리 |
| text.primary | #1C2633 | #E9EEF4 | textPrimary | 본문 |
| text.secondary | #5B6878 | #9AA7B6 | textSecondary | 보조 |
| accent.sky | #2FA8E0 | #5CC3F0 | accentSky | 주요 버튼 채움, 선택, 도보 경로 |
| accent.pressed | #1F8FC4 | #45B2E3 | accentPressed | 주요 버튼 눌림 |
| **accent.text** (신설) | #1A78A8 | #5CC3F0 | accentText | 밝은 표면 위 accent 글자·아이콘 |
| **accent.line** (신설) | #1A8FC6 | #5CC3F0 | accentLine | 선·테두리용 accent: 선택 카드 테두리, 포커스 테두리, 도보 경로선, 선택 필터 칩 테두리 |
| **accent.on** (신설) | #0E1621 | #0E1621 | onAccent | accent 채움 위 글자·아이콘 |
| accent.soft | #2FA8E0 @14% | #5CC3F0 @18% | accentSoft | 선택 링, 선택 필터 칩 배경 |
| route.walk | = accent.line | = accent.line | routeWalk | 도보 경로 점선 |
| route.transit | #6C7BE0 | **#8E9AF0 (제안)** | routeTransit | 대중교통 경로 |
| route.drive | **#3A9A74 (조정)** | **#6FC9A6 (제안)** | routeDrive | 자동차 경로 |
| status.estimated | #E3A23B | #F0B862 | statusEstimated | "추정" 배지 채움 전용 |
| status.error | #D9534F | #F07A76 | statusError | 오류 테두리·아이콘 |
| **status.error.text** (신설) | #B23A36 | #F6A09D | statusErrorText | 오류 메시지 글자 |
| status.error.soft | #D9534F @12% | #F07A76 @16% | statusErrorSoft | 오류 배너 배경 |
| marker.stroke / face | #AEB8C4 / #FFFFFF | 동일 | markerStroke / markerFace | 지도 마커 (지도 위라 테마 무관) |

## 변경 기록 (시작 토큰 대비)

1. **accent.on 신설** — 시작 토큰대로 `accent.sky` 위에 흰 글자를 올리면 대비 **2.7:1** (라이트) / 2.0:1 (다크)로 4.5:1·3:1 모두 미달. accent를 어둡게 하면 "스카이" 느낌이 사라지므로(4.5:1을 넘기려면 #1578A8 수준) 색은 유지하고 **글자를 어두운 남색 #0E1621** 로 뒤집었다 → 라이트 **6.8:1**, 다크 **9.1:1**.
2. **accent.text 신설** — 흰 표면 위에 `#2FA8E0` 글자는 2.7:1. 링크·선택 라벨·아이콘용으로 **#1A78A8 (4.9:1)** 을 둔다. 다크에서는 `#5CC3F0` 이 `#1E2733` 위 **7.6:1** 이라 그대로.
3. **accent.line 신설** — 선택 카드 테두리(2dp)와 도보 경로선(4dp)에 `#2FA8E0` 을 쓰면 흰 카드 위 2.7:1, 지도 땅(#F1F4F7) 위 2.4:1 로 그래픽 3:1 미달. 선·테두리 전용으로 한 단계 짙은 **#1A8FC6 (카드 3.6:1 · 땅 3.3:1)** 을 둔다. 채움(버튼·번호 원·마커)은 그대로 `accent.sky`.
4. **route.drive 라이트 조정** — `#4FB08A` 는 땅 위 2.4:1. **#3A9A74 (3.2:1)** 로 조정. 칩 아이콘에도 같은 값(흰 칩 위 3.3:1).
5. **status.estimated는 채움 전용** — 흰 배경 위 글자로 쓰면 2.2:1. 배지(채움 + `#1C2633` 글자, 6.9:1)로만 쓴다.
6. **status.error.text 신설** — `#D9534F` 를 12sp 오류 메시지에 쓰면 3.4:1. 메시지 글자는 **#B23A36 (5.9:1)**, 다크 **#F6A09D (5.9:1)**. `status.error` 는 테두리·아이콘(그래픽 3:1 통과)에만.
7. **route.transit / route.drive 다크값 제안** — 다크 지도(`#141C26`)에서 가라앉지 않도록 **명도를 올린 #8E9AF0 (6.6:1) / #6FC9A6 (8.7:1)** 을 제안한다.
8. **surface.card / surface.chip / surface.scrim / surface.skeleton 추가** — 패널 안 2단계 표면이 필요해 글래스보다 불투명한 카드(82%)·칩(90%)을 뒀다.
9. **marker.stroke / marker.face 추가** — 마커는 지도 타일 위에 놓이므로 테마와 무관하게 고정. 선택 마커의 `accent.sky` 채움은 흰 2dp 테두리·글로우·크기 변화(32×40→40×50)로 구분하므로 색 대비에 의존하지 않는다.

대비 수치는 `tools/contrast_check.py` 로 계산했다 (패키지 루트의 `CONTRAST.md` 에 전체 표 — 거부한 시작 조합을 제외하면 FAIL 0).

## 간격 (dp, 4 그리드)

| 키 | 값 | 쓰는 곳 |
| --- | --- | --- |
| xs | 4 | 아이콘-텍스트 사이, 배지 안쪽 |
| sm | 8 | 칩·버튼 사이, 세로 리스트 카드-칩 간격 6 (예외) |
| md | 12 | 카드 안쪽 패딩, 패널 안 리스트 패딩 |
| lg | 16 | 패널 안쪽 패딩, Phone 화면 좌우 여백 |
| xl | 24 | Desktop 화면 여백, 섹션 사이 |
| xxl | 32 | 로고 패널 안쪽 |
| touchTarget | 48 | 모든 터치 가능 요소 최소 |

## 모서리 (dp)

| 키 | 값 |
| --- | --- |
| card | 12 |
| panel | 16 |
| sheet (바텀시트 상단) | 20 |
| chip | 완전 라운드 (999) |
| button | 12 + 우상단 chamfer 8 |
| logoFrame | 14 + 우상단 chamfer 12 (Desktop 로고 패널 16 + 12) |
| input | 12 |

### chamfer 모양을 Compose로

`clip-path: path(...)` 를 그대로 `GenericShape` 로 옮긴다. 모든 수치는 dp.

```kotlin
fun chamferShape(radius: Dp, chamfer: Dp) = GenericShape { size, density ->
    val r = with(density) { radius.toPx() }
    val c = with(density) { chamfer.toPx() }
    moveTo(r, 0f)
    lineTo(size.width - c, 0f)
    lineTo(size.width, c)                       // 우상단 사선
    lineTo(size.width, size.height - r)
    arcTo(Rect(size.width - 2 * r, size.height - 2 * r, size.width, size.height), 0f, 90f, false)
    lineTo(r, size.height)
    arcTo(Rect(0f, size.height - 2 * r, 2 * r, size.height), 90f, 90f, false)
    lineTo(0f, r)
    arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
    close()
}
// 주요 버튼: chamferShape(12.dp, 8.dp) · 로고 프레임: chamferShape(14.dp, 12.dp)
```

## 그림자

| 키 | 라이트 | 다크 | 쓰는 곳 |
| --- | --- | --- | --- |
| panel | y 8 · blur 24 · #1C2633 @10% | y 8 · blur 24 · #000 @35% | 글래스 패널·버튼 |
| lift | y 14 · blur 32 · #1C2633 @16% | y 14 · blur 32 · #000 @45% | 드래그 중 카드, 로고 패널 |
| markerSelected | y 6 · blur 10 · accent @45% | 동일 | 선택 마커 글로우 |

Compose: `Modifier.shadow(elevation = 8.dp, shape, ambientColor, spotColor)` 로 근사. blur 24 ≈ elevation 8dp. 정확한 색을 원하면 `drawBehind { drawRoundRect(brush = Brush.radialGradient(...)) }`.

## 블러

| 키 | dp |
| --- | --- |
| panel | 16 |
| sheet | 20 |
| modal | 24 |

Compose Multiplatform에서 **배경 블러(backdrop)는 플랫폼별 지원이 다르다.** Android 12+ 는 `RenderEffect`, Desktop(Skia)은 `Modifier.blur` 가 자기 콘텐츠만 흐리므로 배경 블러가 아니다. 기본 구현은 `surface.glass.solid`(92%)로 가고, 플랫폼별 `expect/actual` 로 블러 가능한 곳만 `surface.glass`(72%) + 블러를 켠다. **블러 유무로 레이아웃·텍스트 대비가 달라지지 않도록** 92%에서 대비를 검증했다.
