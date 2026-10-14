# 10 · 인트로 v2 — 사계절 4장 + 잉크 스택

원본: `source/intro_desktop.html` (1440×900), `source/intro_phone.html` (390×844). v1의 **이미지 4장·fit 규칙·시퀀스는 그대로**, 중앙 스택과 경계 처리만 v2 마감으로 바뀌었다.

## 에셋 (v1과 동일)

| 파일 | 계절 | 원본(팀 보관) | 비율 |
| --- | --- | --- | --- |
| `assets/intro/spring.webp` | 봄 (벚꽃 축제) | 2752×1536 PNG | 1.79 |
| `assets/intro/summer.webp` | 여름 (마리나베이) | 2752×1536 PNG | 1.79 |
| `assets/intro/autumn.webp` | 가을 (마을) | 2400×1792 PNG | 1.34 |
| `assets/intro/winter.webp` | 겨울 (산장) | 2398×1792 PNG | 1.34 |

- 패키지의 webp는 긴 변 2048 · 품질 85 번들용(장당 330~430KB). 원본 PNG는 팀 보관소에.
- 팀 제공 에셋이며 제3자 캐릭터 포함 — 배포 시 권리 처리·교체 판단은 팀 몫. 교체는 같은 파일명으로.
- 배치: Compose Multiplatform 공통 리소스(`composeResources/drawable/`), Android는 nodpi 취급.

## fit-in-view 규칙 (비율이 달라도 딱 맞게 — v1과 동일)

모든 사분면은 **ContentScale.Crop + BiasAlignment** 하나로 통일: 왜곡 없이 확대해 셀을 가득 채우고 넘치는 부분을 잘라낸다. 레터박스·스트레치 금지.

```kotlin
Image(
    painter = painterResource(season.res),
    contentDescription = season.label,
    contentScale = ContentScale.Crop,
    alignment = BiasAlignment(0f, season.focusY),
    modifier = Modifier.fillMaxSize().clipToBounds(),
)
```

CSS `object-position` y% ↔ `verticalBias` 환산: `bias = (y% − 50) / 50`.

| 계절 | focus y% (Desktop) | verticalBias | Phone |
| --- | --- | --- | --- |
| 봄·여름 (1.79) | 50% | 0f | 50% / 0f |
| 가을·겨울 (1.34) | 55% | +0.1f | 58% / +0.16f |

교체 이미지는 중앙 1.6:1(Phone 1.85:1) 안에 핵심 피사체가 들어오면 어떤 비율이든 안전.

## 배치와 경계 (v2에서 바뀜)

- Desktop: 2×2 그리드 (좌상 봄 · 우상 여름 · 좌하 가을 · 우하 겨울). **셀 사이 3dp `ink` 경계선** — 루트 배경을 ink로 깔고 grid gap 3으로 만든다 (v1의 하드 엣지 맞닿음 → v2는 잉크 프레임).
- Phone: 세로 4단 (봄→여름→가을→겨울, 각 ≈211dp), 같은 3dp ink 경계.
- 창 비율 변화에도 규칙 동일 — crop 범위만 달라진다.

## 중앙 로고·메뉴 스택 (배경 박스 없음 — v2 잉크 마감)

정중앙 `Column(CenterHorizontally)`, 폭 Desktop 360 / Phone 310, gap 24 (워드마크 블록↔버튼 스택).

| 요소 | 스펙 |
| --- | --- |
| 워드마크 "Tabiji" | Sora 700, Desktop 58sp/64 (Phone 46/52), 자간 −0.02em, **흰 글자 + ink 아웃라인 2dp 상당 + 하드 섀도 (3,3) ink** + 가독 보조로 아래쪽 soft shadow (0,10,24, #000 35%) 한 장 허용 (유일한 블러 예외) |
| 부제 "여행(旅路) 도우미" | Jua 17sp (한자는 Pretendard JP 폴백), 자간 0.1em, 같은 처리 축소판 (아웃라인 1.5dp, 섀도 (2,2) ink) |
| 버튼 스택 | gap 12, 세로 나열 |

아웃라인 글자의 Compose 구현: 같은 텍스트 2벌 — 아래에 `drawStyle = Stroke(width = 4dp.toPx(), join = Round)` + ink, 위에 Fill 흰색. (CSS의 4방향 text-shadow와 동등 효과.)

버튼 행 (아이콘 좌 22/20 · 제목+부제 · 쉐브론 우):

1. **시작하기** — 높이 58 (Phone 56), `accent` 채움 + 흰 글자 (제목 Jua 18, 부제 "새 여행 일정 만들기" Pretendard JP 11 w600 85%), 2dp ink, 하단 섀도 **(0,4) ink**, chamfer 8 (03_components.md의 패스)
2. **내 여행** — 높이 52 (Phone 50), `card` 채움 + ink 글자 (제목 Jua 16, 부제 "저장된 일정 보기" 11 `sub`), 2dp ink, (0,4) ink, radius 10 (chamfer 없음)
3. **설정** — 내 여행과 동일, 부제 "테마 · 언어 · 데이터"

v1의 글래스 버튼 폐기 — 세 버튼 모두 불투명 면. 로고 가독성은 아웃라인+섀도가 보장하므로 이미지와의 대비 걱정 없음.

## 파티클·패럴랙스·시퀀스 (v1 수치 그대로)

- 파티클: 봄 벚꽃잎 8/4 · 여름 반짝임 6/3 · 가을 낙엽 8/4 · 겨울 눈송이 12/6 (Desktop/Phone), `Canvas` + `withFrameNanos`, seed 고정.
- 패럴랙스: 이미지 ±4dp, 파티클 ±8dp, 240ms 감속.
- 시퀀스: 0–600ms 사분면 순차 페이드인 (각 300ms, stagger 150) → 600–1200ms 스택 12dp 상승 + alpha (워드마크→부제→버튼 80ms 스태거) → 루프. 동작 줄이기 설정 시 전부 끄고 정지 프레임.
- 호버/터치한 사분면 밝기 +8%, 나머지 −6% (120ms).

## 테마와의 관계

인트로 이미지는 테마와 무관하게 항상 4계절 모두 표시한다. 테마가 바꾸는 것은 ink/accent 등 스택·경계의 역할색뿐. (사계절 테마를 골라도 인트로는 4분면 유지 — 테마는 앱 내부 화면의 팔레트다.)
