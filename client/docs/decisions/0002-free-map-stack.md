# C-0002. 지도는 MapLibre Compose + OSM 벡터 타일(OpenFreeMap)로 한다

- 상태: 채택 (C1 스파이크 결과로 재확인)
- 날짜: 2026-10-13
- 결정한 사람: @reidlo5135
- 영역: client

## 맥락

- Tabiji의 핵심 화면은 지도 위의 방문지 마커와 구간 라인이다. Android와 Desktop(JVM)이 1차 타깃이고 iOS·Web이 뒤따른다
- 팀 프로젝트라 지도 사용량 과금·API 키 관리를 피하고 싶다. **클라이언트에 외부 API 키를 두지 않는다**는 원칙도 있다
- 경로·교통 정보는 서버가 맡으므로, 클라이언트 지도는 **타일 표시 + 마커·라인 그리기 + 카메라·클릭**만 하면 된다

## 선택지

| 선택지 | 장점 | 단점 |
| --- | --- | --- |
| Google Maps (Android SDK / Maps Compose) | 성숙, 자료 많음 | Desktop 미지원, API 키·과금, 멀티플랫폼 아님 |
| Mapbox | 품질 좋음 | 키·과금, Desktop 공식 SDK 없음 |
| **MapLibre Compose + OpenFreeMap 타일** | 오픈소스(BSD), 키 없음, Compose Multiplatform 공식 래퍼, Android·iOS·Desktop·Web 모두 목표 | Desktop·Web 지원이 Alpha. API가 아직 변한다 |
| WebView/JCEF + MapLibre GL JS | 웹 MapLibre는 매우 성숙 | 네이티브 Compose와 이벤트·상태 동기화가 번거롭다. JCEF 번들 크기 |

## 결정

**MapLibre Compose(`org.maplibre.compose`) + OpenFreeMap 벡터 타일**을 택한다.

- 2026-10-13 기준 MapLibre Compose 최신 릴리스는 **v0.20.0**(2026-10-10). 공식 README의 안정성 표: Android·iOS **Beta**, Desktop(JVM)·Web **Alpha**, "minor 릴리스에도 깨지는 변경이 있을 수 있다"
  - 출처: <https://github.com/maplibre/maplibre-compose> (README "Stability"), <https://github.com/maplibre/maplibre-compose/releases>
- 타일은 [OpenFreeMap](https://openfreemap.org/)(OSM 데이터, 키 없음). 지도 화면에 OSM 저작권 표기(© OpenStreetMap contributors)를 반드시 표시한다
- 라이브러리 버전은 C1을 시작할 때 다시 확인해 `libs.versions.toml`에 넣는다 (지금은 주석 자리만)

### 리스크와 대응

| 리스크 | 대응 |
| --- | --- |
| **Desktop(JVM)이 Alpha** — Compose·Skia 내부 구현에 기대고 있어 CMP 업그레이드 때 깨질 수 있다 | **C1 스파이크 게이트**: Android·Desktop 둘 다에서 지도·마커·GeoJSON 라인·카메라·클릭을 확인하기 전에는 C2로 넘어가지 않는다 |
| API 변경이 잦다 | 지도 타입은 `MapController` 인터페이스(`core-map`) 뒤에 숨긴다. feature 코드에서 MapLibre import 금지 |
| CMP·Kotlin 버전 궁합 | MapLibre Compose가 요구하는 Kotlin/CMP 버전과 우리 버전을 C1 때 대조. 어긋나면 지도 라이브러리 버전에 맞춘다 |
| OpenFreeMap 가용성 | 타일 URL을 설정값 한 곳에 둔다. 대체 타일 소스(자체 호스팅 PMTiles 등)로 바꿀 수 있게 |

### Desktop 대안 (C1 실패 시)

C1에서 Desktop 지도가 요구 사항을 못 맞추면 **Desktop만** 아래 대안으로 가고, 결과를 새 ADR로 남긴다.

- **JCEF(Java Chromium Embedded Framework) + MapLibre GL JS**: Desktop `actual MapController`가 내장 브라우저에 GL JS를 띄우고, 마커·라인·카메라 명령은 JS 브리지로, 클릭은 콜백으로 받는다
  - 장점: GL JS는 가장 성숙한 MapLibre 구현
  - 단점: 패키지 크기 증가(Chromium), Compose 위에 다른 UI 겹치기 제약, 브리지 코드 유지
- Android는 계속 MapLibre Compose를 쓴다. `MapController` 인터페이스 덕분에 feature 코드는 바뀌지 않는다

## 결과

- 쉬워지는 것: 키·과금 없음, 네 플랫폼을 같은 라이브러리로 노릴 수 있음
- 감수할 것: Desktop 지도 안정성, 라이브러리 업데이트 추적 비용
- 다시 검토할 조건: C1 실패, MapLibre Compose Desktop이 1년 넘게 Alpha에 머물 때, 타일 사용량이 OpenFreeMap 공정 사용 범위를 넘을 때
