# 요청: 앱 아이콘을 "배경 없는 旅, 한지색 획 + 검은 테두리, Pretendard JP Bold"로

- 날짜: 2026-10-16
- 요청자: 클라이언트 담당 (`@reidlo5135`)
- 이전 결정: [`2026-10-15-icon-navy-outline.md`](2026-10-15-icon-navy-outline.md) (handoff-v4, 검은 획 + 남색 테두리)
- 결과: [`handoff/handoff-v6/`](../handoff/handoff-v6/) (아이콘만 바꾸는 델타)

## 배경

`client-v0.1.2`부터 handoff-v4 아이콘(ZCOOL QingKe HuangYou, 검은 획 + 남색 테두리)을 썼다. 요청자가 v3 때 비교한 시안(`~/Pictures/Tabiji/tabiji-icon-options.png`, 레포 밖) 중 **1행 C열 "한지색 글자 + 검은 테두리"**(Pretendard JP Bold)로 바꾸기로 했다.

## 원하는 것

- **배경 없이** 旅 한 글자만
- 획 안쪽은 **한지색**, 획 둘레는 **검은 테두리**
- 서체는 시안 1행의 **Pretendard JP Bold** (앱 본문 서체와 같음)

## 결정

**Pretendard JP Bold + 획 `#F3EBDD`(v3 한지 타일색) + 테두리 `#1C1814`(v3 먹색), 배경 없음.**

- 밝은 획이라 어두운 독에서는 획 자체가, 밝은 배경에서는 검은 테두리가 형태를 잡는다
- 배치·테두리 두께·플랫폼별 예외(Android 런처 배경·iOS 흰 단색)는 v4 그대로
- Pretendard JP는 SIL OFL 1.1이고 앱에 이미 들어 있다. 글리프 윤곽을 로고로 쓰는 것은 허용 범위다
