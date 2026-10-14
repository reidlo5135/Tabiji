#!/usr/bin/env python3
"""Tabiji v2 — 캔버스 보드(.dc.html) → source/*.html 진실 소스 빌드.
- 캔버스 전용 래퍼(support.js, x-dc, helmet, text/x-dc 스크립트) 제거
- {{themeClass}} → '' (기본 cloud), 보드의 Google Fonts Jua 링크 → 로컬 @font-face
- /_blob/<id> → ../assets/ 상대 경로
- 우하단에 '미리보기 전용' 테마 셀렉터 주입 (구현 대상 아님, 주석 참조)
Usage: python3 tools/build_source_v2.py <canvas/project dir> <out source dir>
"""
import re, sys, pathlib

BOARDS = {
    'ThemeV2.dc.html':      'theme_tokens.html',
    'IntroV2.dc.html':      'intro_desktop.html',
    'IntroV2Phone.dc.html': 'intro_phone.html',
    'PlannerV2.dc.html':    'planner_desktop.html',
    'PlannerV2Phone.dc.html': 'planner_phone.html',
    'TripsV2.dc.html':      'trips_desktop.html',
    'TripsV2Phone.dc.html': 'trips_phone.html',
    'AiSheetV2.dc.html':    'ai_sheet_desktop.html',
    'AiSheetV2Phone.dc.html': 'ai_sheet_phone.html',
    'StatesV2.dc.html':     'states_desktop.html',
    'StatesV2Phone.dc.html': 'states_phone.html',
}

BLOBS = {
    '97305331a6c45e3263909f9538df2801': ('../assets/fonts/PretendardJP-Regular.ttf', 'truetype'),
    'c82cb3957fdcba6cec770256310e6237': ('../assets/fonts/PretendardJP-Medium.ttf', 'truetype'),
    '6ff7f10048c0abb1c44475c1521b72cb': ('../assets/fonts/PretendardJP-SemiBold.ttf', 'truetype'),
    '3e7025186bbb3db50025bbab54246436': ('../assets/fonts/PretendardJP-Bold.ttf', 'truetype'),
    'e8fa08349bd0cce6578c9533c628afac': ('../assets/fonts/Sora-Regular.ttf', 'truetype'),
    '057e2bd8dd0e19266f65a75cf171d209': ('../assets/fonts/Sora-SemiBold.ttf', 'truetype'),
    '2d8363cb00af726f33d724617a489583': ('../assets/fonts/Sora-Bold.ttf', 'truetype'),
    '4e24648a1b4c211f54677bb59b89e539': ('../assets/map/map_light.svg', None),
    'ec7fdc2d197fb0828ba69307592e014d': ('../assets/map/map_dark.svg', None),
    '8fef647082ca412d52603e733e78eaf4': ('../assets/intro/spring.webp', None),
    '9b9384f2fef9fc134bdae8832231e894': ('../assets/intro/summer.webp', None),
    '82e7ff57080f4b33c7ddc5c06d307d58': ('../assets/intro/autumn.webp', None),
    '363ad4981db794bb1b1ce2d433ab79fe': ('../assets/intro/winter.webp', None),
}

JUA_FACE = ('<style>@font-face{font-family:"Jua";font-weight:400;'
            'src:url(../assets/fonts/Jua-Regular.ttf) format("truetype")}</style>')

SWITCHER = '''
<!-- ▼ 미리보기 전용 테마 셀렉터 — 구현 대상 아님. 앱에서는 설정 화면의 테마 선택이 이 역할을 한다. -->
<select id="themePreview" aria-label="테마 미리보기"
 style="position:fixed;right:10px;bottom:10px;z-index:9999;font:600 12px system-ui;padding:4px 6px;
 border:2px solid #2A3644;border-radius:8px;background:#FFF;box-shadow:0 2px 0 rgba(42,54,68,.3)"
 onchange="var r=document.querySelector('.v2');r.className='v2'+(this.value==='cloud'?'':' '+this.value)">
<option value="cloud">cloud (기본)</option><option>spring</option><option>summer</option><option>autumn</option>
<option>winter</option><option>korea</option><option>china</option><option>japan</option>
</select>
<!-- ▲ 미리보기 전용 끝 -->
'''

HEADER = ('<!-- Tabiji 旅路 v2 진실 소스 — 기본(cloud) 테마로 렌더됨.\n'
          '     테마 전환 = .v2 루트에 클래스 추가(spring/summer/autumn/winter/korea/china/japan).\n'
          '     구현 수치·색의 최종 기준은 tokens/tokens.json (보드와 미세 차이 시 tokens.json 우선). -->\n')

def build(src: pathlib.Path, dst: pathlib.Path):
    h = src.read_text(encoding='utf-8')
    h = h.replace('<script src="./support.js"></script>\n', '')
    # helmet 내용을 head 로 이동
    m = re.search(r'<helmet>\n?(.*?)</helmet>\n?', h, re.S)
    head_extra = m.group(1) if m else ''
    h = h[:m.start()] + h[m.end():] if m else h
    # Jua Google Fonts 링크 → 로컬 @font-face
    head_extra = re.sub(r'<link href="https://fonts\.googleapis\.com/[^"]*Jua[^"]*"[^>]*>', JUA_FACE, head_extra)
    h = h.replace('</head>', head_extra + '</head>')
    # 캔버스 래퍼 제거
    h = h.replace('<x-dc>\n', '').replace('</x-dc>\n', '').replace('<x-dc>', '').replace('</x-dc>', '')
    h = re.sub(r'<script type="text/x-dc".*?</script>\n?', '', h, flags=re.S)
    # 기본 테마
    h = h.replace(' {{themeClass}}', '').replace('{{themeClass}}', '')
    # 캔버스 에디터 안내문 → 소스 파일 기준으로 교정
    h = h.replace('Tweaks ▸ theme 에서 8종 전환', '우하단 셀렉터로 8종 전환')
    h = h.replace('Tweaks → theme 으로 8종을 직접 넘겨보며 확인', '우하단 셀렉터로 8종을 직접 넘겨보며 확인')
    h = h.replace('② 디스플레이 폰트 확정 (A Jua / B Do Hyeon)', '② 디스플레이 폰트 = Jua 확정(완료)')
    h = h.replace('③ 확정되면 전 화면(인트로 메뉴·여행 목록·AI 시트·공통 상태·컴포넌트)을 v2로 리스킨하고 tokens.json 을 테마×역할 2차원 구조로 바꿔 핸드오프 v2 재발행',
                  '③ 전 화면 v2 리스킨 + tokens.json 테마×역할 2차원 구조 전환(완료 — 이 패키지가 그 결과물)')
    # 블롭 → 상대 경로 (+ ttf 는 format 교정)
    for bid, (path, fmt) in BLOBS.items():
        h = h.replace(f'/_blob/{bid}) format("woff2")', f'{path}) format("{fmt}")' if fmt else f'{path})')
        h = h.replace(f'/_blob/{bid}', path)
    assert '/_blob/' not in h, f'{src.name}: 미해결 블롭 참조 존재'
    assert '{{' not in h, f'{src.name}: 미해결 템플릿 변수 존재'
    h = h.replace('</body>', SWITCHER + '</body>')
    h = h.replace('<!doctype html>\n', '<!doctype html>\n' + HEADER)
    dst.write_text(h, encoding='utf-8')
    print(f'{src.name} -> {dst.name} ({len(h)} bytes)')

if __name__ == '__main__':
    src_dir, out_dir = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
    out_dir.mkdir(parents=True, exist_ok=True)
    for board, out in BOARDS.items():
        build(src_dir / board, out_dir / out)
