# 요청: 앱 아이콘을 "한지에 붓으로 쓴 旅"로

- 날짜: 2026-10-15
- 요청자: 클라이언트 담당 (`@reidlo5135`)
- 결과: [`handoff/handoff-v3/`](../handoff/handoff-v3/) (아이콘만 바꾸는 델타)

## 배경

`client-v0.1.0` 릴리스 전에 `.deb`을 설치해 보니, handoff-v2 `04_icons_favicon.md`의 기본 아이콘(투명 배경 + 진한 `#1C2633` 旅, 고딕)이 Ubuntu의 어두운 독·앱 목록에서 **거의 보이지 않았다.**

## 원하는 것

- **한지에 붓으로 쓴 전통적인 고전 한자 느낌**
- 어두운 배경과 밝은 배경 모두에서 또렷할 것
- 독 크기(48px)와 작은 크기(16~32px)에서도 旅로 읽힐 것

## 검토한 시안

폰트 7종 × 스타일 3종을 어두운 배경 위에 그려 비교했다 (`~/Downloads/tabiji-icon-options.png`, 레포 밖).

| 스타일 | 모습 |
| --- | --- |
| A | 흰 글자 + 굵은 검은 테두리 |
| **B** | **한지색 둥근 타일 + 먹 글씨** |
| C | 한지색 글자 + 검은 테두리 (투명 배경) |

폰트는 Pretendard JP Bold(기존) · Noto Serif CJK Black · Ma Shan Zheng · Zhi Mang Xing · Yuji Boku · Yuji Syuku · Nanum Brush. Zhi Mang Xing(행서)은 작은 크기에서 旅로 안 읽혔고, Nanum Brush는 旅 글리프가 비어 있었다.

## 결정

**Ma Shan Zheng(해서 붓글씨) + 스타일 B**. 획이 또렷해서 작은 크기에서도 읽히고, 타일이 배경을 만들어 독 색과 상관없이 보인다.

- handoff-v2 04의 원칙 "배경색 없이 글자만"을 **바꾼다.** 이제 한지 타일이 마크의 일부다
- Ma Shan Zheng은 SIL OFL 1.1이다. 글리프 윤곽을 로고로 쓰는 것은 OFL 허용 범위다 (v2의 Pretendard JP 글리프와 같은 방식)
