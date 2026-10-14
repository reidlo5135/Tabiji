# 03 · 컴포넌트 — 치수 · 상태 · 여백 (dp)

기준 이미지: `images/screens/components_light.png`, `components_dark.png` (캔버스 "컴포넌트 · 상태별" 아트보드). 원본: `source/components_light.html`. 모든 값은 dp, 글자는 sp. 공통 규칙: 터치 타깃 ≥ 48, 아이콘 선 1.75, 모션은 20_motion.md.

## 공통 표면 레시피

| 이름 | 구성 | Compose 힌트 |
| --- | --- | --- |
| **글래스 패널** | 배경 `surface.glass` (블러 16–24) 또는 `surface.glass.solid` · 테두리 1 `line.subtle` · 상단 안쪽 1 `line.highlight` · 그림자 panel · 모서리 panel 16 | `Box(Modifier.shadow(8.dp, shape).clip(shape).background(color).border(1.dp, line, shape))` + `drawBehind { drawLine(highlight, Offset(r,0.5f), Offset(w-r,0.5f)) }` |
| **실버 표면** | `Brush.verticalGradient(silverFrom, silverTo)` · 테두리 1 `line.subtle` · 상단 1 `line.highlight` | `background(brush, shape)` |
| **카드** | `surface.card` · 테두리 1 `line.subtle` · 모서리 12 · 그림자 없음 | 카드는 패널 안에서만 쓰므로 그림자 없음 |

## 1. 방문지 카드 (VisitCard)

- 폭: 패널 안쪽 폭(Desktop 328, Phone 358). 높이: 내용에 따름 (3줄 기준 92, 2줄 76).
- 패딩 12 · 가로 `Row(gap 12)` · 번호 원 28 (실버, Sora 12/600) · 텍스트 `Column(gap 3)` · 우측 드래그 핸들 아이콘 20 (`text.secondary`).
- 제목 body 14/600, 시간 caption 12 `text.secondary`, 주소 caption 12 `text.secondary` 한 줄 말줄임.

| 상태 | 변화 |
| --- | --- |
| 기본 | 위 레시피 |
| 선택 | 테두리 **2dp `accent.line`** (패딩 11로 보정해 크기 유지) + 바깥 링 4dp `accent.soft` · 번호 원 `accent.sky` 채움 + `accent.on` 글자 700 · 240ms |
| 드래그 중 | `scale 1.02` · 회전 −0.5° · 그림자 lift · 불투명도 0.96 · 핸들 아이콘 `accent.text` · 시작 120ms |
| 고정 시간 있음 | 시간 줄을 `시계 아이콘 14 + "14:00 고정"(600) + "· 투어 예약 · 1시간"(secondary)` 로 교체. 글자는 `text.primary` |

Compose: `Modifier.border(if (selected) 2.dp else 1.dp, ...)` 에서 패딩을 1dp 줄여 레이아웃 점프 방지. 드래그는 `Modifier.graphicsLayer { scaleX = 1.02f; rotationZ = -0.5f; shadowElevation = ... }`.

## 2. 구간 칩 (SegmentChip)

- 행 높이 32, 좌측 패딩 14. `Row(gap 8)`: **연결선 2×32** + 칩.
- 연결선 색 = 이동수단 색(`route.walk` / `route.transit` / `route.drive`). 도보는 `repeating gradient 4 on / 4 off` 점선. 미계산은 `line.subtle`.
- 칩: 높이 28 · 패딩 0 10 · 완전 라운드 · `surface.chip` · 테두리 1 `line.subtle` · `Row(gap 6)`: 아이콘 16(이동수단 색) + caption 12 `text.primary`.

| 상태 | 변화 |
| --- | --- |
| 정확 | 기본 |
| 추정 | 뒤에 **배지** `높이 18 · 패딩 0 6 · 완전 라운드 · status.estimated 채움 · #1C2633 11/600 · ≈ 아이콘 12 + "추정"` |
| 재계산 필요 | 테두리 **점선 1dp `text.secondary`** (`PathEffect.dashPathEffect(floatArrayOf(4f,4f))`) · 글자 `text.secondary` · 뒤에 "재계산 필요" 11/600 |
| 로딩 | 아이콘 자리 16 원 + 96×10 바, `surface.skeleton`, 시머 1.2s |

