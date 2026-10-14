# Tabiji 旅路 — 디자인 핸드오프 패키지 v1

- 버전: **handoff-v1.1** (2026-10-13) — 인트로 개편: 계절 일러스트 4장(팀 제작) + 박스 없는 로고·메뉴 스택(Compose 직접 드로잉)
- 방향: "맑은 하늘 위의 실버 테크"
- 대상: Compose Multiplatform 구현자 (Claude Code 가이더 + 사람). 1차 Android + Desktop
- 디자인 원본: Claude Design 캔버스 "Tabiji 旅路 — 디자인 핸드오프 v1" (아트보드 26장). 이 패키지는 그 캔버스를 파일로 내린 것이다.
- 요청 프롬프트 사본: 리포의 `design/requests/0001-handoff-v1.md`

## 읽는 순서

1. `PROMPT.md` — Claude Code 구현 브리프. 단계·완료 조건·열린 질문. **구현은 여기서 시작**
2. `docs/00_principles.md` — 다섯 원칙. 수치보다 우선
3. `docs/01_tokens.md` + `tokens/tokens.json` — 색·간격·모서리·그림자·Kotlin 이름. 시작 토큰에서 바뀐 것과 이유
4. `docs/02_typography.md` — Pretendard JP + Sora, 스케일, 라이선스
5. `docs/03_components.md` — 컴포넌트 치수·상태
6. `docs/10_intro.md` → `11_planner.md` → `12_trips.md` → `13_ai_sheet.md` — 화면별
7. `docs/20_motion.md`, `docs/30_map_style.md`
8. `CONTRAST.md` — 대비 검증 표 (`tools/contrast_check.py` 로 재생성)

## 구조

```
handoff-v1/
├── README.md                 # 이 문서
├── PROMPT.md                 # Claude Code용 구현 브리프
├── CONTRAST.md               # WCAG 대비 검증 결과 (자동 생성)
├── docs/
│   ├── 00_principles.md
│   ├── 01_tokens.md          # 토큰 표 + Kotlin 이름 매핑 + 변경 기록
│   ├── 02_typography.md      # 폰트, 스케일, 라이선스
│   ├── 03_components.md      # 컴포넌트별 치수·상태·여백 (dp)
│   ├── 04_icons_favicon.md   # 플랫폼 아이콘·파비콘 — 旅 글리프 구성 (웹/Android/Desktop/iOS)
│   ├── 10_intro.md           # 레이어, 파티클 수·속도·방향, 시퀀스, Canvas vs Lottie
│   ├── 11_planner.md         # Desktop/Phone 레이아웃 치수, 바텀시트 높이 단계
│   ├── 12_trips.md
│   ├── 13_ai_sheet.md
│   ├── 20_motion.md          # 이징(cubic-bezier), 길이
│   └── 30_map_style.md       # MapLibre 레이어별 색 지정표
├── tokens/tokens.json        # 라이트/다크 색(hex+alpha), 간격, 모서리, 타이포, 모션 — 기계가 읽는 원본
├── assets/
│   ├── icons/ic_*.svg        # 오리지널 라인 아이콘 22종, 24dp, 선 1.75, currentColor
│   ├── logo/                 # 워드마크 3안(아웃라인 SVG, 라이트/다크), A안 프레임 락업, 구 앱 아이콘(경로+마커, 대안 보존)
│   ├── logo/icon/            # 旅 글리프 플랫폼 아이콘 세트: favicon.svg(라이트/다크 자동)·png 16–512·adaptive 전경·터치/maskable·Desktop 할로 — 04_icons_favicon.md
│   ├── intro/{spring,summer,autumn,winter}.webp # 계절별 일러스트 4장, 번들용 최적화본 (긴 변 2048 · q85 · 장당 ~400KB). 원본 PNG는 팀 보관 — 크롭 규칙은 10_intro.md
│   ├── map/map_{light,dark}.svg         # 지도 플레이스홀더 (실제 타일 아님)
│   └── fonts/                # PretendardJP ×4, Sora ×3, Outfit/Urbanist Bold(비교용) + OFL 라이선스 4종
├── source/*.html             # 디자인 원본 (독립 실행 HTML). 수치가 이미지와 다르면 이게 정답. 1px = 1dp
├── images/screens/           # 기준 스크린샷 자리 — README 참고 (캔버스에서 내보내기)
└── tools/contrast_check.py   # 대비 검증 스크립트
```

## source/*.html 사용법

- 브라우저에서 바로 연다. 폰트·이미지는 `../assets/` 상대 경로로 로드된다 (파일 프로토콜에서 폰트가 막히면 `python3 -m http.server` 로 패키지 루트를 띄운다).
- `<name>_light.html` / `<name>_dark.html` 한 쌍. 개발자 도구로 요소를 찍으면 dp 값(=px)이 그대로 보인다.
- 캔버스 아트보드와 1:1 대응: planner_desktop = Main, planner_phone, intro_*, trips_*, ai_sheet_*, states_*, components, icons, logo, tokens, storyboard.
- 캔버스와 달리 테마 토글이 없다 (다크는 별 파일). `clip-path: path()` 로 그린 chamfer 는 01_tokens.md 의 `chamferShape` 와 같은 도형이다.

## 확정하지 않았던 네 가지에 대한 제안 (근거는 각 문서)

| 항목 | 제안 | 어디 |
| --- | --- | --- |
| 헤더 폰트 | **Sora** (A안). Outfit·Urbanist 는 비교안으로 동봉 | 02_typography.md, 캔버스 "로고" |
| 인트로 제작 | **계절 이미지 4장 + Compose Canvas 파티클/모션** (Lottie 아님). 이미지는 `ContentScale.Crop` + focus 바이어스로 비율 무관하게 fit | 10_intro.md |
| 다크 테마 v1 포함 | **포함** — 토큰이 두 벌이고 지도 앱은 야간 사용이 많다. 지도 다크 스타일만 별도 단계 | PROMPT.md §7 |
| 지도 실버 톤 정도 | **Positron 수준 저채도 + 실버 틴트**, 도로·물·공원 명도 차 유지 | 30_map_style.md |

## 시작 토큰에서 바뀐 것 (요약)

`accent.on`, `accent.text`, `accent.line`, `status.error.text` 신설 · `route.drive` 라이트 #4FB08A → #3A9A74 · `route.transit`/`route.drive` 다크값 제안 · 본문 폰트 Pretendard → **Pretendard JP** (한자·일본어 글리프). 전부 접근성 대비 또는 글리프 커버리지 때문이며 이유는 01_tokens.md, 02_typography.md 에 있다.

v1.1 변경: 인트로가 벡터 레이어 합성 → **계절 일러스트 4장 + 박스 없는 로고·메뉴 스택**으로 바뀜 (10_intro.md 전면 개정, PROMPT.md I 단계·열린 질문 갱신). 부제 카피는 "旅路" → **"여행(旅路) 도우미"**. 인트로 일러스트에는 제3자 캐릭터가 포함되어 있어 배포 전 권리 처리·교체는 팀 판단 (레이아웃은 파일 교체만으로 유지).
