# Tabiji 旅路 — 디자인 핸드오프 v2 · Claude Code 구현 브리프

> **받는 리포의 `CLAUDE.md` 역할 규칙(학습 가이더 모드)이 이 문서보다 우선한다.** 이 문서는 "무엇을 어떤 순서로, 어떤 완료 조건으로" 만들지 정한다. "누가 코드를 치는가"(가이더가 설명하고 사람이 손코딩)는 CLAUDE.md 를 따른다.

## 0. 이 문서를 읽는 법

- v1 브리프를 이미 진행했더라도 **이 문서가 교체한다** — 무드가 글래스 → 도감으로 바뀌었다 (README의 변경 요약). v1 코드가 있으면 T 단계에서 토큰·테마 계층부터 갈아끼운다. `glassSurface`/`silverSurface`/`backdropBlur`는 **삭제 대상**이다.
- 수치 원본: `tokens/tokens.json` 과 `source/*.html` (1px = 1dp). 캡처(`images/screens/`)와 다르면 source, source와 tokens.json 색이 다르면 **tokens.json이 정답** (`docs/01_tokens.md`의 조정 7건).
- 범위: **UI 만**. 로직·네트워크·DB·경로 계산은 범위 밖 — 더미 데이터를 하드코딩한 `FakeTripRepository`.
- 플랫폼: Kotlin Compose Multiplatform, 1차 Android + Desktop. iOS/Web은 컴파일만 유지.
- 각 단계는 작고 눈으로 확인 가능. **완료 조건을 확인하기 전에 다음 단계 금지.** 순서: `T`(토큰·테마) → `P`(플래너) → `I`(인트로) → `L`(여행 목록) → `A`(AI 시트) → `S`(공통 상태). 화면마다 배경 → 골격 → 컴포넌트 → 상호작용 → 모션.
- `source/*.html`은 브라우저로 열어 우하단 셀렉터로 8테마를 직접 넘겨볼 수 있다 (셀렉터 자체는 구현 대상 아님 — 앱에서는 설정 화면).

## 1. 토큰·테마 먼저 (T)

### T1. 테마×역할 컬러 시스템
- 만들 것: `TabijiColors` data class (역할 15개 + `headers` 리스트), `enum TabijiTheme` 8종, `LocalTabijiColors` — 코드는 `docs/01_tokens.md` 그대로. 값은 `tokens/tokens.json`의 `themes.*`를 그대로 옮긴다 (보드 CSS 값 아님!).
- 사용자 테마 선택 `State`는 설정 화면 전까지 디버그 토글로.
- 완료 조건: 디버그 화면에 **8테마 × 역할 스와치 그리드**가 이름과 hex로 나열되고, 테마를 바꾸면 전부 240ms 크로스페이드로 바뀐다. korea만 h4가 한 칸 더 있다. 참고: `images/screens/theme_tokens.png`.

### T2. 폰트와 타이포
- `assets/fonts/`의 Jua(1), PretendardJP(4), Sora(3)를 `composeResources/font/`에. `docs/02_typography.md`의 9 스타일 (`displayXL/L/M/S`, `body`, `caption`, `badge`, `numeric`, `wordmark`).
- **Jua 폴백 필수**: 디스플레이 스타일은 `FontFamily(jua, pretendardJp)` 체인 — Jua에 한자·가나가 없다.
- 힌트: `PlatformTextStyle(includeFontPadding = false)`.
- 완료 조건: 디버그 화면에서 "Day 2 · 시즈오카 센겐 신사 · 駿府城 · 旅路 1.8km" 샘플이 — Day 2는 Jua, 駿府城·旅路는 Pretendard JP 폴백(두부 □ 없음), 1.8km의 숫자는 Sora로 렌더된다.

### T3. 도감 표면 레시피
- 만들 것: `Modifier.inkPanel(pressed)` (card 채움 + 2dp ink 테두리 + radius 10 + **(3,3) blur0 섀도를 drawBehind로**), `Modifier.pressSink()` (press 시 translateY 2dp + 하단 섀도 3→1, 80ms), `chamferShape(radius=12, chamfer=8)` (v1과 동일 패스), `Modifier.paperStripes()` (paper + 45° repeating 스트라이프 — `drawBehind`에서 회전한 라인 반복).
- elevation·RenderEffect 블러 사용 금지. 글래스 관련 코드 삭제.
- 완료 조건: 디버그 화면에 (a) 잉크 패널 240×140, (b) chamfer 주요 버튼 186×48 (press 시 가라앉음), (c) 스트라이프 배경 전면 — `images/screens/theme_tokens.png`의 미니 프리뷰와 같은 질감. 8테마 전환 시 전부 따라 바뀐다.

