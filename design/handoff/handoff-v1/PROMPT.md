# Tabiji 旅路 — 디자인 핸드오프 v1 · Claude Code 구현 브리프

> **받는 리포의 `CLAUDE.md` 역할 규칙(학습 가이더 모드)이 이 문서보다 우선한다.** 이 문서는 "무엇을 어떤 순서로, 어떤 완료 조건으로" 만들지 정한다. "누가 코드를 치는가"(가이더가 설명하고 사람이 손코딩)는 CLAUDE.md 를 따른다.

## 0. 이 문서를 읽는 법

- 패키지 구조는 `README.md`. 수치의 원본은 `tokens/tokens.json` 과 `source/*.html` 이며, 기준 이미지(`images/screens/`)와 수치가 다르면 **source 가 정답**이다.
- 단위는 dp / sp. HTML 원본의 `px` 는 1:1 로 dp 다.
- 범위: **UI 만**. 비즈니스 로직·네트워크·DB·실제 경로 계산은 범위 밖이며 `docs/11_planner.md`, `12_trips.md`, `13_ai_sheet.md` 의 더미 데이터를 하드코딩한 `FakeTripRepository` 로만 채운다.
- 플랫폼: Kotlin Compose Multiplatform, 1차 **Android + Desktop**. iOS/Web 은 컴파일만 깨지지 않게.
- 각 단계는 **작고, 눈으로 확인 가능**하다. 단계마다 "참고 이미지"와 "완료 조건"이 있다. 완료 조건을 눈으로 확인하기 전에는 다음 단계로 가지 않는다.
- 단계 번호는 `T` (토큰) → `P` (플래너) → `I` (인트로) → `L` (여행 목록) → `A` (AI 시트) → `S` (공통 상태). 화면마다 **배경 → 레이아웃 골격 → 컴포넌트 → 상호작용 → 모션** 순서.

## 1. 토큰 먼저 (T)

### T1. 테마 뼈대
- 만들 것: `TabijiTheme` (`CompositionLocal` 로 `colors / spacing / radius / typography / motion` 제공), `TabijiColors` light/dark 두 인스턴스. 값은 `tokens/tokens.json` 을 그대로 옮긴다 (`docs/01_tokens.md` 의 data class 그대로).
- Compose 힌트: 알파 포함 색은 `Color(0xAARRGGBB)` 순서 주의. `isSystemInDarkTheme()` 로 선택하되 수동 토글 `State` 도 둔다 (디버그 메뉴).
- 완료 조건: 디버그 화면에 `tokens.json` 의 색 토큰 32개가 라이트/다크 스와치로 이름과 함께 나열되고, 토글하면 전부 바뀐다. 참고: `images/screens/tokens.png`.

### T2. 폰트와 타이포
- `assets/fonts/PretendardJP-{Regular,Medium,SemiBold,Bold}.ttf`, `Sora-{Regular,SemiBold,Bold}.ttf` 를 `composeResources/font/` 에 넣고 `FontFamily` 생성. `docs/02_typography.md` 의 `TabijiTypography` 8 스타일.
- 힌트: `PlatformTextStyle(includeFontPadding = false)`. Desktop 에서 폰트 로딩은 `Font(Res.font.…)` 가 공통.
- 완료 조건: 디버그 화면에 8 스타일이 "Day 2 · 시즈오카 센겐 신사 · 1.8km" 샘플로 보이고, 한자 旅路 와 일본어 駿府城 이 Pretendard JP 로 렌더된다(시스템 폰트 폴백 아님 — 글자 모양이 Pretendard 한글과 같은 굵기).

### T3. 도형·표면 레시피
- `chamferShape(radius, chamfer)` (`docs/01_tokens.md` 코드), `Modifier.glassSurface(blur: Dp)` (블러 가능하면 `surface.glass` + 블러, 아니면 `surface.glass.solid`; 둘 다 테두리 1 + 상단 하이라이트 1), `Modifier.silverSurface(pressed: Boolean)`.
- 힌트: 배경 블러는 `expect fun Modifier.backdropBlur(radius: Dp): Modifier` 로 두고 Android 12+ 만 `actual` 구현, 나머지는 no-op 반환 → 호출부가 `surface.glass.solid` 로 분기. Modifier 순서: `shadow → clip → background → border → padding`.
- 완료 조건: 디버그 화면에 (a) chamfer 주요 버튼 240×48, (b) 글래스 패널 200×120 을 지도 플레이스홀더 이미지 위에, (c) 실버 원 48 세 개(기본/눌림/블러 대체)가 `images/screens/components_light.png` 의 5·6번 섹션과 같게 보인다.

