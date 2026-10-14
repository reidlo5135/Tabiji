# 20 · 모션 — 이징 · 길이 · 규칙

## 원칙

- **바운스·오버슈트 금지.** 모든 전환은 감속(decelerate) 곡선 하나로 끝난다. 스프링을 쓰더라도 `dampingRatio = 1f` (임계 감쇠) 이상.
- 짧게. 사용자가 기다리는 느낌이 들면 안 된다. 240ms 를 넘는 것은 지도 카메라(600)와 인트로뿐.
- 레이아웃이 바뀌는 전환(시트 단계, 패널 접기, 리스트 재배치)은 **같은 240ms 에 함께** 움직인다. 서로 다른 길이로 어긋나지 않게.
- 동작 줄이기(`LocalAccessibilityManager` / 시스템 설정)가 켜지면 앰비언트·인트로는 0ms, 그 외 전환은 유지(길이가 짧고 의미 전달에 필요).

## 곡선 (cubic-bezier)

| 이름 | 값 | Compose |
| --- | --- | --- |
| decelerate (기본) | `cubic-bezier(0.2, 0, 0, 1)` | `CubicBezierEasing(0.2f, 0f, 0f, 1f)` |
| standard (카메라) | `cubic-bezier(0.4, 0, 0.2, 1)` | `CubicBezierEasing(0.4f, 0f, 0.2f, 1f)` |
| linear (파티클 낙하·시머) | `linear` | `LinearEasing` |
| ease-in-out (구름 왕복) | `cubic-bezier(0.45, 0, 0.55, 1)` | `CubicBezierEasing(0.45f, 0f, 0.55f, 1f)` |

```kotlin
object TabijiMotion {
    val Decelerate = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val Standard   = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val micro      = tween<Float>(120, easing = Decelerate)
    val transition = tween<Float>(240, easing = Decelerate)
    val camera     = tween<Float>(600, easing = Standard)
}
```

## 길이 표

| 키 | ms | 곡선 | 쓰는 곳 |
| --- | --- | --- | --- |
| micro | 120 | decelerate | 버튼 눌림(`scale 0.98/0.96`), 호버 그림자, 체크박스, 사분면 밝기, 절약 시간 숫자 전환 |
| transition | 240 | decelerate | 시트 단계, 패널 360↔56, 일차 접기/펼치기(쉐브론 회전 포함), 카드 선택 테두리, 마커 기본↔선택 확대, 오류 배너 등장, 모달 등장, 리스트 재배치, 드래그 놓기 |
| camera | 600 | standard | 지도 flyTo / fitBounds |
| introQuadrantFade | 300 (stagger 150) | decelerate | 사분면 페이드인 |
| introLogoRise | 600 (delay 600) | decelerate | 로고 패널 12dp 상승 + alpha |
| introHighlightSweep | 400 (delay 800) | linear | 하이라이트 라인 1회 스윕 |
| particleFall | 9000–12000 | linear | 벚꽃·낙엽 |
| snowFall | 11000–15000 | linear | 눈송이 |
| cloudDrift | 14000–18000 | ease-in-out, 왕복 | 구름 |
| glint | 2000–3000 | sine | 반사광 불투명도 |
| shimmer | 1200 | linear, 반복 | 스켈레톤 |
| spinner | 900 | linear, 반복 | 로딩 버튼 |
| hoverLift | 120 | decelerate | 여행 카드 호버 −2dp |
| avatarHop | 240 (−6dp) + 240 원복 | decelerate | 인트로 아바타 |

## 전환별 메모

- **바텀시트 드래그**: 손가락을 따라갈 때는 애니메이션 없음. 놓으면 가장 가까운 단계로 240ms 스냅. 속도 > 800dp/s 면 진행 방향의 다음 단계.
- **패널 접기 (Desktop)**: 폭 360→56 과 지도 컨트롤 right 408→104, MapLibre `padding` 변경을 같은 240ms 에. 패널 내용은 처음 120ms 에 alpha 1→0 으로 먼저 사라진다.
- **마커 선택**: 크기 32×40 → 40×50 를 앵커(아래 꼭짓점) 고정으로 확대. 이전 선택은 동시에 축소.
- **리스트 재배치 (AI 수락)**: `LazyColumn` `Modifier.animateItemPlacement(tween(240))`. 칩은 카드 뒤에 100ms 늦게 페이드인.
- **드래그 중 카드**: 집는 순간 120ms 로 scale/그림자, 놓으면 240ms 로 자리에 안착. 자리 양보는 다른 카드들이 240ms 로 밀린다.
- **오류 배너**: 위에서 높이 0→자연 높이 `expandVertically` 240ms, 콘텐츠는 그만큼 아래로 밀림(덮지 않음).
- **지도 카메라**: 카드 탭 → `flyTo(zoom 유지, center = 마커)` 600ms. 일차 변경 → `fitBounds(패딩 80)` 600ms.