텍스트 형식: `{수단} {분}분 · {km}km` (예 `대중교통 23분 · 6.4km`). 60분 이상은 `1시간 12분`.

## 3. 일차 헤더 (DayHeader)

- 펼침: 높이 72 · 패딩 16 16 12 · 하단 구분선 1. `Row(gap 12)`: `Column(gap 2)`[ `Row(baseline, gap 8)`[ "Day 2" Sora 22/600 · "9월 20일 (토) · 시즈오카" 14 secondary ] · "총 이동 1시간 12분 · 5곳 · 약 17.6km" 12 secondary ] + 접기 버튼 36 실버 원(쉐브론 20).
- 접힘: 높이 56 · 패딩 10 16 · 요약을 한 줄 `"9월 21일 (일) · 4곳 · 이동 52분"` 12 secondary 로 합침 · 쉐브론 180° 회전 240ms · 아래 리스트 `AnimatedVisibility(shrinkVertically)` 240ms.

## 4. 지도 마커 (MapMarker)

| 상태 | 크기 | 구성 |
| --- | --- | --- |
| 기본 | 32×40 | 물방울 path(아래 참고) 실버 그라데이션 + 1dp `marker.stroke` · 흰 원 r10.5 (중심 16,16) · 번호 Sora 12/600 `#1C2633` · 앵커 = 아래 꼭짓점 (16,40) |
| 선택 | 40×50 | `accent.sky` 채움 + 흰 2dp 테두리 · 흰 원 r13 · 번호 14/700 · 글로우 markerSelected · 기본→선택 240ms 확대, z-order 최상 |
| 고정 시간 | 32×40 + 배지 | 우상단(−6,−6)에 **14dp 흰 원 배지** (`marker.face`, 1dp `marker.stroke`) 안에 시계 아이콘 10 |

물방울 path (32×40 기준, 그대로 `Path` 로):
`M16 1 C7.7 1 1 7.7 1 16 C1 26.5 16 39 16 39 C16 39 31 26.5 31 16 C31 7.7 24.3 1 16 1 Z`

MapLibre에서는 마커를 **HTML/Compose 오버레이가 아닌 심볼 레이어 이미지**로 넣는 것을 권장 (성능). 기본·선택·고정 3종 × 번호 1–20을 미리 래스터화하거나, Compose `Canvas` 로 그려 `Bitmap` 생성.

경로 라인: 4dp · 도보 `route.walk`(= `accent.line`) 점선 `dash 2 / gap 9, round cap` · 대중교통 `route.transit` 실선 · 자동차 `route.drive` 실선 · 미계산 구간 45% 불투명. 선택 구간은 6dp. 구간 칩의 연결선·아이콘도 같은 색.

## 5. 주요 버튼 (PrimaryButton, chamfer)

- 높이 48 · 최소 폭 140 · 패딩 0 20 · `chamferShape(12, 8)` · `accent.sky` 채움 · 상단 안쪽 1dp `#FFFFFF @55%` · 라벨 body 14/600 `accent.on` · 아이콘 20 (gap 8).

| 상태 | 변화 |
| --- | --- |
| 기본 | 위 |
| 눌림 | `accent.pressed` · 하이라이트 제거 · `scale 0.98` · 120ms |
| 비활성 | `line.subtle` 채움 · 글자 `text.secondary` · 하이라이트 제거 |
| 로딩 | 채움 85% · 라벨 → "동선 계산 중…" · 좌측 18dp 스피너(2.5dp 선, `accent.on` 25%/100%, 0.9s) · 폭 유지(`Modifier.width(IntrinsicSize)` 로 고정) |

보조 버튼 (SecondaryButton): 높이 44 · 모서리 12 (chamfer 없음) · 실버 표면 · 라벨 14/500 `text.primary`. 텍스트 버튼 (TertiaryButton): 높이 44 · 테두리 1 `line.subtle` · 투명.

## 6. 글래스 원형 버튼 (GlassIconButton)

