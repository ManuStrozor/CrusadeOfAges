package com.strozor.engine;

import com.strozor.engine.gfx.*;
import com.strozor.game.GameManager;
import com.strozor.game.GameObject;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameRender {

    private Settings s;
    private ImageTile objsImg = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);
    private ArrayList<ImageRequest> imageRequest = new ArrayList<>();

    private int pW, pH;
    private int[] p, zb, lm;
    private int camX, camY;
    private int zDepth = 0;
    private boolean processing = false;

    public GameRender(GameContainer gc, Settings settings) {
        s = settings;
        pW = gc.getWidth();
        pH = gc.getHeight();
        p = ((DataBufferInt)gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
        zb = new int[p.length];
        lm = new int[p.length];
    }

    void clear() {
        for(int i = 0; i < p.length; i++) {
            p[i] = 0;
            zb[i] = 0;
            lm[i] = 0xff898989;
        }
    }

    void process() {
        processing = true;

        Collections.sort(imageRequest, new Comparator<ImageRequest>(){
            @Override
            public int compare(ImageRequest i0, ImageRequest i1) {
                return Integer.compare(i0.zDepth, i1.zDepth);
            }
        });

        for(ImageRequest ir : imageRequest) {
            setzDepth(ir.zDepth);
            drawImage(ir.image, ir.offX, ir.offY);
        }

        for(int i = 0; i < p.length; i++) {

            int pR = (p[i] >> 16) & 255;
            int pG = (p[i] >> 8) & 255;
            int pB = p[i] & 255;

            int lR = (lm[i] >> 16) & 255;
            int lG = (lm[i] >> 8) & 255;
            int lB = lm[i] & 255;

            p[i] = (int)(pR * lR / 255f) << 16 | (int)(pG * lG / 255f) << 8 | (int)(pB * lB / 255f);
        }

        imageRequest.clear();
        processing = false;
    }

    private void setPixel(int x, int y, int value) {
        int alpha = (value >> 24) & 255;

        if((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0) return;

        int index = x + y * pW;

        if(zb[index] > zDepth) return;

        zb[index] = zDepth;

        if(alpha == 255) {
            p[index] = value;
        } else {
            int pR = (p[index] >> 16) & 255;
            int pG = (p[index] >> 8) & 255;
            int pB = p[index] & 255;

            int vR = (value >> 16) & 255;
            int vG = (value >> 8) & 255;
            int vB = value & 255;

            int newR = pR - (int)((pR - vR) * alpha / 255f);
            int newG = pG - (int)((pG - vG) * alpha / 255f);
            int newB = pB - (int)((pB - vB) * alpha / 255f);

            p[index] = newR << 16 | newG << 8 | newB;
        }
    }

    private void setLightMap(int x, int y, int value) {
        if(x < 0 || x >= pW || y < 0 || y >= pH) return;

        int baseColor = lm[x + y * pW];

        int maxR = Math.max((baseColor >> 16) & 255, (value >> 16) & 255);
        int maxG = Math.max((baseColor >> 8) & 255, (value >> 8) & 255);
        int maxB = Math.max(baseColor & 255, value & 255);

        lm[x + y * pW] = maxR << 16 | maxG << 8 | maxB;
    }

    private int darken(int color) {
        int val = 100;

        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        int newA = (a - val < 0 ? 0 : a - val) << 24;
        int newR = (r - val < 0 ? 0 : r - val) << 16;
        int newG = (g - val < 0 ? 0 : g - val) << 8;
        int newB = b - val < 0 ? 0 : b - val;

        return newA | newR | newG | newB;
    }

    public void drawText(String text, int offX, int offY, int alignX, int alignY, int color, Font font) {
        if(alignX != 1) {
            int textW = 0;
            for(int i = 0; i < text.length(); i++) textW += font.getWidths()[text.codePointAt(i)];
            if(alignX == 0) offX -= textW / 2;
            else if(alignX == -1) offX -= textW;
        }

        if(alignY != 1) {
            if(alignY == 0) offY -= font.getFontImage().getH() / 2;
            else if(alignY == -1) offY -= font.getFontImage().getH();
        }

        int offset = 0;
        for(int i = 0; i < text.length(); i++) {
            int unicode = text.codePointAt(i);
            //With darker color
            for(int y = 0; y < font.getFontImage().getH(); y++) {
                for(int x = 0; x < font.getWidths()[unicode]; x++) {
                    if(font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xff000000) {
                        setPixel(x + offX + offset - 1, y + offY - 1, darken(color));
                    }
                }
            }
            //With normal color
            for(int y = 0; y < font.getFontImage().getH(); y++) {
                for(int x = 0; x < font.getWidths()[unicode]; x++) {
                    if(font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xff000000) {
                        setPixel(x + offX + offset, y + offY, color);
                    }
                }
            }
            offset += font.getWidths()[unicode];
        }
    }

    private void drawImage(Image image, int offX, int offY) {

        offX -= camX;
        offY -= camY;

        if(image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image, zDepth, offX, offY));
            return;
        }

        if(offX < -image.getW()) return;
        if(offY < -image.getH()) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getW();
        int newHeight = image.getH();

        if(offX < 0) newX -= offX;
        if(offY < 0) newY -= offY;
        if(offX + newWidth >= pW) newWidth -= newWidth + offX - pW;
        if(offY + newHeight >= pH) newHeight -= newHeight + offY - pH;

        for(int y = newY; y < newHeight; y++) {
            for(int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getP()[x + y * image.getW()]);
            }
        }
    }

    public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {

        offX -= camX;
        offY -= camY;

        if(image.isAlpha() && !processing) {
            imageRequest.add(new ImageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
            return;
        }

        if(offX < -image.getTileW()) return;
        if(offY < -image.getTileH()) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = image.getTileW();
        int newHeight = image.getTileH();

        if(offX < 0) newX -= offX;
        if(offY < 0) newY -= offY;
        if(offX + newWidth >= pW) newWidth -= newWidth + offX - pW;
        if(offY + newHeight >= pH) newHeight -= newHeight + offY - pH;

        for(int y = newY; y < newHeight; y++) {
            for(int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, image.getP()[(x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getW()]);
            }
        }
    }

    private void drawRect(int offX, int offY, int width, int height, int color) {

        offX -= camX;
        offY -= camY;

        for(int y = 0; y <= height; y++) {
            setPixel(offX, y + offY, color);
            setPixel(offX + width, y + offY, color);
        }

        for(int x = 0; x <= width; x++) {
            setPixel(x + offX, offY, color);
            setPixel(x + offX, offY + height, color);
        }
    }

    private void fillRect(int offX, int offY, int width, int height, int color) {

        offX -= camX;
        offY -= camY;

        if(offX < -width) return;
        if(offY < -height) return;
        if(offX >= pW) return;
        if(offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if(offX < 0) newX -= offX;
        if(offY < 0) newY -= offY;
        if(offX + newWidth >= pW) newWidth -= newWidth + offX - pW;
        if(offY + newHeight >= pH) newHeight -= newHeight + offY - pH;

        for(int y = newY; y < newHeight; y++) {
            for(int x = newX; x < newWidth; x++) {
                setPixel(x + offX, y + offY, color);
            }
        }
    }

    private void drawLight(Light l, int offX, int offY) {

        offX -= camX;
        offY -= camY;

        for(int i = 0; i < l.getDiameter(); i++) {
            drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
            drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), offX, offY);
            drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
            drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);
        }
    }

    private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offX, int offY) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while(true) {

            int screenX = x0 - l.getRadius() + offX;
            int screenY = y0 - l.getRadius() + offY;

            int lightColor = l.getLightValue(x0, y0);
            if(lightColor == 0) return;

            setLightMap(screenX, screenY, lightColor);

            if(x0 == x1 && y0 == y1) break;

            e2 = 2 * err;

            if(e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if(e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void drawButton(Button b, String text) {
        drawRect(b.getOffX() + camX, b.getOffY() + camY, b.getWidth(), b.getHeight(), 0xffababab);
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, b.getWidth() - 1, b.getHeight() - 1, b.getBgColor());
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, 1, b.getHeight() - 1, 0x99636363);
        fillRect(b.getOffX() + camX + 1, b.getOffY() + camY + 1, b.getWidth() - 1, 1, 0x99636363);
        drawText(text, b.getOffX() + b.getWidth() / 2, b.getOffY() + b.getHeight() / 2, 0, 0, 0xffababab, Font.STANDARD);
    }

    void drawGameStates(GameContainer gc, GameObject obj) {
        int width = GameManager.TS * 6;
        int x = gc.getWidth() / 2 - width / 2;
        fillRect(x, 0, width, GameManager.TS, 0x99000000);
        drawBloc(new Bloc(2), x, 0);
        drawBloc(new Bloc(7), x + GameManager.TS * 2, 0);
        drawBloc(new Bloc(5), x + GameManager.TS * 4, 0);
        drawText("x" + obj.getLives(), x + GameManager.TS-4, GameManager.TS, 1, -1,0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getCoins(), x + GameManager.TS * 3-4, GameManager.TS, 1, -1,0xffcdcdcd, Font.BIG_STANDARD);
        drawText("x" + obj.getKeys(), x + GameManager.TS * 5-4, GameManager.TS, 1, -1,0xffcdcdcd, Font.BIG_STANDARD);
    }

    public void drawMap(GameMap gameMap) {
        for(int y = 0; y < gameMap.getHeight(); y++) {
            for(int x = 0; x < gameMap.getWidth(); x++) {

                Bloc curr = gameMap.getBloc(x, y);

                //draw wall behind non-solid bloc
                if(!curr.isSolid() && !curr.getName().equals("Wall"))
                    drawImageTile(objsImg, x * GameManager.TS, y * GameManager.TS, 1, 0);

                //draw bloc
                drawImageTile(objsImg, x * GameManager.TS, y * GameManager.TS, curr.getTileX(), curr.getTileY());

                //draw shadow under solid bloc
                if(!curr.isSolid() && gameMap.isSolid(x, y-1))
                    drawImageTile(objsImg, x * GameManager.TS, y * GameManager.TS, 0, 3);

                //draw top part of the door
                if(curr.getName().equals("Door"))
                    drawImageTile(objsImg, x * GameManager.TS, (y - 1) * GameManager.TS, curr.getTileX(), curr.getTileY()-1);
            }
        }
    }

    public void drawMapLights(GameMap gameMap, Light lamp) {
        for(int y = 0; y < gameMap.getHeight(); y++) {
            for(int x = 0; x < gameMap.getWidth(); x++) {
                if(gameMap.getBloc(x, y).getName().equals("Torch"))
                    drawLight(lamp, x * GameManager.TS + GameManager.TS / 2, y * GameManager.TS + GameManager.TS / 3);
            }
        }
    }

    private void drawBloc(Bloc bloc, int x, int y) {
        drawImageTile(objsImg, x, y, bloc.getTileX(), bloc.getTileY());
    }

    public void drawDock(GameContainer gc, int[] elems, int selected) {

        int offX = gc.getWidth() / 2 - (elems.length * (GameManager.TS + 5)) / 2 + camX;
        int offY = gc.getHeight() - GameManager.TS - 6 + camY;
        int size = GameManager.TS + 1;
        int width = elems.length * (size + 4);

        for(int i = 0; i < elems.length; i++) {
            drawRect(offX - 3 + (size + 4) * i, offY - 3, size + 4, size + 4, 0xbbc4c4c4);
            drawRect(offX - 2 + (size + 4) * i, offY - 2, size + 2, size + 2, 0x77c4c4c4);
            drawRect(offX - 1 + (size + 4) * i, offY - 1, size, size, 0x33c4c4c4);

            fillRect(offX + (size + 4) * i, offY, GameManager.TS, GameManager.TS, 0x99000000);
            drawBloc(new Bloc(elems[i]), offX + (size + 4) * i, offY);
        }
        drawRect(offX - 4, offY - 4, width + 2, size + 6, 0xff000000);

        drawRect(offX - 4 + (size + 4) * selected, offY - 4, size + 6, size + 5, -1);
        drawRect(offX - 3 + (size + 4) * selected, offY - 3, size + 4, size + 4, 0xbbffffff);
        drawRect(offX - 2 + (size + 4) * selected, offY - 2, size + 2, size + 2, 0x77ffffff);
        drawRect(offX - 1 + (size + 4) * selected, offY - 1, size, size, 0x33ffffff);

        Bloc b = new Bloc(elems[selected]);
        drawText(s.translate(b.getName()), offX + width / 2 - camX, offY - 6 - camY, 0, -1, 0xffababab, Font.STANDARD);
    }

    public void drawBackground(GameContainer gc, Bloc bloc) {
        for(int y = 0; y <= gc.getHeight() / GameManager.TS; y++) {
            for(int x = 0; x <= gc.getWidth() / GameManager.TS; x++) {
                drawBloc(bloc, x * GameManager.TS, y * GameManager.TS);
            }
        }
    }

    public void drawMenuTitle(GameContainer gc, String bigTitle, String smallTitle) {
        drawText(bigTitle, gc.getWidth() / 2, 45, 0, 1, 0xffc0392b, Font.BIG_STANDARD);
        if(!smallTitle.equals(""))
            drawText(smallTitle, gc.getWidth() / 2, 60, 0, 1, 0xffababab, Font.STANDARD);
    }

    public void drawList(int offX, int offY, String title, String[] list) {
        drawText(title, offX, offY, 0, 0, 0xff27ae60, Font.STANDARD);
        for(int i = 0; i < list.length; i++)
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
}
