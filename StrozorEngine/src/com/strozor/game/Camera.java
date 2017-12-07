package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.GameMap;

public class Camera {

    private float offX, offY;
    private GameMap gameMap;
    private String targetTag;
    private GameObject target = null;

    Camera(String tag, GameMap gameMap) {
        this.targetTag = tag;
        this.gameMap = gameMap;
    }

    public void update(GameContainer gc, GameManager gm, float dt) {

        if(target == null) target = gm.getObject(targetTag);
        if(target == null) return;

        float targetX = (target.getPosX() + target.getWidth() / 2) - gc.getWidth() / 2;
        float targetY = (target.getPosY() + target.getHeight() / 2) - gc.getHeight() / 2;

        offX -= dt * (offX - targetX) * 10;
        offY -= dt * (offY - targetY) * 10;

        if(offX < 0) offX = 0;
        if(offY < 0) offY = 0;
        if(offX + gc.getWidth() > gameMap.getWidth() * GameManager.TS) offX = gameMap.getWidth() * GameManager.TS - gc.getWidth();
        if(offY + gc.getHeight() > gameMap.getHeight() * GameManager.TS) offY = gameMap.getHeight() * GameManager.TS - gc.getHeight();
    }

    public void render(GameRender r) {
        r.setCoorCam((int)offX, (int)offY);
    }
}
