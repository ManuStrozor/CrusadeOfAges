package sm.engine.gfx;

import sm.game.Game;

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

    /**
     * Creates Image object using image from : [True] AppData or [False] game assets
     * @param path
     * @param appdata True / False
     */
    public Image(String path, boolean appdata) {
        try {
            BufferedImage image = appdata ? ImageIO.read(new File(path)) : ImageIO.read(Image.class.getResourceAsStream(path));
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

    /**
     * Returns pixels (array of rgba codes) of Image
     * @return int[]
     */
    public int[] getP() {
        return p;
    }

    public void setP(int x, int y, int value) {
        this.p[x + y * getW()] = value;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public void saveIt(String rename) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                bi.setRGB(x, y, p[x + y * w]);
            }
        }
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
            String filename;
            if(rename.equals("")) filename = sdf.format(new Date()) + ".png";
            else filename = rename;
            File out = new File(Game.APPDATA + "/creative_mode/" + filename);
            ImageIO.write(bi, "png", out);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
