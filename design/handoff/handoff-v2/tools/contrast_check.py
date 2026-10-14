#!/usr/bin/env python3
"""WCAG contrast check for Tabiji v2 theme system (8 themes).
Usage: python3 tools/contrast_check.py tokens/tokens.json > CONTRAST.md
기준: 본문·보조 글자 4.5:1 / 헤더 스트립·버튼의 흰 글자(Jua 14sp+ 디스플레이)와
그래픽(테두리·경로선) 3:1 / '추정' 배지 글자 4.5:1."""
import json, sys

def rgb(h):
    h = h.lstrip('#')
    return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))

def lum(c):
    def f(v):
        v = v / 255
        return v / 12.92 if v <= 0.03928 else ((v + 0.055) / 1.055) ** 2.4
    r, g, b = c
    return 0.2126 * f(r) + 0.7152 * f(g) + 0.0722 * f(b)

def ratio(a, b):
    la, lb = lum(a), lum(b)
    hi, lo = max(la, lb), min(la, lb)
    return (hi + 0.05) / (lo + 0.05)

T = json.load(open(sys.argv[1]))['themes']
WHITE = (255, 255, 255)
rows, fails = [], 0
for name, t in T.items():
    ink, paper, card, soft, sub = rgb(t['ink']), rgb(t['paper']), rgb(t['card']), rgb(t['soft']), rgb(t['sub'])
    checks = [
        ('ink on paper', ink, paper, 4.5), ('ink on card', ink, card, 4.5), ('ink on soft', ink, soft, 4.5),
        ('sub on card', sub, card, 4.5), ('sub on soft', sub, soft, 4.5),
        ('WHITE on accent (버튼/헤더)', WHITE, rgb(t['accent']), 3.0),
        ('WHITE on h1', WHITE, rgb(t['h1']), 3.0), ('WHITE on h2', WHITE, rgb(t['h2']), 3.0), ('WHITE on h3', WHITE, rgb(t['h3']), 3.0),
        ('ink border on paper (그래픽)', ink, paper, 3.0),
        ('accent(도보선) on card', rgb(t['accent']), card, 3.0),
        ('h2(대중교통선) on card', rgb(t['h2']), card, 3.0), ('h3(자동차선) on card', rgb(t['h3']), card, 3.0),
        ('#2A1D08 on est 배지', rgb('#2A1D08'), rgb(t['est']), 4.5),
        ('err on errSoft (아이콘, 그래픽)', rgb(t['err']), rgb(t['errSoft']), 3.0),
        ('ink on errSoft (배너 글자)', ink, rgb(t['errSoft']), 4.5),
    ]
    if 'h4' in t:
        checks.append(('WHITE on h4', WHITE, rgb(t['h4']), 3.0))
    for label, fg, bg, need in checks:
        r = ratio(fg, bg)
        ok = r >= need
        if not ok:
            fails += 1
        rows.append((name, label, f'{r:.2f}:1', f'{need}:1', 'PASS' if ok else 'FAIL'))

print('# CONTRAST v2 — 테마 8종 전 조합 검증 (tools/contrast_check.py 자동 생성)\n')
print('헤더 스트립·버튼의 흰 글자는 Jua 디스플레이(14sp 이상, 획 굵음)라 3:1 기준, 본문·보조 글자 4.5:1, 선·테두리 3:1.\n')
print('| 테마 | 조합 | 비율 | 기준 | 결과 |')
print('| --- | --- | --- | --- | --- |')
for r in rows:
    print('| ' + ' | '.join(r) + ' |')
print(f'\nFAIL: **{fails}**')
