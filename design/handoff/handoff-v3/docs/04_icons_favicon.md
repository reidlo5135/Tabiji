# 04 · 플랫폼 아이콘 · 파비콘 v3 — 한지 타일 위 붓글씨 旅

> handoff-v2 `docs/04_icons_favicon.md`를 **전부 덮어쓴다.**

마크는 **한지색 둥근 타일 위에 먹으로 쓴 旅 한 글자**다. 글자는 Ma Shan Zheng(해서 붓글씨) 글리프 윤곽이다. 타일이 배경을 만들기 때문에 어두운 독·밝은 독·브라우저 탭 어디서나 같은 모습이다. v2의 "배경 없이 글자만" 원칙은 폐기한다 (Ubuntu 어두운 독에서 안 보였음, 요청서 참고).

## 치수와 색 (타일 100 기준)

| 요소 | 값 |
| --- | --- |
| 타일 면 | 한지 `#F3EBDD` |
| 타일 테두리 | 먹 `#1C1814`, 두께 2.7 (작은 크기에서도 최소 1px) |
| 타일 여백 · 모서리 | 사방 2.7 · 둥글기 16.4 |
| 글자 | 먹 `#1C1814`, 글리프 긴 변 73, 가운데 정렬 |

## 파일 (`assets/logo/icon/`)

| 파일 | 쓰임 |
| --- | --- |
| `tabi_brush_tile.svg` | 마스터 (벡터). 다른 크기는 여기 또는 생성기에서 |
| `tabi_brush_glyph.svg` | 글자만 (먹, 투명 배경). 마케팅·문서용 |
| `tabi_tile_{16,24,32,48,64,128,256,512,1024}.png` | 크기별 PNG. **각 크기에서 직접 렌더링**했다 (큰 이미지 축소 아님) |
| `desktop-window-512.png` | Desktop 창·설치 패키지 아이콘 (`tabi_tile_512.png`와 같음) |
| `favicon.svg` · `favicon.ico`(16·32·48) | 웹 파비콘 |
| `apple-touch-icon-180.png` | iOS 터치 아이콘. **정사각 한지 면** (모서리는 iOS가 깎으므로 타일 테두리·둥글기 없음) |
| `ic_launcher_tabi_foreground.svg` · `ic_launcher_tabi_background.svg` | Android adaptive icon. 배경 = 한지 단색, 전경 = 먹 글리프 (108 캔버스, 66 안전영역 안 56) |

## 플랫폼별

| 플랫폼 | 구성 |
| --- | --- |
| **Desktop 창·작업표시줄** | `desktop-window-512.png` → Compose `Window(icon = …)` |
| **Desktop 설치 패키지** | Linux `.deb`: 512 PNG / Windows `.msi`: 16~256 ICO / macOS `.dmg`: 1024에서 만든 ICNS |
| **Android 런처** | 배경 레이어 = 한지 단색, 전경 = 먹 글리프, monochrome = 전경과 같음. 런처가 원·사각으로 잘라도 글리프는 안전영역 안 |
| **Android 13+ 테마 아이콘** | monochrome 레이어 (시스템이 틴트) |
| **웹** | `<link rel="icon" href="/favicon.svg" type="image/svg+xml">` + `<link rel="alternate icon" href="/favicon.ico">` |
| **iOS / 애플 터치** | `apple-touch-icon-180.png` |

## 재생성

```bash
python3 tools/gen_brush_icon.py assets/fonts/MaShanZheng-Regular.ttf .
```

Pillow·fontTools가 필요하다. 색·치수를 바꾸려면 스크립트 맨 위 상수를 고친다.