- 48 원 · 글래스 패널 레시피(블러 16) · 아이콘 24 `text.primary` · 그림자 panel.
- 눌림: 실버 pressed 그라데이션 + 안쪽 그림자 `y2 blur4 #1C2633 @18%` + `scale 0.96` · 120ms.
- 블러 불가: `surface.glass.solid`.
- 세로 묶음 gap 8. Desktop 우하단 (right = 24 + 360 + 24 = 408, bottom 24). 패널 접힘 시 right = 24 + 56 + 24 = 104 로 240ms 이동. Phone: right 16, bottom = 시트 높이 + 16.

## 7. 입력 필드 · 검색 (SearchField / TextField)

- 높이 48 · 모서리 12 · 패딩 0 14 · 글래스 패널 레시피 · `Row(gap 10)`: 아이콘 20 `text.secondary` + 입력 body 14 `text.primary` (+ Desktop 우측 단축키 kbd "/" Sora 11).
- 포커스: 테두리 **2dp `accent.line`** (패딩 13) + 링 4dp `accent.soft` · 아이콘 `accent.text` · 지우기 24 원 버튼(`line.subtle`) 등장.
- 오류: 테두리 1 `status.error` + 링 3dp `status.error.soft` · 좌측 아이콘 → 오류 아이콘 `status.error` · 아래 4dp 간격 caption 12 **`status.error.text`** 메시지.
- 플레이스홀더 `"장소, 주소, 역 이름 검색"` `text.secondary`.

## 8. 바텀시트 (Phone) · 패널 접기 (Desktop)

바텀시트: 상단 모서리 20 · 글래스(블러 20) · 테두리 1 (하단 없음) · 그림자 `y −8 blur 24 @12%` · 핸들 36×4 `line.subtle` 상단 8dp 중앙.

| 단계 | 높이 | 내용 |
| --- | --- | --- |
| 접힘 | 96 | 핸들 + 일차 헤더(접힘형) |
| 절반 | 420 | 핸들 + 헤더 + 리스트(스크롤) + 하단 고정 버튼. **기본 진입 상태** |
| 전체 | 화면 − 상태바 − 24 | 지도 상단 24dp만 남김 |

- 단계 전환 240ms 감속. 드래그 속도 > 800dp/s 면 다음 단계로 스냅. 리스트 스크롤은 전체 단계에서만 시트 안 스크롤, 그 외는 시트 드래그.
- 지도 컨트롤은 시트 위 16dp 에 따라 움직인다.

Desktop 패널: 폭 360 ↔ **56 레일** 240ms. 레일에는 일차 점 인디케이터(6dp, 현재 `accent.sky`)와 펼치기 버튼만. 지도 컨트롤도 같은 시간에 이동. 패널 접힘 상태는 기억한다.

## 9. 배너 · 필 · 스켈레톤 (⑤ 공통 상태)

- **오류 배너**: 패널 최상단, 마진 12 · 패딩 10 12 · 모서리 12 · `status.error.soft` 채움 + 테두리 1 `status.error` · `Row(gap 10)`: 아이콘 20 `status.error` + 메시지 13 `text.primary`(flex) + "다시 시도" 32 높이 칩 버튼 + 닫기 32. **콘텐츠는 그대로 두고** 배너만 끼워 넣는다 (`AnimatedVisibility` 240ms). 영향을 받은 칩은 "재계산 필요" 상태로.
- **오프라인 필**: 지도 상단 중앙 (상태바 + 8, Desktop 16) · 높이 36 · 패딩 0 14 · 완전 라운드 · 글래스 · `Row(gap 8)` 오프라인 아이콘 16 + 12/600 "오프라인 · 저장된 일정 표시 중". 모든 구간 칩에 "추정" 배지, AI 버튼 비활성 + 라벨 "AI 동선 최적화 · 온라인 필요".
- **스켈레톤**: 실제 컴포넌트와 같은 틀(카드 92, 칩 28)에 `surface.skeleton` 블록. 시머는 `surface.skeleton → .hi → .skeleton` 가로 그라데이션 1.2s 선형 반복, 동작 줄이기 시 정지.
- **빈 상태**: 글래스 패널 중앙 정렬 `Column(gap 10–12)`: 일러스트(64 마커 또는 48 실버 원 아이콘) + 제목 16/600 + 설명 13 secondary(2줄) + 주요 버튼 180 폭.