### T4. 아이콘
- v1과 동일: `assets/icons/ic_*.svg` 22개 → `TabijiIcons`, `tint = LocalContentColor`.
- 완료 조건: 24dp·48dp 그리드가 ink 색으로 보인다.

### T5. 앱 아이콘·파비콘 (旅 글리프 — v1과 동일)
- `assets/logo/icon/` 세트, 구성은 `docs/04_icons_favicon.md`. v2에서 변경 없음 — 이미 했다면 건너뜀.
- 완료 조건: Android adaptive(흰 배경 필수 자리만 흰색)·Desktop(투명)·브라우저 탭(라이트/다크)에서 旅가 보인다.

## 2. 플래너 (P)

### P1. 배경·지도 자리
- `MapHost`: `assets/map/map_light.svg`를 Crop으로. **지도 패널 자체가 2dp ink + radius 10 + (3,3) 섀도 프레임** (Desktop left16 top76 right408 bottom16).
- 완료 조건: Desktop 1440×900에서 스트라이프 종이 배경 위에 잉크 프레임 지도가 뜬다. `planner_desktop.png`와 외곽 일치.

### P2. 골격 (Desktop) / P3. 골격 (Phone)
- Desktop: 검색 바 480×48 @16,16 · 일정 패널 폭 376 우측 고정 · 지도 컨트롤 46×46 ×2 우하단. Phone: 검색 바 상단(여백 14), 바텀시트(상단 radius 18 + 2dp ink + 그랩바), 수치는 `source/planner_phone.html`.
- 완료 조건: 캡처에 반투명 오버레이로 겹쳐 외곽 일치.

### P4. 패널 컴포넌트
- `DayHeader`(52, h1 채움, 흰 Jua 20 + 이동 합계 배지), `VisitCard`(머리띠 32 로테이션 + 번호 배지 / 기본·선택·고정), `SegmentChip`(커넥터 3×24 + 알약 칩 / 정확·추정·재계산·로딩), `PrimaryButton`(chamfer), `IconButton`, `SearchField`. 치수: `docs/03_components.md`.
- 더미: 슨푸성 공원 → 센겐 신사 → 니혼다이라(선택) → 미호노마쓰바라(14:00 고정) + 구간 3.
- 완료 조건: `planner_desktop.png`·`planner_phone.png`와 동일 — (a) 카드 머리띠가 h1→h2→accent→h3 순, (b) 2→3 칩 '추정' 배지, (c) 3→4 칩 dashed, (d) 4번 카드 시계+고정, (e) 8테마 전환 시 색만 바뀐다.

### P5. 지도 마커·경로
- 물방울 마커 36×44(선택 44×54): 로테이션 채움 + 2.5dp ink 외곽 + 흰 원 + **ink 번호(Jua)**. 경로선 5dp: 도보 accent 점선(2 on 11 off)·대중교통 h2·자동차 h3.
- 완료 조건: 카드 선택 ↔ 마커 확대 동기, 경로색이 칩 커넥터와 일치.

### P6. 상호작용·모션
- 카드 탭 선택(120ms) + 카메라 600ms, 드래그 정렬, 칩 탭 수단 전환 팝오버, press sink 전 버튼 적용. `docs/20_motion.md`.
- 완료 조건: 모든 press가 "가라앉고", 스프링·스케일 팝이 어디에도 없다.

### P7. MapLibre 연결 (선택, 마지막)
- `docs/30_map_style.md` — 타일은 테마 불변, v1 gen_map.py 파이프라인 참조.

## 3. 인트로 (I)

