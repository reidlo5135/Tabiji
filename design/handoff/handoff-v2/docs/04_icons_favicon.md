# 04 · 플랫폼 아이콘 · 파비콘 — 旅 글리프

마크는 **旅 한 글자, 배경색 없이 글자만**이다. 원칙: 어떤 배경색도 쓰지 않는다 — 투명이 허용되는 모든 곳(웹, Desktop)은 완전 투명이고, OS가 불투명 배경을 강제하는 두 곳(Android 런처, iOS 터치 아이콘)만 "무색"에 가장 가까운 **순수 흰색 단색**을 깐다. Pretendard JP Bold에서 아웃라인으로 뽑은 단색 글리프(폰트 임베드 아님 — 로고용 아웃라인 사용은 OFL 허용 범위)이고, 색은 두 가지만 쓴다: 라이트 표면 위 `#1C2633`(text.primary), 다크 표면 위 `#E9EEF4`. accent 변형(#2FA8E0)은 마케팅 전용이며 앱 아이콘에는 쓰지 않는다. 16px 실측에서 실루엣이 유지되는 것을 확인했다 (`images/screens/logo.png` 섹션 참고) — 16px이 하한선이고, 그보다 작은 곳엔 쓰지 않는다.

모든 파일: `assets/logo/icon/`. 글리프의 기하는 `tabi_glyph_*.svg` 하나에서 파생되므로, 교체·재생성 시 그 path만 바꾸면 된다.

## 플랫폼별 구성

| 플랫폼 | 파일 | 배경 | 비고 |
| --- | --- | --- | --- |
| **웹 favicon (기본)** | `favicon.svg` | 투명 | `prefers-color-scheme`로 글리프 색 자동 전환(#1C2633 ↔ #E9EEF4). 최신 브라우저 전부 지원 |
| 웹 폴백 | `favicon-16/32/48.png` → favicon.ico | 투명 | 다크 글리프 고정(라이트 탭 기준). `<link rel="icon" href="favicon.svg">` + `<link rel="alternate icon" href="favicon.ico">` |
| 웹 PWA | `favicon-192.png`, `favicon-512.png` (any) + `icon-maskable-512.png` (maskable) | any=투명 / maskable=**흰색 단색** (규격상 불투명 필수) | manifest `purpose: "any"` 와 `"maskable"` 분리 |
| **Android 런처** | `ic_launcher_tabi_foreground.svg` (전경) + `ic_launcher_tabi_background.svg` (배경, **#FFFFFF 단색**) | **배경 레이어 필수** — 흰색 | adaptive icon 규격: 108dp 캔버스, 글리프는 66dp 안전영역 안 56dp. "완전 배경 없음"은 런처 규격상 불가 |
| Android 13+ 테마 아이콘 | monochrome 레이어 = 전경과 같은 글리프 | 시스템이 틴트 | **여기서는 진짜 "글자만"** 으로 보인다 |
| **Desktop (창/작업표시줄)** | `desktop-window-512.png` (**기본: 투명 + 글자만**) | 투명 | 다크 작업표시줄에서 다크 글리프가 묻히면 `desktop-window-512-halo.png`(흰 할로 옵션)로 교체 — 팀 선택. Compose Desktop `Window(icon = painterResource(...))` |
| iOS (이후) / 애플 터치 | `apple-touch-icon-180.png` | **흰색 단색** (투명 불가 — iOS가 검정을 깔음) | |

## 웹 head 스니펫

```html
<link rel="icon" href="/favicon.svg" type="image/svg+xml">
<link rel="alternate icon" href="/favicon.ico" sizes="16x16 32x32 48x48">
<link rel="apple-touch-icon" href="/apple-touch-icon-180.png">
<link rel="manifest" href="/manifest.webmanifest">
```

manifest 아이콘: `favicon-192.png`/`favicon-512.png` (`"purpose": "any"`), `icon-maskable-512.png` (`"purpose": "maskable"`).

## Android 리소스 구성

```
res/mipmap-anydpi-v26/ic_launcher.xml
  <adaptive-icon>
    <background android:drawable="@drawable/ic_launcher_tabi_background"/>   <!-- 흰색 단색 -->
    <foreground android:drawable="@drawable/ic_launcher_tabi_foreground"/>
    <monochrome android:drawable="@drawable/ic_launcher_tabi_foreground"/>
  </adaptive-icon>
```

SVG 2종은 Android Studio의 Vector Asset(SVG import)으로 VectorDrawable 변환. 기존 "경로+마커" 심볼(`assets/logo/ic_launcher_foreground.svg`, `app_icon_desktop_512.svg`)은 **대안으로 보존** — 스토어 그래픽 등 글자가 통하지 않는 해외 노출면에서 쓸 수 있다.

## 재생성 파이프라인

글리프 교체(굵기·폰트 변경) 시: Pretendard JP Bold에서 U+65C5 아웃라인 추출 → 100 유닛 박스 정규화 → 각 산출물로 전개. PNG는 타깃 크기에서 직접 렌더링(힌팅 유지)하며, 큰 이미지를 축소하지 않는다 — 16px 선명도가 달라진다.
