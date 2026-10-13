# 30 · 지도 스타일 — MapLibre 레이어별 색 지정표 (실버 톤)

대상: MapLibre GL + OpenFreeMap 벡터 타일(OpenMapTiles 스키마). OpenFreeMap의 `positron` 스타일을 베이스로 **색만 바꾼다** — 레이어 구조·줌 규칙·라벨 배치는 건드리지 않는다. `assets/map/map_light.svg`, `map_dark.svg` 가 같은 색으로 그린 모형이다.

## 얼마나 바꿀 것인가 (열린 질문 답)

**"Positron 수준의 저채도 + 실버 틴트"** 를 제안한다. 지도 자체를 은색 UI처럼 만들면 도로·물·공원 구분이 사라져 가독성이 떨어진다. 기준:

- 땅은 UI 배경(`bg.sky.bottom`)보다 **살짝 어둡게**(#F1F4F7) — 글래스 패널이 지도 위에 떠 보이려면 지도가 패널보다 어두워야 한다.
- 물은 유일하게 색조가 분명한 레이어(연한 하늘색). 공원은 아주 연한 녹색. 둘 다 채도를 낮추되 **땅과의 명도 차 ≥ 0.06** 유지.
- 도로는 땅보다 밝은 흰색(라이트) / 땅보다 밝은 회청(다크)으로, 등급별로 두께만 다르게 — 색으로 등급을 나누지 않는다.
- 라벨은 `text.secondary` 계열. 경로 라인(accent·transit·drive)이 **지도에서 가장 채도가 높은 요소**가 되도록 지도 안의 어떤 색도 채도 0.15 를 넘지 않는다.
- 다크는 "회색을 어둡게"가 아니라 `bg.sky.top`(#0E1621) 계열의 **남색 기반**으로 맞춰 UI 배경과 이어진다.

## 색 지정표

| 레이어 (OpenMapTiles) | 라이트 | 다크 | 비고 |
| --- | --- | --- | --- |
| background / landcover (land) | #F1F4F7 | #141C26 | UI 배경보다 한 단계 어둡게 |
| water, waterway | #D3E4F1 | #102538 | 채도 가장 높은 지도 색 (≈0.12) |
| landuse park / grass / wood | #E2EDE4 | #172620 | |
| landuse residential / commercial | #EEF1F4 | #16202B | land 와 거의 같게 |
| building | fill #E6EAEF · outline #DCE2E9 | fill #1B2531 · outline #243040 | 줌 ≥ 15 |
| road minor / service / path | casing #DCE3EA 7 · fill #FFFFFF 5 | casing #2A3644 7 · fill #1F2A37 5 | 폭은 positron 기본값 유지, 색만 |
| road secondary / tertiary | casing #CFD8E1 · fill #FFFFFF | casing #344255 · fill #283446 | |
| road primary / trunk / motorway | casing #CFD8E1 14 · fill #FFFFFF 10 | casing #344255 14 · fill #283446 10 | 고속도로도 노란색 금지 — 두께로만 |
| railway | #C9D1DB dash 14/10 | #3A4655 dash 14/10 | |
| boundary (admin) | #B9C3CF dash | #3A4655 dash | 줌 ≤ 10 |
| label place (city/town) | #4A5668 · halo #F1F4F7 1.5 | #AEB9C7 · halo #141C26 1.5 | Pretendard JP 사용 (타일 폰트 글리프 대신 로컬 폰트 지정 가능하면) |
| label road | #7A8794 · halo #FFFFFF 1 | #8A98A8 · halo #1F2A37 1 | |
| label water | #6E8FA8 italic | #5C7D96 italic | |
| label poi | #5B6878 | #9AA7B6 | 줌 ≥ 15, 중요도 상위만 |

## 라벨 언어

`name:ko` 가 있으면 우선, 없으면 `name:latin` 뒤에 `name` (현지어)을 줄바꿈으로. 일본 여행지에서 "駿府城公園 / Sunpu Castle Park" 처럼 보이게. 식: `["coalesce", ["get","name:ko"], ["concat", ["get","name:latin"], "\n", ["get","name"]]]`.

## 앱 레이어 (지도 위에 Tabiji 가 얹는 것)

| 레이어 | 스타일 |
| --- | --- |
| route-walk (line) | `route.walk` (= `accent.line`, 라이트 #1A8FC6) · width 4 · dasharray [0.5, 2.25] (=2/9 at width 4) · round cap |
| route-transit (line) | `route.transit` · width 4 |
| route-drive (line) | `route.drive` · width 4 |
| route-uncomputed | 위 3개 중 해당 색 · opacity 0.45 |
| route-selected | width 6 |
| marker (symbol) | 03_components.md 마커 이미지 · anchor bottom · 번호 텍스트는 이미지에 포함 |
| my-location | 12 원 `accent.sky` + 흰 2dp 테두리 + 정확도 원 `accent.soft` |

## 카메라

- 초기: 일차 전체 `fitBounds(padding = 80dp, 패널 쪽은 + 패널 폭)`.
- 최대 줌 18, 최소 줌 4. 회전·기울기 비활성(v1).
- 다크 테마 전환 시 스타일을 교체(`setStyle`)하고 앱 레이어를 다시 올린다 — 전환 중 240ms 스크림으로 깜빡임을 가린다.