### I1. 사계절 그리드
- `docs/10_intro.md`: 4장 webp, Desktop 2×2 / Phone 세로 4단, **셀 간 3dp ink 경계**, `ContentScale.Crop + BiasAlignment` (봄·여름 0f / 가을·겨울 +0.1f, Phone +0.16f).
- 완료 조건: 창 비율을 바꿔도 왜곡·레터박스 없이 꽉 차고, 피사체가 셀 중앙 영역에 유지된다. `intro_desktop.png`·`intro_phone.png` 대조.

### I2. 로고·메뉴 스택
- 배경 박스 없음: Sora 700 흰 글자 + **ink 아웃라인(Stroke 2벌 드로잉) + (3,3) 하드 섀도**, 부제 Jua, 버튼 3개 (시작하기 chamfer accent / 내 여행·설정 card) — 수치는 `docs/10_intro.md`.
- 완료 조건: 4계절 어느 이미지 위에서도 워드마크가 또렷하다 (아웃라인 덕에 배경 무관). 버튼 press sink 동작.

### I3. 시퀀스·파티클
- 페이드인 스태거 150 → 스택 상승 600 → 파티클 루프 + 패럴랙스 (±4/±8dp). 동작 줄이기 시 전부 끔.
- 완료 조건: `docs/10_intro.md` 시퀀스 표와 일치, reduce-motion에서 정지 프레임.

## 4. 여행 목록 (L)

- `docs/12_trips.md`: 헤더(워드마크+제목 Jua 26+새 여행 chamfer 버튼) · 필터 칩 · auto-fit 그리드(330) · 여행 카드(머리띠 40 + D-day 배지 + 커버 160 + 정보) · dashed 새 여행 타일.
- 완료 조건: `trips_desktop.png`·`trips_phone.png` 일치, 카드 머리띠 로테이션이 인접 중복 없음, 빈 상태는 S 단계 블록 재사용.

## 5. AI 제안 시트 (A)

- `docs/13_ai_sheet.md`: Desktop 중앙 780 다이얼로그 (딤 34%, (5,5) 모달 섀도, accent 헤더 56 + 절약 배지) / Phone 바텀시트. 전후 2열, 제안 카드(accent 테두리 + 체크 + ok 이유), 푸터 3버튼.
- 완료 조건: `ai_sheet_desktop.png`·`ai_sheet_phone.png` 일치, 체크 해제 시 절약 배지 숫자 갱신, 수락 시 240ms 닫힘 + 리스트 재정렬.

## 6. 공통 상태 (S)

- `docs/03_components.md` 하단 + `source/states_*.html`: 오류 배너(errSoft+err 테두리, 콘텐츠 유지) · 오프라인 필(지도 saturate 55%) · 스켈레톤(잉크 테두리 유지, 펄스만) · 빈 상태(마커 일러스트 + 주요 버튼).
- 완료 조건: `states_desktop.png`·`states_phone.png` 일치, 스켈레톤에 시머 없음, 8테마 전환 정상.

## 7. 마무리 체크리스트

- [ ] 8테마 × 플래너·목록·시트·상태 스크린샷 비교 — 레이아웃 변화 0, 색만 변화
- [ ] `CONTRAST.md`의 조합이 코드 색과 일치 (tokens.json에서 옮겼으면 자동 만족)
- [ ] 글래스·블러·그라데이션·elevation 섀도 잔재 검색 → 0건
- [ ] Jua 단독 FontFamily 사용처 검색 → 0건 (전부 폴백 체인)
- [ ] 터치 타깃 48 미달 요소에 히트 영역 확장
- [ ] 동작 줄이기 설정에서 전 화면 확인

## 8. 열린 질문 (팀 결정 필요)

1. **테마별 다크 변형** — 2단계로 미뤘다. paper/ink 반전 방식이냐 별도 다크 팔레트 8종이냐.
2. **컴포넌트 시트 v2 보드** — v1의 Components 보드는 아직 글래스 기준이다. 구현 중 치수 질문이 생기면 source/*.html이 우선, 필요하면 v2 컴포넌트 보드를 추가 제작.
3. **테마 선택 UI** — 설정 화면의 테마 피커 디자인(스와치 그리드? 미리보기 카드?)은 미설계.
4. **인트로와 테마의 결합** — 현재는 "인트로 항상 4계절". 선택 테마의 계절 이미지를 인트로에 단독 노출하는 옵션을 둘지.
