package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Map;

public class Camera {

    private float offX, offY;
    private Map map;
    private String targetTag;
    private GameObject target = null;

    public Camera(String tag, Map map) {
        this.targetTag = tag;
        this.map = map;
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
        if(offX + gc.getWidth() > map.getWidth() * GameManager.TS) offX = map.getWidth() * GameManager.TS - gc.getWidth();
        if(offY + gc.getHeight() > map.getHeight() * GameManager.TS) offY = map.getHeight() * GameManager.TS - gc.getHeight();
    }

    public void render(GameRender r) {
        r.setCoorCam((int)offX, (int)offY);
    }
}
