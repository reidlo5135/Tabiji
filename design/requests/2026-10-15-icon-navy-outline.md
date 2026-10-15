# 요청: 앱 아이콘을 "배경 없는 旅, 검은 획 + 남색 테두리, 기계적인 서체"로

- 날짜: 2026-10-15
- 요청자: 클라이언트 담당 (`@reidlo5135`)
- 이전 결정: [`2026-10-15-icon-brush-hanji.md`](2026-10-15-icon-brush-hanji.md) (handoff-v3, 한지 타일 + 붓글씨)
- 결과: [`handoff/handoff-v4/`](../handoff/handoff-v4/) (아이콘만 바꾸는 델타)

## 배경

`client-v0.1.1`에 handoff-v3 아이콘(한지 타일 위 붓글씨 旅)을 넣고 GNOME 독에서 확인했다. 잘 보이긴 하지만 요청자 표현으로 "바둑 게임 로고 같다". 원했던 느낌과 다르다.

## 원하는 것

- **배경 없이** 旅 한 글자만
- 획 안쪽은 **검정**, 획 둘레는 **진한 남색 테두리**로 가독성 확보
- 서체는 **로봇·기계 느낌**

## 검토한 시안

기계적인 OFL 서체 8종 × (어두운 독·진한 남색 / 어두운 독·밝은 남색 / 밝은 배경·진한 남색)을 비교했다 (`~/Pictures/Tabiji/tabiji-icon-options-2.png`, 레포 밖). DotGothic16 · Stick · ZCOOL QingKe HuangYou · Dela Gothic One · Train One · Rampart One · RocknRoll One · Reggae One.

시안에서 확인한 점: 어두운 독에서는 검은 속이 배경과 섞이므로 **테두리 색이 가독성을 전부 맡는다.** 진한 남색(`#1E3A6E`)은 48px에서 독에 묻혔고, 밝은 남색(`#2E5AA8`)은 형태가 살았다.

## 결정

**ZCOOL QingKe HuangYou(각진 블록 서체) + 획 `#0C0C10` + 테두리 `#2E5AA8`, 배경 없음.**

- handoff-v3의 한지 타일을 폐기하고 v2의 "배경 없이 글자만" 원칙으로 돌아간다. 대신 테두리로 어두운·밝은 배경 모두에서 보이게 한다
- OS가 불투명 배경을 강제하는 Android 런처 배경 레이어·iOS 터치 아이콘만 흰 단색 (v2와 같은 예외)
- ZCOOL QingKe HuangYou는 SIL OFL 1.1이다. 글리프 윤곽을 로고로 쓰는 것은 허용 범위다
