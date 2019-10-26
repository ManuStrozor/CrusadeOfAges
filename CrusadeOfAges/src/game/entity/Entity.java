package game.entity;

import engine.GameContainer;
import engine.Renderer;
import game.Game;

public abstract class Entity {

    protected String tag;
    protected float posX, posY;
    protected int width, height;
    private int lives, coins, keys, skulls;
    private boolean dead = false;

    public abstract void update(GameContainer gc, float dt);

    public abstract void render(GameContainer gc, Renderer r);

    public String getTag() {
        return tag;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
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

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public int getSkulls() {
        return skulls;
    }

    public void setSkulls(int skulls) {
        this.skulls = skulls;
    }
}
