package com.strozor.engine.gfx;

public class Bloc {

    private int id;
    private boolean solid;
    private String name;
    private int tileX, tileY;
    private float anim;

    public Bloc(int[] pix, int pos) {
        switch(pix[pos]) {
            default:
                name = "Wall";
                id = 0;
                tileX = 1;
                tileY = 0;
                solid = false;
                break;
            case 0xff00ff00:
                name = "Spawn";
                id = -1;
                tileX = 2;
                tileY = 0;
                solid = false;
                break;
            case 0xff000000:
                name = "Floor";
                id = 1;
                tileX = 0;
                tileY = 0;
                solid = true;
                break;
            case 0xffff648c:
                name = "Heart";
                id = 2;
                tileX = 3;
                tileY = 2;
                solid = false;
                break;
            case 0xffff0000:
                name = "Ground trap";
                id = 3;
                tileX = 1;
                tileY = 1;
                solid = false;
                break;
            case 0xffff00ff:
                name = "Ceiling trap";
                id = 4;
                tileX = 2;
                tileY = 1;
                solid = false;
                break;
            case 0xff0000ff:
                name = "Key";
                id = 5;
                tileX = 3;
                tileY = 1;
                solid = false;
                break;
            case 0xffff7700:
                name = "Check point";
                id = 6;
                tileX = 3;
                tileY = 0;
                solid = false;
                break;
            case 0xffffff00:
                name = "Coin";
                id = 7;
                tileX = 5;
                tileY = 0;
                solid = false;
                break;
            case 0xff009900:
                name = "Ladder";
                id = 8;
                tileX = 0;
                tileY = 1;
                solid = false;
                break;
            case 0xff990000:
                name = "Ground trap blooded";
                id = 9;
                tileX = 1;
                tileY = 2;
                solid = false;
                break;
            case 0xff990099:
                name = "Ceiling trap blooded";
                id = 10;
                tileX = 2;
                tileY = 2;
                solid = false;
                break;
            case 0xff00ffff:
                name = "Torch";
                id = 11;
                tileX = 4;
                tileY = 0;
                solid = false;
                break;
            case 0xff777777:
                name = "Slime bloc";
                id = 12;
                tileX = 0;
                tileY = 2;
                solid = true;
                break;
            case 0xff999999:
                name = "Door";
                id = 13;
                tileX = 5;
                tileY = 2;
                solid = false;
                break;
        }
        anim = 0;
    }

    public Bloc(int id) {
        switch(id) {
            case -1:
                name = "Spawn";
                tileX = 2;
                tileY = 0;
                solid = false;
                break;
            case 0:
                name = "Wall";
                tileX = 1;
                tileY = 0;
                solid = false;
                break;
            case 1:
                name = "Floor";
                tileX = 0;
                tileY = 0;
                solid = true;
                break;
            case 2:
                name = "Heart";
                tileX = 3;
                tileY = 2;
                solid = false;
                break;
            case 3:
                name = "Ground trap";
                tileX = 1;
                tileY = 1;
                solid = false;
                break;
            case 4:
                name = "Ceiling trap";
                tileX = 2;
                tileY = 1;
                solid = false;
                break;
            case 5:
                name = "Key";
                tileX = 3;
                tileY = 1;
                solid = false;
                break;
            case 6:
                name = "Check point";
                tileX = 3;
                tileY = 0;
                solid = false;
                break;
            case 7:
                name = "Coin";
                tileX = 5;
                tileY = 0;
                solid = false;
                break;
            case 8:
                name = "Ladder";
                tileX = 0;
                tileY = 1;
                solid = false;
                break;
            case 9:
                name = "Ground trap blooded";
                tileX = 1;
                tileY = 2;
                solid = false;
                break;
            case 10:
                name = "Ceiling trap blooded";
                tileX = 2;
                tileY = 2;
                solid = false;
                break;
            case 11:
                name = "Torch";
                tileX = 4;
                tileY = 0;
                solid = false;
                break;
            case 12:
                name = "Slime bloc";
                tileX = 0;
                tileY = 2;
                solid = true;
                break;
            case 13:
                name = "Door";
                tileX = 5;
                tileY = 2;
                solid = false;
                break;
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public String getName() {
        return name;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public float getAnim() {
        return anim;
    }

    public void setAnim(float anim) {
        this.anim = anim;
    }

    public void remove() {
        name = "Wall";
        id = 0;
        tileX = 1;
        tileY = 0;
        solid = false;
    }
}
