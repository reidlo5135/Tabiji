# Tabiji 旅路 — 디자인 핸드오프 v5 (인트로 이미지 델타)

- 버전: **handoff-v5** (2026-10-15). **인트로 사계절 이미지만** 바꾸는 델타다. 아이콘은 [handoff-v4](../handoff-v4/), 나머지는 [handoff-v2](../handoff-v2/)가 유효하다
- 요청서: [`requests/2026-10-15-intro-art-no-characters.md`](../../requests/2026-10-15-intro-art-no-characters.md)
- 덮어쓰는 절: handoff-v2 `docs/10_intro.md`의 "에셋"과 "fit-in-view 규칙" 절 → 이 패키지의 [`docs/10_intro.md`](docs/10_intro.md)

## 바뀐 것

| | v1·v2 | v5 |
| --- | --- | --- |
| 내용 | 제3자 캐릭터 포함 (커밋·배포 불가) | **캐릭터 없음** (커밋·배포 가능) |
| 비율 | 봄·여름 1.79:1 / 가을·겨울 1.34:1 | **네 장 모두 1.79:1** (2048×1143) |
| 장면 | 벚꽃 축제 · 마리나베이 · 마을 · 산장 | 벚꽃 축제(일본) · 마리나베이(싱가포르) · 불국사(한국) · 빙설대세계(중국) |

## 구조

```
handoff-v5/
├── README.md
├── docs/10_intro.md          # 에셋·fit 규칙 절만
└── assets/intro/{spring,summer,autumn,winter}.webp   # 긴 변 2048, 품질 85
```
