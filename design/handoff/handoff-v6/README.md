# Tabiji 旅路 — 디자인 핸드오프 v6 (아이콘 델타)

- 버전: **handoff-v6** (2026-10-16). **아이콘만** 바꾸는 델타다. 화면은 [handoff-v2](../handoff-v2/), 인트로 일러스트는 [handoff-v5](../handoff-v5/)가 유효하다
- 요청서: [`requests/2026-10-16-icon-hanji-outline.md`](../../requests/2026-10-16-icon-hanji-outline.md)
- 덮어쓰는 절: handoff-v4 `docs/04_icons_favicon.md` 전체 → 이 패키지의 [`docs/04_icons_favicon.md`](docs/04_icons_favicon.md)

## 바뀐 것

| | v4 | v6 |
| --- | --- | --- |
| 글자 | 旅, ZCOOL QingKe HuangYou (각진 블록) | 旅, **Pretendard JP Bold** (고딕) |
| 배경 | 없음 (투명) | 없음 (투명). Android 런처 배경·iOS만 흰 단색 |
| 색 | 획 `#0C0C10` + 테두리 `#2E5AA8` | 획 **`#F3EBDD`(한지색)** + 테두리 **`#1C1814`(먹색)** |

## 구조

```
handoff-v6/
├── README.md
├── docs/04_icons_favicon.md
├── assets/
│   ├── logo/icon/              # 아이콘 세트
│   └── fonts/OFL-PretendardJP.txt
└── tools/gen_hanji_icon.py
```

서체 파일은 앱에 이미 있어서(`client/shared/src/commonMain/composeResources/font/pretendardjp_bold.ttf`, 5.4MB) 이 패키지에 다시 넣지 않는다.
