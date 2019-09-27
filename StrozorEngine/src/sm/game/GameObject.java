package sm.game;

import sm.engine.GameContainer;
import sm.engine.GameRender;

public abstract class GameObject {

    protected String tag;
    protected float posX, posY;
    protected int width, height;
    protected int lives, coins, keys, skulls;
    protected boolean dead = false;

    public abstract void update(GameContainer gc, GameManager gm, float dt);
    public abstract void creativeUpdate(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

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
