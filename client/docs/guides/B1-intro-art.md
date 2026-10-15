# B1 — 인트로 실제 일러스트: 플레이스홀더 교체와 초점 다시 잡기

> 브랜치: `client/feat-b1-intro-art` (Claude가 만들어 둠, 디자인 PR #20의 handoff-v5를 합쳐 둠) · 커밋 footer `Refs: B1`
> 시각 기준: handoff-v5 `docs/10_intro.md`(에셋·fit 규칙), `assets/intro/*.webp`. 배치·경계·스택은 handoff-v2 그대로
> 확인은 **Desktop**에서 한다 (기본 창 · 1440×900 · Phone 390×844 프리셋)

## 목표

A6부터 써 온 격자·십자선 플레이스홀더를 **캐릭터 없는 실제 사계절 일러스트**로 바꾼다.

| 칸 | 그림 |
| --- | --- |
| 봄 | 일본 벚꽃 축제 거리 |
| 여름 | 싱가포르 마리나베이 |
| 가을 | 경주 불국사 단풍 |
| 겨울 | 하얼빈 빙설대세계 |

그리고 **계절마다 초점을 다시 잡는다.** 새 그림은 네 장 모두 1.79:1이다. 예전 가을·겨울(1.34:1)에 맞춘 초점 값(Desktop 0.1, Phone 0.16)은 이제 맞지 않는다.

코드는 거의 바뀌지 않는다. 이번 단계에서 익히는 건 "**같은 이름의 리소스 파일만 바꾸면 화면이 바뀌는 구조**"와 "**`Crop`이 어느 축을 자르는지는 두 비율이 정한다**"는 것이다.

> 그림은 사용자가 직접 만든 캐릭터 없는 버전이다 (`design/requests/2026-10-15-intro-art-no-characters.md`). 이제 레포에 커밋하고 설치 파일에도 넣어 배포한다.

---

## 1. 핵심 개념

### 1-1. 리소스 교체 = 파일만 바꾸면 끝

A6에서 그림이 화면까지 오는 길을 이렇게 만들어 두었다.

```
composeResources/drawable/intro_autumn.webp      ← 파일 (이번에 내용만 바꿈)
└── Res.drawable.intro_autumn                    ← 빌드 때 자동 생성되는 표지 (이름이 같으니 그대로)
    └── Season.AUTUMN.image                      ← enum이 표지를 들고 있음 (그대로)
        └── painterResource(season.image)        ← 화면을 그릴 때 실제 그림을 읽음 (그대로)
            └── Image(contentScale = Crop, alignment = BiasAlignment(0f, focusY))
```

파일 이름이 같으면 `Res.drawable.intro_autumn`도 같다. 그래서 Kotlin 코드는 한 줄도 안 바뀐다. React 프로젝트에서 `import autumn from './intro_autumn.webp'`를 그대로 두고 파일만 바꿔 끼우는 것과 같다. 플레이스홀더를 같은 이름·같은 비율로 만들어 둔 이유가 이것이다.

### 1-2. `Crop`은 어느 축을 자르나 — 두 비율을 비교한다

`ContentScale.Crop`은 "그림 비율을 유지한 채 칸을 빈틈없이 덮을 때까지 키우고, 넘치는 쪽을 자른다"였다 (A6). **넘치는 쪽이 어디냐**는 그림 비율과 칸 비율 중 무엇이 더 넓은지로 정해진다.

| 비교 | 그림을 키우는 기준 | 잘리는 쪽 | 의미 있는 bias |
| --- | --- | --- | --- |
| 칸이 그림보다 **넓다** (칸 비율 > 1.79) | 칸 폭에 맞춤 → 그림이 칸보다 높아짐 | **위아래** | `verticalBias` |
| 칸이 그림보다 **좁다** (칸 비율 < 1.79) | 칸 높이에 맞춤 → 그림이 칸보다 넓어짐 | **좌우** | `horizontalBias` |

CSS `object-fit: cover`와 똑같은 규칙이다. 잘리지 않는 축의 bias는 아무 효과가 없다. 움직일 여유가 없기 때문이다.

우리 앱의 세 크기를 대략 계산하면 이렇다 (창 제목줄 약 37 · 메뉴바 약 22 · 경계 3dp를 뺀 근삿값).

| 창 | 칸 하나 | 칸 비율 | 잘리는 쪽 |
| --- | --- | --- | --- |
| 기본 창 (1920×1080 모니터의 85%×75%, 약 1632×790) | 약 814×364 | **약 2.2** | 위아래 (그림 높이의 약 20%) |
| Debug ▸ Desktop 1440×900 | 약 718×419 | **약 1.7** | 좌우 (그림 폭의 약 4%) |
| Debug ▸ Phone 390×844 (세로 4단) | 약 390×194 | **약 2.0** | 위아래 (약 11%) |

결론적으로 `verticalBias`가 효과를 내는 건 **기본 창과 Phone**이고, 1440×900에서는 좌우만 조금 잘린다. 그래서 초점은 기본 창과 Phone 프리셋에서 보며 정한다.

### 1-3. 그림을 보고 초점 정하기

`verticalBias`는 −1(위 끝을 남김) ~ 0(가운데) ~ +1(아래 끝을 남김)이다 (A6). 새 그림의 핵심 피사체 위치는 이렇다.

| 계절 | 핵심 피사체 | 세로 위치 |
| --- | --- | --- |
| 봄 | 노점 거리와 토리이, 걷는 사람들 | 가운데 |
| 여름 | 멀라이언과 마리나베이 샌즈 | 가운데 |
| 가을 | 불국사(가운데)와 **한복 입은 인물들(아래쪽)** | 가운데~아래 |
| 겨울 | 얼음 성(가운데)과 **아이들·눈사람(아래쪽)** | 가운데~아래 |

- 출발점은 네 장 모두 `0f`다 (handoff-v5 기준값)
- 가을·겨울은 기본 창에서 아래쪽 인물의 머리·발이 잘리는지 본다. 잘리면 양수 쪽으로 조금(`0.2f` 단위) 옮긴다. 대신 위쪽(지붕·성의 첨탑)이 그만큼 더 잘린다. 둘 다 살리는 값을 찾는다
- Phone은 잘리는 양이 11% 정도로 기본 창(20%)보다 적다. Desktop 값보다 작은 값이거나 `0f`로 충분한 경우가 많다

`Season` enum이 이미 `desktopFocusY`·`phoneFocusY` 두 값을 들고 있으니(A7), 값만 바꾸면 된다.

### 1-4. 그림 크기와 메모리

`painterResource`는 webp를 **풀어서(디코딩)** 픽셀 배열로 메모리에 올린다. 2048×1143 그림 한 장은 픽셀당 4바이트라 약 9.4MB이고, 네 장이면 약 37MB다. 파일 크기(장당 360~570KB)와 상관없이, **화면에 올라가는 크기는 해상도가 정한다.** Desktop에는 충분히 감당할 만한 양이다. 메모리가 적은 Android 기기를 다룰 때(Phase M2)는 화면 밀도별로 작은 사본(`drawable-xxhdpi/` 같은 한정자 폴더)을 두는 것을 검토한다. 이번 단계에서는 바꾸지 않는다.

---

## 2. 만들 것

| 경로 | 바꾸는 것 |
| --- | --- |
| `shared/src/commonMain/composeResources/drawable/intro_{spring,summer,autumn,winter}.webp` | 플레이스홀더 → handoff-v5 그림 (파일 복사) |
| `shared/…/feature/intro/Season.kt` | 네 계절의 `desktopFocusY`·`phoneFocusY` 값 |
| `shared/…/feature/intro/IntroMenuButton.kt` | `// TODO(B1): 아이콘` → `// TODO(B2): 아이콘` (로드맵 번호가 하나씩 밀림) |
| `shared/…/feature/intro/IntroScreen.kt` | `// TODO(B3): 내비게이션` → `// TODO(B4): 내비게이션` (세 곳) |

import 변화는 없다. 레이아웃도 안 바뀌어서 이번 단계에는 트리 그림 대신 1-1의 "파일 → 화면" 흐름도를 쓴다.

> 로드맵 변경: Phase B 맨 앞에 이 단계(B1)를 넣고 기존 B1~B9를 B2~B10으로 밀었다. 플래너 정적 화면은 B2, 내비게이션은 B4, 설정 화면은 B7이다.

---

## 3. 순서

### 3-1. 그림 복사

```bash
cd ~/ws/src/Tabiji/client
git switch client/feat-b1-intro-art && git pull
for s in spring summer autumn winter; do
  cp ../design/handoff/handoff-v5/assets/intro/$s.webp shared/src/commonMain/composeResources/drawable/intro_$s.webp
done
```

✅ 확인: 앱을 다시 띄우면(새 리소스라 Hot Reload가 아니라 재시작) 네 칸에 실제 그림이 나온다. 이 시점의 초점은 A7 값 그대로라 가을·겨울이 살짝 아래로 치우쳐 있을 수 있다.

### 3-2. 초점을 0으로 되돌리고 세 크기에서 보기

`Season.kt`의 네 계절을 모두 `desktopFocusY = 0f, phoneFocusY = 0f`로 바꾼다. 그리고 세 크기에서 본다.

1. **기본 창** (앱을 띄운 그대로): 위아래가 약 20% 잘린다. 가을의 한복 인물, 겨울의 아이들 발이 잘리는지 본다
2. **Debug ▸ Desktop 1440×900**: 좌우가 조금 잘린다. 여름의 멀라이언(왼쪽)이 남는지 본다. 세로 초점은 여기선 효과가 없다
3. **Debug ▸ Phone 390×844**: 세로 4단, 위아래가 약 11% 잘린다

### 3-3. 필요한 계절만 조정

1-3 표를 기준으로 가을·겨울의 `desktopFocusY`를 `0.2f`씩 옮겨 가며 기본 창에서 비교한다. Hot Reload로 하면 빠르다 (`Season.kt`는 Kotlin 코드라 저장하면 바로 반영된다). 아래 인물과 위 지붕·첨탑이 둘 다 충분히 보이는 값을 고른다. Phone 값은 Phone 프리셋에서 같은 방법으로 정한다.

### 3-4. TODO 번호 정리

2장 표대로 TODO 주석의 단계 번호를 바꾼다. 로드맵 번호가 밀렸으니, 나중에 해당 단계를 시작할 때 `grep -rn "TODO(B2)"`로 찾을 수 있게 맞춰 두는 것이다.

### 3-5. 커밋할 그림이 handoff와 같은지 확인

```bash
cd ~/ws/src/Tabiji
for s in spring summer autumn winter; do
  sha256sum design/handoff/handoff-v5/assets/intro/$s.webp client/shared/src/commonMain/composeResources/drawable/intro_$s.webp
done
```

같은 계절끼리 해시가 같아야 한다. 이제 이 그림들은 **커밋 대상**이다. A6 때처럼 되돌릴 필요가 없다.

---

## 4. 흔한 문제와 원인

| 증상 | 원인 |
| --- | --- |
| 복사했는데 여전히 플레이스홀더 | 앱을 재시작하지 않음 (리소스 파일 변경은 Hot Reload가 반영 못 함) 또는 파일 이름이 `intro_` 없이 들어감 |
| 빌드가 `Unresolved reference: intro_spring` | 파일 이름이 `spring.webp`로 복사됨. `intro_spring.webp`여야 `Res.drawable.intro_spring`이 생긴다 |
| `desktopFocusY`를 바꿔도 1440×900에서 변화 없음 | 정상. 그 크기에서는 좌우가 잘려서 세로 bias가 효과가 없다 (1-2) |
| 기본 창에서도 변화가 거의 없음 | 모니터 비율에 따라 칸 비율이 1.79에 가까우면 잘리는 양이 적다. 창을 더 넓게 늘려 보면 차이가 커진다 |
| 그림이 흐릿하다 | 원본보다 크게 확대된 경우. 4K 모니터 전체 화면에서는 칸 폭이 2048px을 넘을 수 있다. 문제가 되면 원본 PNG(2752px)에서 다시 만들도록 디자인 쪽에 요청한다 |

## 5. 완료 조건 (모두 눈으로)

- [ ] 네 칸에 실제 일러스트 (봄 축제 · 여름 마리나베이 · 가을 불국사 · 겨울 빙설대세계)
- [ ] 기본 창 · 1440×900 · Phone 세 크기에서 핵심 피사체가 잘리지 않음 (특히 가을·겨울 아래쪽 인물)
- [ ] A4 선택 덮개, A5 테마 전환, A8 워드마크 아웃라인, A9 버튼이 그림 위에서 그대로 동작
- [ ] `intro_*.webp` 해시가 handoff-v5와 같음, TODO 번호 갱신
- [ ] PR의 `client-ci` 초록

## 6. 끝나면 붙여넣어 줄 것

1. 정한 초점 값 (`Season.kt` 변경분)
2. 스크린샷: 기본 창, 1440×900, Phone 프리셋 각 한 장
3. 3-5의 `sha256sum` 출력

→ 진단을 통과하면 Claude가 커밋(`feat(client): use the character-free intro illustrations`, `Refs: B1`)과 push를 하고, #20 머지 뒤 PR을 연다. 그림이 들어갔으니 릴리스(0.1.3 패치 또는 0.2.0과 함께)를 정한다. 그다음은 B2(플래너 정적 화면)다.
