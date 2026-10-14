#!/usr/bin/env python3
"""WCAG 2.x contrast check for Tabiji tokens.
Composites alpha surfaces over the worst-case map background before measuring.
Usage: python3 tools/contrast_check.py tokens/tokens.json > CONTRAST.md
"""
import json, sys

def hex_to_rgba(h):
    h = h.lstrip('#')
    r, g, b = int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)
    a = int(h[6:8], 16) / 255 if len(h) == 8 else 1.0
    return (r, g, b, a)

def over(fg, bg):
    """composite fg (with alpha) over opaque bg -> opaque rgb"""
    r, g, b, a = fg
    return tuple(round(c * a + d * (1 - a)) for c, d in zip((r, g, b), bg[:3])) + (1.0,)

def lum(rgb):
    def ch(c):
        c = c / 255
        return c / 12.92 if c <= 0.03928 else ((c + 0.055) / 1.055) ** 2.4
    r, g, b = rgb[:3]
    return 0.2126 * ch(r) + 0.7152 * ch(g) + 0.0722 * ch(b)

def ratio(a, b):
    la, lb = lum(a), lum(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)

def fmt(rgb):
    return '#%02X%02X%02X' % rgb[:3]

tokens = json.load(open(sys.argv[1]))
C = tokens['color']
def col(name, theme): return hex_to_rgba(C[name][theme])

# worst-case map backgrounds under a glass panel (from 30_map_style.md)
MAP_WORST = {'light': hex_to_rgba('#E6EAEF'), 'dark': hex_to_rgba('#102538')}
MAP_LAND = {'light': hex_to_rgba('#F1F4F7'), 'dark': hex_to_rgba('#141C26')}

rows = []
def check(label, fg, bg, need, theme, note=''):
    r = ratio(fg, bg)
    rows.append((theme, label, fmt(fg), fmt(bg), r, need, 'PASS' if r >= need else 'FAIL', note))

for theme in ('light', 'dark'):
    glass_solid = over(col('surface.glass.solid', theme), MAP_WORST[theme])
    glass = over(col('surface.glass', theme), MAP_WORST[theme])
    card_on_glass = over(col('surface.card', theme), glass_solid)
    chip_on_glass = over(col('surface.chip', theme), glass_solid)
    bg_bottom = col('bg.sky.bottom', theme)
    check('text.primary on glass.solid (92%) over worst map', col('text.primary', theme), glass_solid, 4.5, theme)
    check('text.primary on glass (72%) over worst map', col('text.primary', theme), glass, 4.5, theme, 'blur 표면')
    check('text.secondary on glass.solid over worst map', col('text.secondary', theme), glass_solid, 4.5, theme)
    check('text.secondary on glass (72%) over worst map', col('text.secondary', theme), glass, 4.5, theme, 'blur 표면')
    check('text.secondary on card on glass', col('text.secondary', theme), card_on_glass, 4.5, theme)
    check('text.primary on chip', col('text.primary', theme), chip_on_glass, 4.5, theme)
    check('text.secondary on bg.sky.bottom', col('text.secondary', theme), bg_bottom, 4.5, theme)
    check('accent.on on accent.sky (primary button)', col('accent.on', theme), col('accent.sky', theme), 4.5, theme)
    check('WHITE on accent.sky (rejected)', hex_to_rgba('#FFFFFF'), col('accent.sky', theme), 4.5, theme, '시작 토큰 조합 — 미달')
    check('accent.text on glass.solid over worst map', col('accent.text', theme), glass_solid, 4.5, theme)
    check('accent.text on card', col('accent.text', theme), card_on_glass, 4.5, theme)
    check('#1C2633 on status.estimated badge', hex_to_rgba('#1C2633'), col('status.estimated', theme), 4.5, theme)
    check('status.estimated as TEXT on card (rejected)', col('status.estimated', theme), card_on_glass, 4.5, theme, '글자색 금지')
    check('status.error as TEXT on error.soft (rejected)', col('status.error', theme), over(col('status.error.soft', theme), glass_solid), 4.5, theme, '글자 금지 — 아이콘·테두리 전용')
    check('status.error icon on error.soft (graphic 3:1)', col('status.error', theme), over(col('status.error.soft', theme), glass_solid), 3.0, theme)
    check('status.error.text on card (input message)', col('status.error.text', theme), card_on_glass, 4.5, theme)
    check('status.error.text on error.soft over glass', col('status.error.text', theme), over(col('status.error.soft', theme), glass_solid), 4.5, theme)
    check('accent.line selected border on card (graphic 3:1)', col('accent.line', theme), card_on_glass, 3.0, theme)
    check('accent.line focus border on glass.solid (graphic 3:1)', col('accent.line', theme), glass_solid, 3.0, theme)
    check('text.primary on error banner', col('text.primary', theme), over(col('status.error.soft', theme), glass_solid), 4.5, theme)
    check('text.secondary on line.subtle (disabled button)', col('text.secondary', theme), over(col('line.subtle', theme), glass_solid), 4.5, theme, '비활성 — 3:1 이면 허용')
    check('marker number #1C2633 on marker.face', hex_to_rgba('#1C2633'), col('marker.face', theme), 4.5, theme)
    check('route.walk line on map land (graphic 3:1)', col('route.walk', theme), MAP_LAND[theme], 3.0, theme)
    check('route.transit line on map land (graphic 3:1)', col('route.transit', theme), MAP_LAND[theme], 3.0, theme)
    check('route.drive line on map land (graphic 3:1)', col('route.drive', theme), MAP_LAND[theme], 3.0, theme)
    check('accent.sky as selected border on card (rejected)', col('accent.sky', theme), card_on_glass, 3.0, theme, '시작 토큰 조합 — accent.line 으로 대체')
    check('accent.sky selected marker fill vs white 2dp stroke (graphic 3:1)', col('accent.sky', theme), hex_to_rgba('#FFFFFF'), 3.0, theme, '마커는 흰 테두리+글로우+크기 변화로 보완 (40×50)')
    check('line.subtle border on glass.solid (decorative)', col('line.subtle', theme), glass_solid, 1.0, theme, '장식 — 기준 없음')

print('# CONTRAST — WCAG 대비 검증 (tools/contrast_check.py 자동 생성)\n')
print('글래스 표면은 지도 최악 배경(라이트 건물 #E6EAEF, 다크 물 #102538) 위에 알파 합성한 뒤 측정. 본문 4.5:1, 큰 글자·그래픽 3:1 기준.\n')
print('| 테마 | 조합 | 전경 | 배경(합성 후) | 비율 | 기준 | 결과 | 비고 |')
print('| --- | --- | --- | --- | --- | --- | --- | --- |')
fails = 0
for theme, label, fg, bg, r, need, res, note in rows:
    if res == 'FAIL' and 'rejected' not in label and '장식' not in note and '비활성' not in note and '보완' not in note:
        fails += 1
    print(f'| {theme} | {label} | {fg} | {bg} | {r:.2f}:1 | {need}:1 | {res} | {note} |')
print(f'\n의도적으로 거부한 조합(rejected)·장식·비활성을 제외한 FAIL: **{fails}**')
