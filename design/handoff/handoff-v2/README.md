# Tabiji 旅路 — 디자인 핸드오프 패키지 v2

- 버전: **handoff-v2** (2026-10-14) — 디자인 기조 전면 교체
- 방향: **"도감 같은 여행 수첩"** — 잉크 테두리 · 하드 섀도 · 색 머리띠 · Jua 디스플레이 + **사용자 선택 테마 8종** (기본 구름 + 사계절 4 + 한중일 3)
- v1("맑은 하늘 위의 실버 테크" / 글래스)은 폐기. **v1 대비 유지되는 것**: 인트로 계절 일러스트 4장과 fit 규칙, 旅 플랫폼 아이콘 세트, chamfer 버튼, 간격 체계(4 그리드), Pretendard JP·Sora, 아이콘 22종, 정보 구조(화면 ①~⑤)와 더미 데이터
- 대상: Compose Multiplatform 구현자 (Claude Code 가이더 + 사람). 1차 Android + Desktop
- 디자인 원본: Claude Design 캔버스 "Tabiji 旅路 — 디자인 핸드오프 v1"의 **v2 보드 11장** (상단 영역, Tweaks ▸ theme 으로 8종 전환). 이 패키지는 그 보드를 파일로 내린 것

## v1 → v2 변경 요약

| | v1 | v2 |
| --- | --- | --- |
| 무드 | 글래스·실버·블러 | 도감: 2dp 잉크 테두리, 평색, 색 머리띠 |
| 색 | 라이트/다크 1팔레트 | **테마 8종 × 역할** 2차원 (다크는 2단계) |
| 섀도 | 블러 소프트 섀도 | **블러 0 솔리드 오프셋** |
| 디스플레이 폰트 | Pretendard JP | **Jua** (본문은 Pretendard JP 유지) |
| 배경 | 하늘 그라데이션 | paper + 45° 스트라이프 |
| 마커 | 그라데이션 핀 | 로테이션 채움 + ink 외곽 + ink 번호 |

## 읽는 순서

1. `PROMPT.md` — 구현 브리프. 단계·완료 조건·열린 질문. **구현은 여기서 시작**
2. `docs/00_principles.md` — v2 8원칙. 수치보다 우선
3. `docs/01_tokens.md` + `tokens/tokens.json` — 테마×역할 모델, Kotlin 매핑, **보드→토큰 색 조정 기록**
4. `docs/02_typography.md` — Jua + Pretendard JP + Sora
5. `docs/03_components.md` — v2 레시피 (패널·머리띠·칩·버튼·마커·시트·상태)
6. `docs/10_intro.md` → `11_planner.md` → `12_trips.md` → `13_ai_sheet.md`
7. `docs/04_icons_favicon.md` (v1과 동일) · `docs/20_motion.md` · `docs/30_map_style.md`
8. `CONTRAST.md` — 8테마 전 조합 검증 (FAIL 0, `tools/contrast_check.py`로 재생성)

## 구조

```
handoff-v2/
├── README.md · PROMPT.md · CONTRAST.md
├── docs/            # 00,01,02,03,04, 10,11,12,13, 20,30
├── tokens/tokens.json        # 수치·색 최종 기준 (테마 8종)
├── source/*.html              # 레이아웃 진실 (1px=1dp, 기본 cloud, 우하단 테마 미리보기 셀렉터)
│   # theme_tokens / intro_* / planner_* / trips_* / ai_sheet_* / states_* (desktop·phone)
├── images/screens/            # 보드 캡처 네이밍 규칙 (README 참조)
├── assets/
│   ├── fonts/                 # Jua, PretendardJP ×4, Sora ×3 + OFL 전문
│   ├── icons/                 # 라인 아이콘 22 (v1과 동일)
│   ├── logo/                  # 워드마크 + icon/ 旅 플랫폼 아이콘 세트 (v1과 동일)
│   ├── intro/                 # 계절 일러스트 webp 4 (v1과 동일)
│   └── map/                   # 지도 목업 svg
└── tools/
    ├── contrast_check.py      # 8테마 대비 검증 → CONTRAST.md
    └── build_source_v2.py     # 캔버스 보드 → source/*.html (참고용, 재실행엔 캔버스 원본 필요)
```

## 진실 소스 우선순위

**tokens.json → source/*.html → images/screens/**. 보드·소스의 CSS 색과 tokens.json이 다른 7건은 대비 검증 후 토큰만 어둡게 확정한 것 — 목록은 `docs/01_tokens.md`.

## 라이선스

- 폰트: Jua · Pretendard JP · Sora — 모두 SIL OFL 1.1 (`assets/fonts/OFL-*.txt`)
- 지도: © OpenStreetMap contributors · OpenFreeMap
- 인트로 일러스트: 팀 제공 에셋, 제3자 캐릭터 포함 — 배포 시 권리 처리는 팀 책임 (비영리·소스 공개 방침)
- 아이콘·로고·그 외 디자인 산출물: 프로젝트 라이선스를 따름
