package engine;

import engine.gfx.*;
import game.Conf;
import game.Game;
import game.entity.Entity;
import engine.view.CreativeMode;
import engine.view.GameSelection;

import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Renderer {

    private static final int AMBIENTCOLOR = 0xff020202;

    private GameContainer gc;
    private Settings settings;

    private Sprite objs, floor, water;
    private ArrayList<ImageRequest> imageRequest = new ArrayList<>();

    private int[] p, zb, lm;
    private int camX, camY;
    private int zDepth = 0;
    private boolean processing = false;
    private int ts = Game.TS;

    public Renderer(GameContainer gc, Settings settings) {
        this.gc = gc;
        this.settings = settings;

        // Sprites
        objs = new Sprite(Conf.SM_FOLDER + "/assets/objects.png", ts, ts, true);
        floor = new Sprite(Conf.SM_FOLDER + "/assets/objects/floor.png", ts, ts, true);
        water = new Sprite(Conf.SM_FOLDER + "/assets/objects/water.png", ts, ts, true);

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
        if ((x < 0 || x >= gc.getWidth() || y < 0 || y >= gc.getHeight()) || alpha == 0) return;

        int index = x + y * gc.getWidth();

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

        if (x < 0 || x >= gc.getWidth() || y < 0 || y >= gc.getHeight()) return;

        int baseColor = lm[x + y * gc.getWidth()];

        int maxR = Math.max((baseColor >> 16) & 255, (value >> 16) & 255);
        int maxG = Math.max((baseColor >> 8) & 255, (value >> 8) & 255);
        int maxB = Math.max(baseColor & 255, value & 255);

        lm[x + y * gc.getWidth()] = maxR << 16 | maxG << 8 | maxB;
    }

    public int darken(int color, int diff) {

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        diff = Math.abs(diff);
        int newA = a << 24;
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

    public int textSize(String text, Font font) {

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
            if (alignY == 0) offY -= font.getFontImage().getHeight() / 2;
            else if (alignY == -1) offY -= font.getFontImage().getHeight();
        }

        int offset = 0;
        for (int i = 0; i < text.length(); i++) {
            int unicode = text.codePointAt(i);
            // With darker color
            for (int y = 0; y < font.getFontImage().getHeight(); y++) {
                for (int x = 0; x < font.getWidths()[Math.min(unicode, 255)]; x++) {
                    if (font.getFontImage().getP()[(x + font.getOffsets()[Math.min(unicode, 255)]) + y * font.getFontImage().getWidth()] == 0xff000000) {
                        setPixel(x + offX + offset - 1, y + offY - 1, darken(color, 100));
                    }
                }
            }
            // With normal color
            for (int y = 0; y < font.getFontImage().getHeight(); y++) {
                for (int x = 0; x < font.getWidths()[Math.min(unicode, 255)]; x++) {
                    if (font.getFontImage().getP()[(x + font.getOffsets()[Math.min(unicode, 255)]) + y * font.getFontImage().getWidth()] == 0xff000000) {
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

        if (offX < -image.getWidth()) return;
        if (offY < -image.getHeight()) return;
        if (offX >= gc.getWidth()) return;
        if (offY >= gc.getHeight()) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        if (offX < 0) newX -= offX;
        if (offY < 0) newY -= offY;
        if (offX + newWidth >= gc.getWidth()) newWidth -= newWidth + offX - gc.getWidth();
        if (offY + newHeight >= gc.getHeight()) newHeight -= newHeight + offY - gc.getHeight();

        for (int y = newY; y < newHeight; y++) {
            for (int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getP()[x + y * image.getWidth()]);
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

        float plus = Game.TS / (float)tileSize;
        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                int position = (int)(x*plus + tileX * image.getW()) + (int)(y*plus + tileY * image.getH()) * image.getWidth();
                setPixel(x + offX, y + offY, image.getP()[Math.max(Math.min(position, image.getP().length-1), 0)]);
            }
        }
    }

    public void drawRect(int x, int y, int w, int h, int col) {

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
        if (x >= gc.getWidth()) return;
        if (y >= gc.getHeight()) return;

        int newX = 0;
        int newY = 0;
        int newWidth = w;
        int newHeight = h;

        if (x < 0) newX -= x;
        if (y < 0) newY -= y;
        if (x + newWidth >= gc.getWidth()) newWidth -= newWidth + x - gc.getWidth();
        if (y + newHeight >= gc.getHeight()) newHeight -= newHeight + y - gc.getHeight();

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

    public void drawButton(Button b, boolean isHover) {

        int col = isHover ? darken(b.getBgColor(), 20) : b.getBgColor();
        //Border-out
        drawRect(b.getOffX() + camX, b.getOffY() + camY, b.getWidth(), b.getHeight(), 0xff333333);
        //background & text
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, b.getWidth() - 2, b.getHeight() - 2, col);
        drawText(settings.translate(b.getText()), b.getOffX() + b.getWidth() / 2, b.getOffY() + b.getHeight() / 2, 0, 0, lighten(col, 100), Font.STANDARD);
        //Border-in lighter
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, 1, b.getHeight() - 2, lighten(col, 40));
        fillRect(b.getOffX() + camX + 2, b.getOffY() + camY + 1, b.getWidth() - 4, 1, lighten(col, 40));
        //Border-in darker
        fillRect(b.getOffX() + camX + b.getWidth() - 2, b.getOffY() + camY + 1, 1, b.getHeight() - 2, darken(col, 40));
        fillRect(b.getOffX() + camX + 2, b.getOffY() + camY + b.getHeight() - 2, b.getWidth() - 4, 1, darken(col, 40));
    }

    public void drawCheckbox(Checkbox c) {

        int col = c.getBgColor();
        //Border-out
        drawRect(c.getOffX() + camX, c.getOffY() + camY, c.getWidth(), c.getHeight(), 0xff333333);
        //background & text
        fillRect(c.getOffX() + camX + 1, c.getOffY() + camY + 1, c.getWidth() - 2, c.getHeight() - 2, col);

        if (c.isChecked()) {
            drawText("X", c.getOffX() + c.getWidth() / 2, c.getOffY() + c.getHeight() / 2, 0, 0, -1, Font.STANDARD);
        }

        //Border-in lighter
        fillRect(c.getOffX() + camX + 1, c.getOffY() + camY + 1, 1, c.getHeight() - 2, lighten(col, 40));
        fillRect(c.getOffX() + camX + 2, c.getOffY() + camY + 1, c.getWidth() - 4, 1, lighten(col, 40));
        //Border-in darker
        fillRect(c.getOffX() + camX + c.getWidth() - 2, c.getOffY() + camY + 1, 1, c.getHeight() - 2, darken(col, 40));
        fillRect(c.getOffX() + camX + 2, c.getOffY() + camY + c.getHeight() - 2, c.getWidth() - 4, 1, darken(col, 40));
    }

    public void drawTextInput(TextInput textInput, int x, int y, int w, int h, int color) {
        //Background & text
        fillRect(x, y, w, h, color);
        drawText(textInput.getText(), x + 4, y + h / 2, 1, 0, -1, Font.STANDARD);
        //Blink bar
        int blinkBarPos = textSize(textInput.getText().substring(0, textInput.getBlinkBarPos()), Font.STANDARD);
        fillRect(x + 4 + blinkBarPos, y + 3, 1, h - 6, -1);
        //Border-out darker
        fillRect(x, y, w, 1, darken(color, 40));
        fillRect(x - 1, y + 1, 1, h - 1, darken(color, 40));
        //Border-out lighter
        fillRect(x, y + h, w, 1, lighten(color, 40));
        fillRect(x + w, y + 1, 1, h - 1, lighten(color, 40));
    }

    void drawHUD(Entity obj) {
        int width = ts * 6;
        int x = gc.getWidth() / 2 - width / 2;
        fillRect(x, 0, width, ts, 0x99000000);

        drawSprite(objs, x, 0, 3, 2, ts);
        drawSprite(objs, x + ts * 2, 0, 5, 0, ts);
        drawSprite(objs, x + ts * 4, 0, 3, 1, ts);

        drawText("x" + Math.max(obj.getLives(), 0), x + ts - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getCoins(), x + ts * 3 - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getKeys(), x + ts * 5 - 4, ts, 1, -1, 0xffcdcdcd, Font.BIG_STANDARD);
    }

    public void drawLevel(boolean gameMode) {

        int offX = Math.max(camX/ts, 0);
        int offY = Math.max(camY/ts, 0);

        int endX = Math.min(((gc.getWidth()+camX)/ts)+1, gc.getWorld().getLevel().getWidth());
        int endY = Math.min(((gc.getHeight()+camY)/ts)+1, gc.getWorld().getLevel().getHeight());

        for (int y = offY; y < endY; y++) {
            for (int x = offX; x < endX; x++) {

                int tileX = gc.getWorld().getBlocMap(x, y).getX();
                int tileY = gc.getWorld().getBlocMap(x, y).getY();

                // Murs derriere les blocs non-solides
                drawSprite(objs, x * ts, y * ts, 1, 0, ts);

                // Affichage des blocs
                switch (gc.getWorld().getBlocMap(x, y).getTag()) {
                    case "free":
                        if (!gameMode) drawSprite(objs, x * ts, y * ts, tileX, tileY, ts);
                        break;
                    case "floor":
                        drawSprite(floor, x * ts, y * ts, getTileX(gc.getWorld(), x, y), getTileY(gc.getWorld(), x, y), ts);
                        break;
                    case "water":
                        drawSprite(water, x * ts, y * ts, 0, getWaterTile(gc.getWorld(), x, y), ts);
                        break;
                    default:
                        drawSprite(objs, x * ts, y * ts, tileX, tileY, ts);
                        break;
                }

                // Ombres sous les blocs
                if (!gc.getWorld().getBlocMap(x, y).isSolid() && solidTop(gc.getWorld(), x, y))
                    drawSprite(objs, x * ts, y * ts, 0, 3, ts);

                // Partie haute de la porte
                if (gc.getWorld().getBlocMap(x, y).isTagged("door"))
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

    public void drawLevelLights(Light lamp) { // A optimiser (recherche des lamps sur la zone camera)
        for (int y = 0; y < gc.getWorld().getLevel().getHeight(); y++) {
            for (int x = 0; x < gc.getWorld().getLevel().getWidth(); x++) {
                if (gc.getWorld().getBlocMap(x, y).isTagged("torch")) {
                    drawLight(lamp, x * ts + ts / 2, y * ts + ts / 3);
                }
            }
        }
    }

    private void drawBloc(String tag, int x, int y, int tileSize) {
        drawSprite(objs, x, y, gc.getWorld().getBloc(tag).getX(), gc.getWorld().getBloc(tag).getY(), tileSize);
    }

    public void drawDock(String[] dock, int scroll, int tileSize) {
        int midH = camY + gc.getHeight() / 2;
        int s = tileSize + 1;
        int y = midH - (dock.length * s) / 2 + (dock.length / 2 - scroll) * s;

        if (dock.length % 2 != 0)
            y += tileSize / 2;

        fillRect(camX, camY, s + 4, gc.getHeight(), 0x89000000);

        for (int i = 0; i < dock.length; i++) {
            drawBloc(dock[i], camX + 4, y - tileSize / 2 + s * i, tileSize);
        }

        drawRect(camX + 1, midH - tileSize / 2 - 3, s + 4, s + 4, 0xbbffffff);
        drawRect(camX + 2, midH - tileSize / 2 - 2, s + 2, s + 2, 0x77ffffff);
        drawRect(camX + 3, midH - tileSize / 2 - 1, s, s, 0x33ffffff);
    }

    public void drawMiniMap(Image img, int h) {
        Image thumb = img.getThumbnail(h*img.getWidth()/img.getHeight(), h);
        float diffW = thumb.getWidth() / (float)img.getWidth();
        float diffH = thumb.getHeight() / (float)img.getHeight();
        int xMMap = camX + gc.getWidth() - thumb.getWidth() - 4;
        int yMMap = camY + gc.getHeight() - thumb.getHeight() - 4;
        fillRect(xMMap, yMMap, thumb.getWidth(), thumb.getHeight(), 0x22ffffff);
        drawImage(thumb.setOpacity(0x99), xMMap, yMMap);
        drawRect(xMMap + (int)(camX/ ts * diffW), yMMap + (int)(camY/ ts * diffH),
                (int)(gc.getWidth() / ts * diffW), (int)(gc.getHeight() / ts * diffH), 0x66ffffff);
    }

    public void drawArrows(int width, int height, int tileSize) {
        if (camY > 0)
            drawBloc("arrow_up", camX + gc.getWidth() / 2 - tileSize / 2, camY, tileSize);
        if (camY + gc.getHeight() < height * this.ts)
            drawBloc("arrow_down", camX + gc.getWidth() / 2 - tileSize / 2, camY + gc.getHeight() - tileSize, tileSize);
        if (camX > 0)
            drawBloc("arrow_left", camX, camY + gc.getHeight() / 2 - tileSize / 2, tileSize);
        if (camX + gc.getWidth() < width * this.ts)
            drawBloc("arrow_right", camX + gc.getWidth() - tileSize, camY + gc.getHeight() / 2 - tileSize / 2, tileSize);
    }

    public void drawLevels(String[][] levels, PlayerStats ps) {

        int largest = 0;
        for (String[] level : levels) {
            int len = textSize(level[1], Font.STANDARD);
            if (len + 30 > largest) largest = len + 30;
        }

        int x = gc.getWidth() / 2 - largest / 2;
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

        int hTotal = gc.getHeight() - (3 * ts);
        int minus = Math.max(hUsed - hTotal, 0);

        drawScrollBar(gc.getWidth() / 2 + largest / 2 + 20, ts, 10, hTotal, GameSelection.scroll, minus);
    }

    public void drawCreaList(File[] files, Image[] imgs, String nothingMessage) {

        if (files == null || files.length == 0) {
            drawText(settings.translate(nothingMessage), gc.getWidth() / 2, gc.getHeight() / 2, 0, 0, 0xffababab, Font.STANDARD);
        } else {
            Date[] dates = new Date[files.length];
            int[] widths = new int[files.length];

            int largest = 0;
            for (int i = 0; i < files.length; i++) {

                widths[i] = Image.THUMBH * imgs[i].getWidth() / imgs[i].getHeight();
                dates[i] = new Date(files[i].lastModified());

                int size = max3(
                        textSize(files[i].getName(), Font.STANDARD),
                        textSize(dates[i].toString(), Font.STANDARD),
                        textSize("Dimensions: " + imgs[i].getWidth() + "x" + imgs[i].getHeight(), Font.STANDARD)
                );

                largest = Math.max(size + widths[i], largest);
            }

            int x = gc.getWidth() / 2 - largest / 2;
            int y = ts + 10 - CreativeMode.scroll - Image.THUMBH - 10;

            int hUsed = 10;
            for (int i = 0; i < files.length; i++) {

                y += Image.THUMBH + 10;
                if (CreativeMode.focus && i == CreativeMode.fIndex)
                    drawRect(x - 4, y - 4, largest + 12, Image.THUMBH + 8, 0xff696969);

                fillRect(x, y, widths[i], Image.THUMBH, -1);
                drawImage(imgs[i].getThumbnail(widths[i], Image.THUMBH), x, y);
                drawText(files[i].getName(), x + widths[i] + 4, y - 2, 1, 1, -1, Font.STANDARD);
                drawText("Dimensions: " + imgs[i].getWidth() + "x" + imgs[i].getHeight(), x + widths[i] + 4, y + Image.THUMBH/2, 1, 0, 0xff898989, Font.STANDARD);
                drawText(dates[i].toString(), x + widths[i] + 4, y + Image.THUMBH + 2, 1, -1, 0xff898989, Font.STANDARD);

                hUsed += Image.THUMBH + 10;
            }

            int hTotal = gc.getHeight() - 3 * ts;
            int minus = Math.max(hUsed - hTotal, 0);

            drawScrollBar(gc.getWidth() / 2 + largest / 2 + 20, ts, 8, hTotal, CreativeMode.scroll / 8, minus / 8);
        }
    }

    private int max3(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
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

    public void drawBackground() {
        for (int y = 0; y <= gc.getHeight() / ts; y++) {
            for (int x = 0; x <= gc.getWidth() / ts; x++) {
                drawBloc("wall", x* ts, y* ts, ts);
            }
        }
    }

    public void fillAreaBloc(int nX, int nY, int nW, int nH, String tag) {
        for (int y = 0; y < nH; y++) {
            for (int x = 0; x < nW; x++) {
                drawBloc(tag, nX + x * ts, nY + y * ts, ts);
            }
        }
    }

    public void drawMenuTitle(String title, String small) {
        drawText(settings.translate(title).toUpperCase(), gc.getWidth() /2, 45, 0, 1, 0xffc0392b, Font.BIG_STANDARD);
        if (small != null) {
            drawText(settings.translate(small), gc.getWidth() /2, 60, 0, 1, 0xffababab, Font.STANDARD);
        }
    }

    public void drawList(String title, String[] list, int offX, int offY) {
        drawText(settings.translate(title), offX, offY, 0, 0, 0xff27ae60, Font.STANDARD);
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

    public void zoomIn(int mouseX, int mouseY, int amount) {
        camX += (mouseX + camX) / ts;
        camY += (mouseY + camY) / ts;
        ts += amount;
    }

    public void zoomOut(int mouseX, int mouseY, int amount) {
        camX -= (mouseX + camX) / ts;
        camY -= (mouseY + camY) / ts;
        if (ts > amount) ts -= amount;
    }

    public void drawChrono(int chrono) {
        String text = "Chrono : ";
        drawText(text, 5, 10, 1, 1, -1, Font.STANDARD);
        drawText("" + chrono, textSize(text, Font.STANDARD) + 5, 5, 1, 1, -1, Font.BIG_STANDARD);
    }
}
