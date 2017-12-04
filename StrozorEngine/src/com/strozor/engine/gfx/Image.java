package com.strozor.engine.gfx;

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

    public Image(String path, boolean test) {
        BufferedImage image = null;

        try {
            image = test ? ImageIO.read(new File(path)) : ImageIO.read(Image.class.getResourceAsStream(path));
        } catch(IOException e) {
            e.printStackTrace();
        }

        w = image.getWidth();
        h = image.getHeight();
        p = image.getRGB(0, 0, w, h, null, 0, w);

        image.flush();
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
        try {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    bi.setRGB(x, y, p[x + y * w]);
                }
            }
            DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            File out = new File(sdf.format(new Date())+".png");
            ImageIO.write(bi, "png", out);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
