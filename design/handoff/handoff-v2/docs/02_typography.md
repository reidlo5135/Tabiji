# 02 · 타이포그래피 v2 — Jua + Pretendard JP + Sora

v2에서 디스플레이 폰트가 **Jua**로 바뀌었다 (도감 무드의 핵심). 본문은 v1과 같은 Pretendard JP, 숫자·워드마크는 Sora 유지. 세 폰트 모두 OFL — 라이선스 전문은 `assets/fonts/OFL-*.txt`.

## 폰트 파일 (`assets/fonts/`)

| 파일 | 역할 | 비고 |
| --- | --- | --- |
| `Jua-Regular.ttf` | 디스플레이 (제목·버튼 라벨·Day N·카드 헤더·탭·필터 칩) | fontsource 유니코드 분할본 88개를 단일 TTF로 병합한 것. 2,519 글리프 — **한글·라틴 전용, 한자·가나 없음** |
| `PretendardJP-{Regular,Medium,SemiBold,Bold}.ttf` | 본문 | JP 변형이어야 旅路 등 한자·가나가 깨지지 않는다 (일반 Pretendard 금지) |
| `Sora-{Regular,SemiBold,Bold}.ttf` | 숫자·날짜·거리·시간, 워드마크 | |

**Jua 폴백 규칙**: Jua에 한자·가나가 없으므로, 디스플레이 자리라도 한자·가나가 섞이는 문자열(여행지 이름 등)은 Compose `FontFamily(Jua, PretendardJP)` 체인으로 선언해 글자 단위 폴백이 일어나게 한다. 제목 전용 스타일에 Jua 단독 지정 금지.

## 스케일 (sp / lineHeight)

| 스타일 | 크기 | 폰트 | 쓰임 |
| --- | --- | --- | --- |
| displayXL | 26 / 32 | Jua | 화면 제목 (내 여행) |
| displayL | 20 / 26 | Jua | 패널 헤더 (Day 2) · 모달 제목 |
| displayM | 16 / 22 | Jua | 카드 헤더 · 버튼 라벨 · 필터 칩 |
| displayS | 14 / 18 | Jua | 작은 카드 헤더 · 탭 |
| body | 14 / 20 · w600 | Pretendard JP | 방문지 정보 주 텍스트 — **v2 기본 본문은 600** (잉크 테두리와 무게를 맞춤) |
| caption | 12 / 16 · w600 | Pretendard JP | 보조 정보 |
| badge | 11 / 14 · w700 | Pretendard JP | 배지·칩 내부 |
| numeric | 12 / 16 · w600 | Sora | 날짜·거리·시간 |

- Jua는 단일 웨이트(400)다. 디스플레이 위계는 **크기로만** 만든다.
- 헤더 스트립 위 글자는 전 테마 **#FFFFFF** (대비는 CONTRAST.md에서 보증). 스트립 안의 보조 정보(날짜 등)는 Pretendard JP 600, 흰색 92% 불투명.

## 워드마크 (인트로)

로고 "Tabiji"는 v1과 동일하게 **Sora 700** (글자, 배경 박스 없음). v2에서 바뀐 것은 마감 — 흰 글자에 **잉크 아웃라인 + 하드 섀도**를 입힌다 (10_intro.md에 Compose 구현).

- Desktop: 58sp, 아웃라인 2dp 상당, 섀도 (3, 3) ink
- 부제 "여행(旅路) 도우미": Jua 17sp (한자는 Pretendard JP 폴백), 자간 0.1em, 같은 처리의 축소판 (아웃라인 1.5dp, 섀도 2dp)