### T4. 아이콘
- `assets/icons/ic_*.svg` 22개를 `composeResources/drawable/` 에 넣고 `TabijiIcons` 객체로 노출. `tint = LocalContentColor`.
- 완료 조건: 디버그 화면에 22개가 24dp 와 48dp 로 `images/screens/icons.png` 와 같은 배치로 보인다.

## 2. 플래너 (P) — 메인 화면

### P1. 배경: 지도 자리
- `MapHost` composable: v1 단계에서는 `assets/map/map_light.svg` / `map_dark.svg` 를 `ContentScale.Crop` 으로 깔아 둔다 (MapLibre 연결은 P7).
- 완료 조건: Desktop 1440×900 창과 Android 에뮬레이터(390×844)에서 지도 모형이 꽉 차고 테마 토글 시 다크 지도로 바뀐다.

### P2. 레이아웃 골격 (Desktop)
- `Box(fillMaxSize)` 안에 검색 필드 자리(360×48 @ 24,24), 우측 패널 자리(360, 여백 24), 컨트롤 묶음 자리(right 408, bottom 24). 내용은 빈 글래스 박스.
- 힌트: `WindowSizeClass` 로 Compact/Medium 이면 P3 의 Phone 골격으로 분기.
- 완료 조건: `images/screens/planner_desktop_light.png` 위에 반투명으로 겹쳐 보면 네 박스의 외곽이 일치한다 (치수: `docs/11_planner.md`).

### P3. 레이아웃 골격 (Phone)
- 검색 필드(상단 여백 상태바+8, 좌우 16), 바텀시트 **절반 420** 고정 높이, 컨트롤(right 16, bottom 436).
- 힌트: 바텀시트는 처음엔 직접 만든 `Box` 로. Material `BottomSheetScaffold` 는 모양·단계 제어가 달라 쓰지 않는다.
- 완료 조건: `planner_phone_light.png` 와 외곽 일치.

### P4. 컴포넌트 — 패널 내용
- `DayHeader`, `VisitCard`(기본/선택/고정 시간), `SegmentChip`(정확/추정/재계산/로딩), `PrimaryButton`, `GlassIconButton`, `SearchField`. 치수·상태는 `docs/03_components.md`.
- 더미 데이터 5곳 + 4구간을 `LazyColumn` 에 카드/칩 번갈아.
- 완료 조건: Desktop·Phone 모두 `planner_*_light.png` 와 글자·여백·색이 같다. 특히 (a) 3번 카드가 accent 테두리 2dp + 링, (b) 2→3 칩에 "추정" 배지, (c) 3→4 칩이 점선 테두리, (d) 4→5 칩이 스켈레톤, (e) 4번 카드에 시계 아이콘 + "14:00 고정".

### P5. 컴포넌트 — 지도 위 마커·경로
- 마커 3종(기본/선택/고정)을 `Canvas` 로 그려 `ImageBitmap` 생성 (`docs/03_components.md` 의 path). 경로는 v1 에서 `Canvas` 오버레이로 (P7 에서 MapLibre 레이어로 교체).
- 완료 조건: 5개 마커가 `docs/11_planner.md` 좌표에, 경로 3구간이 도보 점선 / 대중교통 / 자동차 색으로 보인다.

### P6. 상호작용
- 카드 탭 → 선택 상태 + 마커 선택. 마커 탭 → 카드 선택 + 리스트 스크롤. 일차 헤더 접기. Desktop 패널 접기(360↔56, 컨트롤 이동). Phone 시트 3단 드래그 스냅.
- 힌트: 시트는 `AnchoredDraggable` (앵커 96 / 420 / 전체). 패널 접기는 `animateDpAsState(240ms)` 로 폭과 컨트롤 오프셋을 같은 상태에서 파생.
- 완료 조건: 위 다섯 동작이 모두 되고, 접힘 레일(`components_light.png` 8번 섹션)과 접힘 헤더 모양이 맞다.

