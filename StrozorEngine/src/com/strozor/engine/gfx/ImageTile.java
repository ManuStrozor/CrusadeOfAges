package com.strozor.engine.gfx;

public class ImageTile extends Image {

    private int tileW, tileH;

    public ImageTile(String path, int tileW, int tileH, boolean ext) {
        super(path, ext);
        this.tileW = tileW;
        this.tileH = tileH;
    }

    public Image getTileImage(int tileX, int tileY) {
        int[] p = new int[tileW * tileH];

        System.arraycopy(this.getP(), (tileX * tileW) + (tileY * tileH) * this.getW(), p, 0, tileW * tileH);

        return new Image(p, tileW, tileH);
    }

    public int getTileW() {
        return tileW;
    }

    public int getTileH() {
        return tileH;
    }
}
