# Tabiji 旅路 — 디자인 핸드오프 v3 (아이콘 델타)

- 버전: **handoff-v3** (2026-10-15). **아이콘만** 바꾸는 델타다. 나머지(테마·타이포·화면)는 [handoff-v2](../handoff-v2/)가 그대로 유효하다
- 요청서: [`requests/2026-10-15-icon-brush-hanji.md`](../../requests/2026-10-15-icon-brush-hanji.md)
- 덮어쓰는 절: handoff-v2 `docs/04_icons_favicon.md` 전체 → 이 패키지의 [`docs/04_icons_favicon.md`](docs/04_icons_favicon.md)

## 바뀐 것

| | v2 | v3 |
| --- | --- | --- |
| 글자 | 旅, Pretendard JP Bold (고딕) | 旅, **Ma Shan Zheng** (해서 붓글씨) |
| 배경 | 없음 (투명). 런처·iOS만 흰 단색 | **한지색 둥근 타일** `#F3EBDD` + 먹 테두리 `#1C1814` |
| 글자색 | `#1C2633` (다크 표면엔 `#E9EEF4`) | 먹 `#1C1814` 한 가지 (타일이 배경이라 표면에 따라 바꾸지 않음) |

## 구조

```
handoff-v3/
├── README.md
├── docs/04_icons_favicon.md       # v2 04를 덮어씀
├── assets/
│   ├── logo/icon/                 # 아이콘 세트 (아래 표)
│   └── fonts/MaShanZheng-Regular.ttf + OFL-MaShanZheng.txt   # 재생성용 원본, 앱에는 넣지 않음
└── tools/gen_brush_icon.py        # 세트 재생성 스크립트
```

## 라이선스

- Ma Shan Zheng: SIL OFL 1.1 (`assets/fonts/OFL-MaShanZheng.txt`). 아이콘은 글리프 윤곽을 그린 이미지·SVG 경로이고, 폰트 자체는 앱에 넣지 않는다