### P7. 모션 + MapLibre 교체
- `docs/20_motion.md` 의 길이·곡선 적용(선택 240, 눌림 120, 시트 240). MapLibre 를 넣고 `docs/30_map_style.md` 색표로 스타일 JSON 작성, 마커·경로를 MapLibre 레이어로 이동.
- 힌트: Compose Multiplatform 에서 MapLibre 는 Android(`maplibre-android`), Desktop(JCEF/WebView 로 MapLibre GL JS) 가 현실적. `expect/actual MapView` 로 감싸고 공통 API(`setCamera`, `setRoute`, `setMarkers`)만 노출.
- 완료 조건: 실제 타일 위에서 `planner_desktop_light.png` 의 분위기(땅보다 밝은 패널, 저채도 지도, 채도 높은 경로)가 재현되고, 카드 탭 시 600ms 로 카메라가 움직인다.

## 3. 인트로 (I) — v1.1: 계절 이미지 4장 + Compose 드로잉 로고

### I1. 배경: 계절 이미지 4장 (fit-in-view)
- `assets/intro/{spring,summer,autumn,winter}.webp` (번들용 최적화본, 원본은 팀 보관) 를 Desktop 2×2 `Grid` / Phone 세로 4단에 배치. 각 셀은 `Image(contentScale = ContentScale.Crop, alignment = BiasAlignment(0f, focusY), modifier = fillMaxSize().clipToBounds())` 하나 — **비율이 달라도 왜곡·레터박스 없이 셀을 가득 채우는 것이 규칙** (focusY 값과 환산식: `docs/10_intro.md`).
- 완료 조건: Desktop·Phone 모두 `intro_*_light.png` 와 같은 크롭으로 보이고, 가을·겨울 셀에서 인물이 잘리지 않는다. 창 크기를 바꿔도 이미지가 늘어나거나 여백이 생기지 않는다.

### I2. 로고·메뉴 스택 (배경 박스 없음, 직접 드로잉)
- 중앙 `Column`: 워드마크 "Tabiji" Sora Bold 56sp(Phone 44) 흰색 + 그림자 2겹 → 6dp 아래 부제 "여행(旅路) 도우미" 16sp(14) 600 흰색 95% → 28dp 아래 버튼 스택 340dp(300): ① 시작하기 56dp chamfer accent ② 내 여행 ③ 설정 각 52dp 글래스 행(아이콘·제목+부제·쉐브론). 정확한 스펙: `docs/10_intro.md`.
- 완료 조건: 로고·부제에 어떤 배경 표면도 없고, 네 이미지 어느 위에서도 그림자 덕에 글자가 읽힌다. "시작하기" → 플래너, "내 여행" → 목록 이동.

### I3. 파티클 오버레이 (Canvas)
- `docs/10_intro.md` 표대로 사분면당 파티클 (벚꽃 8 · 반짝임 6 · 낙엽 8 · 눈 12, Phone 절반). seed 고정. 이미지 위 오버레이로 그린다.
- 완료 조건: 지정 속도로 루프, 동작 줄이기 켜면 정지.

### I4. 시퀀스 + 호버 + 패럴랙스
- 0–1.2초 시퀀스 (`storyboard.png` F0–F2: 사분면 stagger 150ms 페이드인 → 스택 12dp 상승, 워드마크→부제→버튼 80ms 스태거), 호버 시 사분면 밝기 ±(120ms), 패럴랙스 이미지 ±4dp · 파티클 ±8dp.
- 완료 조건: 타임라인(0/150/300/450/600/1200ms)과 맞고, 다크 테마에서 `#0E1621` 62→50% 오버레이가 얹힌다.

## 4. 여행 목록 (L)

### L1. 배경 + 골격: 하늘 그라데이션, 헤더, 필터 칩, 그리드(adaptive 340) / Phone 세로 리스트 + 하단 고정 버튼.
### L2. 컴포넌트: `TripCard`(썸네일 180/140, D-day 배지 3종), 새 여행 만들기 카드, 빈 상태(`states_desktop_light.png` 4번).
### L3. 상호작용·모션: 카드 탭 → 플래너, 호버 lift 120ms.
- 완료 조건(L 전체): `trips_*_light.png` 와 일치. 더미 3건 + 빈 상태 토글(디버그)이 동작.

## 5. AI 제안 시트 (A)

