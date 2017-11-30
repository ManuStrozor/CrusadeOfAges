package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;

public abstract class GameObject {

    protected String tag;
    protected float posX, posY;
    protected int width, height;
    protected int lives, coins, keys;
    protected boolean dead = false;

    public abstract void update(GameContainer gc, GameManager gm, float dt);
    public abstract void render(GameContainer gc, GameManager gm, GameRender r);

    public String getTag() {
        return tag;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getLives() {
        return lives;
    }

    public int getCoins() {
        return coins;
    }

    public int getKeys() {
        return keys;
    }
}
