"""旅 아이콘 v4 생성기: 배경 없음 · 검은 획 · 남색 테두리 · ZCOOL QingKe HuangYou. handoff-v4/tools/gen_navy_icon.py"""
import sys, os
from fontTools.ttLib import TTFont
from fontTools.pens.boundsPen import BoundsPen
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.transformPen import TransformPen
from PIL import Image, ImageDraw, ImageFont

FONT, OUT = sys.argv[1], sys.argv[2]
FILL, OUTLINE, PLATE = "#0C0C10", "#2E5AA8", "#FFFFFF"
CH = 0x65C5
GLYPH_BOX = 80.0        # 캔버스(100) 안 글리프 긴 변 (테두리 포함 여유를 남김)
STROKE = 6.0            # 테두리 두께 (100 기준, 바깥으로 보이는 두께는 절반이 아니라 전부 — paint-order로 획 아래에 깔기 때문)

font = TTFont(FONT); gs = font.getGlyphSet(); gname = font.getBestCmap()[CH]
bp = BoundsPen(gs); gs[gname].draw(bp); xmin, ymin, xmax, ymax = bp.bounds
w, h = xmax - xmin, ymax - ymin

def glyph_path(box, cx, cy):
    s = box / max(w, h)
    sp = SVGPathPen(gs)
    gs[gname].draw(TransformPen(sp, (s, 0, 0, -s, cx - (xmin + w/2)*s, cy + (ymin + h/2)*s)))
    return sp.getCommands()

def svg(body, vb=100):
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {vb} {vb}" width="{vb}" height="{vb}">\n{body}\n</svg>\n'

def outlined(d, stroke):
    # 테두리를 먼저(아래), 검은 획을 위에: 바깥으로 stroke/2가 보이고 획 안쪽은 검정 그대로
    return (f'  <path d="{d}" fill="{FILL}" stroke="{OUTLINE}" stroke-width="{stroke}" '
            f'stroke-linejoin="round" paint-order="stroke"/>')

icon = os.path.join(OUT, "assets/logo/icon")
p100 = glyph_path(GLYPH_BOX, 50, 50)
open(f"{icon}/tabi_navy_glyph.svg", "w").write(svg(outlined(p100, STROKE)))
open(f"{icon}/favicon.svg", "w").write(svg(outlined(p100, STROKE)))
# Android adaptive: 배경 레이어는 규격상 필수 → 흰 단색 (v2와 같은 원칙). 전경은 66 안전영역 안 56
p108 = glyph_path(56, 54, 54)
open(f"{icon}/ic_launcher_tabi_foreground.svg", "w").write(svg(outlined(p108, STROKE * 56 / GLYPH_BOX), 108))
open(f"{icon}/ic_launcher_tabi_background.svg", "w").write(svg(f'  <rect width="108" height="108" fill="{PLATE}"/>', 108))

def render(px, plate=False):
    """타깃 크기에서 직접 렌더링. 테두리는 최소 1px."""
    img = Image.new("RGBA" if not plate else "RGB", (px, px), (0, 0, 0, 0) if not plate else PLATE)
    d = ImageDraw.Draw(img)
    k = px / 100
    sw = max(1, round(STROKE / 2 * k))           # PIL stroke_width는 바깥 두께
    size = max(6, round(GLYPH_BOX * k * 1000 / max(w, h)))
    f = ImageFont.truetype(FONT, size)
    bb = d.textbbox((0, 0), "旅", font=f, stroke_width=sw)
    d.text(((px - (bb[2]-bb[0]))/2 - bb[0], (px - (bb[3]-bb[1]))/2 - bb[1]), "旅", font=f,
           fill=FILL, stroke_width=sw, stroke_fill=OUTLINE)
    return img

sizes = [16, 24, 32, 48, 64, 128, 256, 512, 1024]
ims = {s: render(s) for s in sizes}
for s, im in ims.items():
    im.save(f"{icon}/tabi_navy_{s}.png")
ims[512].save(f"{icon}/desktop-window-512.png")
ims[16].save(f"{icon}/favicon.ico", sizes=[(16,16),(32,32),(48,48)], append_images=[ims[32], ims[48]])
render(180, plate=True).save(f"{icon}/apple-touch-icon-180.png")
print("ok", len(os.listdir(icon)))
