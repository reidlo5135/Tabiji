# 02 · 타이포그래피 — 폰트 · 스케일 · 라이선스

## 폰트 선택

| 역할 | 폰트 | 굵기 | 라이선스 | 파일 |
| --- | --- | --- | --- | --- |
| 본문 (한글·일본어·라틴) | **Pretendard JP** | 400 / 500 / 600 / 700 | SIL OFL 1.1 | `assets/fonts/PretendardJP-{Regular,Medium,SemiBold,Bold}.ttf` |
| 로고 · 헤더 · 숫자 | **Sora** (추천 A안) | 400 / 600 / 700 | SIL OFL 1.1 | `assets/fonts/Sora-{Regular,SemiBold,Bold}.ttf` |
| 비교안 | Outfit Bold, Urbanist Bold | 700 | SIL OFL 1.1 | `assets/fonts/Outfit-Bold.ttf`, `Urbanist-Bold.ttf` (로고 확정 후 삭제 가능) |

라이선스 원문: `assets/fonts/OFL-PretendardJP.txt`, `OFL-Sora.txt`, `OFL-Outfit.txt`, `OFL-Urbanist.txt`. OFL은 앱 번들에 포함·재배포를 허용하되 **폰트 파일 자체를 판매할 수 없고 Reserved Font Name을 바꾼 파생본에 쓸 수 없다**. 앱의 라이선스 화면(설정 → 오픈소스 라이선스)에 이 네 파일 내용을 그대로 싣는다.

### 왜 Pretendard **JP** 인가

브리프의 "본문 Pretendard"를 **Pretendard JP** 로 바꿨다. 이유: 기본 Pretendard에는 한자(CJK Ideograph)가 없어 **부제 旅路와 일본 여행지 이름(駿府城公園, 日本平 등)이 시스템 폰트로 떨어진다.** Pretendard JP는 같은 디자인에 일본어 글리프를 더한 공식 변형(동일 OFL)이라 한국어·일본어·라틴을 한 서체로 통일할 수 있다. 대가는 용량: 가중치당 TTF 약 5.4MB (woff2 2.1MB). v1에서는 4가중치 전부 번들하고, 출시 전에 `pyftsubset`으로 KR/JP 상용 글리프 서브셋을 만들어 줄인다 (열린 질문 참고).

### 왜 Sora 인가 (헤더 폰트 최종 선택 근거)

세 후보를 같은 조건으로 비교했다 (캔버스 `로고` 아트보드, `source/logo.html`).

| 후보 | 장점 | 단점 | 판단 |
| --- | --- | --- | --- |
| **Sora** | 기하학적이면서 볼이 둥글어 금속 UI의 차가움을 눌러 줌. 숫자가 또렷해 Day 2·거리·시간을 같은 서체로 쓸 수 있음. 소문자 i·j 점이 마커를 연상 | Hangul 없음 (한글 헤더는 Pretendard 600으로 대체) | **채택** |
| Outfit | 가장 친근함. 경로 밑줄 아이디어와 잘 맞음 | 캐주얼로 기울어 "실버 테크"와 거리. 한자 부제와 무게 차가 큼 | 보류 |
| Urbanist | 날렵·현대적. 타일 락업이 앱 아이콘과 1:1 | 획 대비가 커 22sp 이하에서 j 디센더가 흐려짐. Pretendard 옆에서 가늘어 보임 | 보류 |

Sora를 쓰는 곳: 워드마크, `Day N`, 숫자(날짜·거리·시간·D-day), 마커 번호. **한글이 섞이는 제목은 Pretendard JP 600** 이다 ("내 여행", "AI 동선 제안").

## 스케일 (sp / 줄 높이 sp)

| 키 | 크기 | 줄 높이 | 굵기 | 자간 | 서체 | 예 |
| --- | --- | --- | --- | --- | --- | --- |
| display | 40 | 48 | 700 | −0.02em | Sora | 인트로 워드마크 (Phone 36/44) |
| title | 22 | 28 | 600 | −0.01em | Sora (숫자) / Pretendard (한글) | Day 2 · 내 여행 |
| subtitle | 16 | 22 | 600 | 0 | Pretendard | 여행 카드 제목, 시트 제목 |
| body | 14 | 20 | 400 (강조 600) | 0 | Pretendard | 방문지 이름, 버튼 라벨, 입력 |
| caption | 12 | 16 | 400 (강조 600) | 0 | Pretendard | 시간·주소·칩 텍스트 |
| numeric | 12 | 16 | 600 | 0 | Sora | 2026.09.19 – 09.22 · 1.8km |
| badge | 11 | 14 | 600 | 0 | Pretendard | "추정", "재계산 필요", D-0 |

```kotlin
val TabijiTypography = TabijiTypographyTokens(
    display  = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Bold,     fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.02).em),
    title    = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.01).em),
    titleKo  = TextStyle(fontFamily = PretendardJP, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    subtitle = TextStyle(fontFamily = PretendardJP, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    body     = TextStyle(fontFamily = PretendardJP, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    caption  = TextStyle(fontFamily = PretendardJP, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    numeric  = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    badge    = TextStyle(fontFamily = PretendardJP, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 14.sp),
)
```

## 규칙

- **Pretendard는 `includeFontPadding = false`** 에 해당하는 `PlatformTextStyle(includeFontPadding = false)` 로 쓴다. 한글 줄 높이가 지정값대로 나오게.
- 숫자는 가능하면 Sora로 분리해서 `Row` 로 조립한다 (`Day 2` + `9월 20일 (토)`). 한 `Text` 에 섞어야 하면 `AnnotatedString` 으로 숫자 구간만 Sora.
- 최소 글자 크기 11sp(배지). 10sp 이하는 쓰지 않는다.
- 글자 색: `text.primary` / `text.secondary` / `accent.text` / `accent.on` / `status.error` 다섯 가지만. 배지 안 글자는 배지 채움과 무관하게 `#1C2633` 고정 (추정 배지는 라이트·다크 모두 밝은 주황 채움이라).
- 말줄임: 주소 한 줄은 `maxLines = 1, overflow = Ellipsis`. 방문지 이름은 2줄까지 허용.

## 부제 旅路

- 워드마크 옆 세로쓰기(Desktop 로고 프레임) 또는 아래 가로쓰기(Phone·앱바). 크기 14–16sp, `text.secondary`, 자간 0.2–0.32em.
- Pretendard JP의 한자로 렌더링한다. 로고 SVG(`assets/logo/lockup_A_framed_*.svg`)에서는 폰트 참조 텍스트로 남겨 뒀다 — 최종 로고 확정 후 아웃라인화할 것 (열린 질문).
