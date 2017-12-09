package com.strozor.engine.gfx;

public class Bloc {

    private int id, code;
    private boolean solid;
    private String name;
    private int tileX, tileY;
    private float anim;

    public Bloc(int[] pArray, int pos) {
        switch(pArray[pos]) {
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
                name = "Health pill";
                id = 2;
                tileX = 3;
                tileY = 2;
                solid = false;
                break;
            case 0xffff0000:
                name = "Ground spikes";
                id = 3;
                tileX = 1;
                tileY = 1;
                solid = false;
                break;
            case 0xffff00ff:
                name = "Ceiling spikes";
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
                name = "Ground spikes blooded";
                id = 9;
                tileX = 1;
                tileY = 2;
                solid = false;
                break;
            case 0xff990099:
                name = "Ceiling spikes blooded";
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
            case 0x4200ff00:
                name = "Arrow up";
                id = 14;
                tileX = 6;
                tileY = 3;
                solid = false;
                break;
            case 0x42000000:
                name = "Arrow down";
                id = 15;
                tileX = 6;
                tileY = 0;
                solid = false;
                break;
            case 0x42ff0000:
                name = "Arrow left";
                id = 16;
                tileX = 6;
                tileY = 1;
                solid = false;
                break;
            case 0x420000ff:
                name = "Arrow right";
                id = 17;
                tileX = 6;
                tileY = 2;
                solid = false;
                break;
            case 0x66000000:
                name = "Under shadow";
                id = 18;
                tileX = 0;
                tileY = 3;
                solid = false;
                break;
            case 0x69000000:
                name = "Above shadow";
                id = 19;
                tileX = 0;
                tileY = 4;
                solid = false;
                break;
        }
        anim = 0;
    }

    public Bloc(int id) {
        switch(id) {
            case -1:
                code = 0xff00ff00;
                name = "Spawn";
                tileX = 2;
                tileY = 0;
                solid = false;
                break;
            case 0:
                code = 0x00000000;
                name = "Wall";
                tileX = 1;
                tileY = 0;
                solid = false;
                break;
            case 1:
                code = 0xff000000;
                name = "Floor";
                tileX = 0;
                tileY = 0;
                solid = true;
                break;
            case 2:
                code = 0xffff648c;
                name = "Health pill";
                tileX = 3;
                tileY = 2;
                solid = false;
                break;
            case 3:
                code = 0xffff0000;
                name = "Ground spikes";
                tileX = 1;
                tileY = 1;
                solid = false;
                break;
            case 4:
                code = 0xffff00ff;
                name = "Ceiling spikes";
                tileX = 2;
                tileY = 1;
                solid = false;
                break;
            case 5:
                code = 0xff0000ff;
                name = "Key";
                tileX = 3;
                tileY = 1;
                solid = false;
                break;
            case 6:
                code = 0xffff7700;
                name = "Check point";
                tileX = 3;
                tileY = 0;
                solid = false;
                break;
            case 7:
                code = 0xffffff00;
                name = "Coin";
                tileX = 5;
                tileY = 0;
                solid = false;
                break;
            case 8:
                code = 0xff009900;
                name = "Ladder";
                tileX = 0;
                tileY = 1;
                solid = false;
                break;
            case 9:
                code = 0xff690000;
                name = "Ground spikes blooded";
                tileX = 1;
                tileY = 2;
                solid = false;
                break;
            case 10:
                code = 0xff690069;
                name = "Ceiling spikes blooded";
                tileX = 2;
                tileY = 2;
                solid = false;
                break;
            case 11:
                code = 0xff00ffff;
                name = "Torch";
                tileX = 4;
                tileY = 0;
                solid = false;
                break;
            case 12:
                code = 0xff777777;
                name = "Slime bloc";
                tileX = 0;
                tileY = 2;
                solid = true;
                break;
            case 13:
                code = 0xff999999;
                name = "Door";
                tileX = 5;
                tileY = 2;
                solid = false;
                break;
            case 14:
                code = 0x4200ff00;
                name = "Arrow up";
                tileX = 6;
                tileY = 3;
                solid = false;
                break;
            case 15:
                code = 0x42000000;
                name = "Arrow down";
                tileX = 6;
                tileY = 0;
                solid = false;
                break;
            case 16:
                code = 0x42ff0000;
                name = "Arrow left";
                tileX = 6;
                tileY = 1;
                solid = false;
                break;
            case 17:
                code = 0x420000ff;
                name = "Arrow right";
                tileX = 6;
                tileY = 2;
                solid = false;
                break;
            case 18:
                code = 0x66000000;
                name = "Under shadow";
                tileX = 0;
                tileY = 3;
                solid = false;
                break;
            case 19:
                code = 0x69000000;
                name = "Above shadow";
                tileX = 0;
                tileY = 4;
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

    public int getCode() {
        return code;
    }

    public void remove() {
        name = "Wall";
        id = 0;
        tileX = 1;
        tileY = 0;
        solid = false;
    }
}
