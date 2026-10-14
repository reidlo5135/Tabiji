# CONTRAST — WCAG 대비 검증 (tools/contrast_check.py 자동 생성)

글래스 표면은 지도 최악 배경(라이트 건물 #E6EAEF, 다크 물 #102538) 위에 알파 합성한 뒤 측정. 본문 4.5:1, 큰 글자·그래픽 3:1 기준.

| 테마 | 조합 | 전경 | 배경(합성 후) | 비율 | 기준 | 결과 | 비고 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| light | text.primary on glass.solid (92%) over worst map | #1C2633 | #FDFDFE | 15.03:1 | 4.5:1 | PASS |  |
| light | text.primary on glass (72%) over worst map | #1C2633 | #F8F9FB | 14.50:1 | 4.5:1 | PASS | blur 표면 |
| light | text.secondary on glass.solid over worst map | #5B6878 | #FDFDFE | 5.59:1 | 4.5:1 | PASS |  |
| light | text.secondary on glass (72%) over worst map | #5B6878 | #F8F9FB | 5.39:1 | 4.5:1 | PASS | blur 표면 |
| light | text.secondary on card on glass | #5B6878 | #FFFFFF | 5.68:1 | 4.5:1 | PASS |  |
| light | text.primary on chip | #1C2633 | #FFFFFF | 15.28:1 | 4.5:1 | PASS |  |
| light | text.secondary on bg.sky.bottom | #5B6878 | #F7F9FC | 5.39:1 | 4.5:1 | PASS |  |
| light | accent.on on accent.sky (primary button) | #0E1621 | #2FA8E0 | 6.75:1 | 4.5:1 | PASS |  |
| light | WHITE on accent.sky (rejected) | #FFFFFF | #2FA8E0 | 2.69:1 | 4.5:1 | FAIL | 시작 토큰 조합 — 미달 |
| light | accent.text on glass.solid over worst map | #1A78A8 | #FDFDFE | 4.81:1 | 4.5:1 | PASS |  |
| light | accent.text on card | #1A78A8 | #FFFFFF | 4.89:1 | 4.5:1 | PASS |  |
| light | #1C2633 on status.estimated badge | #1C2633 | #E3A23B | 6.91:1 | 4.5:1 | PASS |  |
| light | status.estimated as TEXT on card (rejected) | #E3A23B | #FFFFFF | 2.21:1 | 4.5:1 | FAIL | 글자색 금지 |
| light | status.error as TEXT on error.soft (rejected) | #D9534F | #F9E8E9 | 3.35:1 | 4.5:1 | FAIL | 글자 금지 — 아이콘·테두리 전용 |
| light | status.error icon on error.soft (graphic 3:1) | #D9534F | #F9E8E9 | 3.35:1 | 3.0:1 | PASS |  |
| light | status.error.text on card (input message) | #B23A36 | #FFFFFF | 5.91:1 | 4.5:1 | PASS |  |
| light | status.error.text on error.soft over glass | #B23A36 | #F9E8E9 | 5.00:1 | 4.5:1 | PASS |  |
| light | accent.line selected border on card (graphic 3:1) | #1A8FC6 | #FFFFFF | 3.63:1 | 3.0:1 | PASS |  |
| light | accent.line focus border on glass.solid (graphic 3:1) | #1A8FC6 | #FDFDFE | 3.57:1 | 3.0:1 | PASS |  |
| light | text.primary on error banner | #1C2633 | #F9E8E9 | 12.91:1 | 4.5:1 | PASS |  |
| light | text.secondary on line.subtle (disabled button) | #5B6878 | #D5DCE4 | 4.11:1 | 4.5:1 | FAIL | 비활성 — 3:1 이면 허용 |
| light | marker number #1C2633 on marker.face | #1C2633 | #FFFFFF | 15.28:1 | 4.5:1 | PASS |  |
| light | route.walk line on map land (graphic 3:1) | #1A8FC6 | #F1F4F7 | 3.29:1 | 3.0:1 | PASS |  |
| light | route.transit line on map land (graphic 3:1) | #6C7BE0 | #F1F4F7 | 3.43:1 | 3.0:1 | PASS |  |
| light | route.drive line on map land (graphic 3:1) | #3A9A74 | #F1F4F7 | 3.14:1 | 3.0:1 | PASS |  |
| light | accent.sky as selected border on card (rejected) | #2FA8E0 | #FFFFFF | 2.69:1 | 3.0:1 | FAIL | 시작 토큰 조합 — accent.line 으로 대체 |
| light | accent.sky selected marker fill vs white 2dp stroke (graphic 3:1) | #2FA8E0 | #FFFFFF | 2.69:1 | 3.0:1 | FAIL | 마커는 흰 테두리+글로우+크기 변화로 보완 (40×50) |
| light | line.subtle border on glass.solid (decorative) | #D5DCE4 | #FDFDFE | 1.36:1 | 1.0:1 | PASS | 장식 — 기준 없음 |
| dark | text.primary on glass.solid (92%) over worst map | #E9EEF4 | #1D2733 | 12.95:1 | 4.5:1 | PASS |  |
| dark | text.primary on glass (72%) over worst map | #E9EEF4 | #1A2634 | 13.13:1 | 4.5:1 | PASS | blur 표면 |
| dark | text.secondary on glass.solid over worst map | #9AA7B6 | #1D2733 | 6.17:1 | 4.5:1 | PASS |  |
| dark | text.secondary on glass (72%) over worst map | #9AA7B6 | #1A2634 | 6.26:1 | 4.5:1 | PASS | blur 표면 |
| dark | text.secondary on card on glass | #9AA7B6 | #1E2733 | 6.16:1 | 4.5:1 | PASS |  |
| dark | text.primary on chip | #E9EEF4 | #232D3B | 11.92:1 | 4.5:1 | PASS |  |
| dark | text.secondary on bg.sky.bottom | #9AA7B6 | #18212D | 6.63:1 | 4.5:1 | PASS |  |
| dark | accent.on on accent.sky (primary button) | #0E1621 | #5CC3F0 | 9.10:1 | 4.5:1 | PASS |  |
| dark | WHITE on accent.sky (rejected) | #FFFFFF | #5CC3F0 | 2.00:1 | 4.5:1 | FAIL | 시작 토큰 조합 — 미달 |
| dark | accent.text on glass.solid over worst map | #5CC3F0 | #1D2733 | 7.57:1 | 4.5:1 | PASS |  |
| dark | accent.text on card | #5CC3F0 | #1E2733 | 7.55:1 | 4.5:1 | PASS |  |
| dark | #1C2633 on status.estimated badge | #1C2633 | #F0B862 | 8.54:1 | 4.5:1 | PASS |  |
| dark | status.estimated as TEXT on card (rejected) | #F0B862 | #1E2733 | 8.42:1 | 4.5:1 | PASS | 글자색 금지 |
| dark | status.error as TEXT on error.soft (rejected) | #F07A76 | #3F343E | 4.37:1 | 4.5:1 | FAIL | 글자 금지 — 아이콘·테두리 전용 |
| dark | status.error icon on error.soft (graphic 3:1) | #F07A76 | #3F343E | 4.37:1 | 3.0:1 | PASS |  |
| dark | status.error.text on card (input message) | #F6A09D | #1E2733 | 7.49:1 | 4.5:1 | PASS |  |
| dark | status.error.text on error.soft over glass | #F6A09D | #3F343E | 5.89:1 | 4.5:1 | PASS |  |
| dark | accent.line selected border on card (graphic 3:1) | #5CC3F0 | #1E2733 | 7.55:1 | 3.0:1 | PASS |  |
| dark | accent.line focus border on glass.solid (graphic 3:1) | #5CC3F0 | #1D2733 | 7.57:1 | 3.0:1 | PASS |  |
| dark | text.primary on error banner | #E9EEF4 | #3F343E | 10.16:1 | 4.5:1 | PASS |  |
| dark | text.secondary on line.subtle (disabled button) | #9AA7B6 | #334051 | 4.30:1 | 4.5:1 | FAIL | 비활성 — 3:1 이면 허용 |
| dark | marker number #1C2633 on marker.face | #1C2633 | #FFFFFF | 15.28:1 | 4.5:1 | PASS |  |
| dark | route.walk line on map land (graphic 3:1) | #5CC3F0 | #141C26 | 8.60:1 | 3.0:1 | PASS |  |
| dark | route.transit line on map land (graphic 3:1) | #8E9AF0 | #141C26 | 6.56:1 | 3.0:1 | PASS |  |
| dark | route.drive line on map land (graphic 3:1) | #6FC9A6 | #141C26 | 8.65:1 | 3.0:1 | PASS |  |
| dark | accent.sky as selected border on card (rejected) | #5CC3F0 | #1E2733 | 7.55:1 | 3.0:1 | PASS | 시작 토큰 조합 — accent.line 으로 대체 |
| dark | accent.sky selected marker fill vs white 2dp stroke (graphic 3:1) | #5CC3F0 | #FFFFFF | 2.00:1 | 3.0:1 | FAIL | 마커는 흰 테두리+글로우+크기 변화로 보완 (40×50) |
| dark | line.subtle border on glass.solid (decorative) | #334051 | #1D2733 | 1.43:1 | 1.0:1 | PASS | 장식 — 기준 없음 |

의도적으로 거부한 조합(rejected)·장식·비활성을 제외한 FAIL: **0**
