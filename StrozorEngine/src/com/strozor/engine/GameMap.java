package com.strozor.engine;

import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Image;

public class GameMap {

    private Bloc[] blocs;
    private int width, height, spawnX, spawnY;

    public GameMap() {}

    public void init(Image img) {
        this.width = img.getW();
        this.height = img.getH();
        int[] p = img.getP();

        blocs = new Bloc[width * height];

        for(int i = 0; i < width * height; i++) {
            if(p[i] == 0xff00ff00) {
                spawnY = i / width;
                spawnX = i - spawnY * width;
            }
            blocs[i] = new Bloc(p, i);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public Bloc getBloc(int x, int y) {
        return blocs[x + y * width];
    }

    public boolean isSolid(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height || blocs[x + y * width].isSolid();
    }

    public void animate(float speed) {
        for(int i = 0; i < width * height; i++) {
            blocs[i].setAnim(blocs[i].getAnim() + speed);
            if(blocs[i].getName().equals("Torch")) {
                if(blocs[i].getAnim() > 3) blocs[i].setAnim(0);
                blocs[i].setTileY((int) blocs[i].getAnim());
            }
        }
    }

    public void setBloc(int x, int y, int id) {
        blocs[x + y * width] = null;
        blocs[x + y * width] = new Bloc(id);
    }
}
