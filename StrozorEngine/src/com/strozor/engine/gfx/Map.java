package com.strozor.engine.gfx;

public class Map {

    private int width, height, spawnX, spawnY;
    private String[] name;
    private int[] id, tileX, tileY;
    private float[] anim;
    private boolean[] solid;

    public Map() {}

    public void initMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.name = new String[width * height];
        this.id = new int[width * height];
        this.tileX = new int[width * height];
        this.tileY = new int[width * height];
        this.anim = new float[width * height];
        this.solid = new boolean[width * height];
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

    public String getName(int x, int y) {
        return name[x + y * width];
    }

    public int getId(int x, int y) {
        return id[x + y * width];
    }

    public int getTileX(int x, int y) {
        return tileX[x + y * width];
    }

    public int getTileY(int x, int y) {
        return tileY[x + y * width];
    }

    public boolean getSolid(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height || solid[x + y * width];
    }

    public void animate(float speed) {
        for(int i = 0; i < width * height; i++) {
            anim[i] += speed;
            if(name[i].equals("Torch")) tileY[i] = (int) (anim[i] > 3 ? anim[i] = 0 : anim[i]);
        }
    }

    public void setBloc(int x, int y, int id) {
        int index = x + y * width;
        switch(id) {
            case -1:
                tileX[index] = 2;
                tileY[index] = 0;
                name[index] = "Spawn";
                solid[index] = false;
                break;
            case 0:
                tileX[index] = 1;
                tileY[index] = 0;
                name[index] = "Wall";
                solid[index] = false;
                break;
            case 1:
                tileX[index] = 0;
                tileY[index] = 0;
                name[index] = "Floor";
                solid[index] = true;
                break;
            case 2:
                tileX[index] = 3;
                tileY[index] = 2;
                name[index] = "Heart";
                solid[index] = false;
                break;
            case 3:
                tileX[index] = 1;
                tileY[index] = 1;
                name[index] = "Bottom trap";
                solid[index] = false;
                break;
            case 4:
                tileX[index] = 2;
                tileY[index] = 1;
                name[index] = "Top trap";
                solid[index] = false;
                break;
            case 5:
                tileX[index] = 3;
                tileY[index] = 1;
                name[index] = "Key";
                solid[index] = false;
                break;
            case 6:
                tileX[index] = 3;
                tileY[index] = 0;
                name[index] = "Check point";
                solid[index] = false;
                break;
            case 7:
                tileX[index] = 5;
                tileY[index] = 0;
                anim[index] = 0;
                name[index] = "Coin";
                solid[index] = false;
                break;
            case 8:
                tileX[index] = 0;
                tileY[index] = 1;
                name[index] = "Ladder";
                solid[index] = false;
                break;
            case 9:
                tileX[index] = 1;
                tileY[index] = 2;
                name[index] = "";
                solid[index] = false;
                break;
            case 10:
                tileX[index] = 2;
                tileY[index] = 2;
                name[index] = "";
                solid[index] = false;
                break;
            case 11:
                tileX[index] = 4;
                tileY[index] = 0;
                anim[index] = 0;
                name[index] = "Torch";
                solid[index] = false;
                break;
            case 12:
                tileX[index] = 0;
                tileY[index] = 2;
                name[index] = "Bouncing bloc";
                solid[index] = true;
                break;
            case 13:
                tileX[index] = 5;
                tileY[index] = 2;
                name[index] = "Door";
                solid[index] = false;
                break;
        }
        this.id[index] = id;
    }

    public void initBloc(int x, int y, int color) {
        int index = x + y * width;
        switch(color) {
            default:
                tileX[index] = 1;
                tileY[index] = 0;
                name[index] = "Wall";
                solid[index] = false;
                id[index] = 0;
                break;
            case 0xff00ff00:
                spawnX = x;
                spawnY = y;
                tileX[index] = 2;
                tileY[index] = 0;
                name[index] = "Spawn";
                solid[index] = false;
                id[index] = -1;
                break;
            case 0xff000000:
                tileX[index] = 0;
                tileY[index] = 0;
                name[index] = "Floor";
                solid[index] = true;
                id[index] = 1;
                break;
            case 0xffff648c:
                tileX[index] = 3;
                tileY[index] = 2;
                name[index] = "Heart";
                solid[index] = false;
                id[index] = 2;
                break;
            case 0xffff0000:
                tileX[index] = 1;
                tileY[index] = 1;
                name[index] = "Bottom trap";
                solid[index] = false;
                id[index] = 3;
                break;
            case 0xffff00ff:
                tileX[index] = 2;
                tileY[index] = 1;
                name[index] = "Top trap";
                solid[index] = false;
                id[index] = 4;
                break;
            case 0xff0000ff:
                tileX[index] = 3;
                tileY[index] = 1;
                name[index] = "Key";
                solid[index] = false;
                id[index] = 5;
                break;
            case 0xffff7700:
                tileX[index] = 3;
                tileY[index] = 0;
                name[index] = "Check point";
                solid[index] = false;
                id[index] = 6;
                break;
            case 0xffffff00:
                tileX[index] = 5;
                tileY[index] = 0;
                anim[index] = 0;
                name[index] = "Coin";
                solid[index] = false;
                id[index] = 7;
                break;
            case 0xff009900:
                tileX[index] = 0;
                tileY[index] = 1;
                name[index] = "Ladder";
                solid[index] = false;
                id[index] = 8;
                break;
            case 0xff990000:
                tileX[index] = 1;
                tileY[index] = 2;
                name[index] = "Bottom trap blooded";
                solid[index] = false;
                id[index] = 9;
                break;
            case 0xff990099:
                tileX[index] = 2;
                tileY[index] = 2;
                name[index] = "Top trap blooded";
                solid[index] = false;
                id[index] = 10;
                break;
            case 0xff00ffff:
                tileX[index] = 4;
                tileY[index] = 0;
                anim[index] = 0;
                name[index] = "Torch";
                solid[index] = false;
                id[index] = 11;
                break;
            case 0xff777777:
                tileX[index] = 0;
                tileY[index] = 2;
                name[index] = "Bouncing bloc";
                solid[index] = true;
                id[index] = 12;
                break;
            case 0xff999999:
                tileX[index] = 5;
                tileY[index] = 2;
                name[index] = "Door";
                solid[index] = false;
                id[index] = 13;
                break;
        }
    }
}
