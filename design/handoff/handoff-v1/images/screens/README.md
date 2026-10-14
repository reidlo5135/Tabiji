# images/screens — 기준 스크린샷

이 폴더에는 PNG 가 아직 없다. 기준 이미지는 Claude Design 캔버스에서 내보낸다 (아트보드 선택 → Share › Export → PNG). 파일명 규칙은 아래와 같으며 PROMPT.md 와 docs/ 는 이 이름으로 참조한다.

```
<screen>_<desktop|phone>_<light|dark>.png
```

| 아트보드 | 파일명 |
| --- | --- |
| ① 인트로 · Desktop · Light / Dark | intro_desktop_light.png / intro_desktop_dark.png |
| ① 인트로 · Phone · Light / Dark | intro_phone_light.png / intro_phone_dark.png |
| ② 플래너 · Desktop (Main) · Light / Dark | planner_desktop_light.png / planner_desktop_dark.png |
| ② 플래너 · Phone · Light / Dark | planner_phone_light.png / planner_phone_dark.png |
| ③ 여행 목록 · Desktop / Phone · Light / Dark | trips_desktop_*.png / trips_phone_*.png |
| ④ AI 제안 시트 · Desktop / Phone · Light / Dark | ai_sheet_desktop_*.png / ai_sheet_phone_*.png |
| ⑤ 공통 상태 · Desktop / Phone · Light / Dark | states_desktop_*.png / states_phone_*.png |
| 컴포넌트 · Light / Dark | components_light.png / components_dark.png |
| 아이콘 세트 | icons.png |
| 로고 · 워드마크 3안 + 앱 아이콘 | logo.png |
| 토큰 시트 | tokens.png |
| 인트로 스토리보드 | storyboard.png |

PNG 없이도 `source/*.html` 을 브라우저로 열면 같은 화면이 나온다 (README.md 참고). 구현 비교용 스크린샷은 `<name>_impl.png` 로 옆에 저장한다.
