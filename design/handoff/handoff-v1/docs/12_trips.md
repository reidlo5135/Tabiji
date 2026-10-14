# 12 · 여행 목록

기준 이미지: `images/screens/trips_desktop_light.png`, `trips_desktop_dark.png`, `trips_phone_light.png`, `trips_phone_dark.png`. 원본: `source/trips_desktop_light.html`, `source/trips_phone_light.html`. 빈 상태는 `states_*` 에 있다.

## Desktop

- 배경: `gradient.sky` 세로. 지도 없음.
- 콘텐츠 최대 폭 1200, 좌우 패딩 48, 상단 40. `Column(gap 32)`.
- 헤더 `Row(center, gap 24)`: 워드마크(Sora 22/700 + 旅路 12 자간 0.2em) → 1×24 구분선 → "내 여행" title 22/600 (flex) → 주요 버튼 "새 여행 만들기" 180×48 chamfer (+ 아이콘 20).
- 필터 칩 `Row(gap 8)`: 높이 36 · 패딩 0 14 · 완전 라운드. 선택: 테두리 `accent.line` + `accent.soft` 채움 + `accent.text` 13/600. 비선택: `line.subtle` + `surface.card` + 13/500.
- 카드 그리드: `LazyVerticalGrid(adaptive(minSize = 340.dp), gap 24)`. 1200 폭에서 3열.

## 여행 카드 (TripCard)

- 모서리 12 · `surface.card` · 테두리 1 · 그림자 panel · 상단 안쪽 하이라이트.
- 썸네일 영역 높이 180 (Phone 140): 실버 그라데이션 바탕 + **대표 썸네일 자리** (지도 스냅샷 또는 사진, `ContentScale.Crop`). 없으면 지도 플레이스홀더 90% + 우하단 "대표 썸네일 자리" 라벨(개발 중에만).
- 좌상단 D-day 배지 (12,12): 높이 24 · 패딩 0 10 · 완전 라운드 · Sora 11/600. 진행 중: `accent.sky` + `accent.on` ("D-0 · 진행 중"). 예정: 실버 + `text.primary` ("D-166"). 지난 여행: `line.subtle` + `text.secondary`.
- 본문 패딩 16 (Phone 14 16) `Column(gap 6 / 4)`: 제목 subtitle 16/600 · 지역 body 14 secondary ("시즈오카 · 일본") · 날짜 numeric 12 Sora secondary ("2026.09.19 – 09.22 · 4일 · 18곳").
- 전체가 하나의 클릭 타깃 → 플래너로 이동 (`Modifier.clickable`).
- 호버(Desktop): 그림자 lift + `translationY −2dp` 120ms.

## 새 여행 만들기 카드 (Desktop 그리드 마지막 칸)

- 높이 288 (카드와 같은 높이) · 점선 테두리 1.5 `line.subtle` · 투명 · `Column(center, gap 12)`: 48 실버 원(+ 아이콘) · "새 여행 만들기" 14/500 · "지역과 날짜만 정하면 시작됩니다" 12 secondary.
- 터치 타깃은 카드 전체.

## Phone

- 상단 56 (상태바 아래) · 좌우 16 · `Column(gap 16)` 스크롤.
- 헤더: 워드마크 + 旅路 + 우측 44 글래스 원형 설정 버튼.
- "내 여행" title 22 → 필터 칩 → 카드 세로 나열 (gap 16).
- 하단 고정 **주요 버튼 358×48 "새 여행 만들기"** (패딩 12 16 28, 뒤에 `bg.sky.bottom` 으로 페이드되는 그라데이션 40%). 리스트 하단 패딩 112 로 가려지지 않게.

## 더미 데이터

| 제목 | 지역 | 날짜 | 상태 |
| --- | --- | --- | --- |
| 시즈오카 3박4일 | 시즈오카 · 일본 | 2026.09.19 – 09.22 · 4일 · 18곳 | D-0 · 진행 중 |
| 오사카·교토 벚꽃 | 간사이 · 일본 | 2027.03.28 – 04.01 · 5일 · 일정 작성 중 | D-166 |
| 제주 봄 드라이브 | 제주 · 대한민국 | 2027.04.03 – 04.05 · 3일 · 9곳 | D-172 |

(D-day는 2026-10-13 기준.)
