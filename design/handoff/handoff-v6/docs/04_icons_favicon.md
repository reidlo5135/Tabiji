# 04 · 플랫폼 아이콘 · 파비콘 v6 — 한지색 旅 + 검은 테두리

> handoff-v4 `docs/04_icons_favicon.md`(와 그것이 덮어쓴 v3·v2 04)를 **전부 덮어쓴다.**

마크는 **배경 없이 旅 한 글자**다. 글자는 Pretendard JP Bold 글리프 윤곽이고, 획 안은 한지색 `#F3EBDD`, 획 둘레는 먹색 `#1C1814` 테두리다. 어두운 독에서는 밝은 획이, 밝은 배경에서는 검은 테두리가 형태를 잡는다.

## 치수와 색 (캔버스 100 기준)

| 요소 | 값 |
| --- | --- |
| 글리프 긴 변 | 80, 가운데 정렬 |
| 획 | `#F3EBDD` |
| 테두리 | `#1C1814`, 획 바깥으로 3 (SVG `stroke-width="6"` + `paint-order="stroke"`, 작은 크기에서도 최소 1px), 모서리 둥글게 |

## 파일 (`assets/logo/icon/`)

| 파일 | 쓰임 |
| --- | --- |
| `tabi_hanji_glyph.svg` | 마스터 (벡터, 투명) |
| `tabi_hanji_{16,24,32,48,64,128,256,512,1024}.png` | 크기별 PNG. **각 크기에서 직접 렌더링** |
| `desktop-window-512.png` | Desktop 창·설치 패키지 아이콘 (`tabi_hanji_512.png`와 같음) |
| `favicon.svg` · `favicon.ico`(16·32·48) | 웹 파비콘 (투명) |
| `apple-touch-icon-180.png` | iOS 터치 아이콘. 흰 정사각 (iOS가 투명을 검게 칠하므로) |
| `ic_launcher_tabi_foreground.svg` · `ic_launcher_tabi_background.svg` | Android adaptive. 배경 = 흰 단색(규격상 필수), 전경 = 테두리 있는 글리프 (108 캔버스, 66 안전영역 안 56). monochrome = 전경 |

## 플랫폼별

| 플랫폼 | 구성 |
| --- | --- |
| Desktop 창·작업표시줄 | `desktop-window-512.png` → `Window(icon = …)` |
| Desktop 설치 패키지 | Linux 512 PNG / Windows 16~256 ICO / macOS ICNS |
| Android 런처 · 13+ 테마 아이콘 | 위 adaptive 레이어 |
| 웹 | `favicon.svg` + `favicon.ico` |
| iOS | `apple-touch-icon-180.png` |

## 재생성 (레포 루트에서)

```bash
python3 design/handoff/handoff-v6/tools/gen_hanji_icon.py \
  client/shared/src/commonMain/composeResources/font/pretendardjp_bold.ttf design/handoff/handoff-v6
```
