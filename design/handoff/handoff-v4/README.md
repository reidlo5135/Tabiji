# Tabiji 旅路 — 디자인 핸드오프 v4 (아이콘 델타)

- 버전: **handoff-v4** (2026-10-15). **아이콘만** 바꾸는 델타다. 나머지는 [handoff-v2](../handoff-v2/)가 유효하다
- 요청서: [`requests/2026-10-15-icon-navy-outline.md`](../../requests/2026-10-15-icon-navy-outline.md)
- 덮어쓰는 절: handoff-v3 `docs/04_icons_favicon.md`(그리고 그것이 덮어쓴 v2 04) 전체 → 이 패키지의 [`docs/04_icons_favicon.md`](docs/04_icons_favicon.md)

## 바뀐 것

| | v3 | v4 |
| --- | --- | --- |
| 글자 | 旅, Ma Shan Zheng (붓글씨) | 旅, **ZCOOL QingKe HuangYou** (각진 블록) |
| 배경 | 한지 타일 | **없음 (투명)**. Android 런처 배경·iOS만 흰 단색 |
| 색 | 먹 글씨 | 획 `#0C0C10` + **남색 테두리 `#2E5AA8`** |

## 구조

```
handoff-v4/
├── README.md
├── docs/04_icons_favicon.md
├── assets/
│   ├── logo/icon/                       # 아이콘 세트
│   └── fonts/ZCOOLQingKeHuangYou-Regular.ttf + OFL   # 재생성용, 앱에는 넣지 않음
└── tools/gen_navy_icon.py
```
