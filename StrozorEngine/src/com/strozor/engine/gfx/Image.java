package com.strozor.engine.gfx;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import javax.swing.JOptionPane;

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

    public void saveImage() {
        JOptionPane jop = new JOptionPane();

        String filename = jop.showInputDialog(null,
                "Give a name to your level map",
                "Skewer Maker - Creative Mode",
                JOptionPane.QUESTION_MESSAGE);

        JSONObject mainObj = new JSONObject();
        mainObj.put("name", filename);
        mainObj.put("width", w);
        mainObj.put("height", h);

        JSONArray jsonMap = new JSONArray();
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                int bloc = 0;
                switch(p[x + y * w]) {
                    case 0xff00ff00: bloc = -1;break;//Spawn
                    case 0x00000000: bloc = 0; break;//Wall
                    case 0xff000000: bloc = 1; break;//Floor
                    case 0xffff648c: bloc = 2; break;//Heart
                    case 0xffff0000: bloc = 3; break;//Bottom trap
                    case 0xffff00ff: bloc = 4; break;//Top trap
                    case 0xff0000ff: bloc = 5; break;//Key
                    case 0xffff7700: bloc = 6; break;//Check point
                    case 0xffffff00: bloc = 7; break;//Coin
                    case 0xff00ffff: bloc = 11;break;//Torch
                    case 0xff777777: bloc = 12;break;//Bouncing bloc
                    case 0xff999999: bloc = 13;break;//Door
                }
                jsonMap.put(bloc);
            }
        }
        mainObj.put("map", jsonMap);

        try(FileWriter file = new FileWriter(filename+".json")) {

            file.write(mainObj.toString());

            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for(int y = 0; y < h; y++) {
                for(int x = 0; x < w; x++) {
                    bi.setRGB(x, y, p[x + y * w]);
                }
            }
            File out = new File(filename+".png");
            ImageIO.write(bi, "png", out);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
