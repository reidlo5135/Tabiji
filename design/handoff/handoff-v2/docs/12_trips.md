# 12 · 여행 목록 v2

원본: `source/trips_desktop.html`, `source/trips_phone.html`.

## Desktop (콘텐츠 폭 1200 중앙, 패딩 36/48/64, 섹션 gap 24)

- **헤더 행**: 워드마크 "Tabiji" Sora 24 w700 + 부제 Jua 13 `sub` (베이스라인 정렬) · 2×26 ink 50% 세로 구분선 · 제목 "내 여행" **Jua 26** (displayXL) · 우측 **새 여행 만들기** 버튼 186×48 (chamfer 주요 버튼, + 아이콘 18).
- **필터 칩 행**: 전체 N / 예정 / 지난 여행 — 높이 36 알약, 선택 = `accent` 채움 흰 Jua, 비선택 = `card` ink Jua (03_components.md).
- **카드 그리드**: `repeat(auto-fit, minmax(330, 1fr))`, gap 24.

## 여행 카드

위→아래 3단, 전체 2dp ink + radius 10 + (3,3) `shadow`, `card` 면:

1. **머리띠 40** — h1→h2→h3 로테이션, 흰 Jua 16 여행 이름 + 좌측 **D-day 배지** (흰 알약 + 2dp ink, 11 w700 ink — "D-0", "D-166").
2. **커버 이미지 160** — object-fit cover, 아래 2dp ink 구분선. 커버가 없으면 `soft` 면 + 지역명 Jua.
3. **정보 12/14** — 지역·국가 14 w600, 기간·일수·장소 수·상태 12 `sub`.

- hover/press: 카드 전체 press 규칙 (translateY 2, 섀도 3→1).
- **새 여행 만들기 타일**: 같은 그리드 셀, min-height 268, **2dp dashed ink** + 투명 면, 중앙에 + 아이콘 버튼 48×48 (card + 2dp ink + (0,3) `shadow`) + Jua 16 + 안내 12 `sub`.

## Phone

- 단일 컬럼, 여백 14. 헤더는 제목 Jua 24 + 우측 + 아이콘 버튼 44. 필터 칩 가로 스크롤.
- 카드 스펙 동일 (커버 140), 목록 하단에 새 여행 타일.
- 빈 상태 (여행 0개): `source/states_*.html`의 빈 상태 블록 전면 배치.