### A1. 골격: Desktop 모달 760 (2열) / Phone 시트 740 (세그먼트 탭). 스크림.
### A2. 컴포넌트: 헤더 + 절약 배지, `OrderRow`, `ChangeRow`(체크박스, 이유 줄), 푸터 3버튼.
### A3. 상호작용·모션: 체크 해제 시 절약 시간 재계산(−24 → −18), 수락 시 플래너 리스트 `animateItemPlacement` 240ms.
- 완료 조건(A 전체): `ai_sheet_*_light.png` 와 일치, 더미 변경 2건 동작.

## 6. 공통 상태 (S)

### S1. 스켈레톤(카드·칩 틀 유지 + 시머 1.2s) · S2. 오류 배너(콘텐츠 유지, 240ms 삽입, 칩 → 재계산 필요) · S3. 오프라인 필 + 칩 전부 "추정" + AI 버튼 비활성 · S4. 빈 상태 2종.
- 완료 조건: 디버그 메뉴에서 네 상태를 토글하면 `states_*_light.png` 와 같게 보인다.

## 7. 다크 테마

- **v1 범위에 포함**(권장). 이유: 토큰이 처음부터 두 벌이고, 지도 앱은 야간 사용 비중이 커서 나중에 넣으면 모든 화면을 다시 봐야 한다. 비용은 T1 에서 색을 두 벌 넣는 것뿐이며, 각 단계의 완료 조건에 "다크 토글 시 `*_dark.png` 와 같다"를 더한다.
- 단, 지도 다크 스타일(P7)은 라이트 완료 후 별도 단계로.

## 8. 검증 방법

- 각 단계 완료 시 Desktop 1440×900 과 Phone 390×844 스크린샷을 `images/screens/` 의 같은 이름 옆에 `<name>_impl.png` 로 저장해 비교한다.
- 대비: 글래스 패널 위 글자는 `surface.glass.solid`(92%) 와 지도 최악 배경(라이트 `#E6EAEF` 건물, 다크 `#102538` 물) 위에서 4.5:1 이상 — `CONTRAST.md` 표 참고.
- 터치 타깃 48 이상: Android Studio Layout Inspector 로 확인.

## 9. 열린 질문 (디자인 측 → 개발 측)

1. **Pretendard JP 용량** — 4 가중치 TTF 약 22MB. v1 은 그대로 번들하되, 출시 전 서브셋(KR 2,350자 + JP 상용 2,136자 + 라틴) 으로 줄일지, 또는 Medium 을 빼고 3 가중치로 갈지.
2. **배경 블러** — Desktop(Skia) 에서 실제 backdrop blur 를 구현할지(렌더 비용), 아니면 Desktop 은 항상 92% 불투명 대체 표면으로 갈지. 디자인은 둘 다 허용.
3. **MapLibre Desktop 경로** — JCEF(WebView) vs 네이티브 바인딩. 디자인 측 요구는 "스타일 JSON 색표 적용 + 마커·경로 레이어" 뿐.
4. **旅路 아웃라인** — 로고 SVG 의 旅路 가 아직 폰트 참조 텍스트다. 로고 A안 확정 후 아웃라인 SVG 로 교체 예정. 앱바에서는 텍스트로 렌더해도 무방.
5. **계절 애니메이션 확장 방식** — v1 은 파티클 오버레이 + 2단 패럴랙스까지. 다음 단계에서 이미지를 레이어로 분리할지(원경/인물), 시네마그래프식 부분 애니메이션으로 갈지 — 에셋 재제작 범위가 달라지므로 팀 결정 필요.
6. **지도 라벨 폰트** — 타일 글리프(Noto Sans) 대신 Pretendard JP 를 지도 라벨에 쓸 수 있는지(MapLibre `localIdeographFontFamily`).
7. **인트로 이미지 해상도** — 패키지의 webp(2048, 장당 ~400KB)로 시작. 대형 Desktop 화면에서 부족하면 원본 PNG에서 플랫폼별 해상도 2종(1440/2752)을 다시 뽑을지 결정. 교체는 파일명 유지로 끝.
8. **인트로 일러스트 권리** — 네 장에 제3자 캐릭터가 포함되어 있음. 배포 전 권리 처리 또는 교체는 팀 판단 (코드·레이아웃은 교체에 영향 없음).
