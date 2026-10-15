# Desktop 설치 패키지 아이콘

`nativeDistributions`(jpackage)가 설치 파일에 넣는 앱 아이콘이다. 창 안의 아이콘(`Window(icon = …)`)은 `shared`의 Compose Resources `drawable/app_icon.png`를 쓴다. 둘 다 같은 512px 타일이다.

원본은 **handoff-v3**(`design/handoff/handoff-v3/assets/logo/icon/`)의 "한지 타일 위 붓글씨 旅"다. v2의 투명 배경 고딕 旅는 Ubuntu 어두운 독에서 안 보여서 바꿨다 (`design/requests/2026-10-15-icon-brush-hanji.md`).

| 파일 | OS | 만든 방법 |
| --- | --- | --- |
| `tabiji.png` | Linux (`.deb`) | `tabi_tile_512.png` 그대로 복사 |
| `tabiji.ico` | Windows (`.msi`) | `tabi_tile_{16,24,32,48,64,128,256}.png`을 Pillow로 한 파일에 묶음 (각 크기는 핸드오프에서 직접 렌더링한 것) |
| `tabiji.icns` | macOS (`.dmg`) | `tabi_tile_{16…1024}.png`을 Pillow로 묶음 |

아이콘이 바뀌면 핸드오프 `tools/gen_brush_icon.py`로 다시 만들고, 이 세 파일과 `app_icon.png`를 교체한다.
