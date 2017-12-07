package com.strozor.engine.gfx;

import com.strozor.game.GameManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image {

    private int w, h;
    private int[] p;
    private boolean alpha = false;

    public Image(String path, boolean ext) {
        try {
            BufferedImage image = ext ? ImageIO.read(new File(path)) : ImageIO.read(Image.class.getResourceAsStream(path));
            w = image.getWidth();
            h = image.getHeight();
            p = image.getRGB(0, 0, w, h, null, 0, w);
            image.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Image(int[] p, int w, int h) {
        this.p = p;
        this.w = w;
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int x, int y, int value) {
        this.p[x + y * getW()] = value;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void saveIt() {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                bi.setRGB(x, y, p[x + y * w]);
            }
        }
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            File out = new File(GameManager.APPDATA + "\\creative_mode\\" + sdf.format(new Date()) + ".png");
            ImageIO.write(bi, "png", out);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
