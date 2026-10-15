# Desktop 설치 패키지 아이콘

`nativeDistributions`(jpackage)가 설치 파일에 넣는 앱 아이콘이다. 창 안의 아이콘(`Window(icon = …)`)은 `shared`의 Compose Resources(`drawable/app_icon.png`)를 쓰고, 이 폴더는 설치 패키지 전용이다.

| 파일 | OS | 만든 방법 |
| --- | --- | --- |
| `tabiji.png` | Linux (`.deb`) | `design/handoff/handoff-v2/assets/logo/icon/desktop-window-512.png` 그대로 복사 |
| `tabiji.ico` | Windows (`.msi`) | 위 512px PNG에서 Pillow로 16·24·32·48·64·128·256px 묶음 생성 |
| `tabiji.icns` | macOS (`.dmg`) | 위 512px PNG에서 Pillow로 생성 |

- 旅 글리프, 투명 배경, 진한 글자색 `#1C2633` (handoff-v2 `docs/04_icons_favicon.md`의 Desktop 기본)
- 어두운 독·작업표시줄에서 글자가 묻히면 `desktop-window-512-halo.png`(흰 테두리)로 바꾸는 것이 핸드오프의 선택지다 (팀 결정)
- `.ico`·`.icns`의 작은 크기는 512px을 축소해 만들었다. 핸드오프는 16px 선명도를 위해 타깃 크기에서 직접 렌더링하라고 권한다. 작은 크기가 뭉개지면 그 파이프라인으로 다시 만든다
- 원본 글리프가 바뀌면 세 파일을 다시 만든다
