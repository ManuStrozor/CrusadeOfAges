package engine;

import engine.gfx.Button;
import engine.gfx.Font;
import engine.gfx.Image;
import engine.gfx.ImageRequest;
import engine.gfx.Sprite;
import engine.gfx.Light;
import game.Conf;
import game.GameManager;
import game.objects.GameObject;
import engine.view.CreativeMode;
import engine.view.GameSelection;
import engine.view.InputDialog;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Renderer {

    private static final int AMBIENTCOLOR = 0xff020202;

    private Sprite objs, floor, water;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<>();

    private int gcW, gcH;
    private int[] p, zb, lm;
    private int camX, camY;
    private int zDepth = 0;
    private boolean processing = false;
    private int ts = GameManager.TS;

    public Renderer(GameContainer gc) {
        // Sprites
        objs = new Sprite(Conf.SM_FOLDER + "/assets/objects.png", ts, ts, true);
        floor = new Sprite(Conf.SM_FOLDER + "/assets/objects/floor.png", ts, ts, true);
        water = new Sprite(Conf.SM_FOLDER + "/assets/objects/water.png", ts, ts, true);

        gcW = gc.getWidth();
        gcH = gc.getHeight();
        p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zb = new int[p.length];
        lm = new int[p.length];
    }

    void clear() {
        for (int i = 0; i < p.length; i++) {
            p[i] = 0;
            zb[i] = 0;
            lm[i] = AMBIENTCOLOR;
        }
    }

    void process() {
        processing = true;

        imageRequest.sort(Comparator.comparingInt(i0 -> i0.zDepth));

        for (ImageRequest ir : imageRequest) {
            setzDepth(ir.zDepth);
            drawImage(ir.image, ir.offX, ir.offY);
        }

        for (int i = 0; i < p.length; i++) {

            int pR = (p[i] >> 16) & 0xff;
            int pG = (p[i] >> 8) & 0xff;
            int pB = p[i] & 0xff;

            int lR = (lm[i] >> 16) & 0xff;
            int lG = (lm[i] >> 8) & 0xff;
            int lB = lm[i] & 0xff;

            p[i] = (int) (pR * lR / 255f) << 16 | (int) (pG * lG / 255f) << 8 | (int) (pB * lB / 255f);
        }

        imageRequest.clear();
        processing = false;
    }

    private void setPixel(int x, int y, int value) {

        int alpha = (value >> 24) & 0xff;
        if ((x < 0 || x >= gcW || y < 0 || y >= gcH) || alpha == 0) return;

        int index = x + y * gcW;

        if (zb[index] > zDepth) return;

        zb[index] = zDepth;

        if (alpha == 0xff) {
            p[index] = value;
        } else {
            int pR = (p[index] >> 16) & 0xff;
            int pG = (p[index] >> 8) & 0xff;
            int pB = p[index] & 0xff;

            int vR = (value >> 16) & 0xff;
            int vG = (value >> 8) & 0xff;
            int vB = value & 0xff;

            int newR = pR - (int) ((pR - vR) * alpha / 255f);
            int newG = pG - (int) ((pG - vG) * alpha / 255f);
            int newB = pB - (int) ((pB - vB) * alpha / 255f);

            p[index] = 0xff << 24 | newR << 16 | newG << 8 | newB;
        }
    }

    private void setLightWorld(int x, int y, int value) {

        if (x < 0 || x >= gcW || y < 0 || y >= gcH) return;

        int baseColor = lm[x + y * gcW];

        int maxR = Math.max((baseColor >> 16) & 255, (value >> 16) & 255);
        int maxG = Math.max((baseColor >> 8) & 255, (value >> 8) & 255);
        int maxB = Math.max(baseColor & 255, value & 255);

        lm[x + y * gcW] = maxR << 16 | maxG << 8 | maxB;
    }

    private int darken(int color, int diff) {

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        diff = Math.abs(diff);
        int newA = (Math.max(a - diff, 0)) << 24;
        int newR = (Math.max(r - diff, 0)) << 16;
        int newG = (Math.max(g - diff, 0)) << 8;
        int newB = Math.max(b - diff, 0);

        return newA | newR | newG | newB;
    }

    private int lighten(int color, int diff) {

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        diff = Math.abs(diff);
        int newA = (Math.min(a + diff, 255)) << 24;
        int newR = (Math.min(r + diff, 255)) << 16;
        int newG = (Math.min(g + diff, 255)) << 8;
        int newB = Math.min(b + diff, 255);

        return newA | newR | newG | newB;
    }

    private int textSize(String text, Font font) {

        int textW = 0;
        for (int i = 0; i < text.length(); i++) {
            textW += font.getWidths()[Math.min(text.codePointAt(i), 255)];
        }
        return textW;
    }

    public void drawText(String text, int offX, int offY, int alignX, int alignY, int color, Font font) {

        if (alignX != 1) {
            if (alignX == 0) offX -= textSize(text, font) / 2;
            else if (alignX == -1) offX -= textSize(text, font);
        }

        if (alignY != 1) {
            if (alignY == 0) offY -= font.getFontImage().getH() / 2;
            else if (alignY == -1) offY -= font.getFontImage().getH();
        }

        int offset = 0;
        for (int i = 0; i < text.length(); i++) {
            int unicode = text.codePointAt(i);
            // With darker color
            for (int y = 0; y < font.getFontImage().getH(); y++) {
                for (int x = 0; x < font.getWidths()[Math.min(unicode, 255)]; x++) {
                    if (font.getFontImage().getP()[(x + font.getOffsets()[Math.min(unicode, 255)]) + y * font.getFontImage().getW()] == 0xff000000) {
                        setPixel(x + offX + offset - 1, y + offY - 1, darken(color, 100));
                    }
                }
            }
            // With normal color
            for (int y = 0; y < font.getFontImage().getH(); y++) {
                for (int x = 0; x < font.getWidths()[Math.min(unicode, 255)]; x++) {
                    if (font.getFontImage().getP()[(x + font.getOffsets()[Math.min(unicode, 255)]) + y * font.getFontImage().getW()] == 0xff000000) {
                        setPixel(x + offX + offset, y + offY, color);
                    }
                }
            }
            offset += font.getWidths()[Math.min(unicode, 255)];
        }
    }

    private void drawImage(Image image, int offX, int offY) {

        offX -= camX;
        offY -= camY;

        if (image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
            return;
        }

        if (offX < -image.getW()) return;
        if (offY < -image.getH()) return;
        if (offX >= gcW) return;
        if (offY >= gcH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getW();
        int newHeight = image.getH();

        if (offX < 0) newX -= offX;
        if (offY < 0) newY -= offY;
        if (offX + newWidth >= gcW) newWidth -= newWidth + offX - gcW;
        if (offY + newHeight >= gcH) newHeight -= newHeight + offY - gcH;

        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getP()[x + y * image.getW()]);
            }
        }
    }

    public void drawSprite(Sprite image, int offX, int offY, int tileX, int tileY, int tileSize) {

        offX -= camX;
        offY -= camY;

        if (image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image.getSprite(tileX, tileY), zDepth, offX, offY));
            return;
        }

        if (offX < -image.getWidth()) return;
        if (offY < -image.getHeight()) return;
        if (offX >= gcW) return;
        if (offY >= gcH) return;

        int newX = 0;
        int newY = 0;

        if (offX < 0) newX -= offX;
        if (offY < 0) newY -= offY;

        float plus = GameManager.TS / (float)tileSize;
        for (int y = newY; y < tileSize; y++) {
            for (int x = newX; x < tileSize; x++) {
                int position = (int)(x*plus + tileX * image.getWidth()) + (int)(y*plus + tileY * image.getHeight()) * image.getW();
                setPixel(x + offX, y + offY, image.getP()[Math.max(Math.min(position, image.getP().length-1), 0)]);
            }
        }
    }

    private void drawRect(int x, int y, int w, int h, int col) {

        x -= camX;
        y -= camY;

        for (int i = 0; i < w; i++) {
            setPixel(x + i, y, col);
            setPixel(x + i, y + h - 1, col);
        }

        for (int i = 0; i < h; i++) {
            setPixel(x, y + i, col);
            setPixel(x + w - 1, y + i, col);
        }
    }

    public void fillRect(int x, int y, int w, int h, int col) {

        x -= camX;
        y -= camY;

        if (x < -w) return;
        if (y < -h) return;
        if (x >= gcW) return;
        if (y >= gcH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = w;
        int newHeight = h;

        if (x < 0) newX -= x;
        if (y < 0) newY -= y;
        if (x + newWidth >= gcW) newWidth -= newWidth + x - gcW;
        if (y + newHeight >= gcH) newHeight -= newHeight + y - gcH;

        for (int j = newY; j < newHeight; j++) {
            for (int i = newX; i < newWidth; i++) {
                setPixel(i + x, j + y, col);
            }
        }
    }

    public void drawLight(Light l, int offX, int offY) {

        offX -= camX;
        offY -= camY;

        for (int i = 0; i < l.getDiameter(); i++) {
            // North
            drawLightCard(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
            // South
            drawLightCard(l, l.getRadius(), l.getRadius(), i, l.getDiameter() - 1, offX, offY); // -1 : quelques pixels manquent
            // West
            drawLightCard(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
            // Est
            drawLightCard(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);
        }
    }

    private void drawLightCard(Light l, int x0, int y0, int x1, int y1, int offX, int offY) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while (true) {

            int screenX = x0 - l.getRadius() + offX;
            int screenY = y0 - l.getRadius() + offY;

            int lightColor = l.getLightValue(x0, y0);
            if (lightColor == 0) return;

            setLightWorld(screenX, screenY, lightColor);

            if (x0 == x1 && y0 == y1) break;

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void drawButton(Button b, String text) {
        int col = b.isHover() ? darken(b.getBgColor(), 20) : b.getBgColor();
        //Border-out
        drawRect(b.getOffX() + camX, b.getOffY() + camY, b.getWidth(), b.getHeight(), 0xff333333);
        //background & text
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, b.getWidth() - 2, b.getHeight() - 2, col);
        drawText(text, b.getOffX() + b.getWidth() / 2, b.getOffY() + b.getHeight() / 2, 0, 0, lighten(col, 100), Font.STANDARD);
        //Border-in lighter
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, 1, b.getHeight() - 2, lighten(col, 40));
        fillRect(b.getOffX() + camX + 2, b.getOffY() + camY + 1, b.getWidth() - 4, 1, lighten(col, 40));
        //Border-in darker
        fillRect(b.getOffX() + camX + b.getWidth() - 2, b.getOffY() + camY + 1, 1, b.getHeight() - 2, darken(col, 40));
        fillRect(b.getOffX() + camX + 2, b.getOffY() + camY + b.getHeight() - 2, b.getWidth() - 4, 1, darken(col, 40));
    }

    public void drawInput(int x, int y, int w, int h, int color) {

        String text = InputDialog.input;

        int position = textSize(text.substring(0, InputDialog.blink), Font.STANDARD);

        //Background & text
        fillRect(x, y, w, h, color);
        drawText(text, x + 4, y + h / 2, 1, 0, -1, Font.STANDARD);
        //Blink bar
        fillRect(x + 4 + position, y + 3, 1, h - 6, -1);
        //Border-out darker
        fillRect(x, y, w, 1, darken(color, 40));
        fillRect(x - 1, y + 1, 1, h - 1, darken(color, 40));
        //Border-out lighter
        fillRect(x, y + h, w, 1, lighten(color, 40));
        fillRect(x + w, y + 1, 1, h - 1, lighten(color, 40));
    }

    void drawHUD(GameObject obj) {
        int width = ts * 6;
        int x = gcW / 2 - width / 2;
        fillRect(x, 0, width, ts, 0x99000000);

        drawSprite(objs, x, 0, 3, 2, ts);
        drawSprite(objs, x + ts * 2, 0, 5, 0, ts);
        drawSprite(objs, x + ts * 4, 0, 3, 1, ts);

        drawText("x" + obj.getLives(), x + ts - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getCoins(), x + ts * 3 - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getKeys(), x + ts * 5 - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
    }

    public void drawWorld(World world) {

        int offX = Math.max(camX/ ts, 0);
        int offY = Math.max(camY/ ts, 0);

        int endX = Math.min(gcW/ ts +offX+2, world.getWidth());
        int endY = Math.min(gcH/ ts +offY+2, world.getHeight());

        for (int y = offY; y < endY; y++) {
            for (int x = offX; x < endX; x++) {

                int tileX = world.getBlocMap(x, y).getX();
                int tileY = world.getBlocMap(x, y).getY();

                // Murs derriere les blocs non-solides
                drawSprite(objs, x * ts, y * ts, 1, 0, ts);

                // Affichage des blocs
                switch (world.getBlocMap(x, y).getTag()) {
                    case "floor":
                        drawSprite(floor, x * ts, y * ts, getTileX(world, x, y), getTileY(world, x, y), ts);
                        break;
                    case "water":
                        drawSprite(water, x * ts, y * ts, 0, getWaterTile(world, x, y), ts);
                        break;
                    default:
                        drawSprite(objs, x * ts, y * ts, tileX, tileY, ts);
                        break;
                }

                // Ombres sous les blocs
                if (!world.getBlocMap(x, y).isSolid() && solidTop(world, x, y))
                    drawSprite(objs, x * ts, y * ts, 0, 3, ts);

                // Partie haute de la porte
                if (world.getBlocMap(x, y).isTagged("door"))
                    drawSprite(objs, x * ts, (y - 1) * ts, tileX, tileY - 1, ts);
            }
        }
    }

    private int getWaterTile(World world, int x, int y) {
        if (!world.getBlocMap(x, y-1).getTag().equals("water")) {
            return 0;
        } else {
            return 1;
        }
    }

    private int getTileY(World world, int x, int y) {
        if (!solidTop(world, x, y) && solidBottom(world, x, y)) {
            return 1;
        } else if (solidTop(world, x, y) && solidBottom(world, x, y)) {
            return 2;
        } else if (solidTop(world, x, y) && !solidBottom(world, x, y)) {
            return 3;
        } else {
            return 0;
        }
    }

    private int getTileX(World world, int x, int y) {
        if (!solidLeft(world, x, y) && solidRight(world, x, y)) {
            return 0;
        } else if (solidLeft(world, x, y) && solidRight(world, x, y)) {
            return 1;
        } else if (solidLeft(world, x, y) && !solidRight(world, x, y)) {
            return 2;
        } else {
            return 3;
        }
    }

    private boolean solidRight(World world, int x, int y) {
        return world.getBlocMap(x+1, y).isSolid();
    }

    private boolean solidLeft(World world, int x, int y) {
        return world.getBlocMap(x-1, y).isSolid();
    }

    private boolean solidTop(World world, int x, int y) {
        return world.getBlocMap(x, y-1).isSolid();
    }

    private boolean solidBottom(World world, int x, int y) {
        return world.getBlocMap(x, y+1).isSolid();
    }

    public void drawWorldLights(World world, Light lamp) {
        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
                if (world.getBlocMap(x, y).isTagged("torch"))
                    drawLight(lamp, x * ts + ts / 2, y * ts + ts / 3);
            }
        }
    }

    private void drawBloc(World world, String tag, int x, int y, int tileSize) {
        drawSprite(objs, x, y, world.getBloc(tag).getX(), world.getBloc(tag).getY(), tileSize);
    }

    public void drawDock(World world, String[] dock, int scroll, int tileSize) {
        int midH = camY + gcH / 2;
        int s = tileSize + 1;
        int y = midH - (dock.length * s) / 2 + (dock.length / 2 - scroll) * s;

        if (dock.length % 2 != 0)
            y += tileSize / 2;

        fillRect(camX, camY, s + 4, gcH, 0x89000000);

        for (int i = 0; i < dock.length; i++) {
            drawBloc(world, dock[i], camX + 4, y - tileSize / 2 + s * i, tileSize);
        }

        drawRect(camX + 1, midH - tileSize / 2 - 3, s + 4, s + 4, 0xbbffffff);
        drawRect(camX + 2, midH - tileSize / 2 - 2, s + 2, s + 2, 0x77ffffff);
        drawRect(camX + 3, midH - tileSize / 2 - 1, s, s, 0x33ffffff);
    }

    public void drawMiniMap(Image img, int h) {
        Image thumb = img.getThumbnail(h*(img.getW()/img.getH()), h);
        float diffW = thumb.getW() / (float)img.getW();
        float diffH = thumb.getH() / (float)img.getH();
        int xMMap = camX + gcW - thumb.getW() - 4;
        int yMMap = camY + gcH - thumb.getH() - 4;
        drawImage(thumb, xMMap, yMMap);
        drawRect(xMMap + (int)(camX/ ts * diffW), yMMap + (int)(camY/ ts * diffH),
                (int)(gcW / ts * diffW), (int)(gcH / ts * diffH), 0x99ababab);
    }

    public void drawArrows(World world, int width, int height, int tileSize) {
        if (camY > 0)
            drawBloc(world, "arrow_up", camX + gcW / 2 - tileSize / 2, camY, tileSize);
        if (camY + gcH < height * this.ts)
            drawBloc(world, "arrow_down", camX + gcW / 2 - tileSize / 2, camY + gcH - tileSize, tileSize);
        if (camX > 0)
            drawBloc(world, "arrow_left", camX, camY + gcH / 2 - tileSize / 2, tileSize);
        if (camX + gcW < width * this.ts)
            drawBloc(world, "arrow_right", camX + gcW - tileSize, camY + gcH / 2 - tileSize / 2, tileSize);
    }

    public void drawLevels(String[][] levels, PlayerStats ps) {

        int largest = 0;
        for (String[] level : levels) {
            int len = textSize(level[1], Font.STANDARD);
            if (len + 30 > largest) largest = len + 30;
        }

        int x = gcW / 2 - largest / 2;
        int y = ts + 10 - GameSelection.scroll;

        int hUsed = 10;
        for (int i = 0; i < Math.min(ps.getValueOf("Level up"), levels.length) + 1; i++) {

            int size = 30;
            if (i != 0) y += 30 + 10;

            if (i < levels.length) {
                if (GameSelection.focus && i == GameSelection.fIndex)
                    drawRect(x - 4, y - 4, largest + 12, 30 + 8, 0xff696969);

                fillRect(x, y, size, size, -1);
                drawText("" + i, x + size / 2, y + size / 2, 0, 0, 0xff000000, Font.STANDARD);
                drawText(levels[i][1], x + size + 4, y + 15, 1, 0, 0xff898989, Font.STANDARD);
            }

            hUsed += size + 10;
        }

        int hTotal = gcH - (3 * ts);
        int minus = Math.max(hUsed - hTotal, 0);

        drawScrollBar(gcW / 2 + largest / 2 + 20, ts, 10, hTotal, GameSelection.scroll, minus);
    }

    public void drawListOfFiles(ArrayList<Image> f, ArrayList<String> n, ArrayList<Date> d, String nothing) {

        if (f.size() == 0)
            drawText(nothing, gcW / 2, gcH / 2, 0, 0, 0xffababab, Font.STANDARD);

        int w = Image.THUMBW;
        int h = Image.THUMBH;
        int largest = 0;
        for (int i = 0; i < f.size(); i++) {
            int size = Math.max(textSize(n.get(i), Font.STANDARD), textSize(d.get(i).toString(), Font.STANDARD));
            size = Math.max(size, textSize("Dimensions: " + f.get(i).getW() + "x" + f.get(i).getH(), Font.STANDARD));
            if (size + w > largest) largest = size + w;
        }

        int x = gcW / 2 - largest / 2;
        int y = ts + 10 - CreativeMode.scroll;

        int hUsed = 10;
        for (int i = 0; i < f.size(); i++) {

            if (i != 0) y += h + 10;

            if (CreativeMode.focus && i == CreativeMode.fIndex)
                drawRect(x - 4, y - 4, largest + 12, h + 8, 0xff696969);

            fillRect(x, y, w, h, -1);
            drawImage(f.get(i).getThumbnail(Image.THUMBW, Image.THUMBH), x, y);

            drawText(n.get(i), x + w + 4, y - 2, 1, 1, -1, Font.STANDARD);
            drawText("Dimensions: " + f.get(i).getW() + "x" + f.get(i).getH(), x + w + 4, y + h/2, 1, 0, 0xff898989, Font.STANDARD);
            drawText(d.get(i).toString(), x + w + 4, y + h + 2, 1, -1, 0xff898989, Font.STANDARD);

            hUsed += h + 10;
        }

        int hTotal = gcH - 3 * ts;
        int minus = Math.max(hUsed - hTotal, 0);

        if (f.size() != 0)
            drawScrollBar(gcW / 2 + largest / 2 + 20, ts, 8, hTotal, CreativeMode.scroll / 8, minus / 8);
    }

    private void drawScrollBar(int x, int y, int w, int h, int scroll, int minus) {
        //BackBar
        fillRect(x, y, w, h, 0x66ffffff);
        fillRect(x + w, y, 1, h, 0xff444244);
        //Bar
        fillRect(x, y + scroll, w, h - minus, 0xffd4d2cc);
        //Relief
        fillRect(x + (w - 1), y + 1 + scroll, 1, h - 1 - minus, 0xff848284);
        fillRect(x + 1, y + h - minus + scroll - 1, w - 1, 1, 0xff848284);
        fillRect(x + 1, y + 1 + scroll, w - 2, 1, 0xfffcfefc);
        fillRect(x + 1, y + 1 + scroll, 1, h - 2 - minus, 0xfffcfefc);
        fillRect(x, y + h - minus + scroll, w, 1, 0xff444244);
    }

    public void drawBackground(World world) {
        for (int y = 0; y <= gcH / ts; y++) {
            for (int x = 0; x <= gcW / ts; x++) {
                drawBloc(world, "wall", x* ts, y* ts, ts);
            }
        }
    }

    public void fillAreaBloc(int nX, int nY, int nW, int nH, World world, String tag) {
        for (int y = 0; y < nH; y++) {
            for (int x = 0; x < nW; x++) {
                drawBloc(world, tag, nX + x * ts, nY + y * ts, ts);
            }
        }
    }

    public void drawMenuTitle(String title, String small) {
        drawText(title, gcW /2, 45, 0, 1, 0xffc0392b, Font.BIG_STANDARD);
        if (small != null) {
            drawText(small, gcW /2, 60, 0, 1, 0xffababab, Font.STANDARD);
        }
    }

    public void drawList(int offX, int offY, String title, String[] list) {
        drawText(title, offX, offY, 0, 0, 0xff27ae60, Font.STANDARD);
        for (int i = 0; i < list.length; i++)
            drawText(list[i], offX, offY + 14 * (i + 1), 0, 0, 0xffababab, Font.STANDARD);
    }

    public int getCamX() {
        return camX;
    }

    public void setCamX(int camX) {
        this.camX = camX;
    }

    public int getCamY() {
        return camY;
    }

    public void setCamY(int camY) {
        this.camY = camY;
    }

    public void setCoorCam(int camX, int camY) {
        this.camX = camX;
        this.camY = camY;
    }

    private void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public void zoomIn(int mouseX, int mouseY) {
        camX += (mouseX + camX) / ts;
        camY += (mouseY + camY) / ts;
        ts+=1;
    }

    public void zoomOut(int mouseX, int mouseY) {
        camX -= (mouseX + camX) / ts;
        camY -= (mouseY + camY) / ts;
        if (ts > 1) ts-=1;
    }
}
