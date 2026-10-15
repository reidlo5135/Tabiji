"""旅 붓글씨(Ma Shan Zheng) + 한지 타일 아이콘 세트 생성기. handoff-v3/tools/gen_brush_icon.py 로 함께 보관한다."""
import sys, os
from fontTools.ttLib import TTFont
from fontTools.pens.boundsPen import BoundsPen
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.transformPen import TransformPen
from PIL import Image, ImageDraw, ImageFont

FONT, OUT = sys.argv[1], sys.argv[2]
HANJI, INK = "#F3EBDD", "#1C1814"
CH = 0x65C5                      # 旅
GLYPH_BOX = 73.0                 # 타일(100) 안에서 글리프 긴 변
INSET, RADIUS, BORDER = 2.7, 16.4, 2.7   # 타일 여백·모서리·테두리 (100 기준)

font = TTFont(FONT); gs = font.getGlyphSet(); gname = font.getBestCmap()[CH]
bp = BoundsPen(gs); gs[gname].draw(bp); xmin, ymin, xmax, ymax = bp.bounds
w, h = xmax - xmin, ymax - ymin

def glyph_path(box, cx, cy):
    s = box / max(w, h)
    dx = cx - (xmin + w / 2) * s
    dy = cy + (ymin + h / 2) * s
    sp = SVGPathPen(gs); gs[gname].draw(TransformPen(sp, (s, 0, 0, -s, dx, dy)))
    return sp.getCommands()

def svg(body, vb=100):
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {vb} {vb}" width="{vb}" height="{vb}">\n{body}\n</svg>\n'

icon = os.path.join(OUT, "assets/logo/icon")
p100 = glyph_path(GLYPH_BOX, 50, 50)
open(f"{icon}/tabi_brush_glyph.svg", "w").write(svg(f'  <path fill="{INK}" d="{p100}"/>'))
tile = (f'  <rect x="{INSET}" y="{INSET}" width="{100-2*INSET}" height="{100-2*INSET}" rx="{RADIUS}" '
        f'fill="{HANJI}" stroke="{INK}" stroke-width="{BORDER}"/>\n  <path fill="{INK}" d="{p100}"/>')
open(f"{icon}/tabi_brush_tile.svg", "w").write(svg(tile))
open(f"{icon}/favicon.svg", "w").write(svg(tile))
# Android adaptive (108dp 캔버스, 글리프는 66dp 안전영역 안 56dp — v2 04와 같은 배치)
p108 = glyph_path(56, 54, 54)
open(f"{icon}/ic_launcher_tabi_foreground.svg", "w").write(svg(f'  <path fill="{INK}" d="{p108}"/>', 108))
open(f"{icon}/ic_launcher_tabi_background.svg", "w").write(svg(f'  <rect width="108" height="108" fill="{HANJI}"/>', 108))

pil_font_cache = {}
def render_tile(px):
    """타깃 크기에서 직접 렌더링 (큰 이미지 축소 금지 — 작은 크기 선명도)."""
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    k = px / 100
    bw = max(1, round(BORDER * k)); ins = max(0, round(INSET * k))
    d.rounded_rectangle([ins, ins, px - 1 - ins, px - 1 - ins], radius=max(2, round(RADIUS * k)),
                        fill=HANJI, outline=INK, width=bw)
    size = max(6, round(GLYPH_BOX * k * 1000 / max(w, h)))   # 글리프 긴 변이 GLYPH_BOX가 되도록 em 크기 환산
    f = ImageFont.truetype(FONT, size)
    bb = d.textbbox((0, 0), "旅", font=f)
    gx = (px - (bb[2] - bb[0])) / 2 - bb[0]; gy = (px - (bb[3] - bb[1])) / 2 - bb[1]
    d.text((gx, gy), "旅", font=f, fill=INK)
    return img

def render_square(px):
    """iOS 터치 아이콘: 불투명 정사각 한지 (모서리는 OS가 깎는다)."""
    img = Image.new("RGB", (px, px), HANJI); d = ImageDraw.Draw(img)
    size = round(GLYPH_BOX * px / 100 * 1000 / max(w, h)); f = ImageFont.truetype(FONT, size)
    bb = d.textbbox((0, 0), "旅", font=f)
    d.text(((px - (bb[2]-bb[0]))/2 - bb[0], (px - (bb[3]-bb[1]))/2 - bb[1]), "旅", font=f, fill=INK)
    return img

sizes = [16, 24, 32, 48, 64, 128, 256, 512, 1024]
tiles = {s: render_tile(s) for s in sizes}
for s, im in tiles.items():
    im.save(f"{icon}/tabi_tile_{s}.png")
tiles[512].save(f"{icon}/desktop-window-512.png")
tiles[16].save(f"{icon}/favicon.ico", sizes=[(16,16),(32,32),(48,48)], append_images=[tiles[32], tiles[48]])
render_square(180).save(f"{icon}/apple-touch-icon-180.png")
print("ok", len(os.listdir(icon)), "files")
