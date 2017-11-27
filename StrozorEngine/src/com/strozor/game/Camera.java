package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;

public class Camera {

    private float offX, offY;

    private String targetTag;
    private GameObject target = null;

    public Camera(String tag) {
        this.targetTag = tag;
    }

    public void update(GameContainer gc, GameManager gm, float dt) {

        if(target == null) target = gm.getObject(targetTag);
        if(target == null) return;

        float targetX = (target.getPosX() + target.getWidth() / 2) - gc.getWidth() / 2;
        float targetY = (target.getPosY() + target.getHeight() / 2) - gc.getHeight() / 2;

        offX -= dt * (offX - targetX) * 8;
        offY -= dt * (offY - targetY) * 8;

        if(offX < 0) offX = 0;
        if(offY < 0) offY = 0;
        if(offX + gc.getWidth() > gm.getLevelW() * gm.TS) offX = gm.getLevelW() * gm.TS - gc.getWidth();
        if(offY + gc.getHeight() > gm.getLevelH() * gm.TS) offY = gm.getLevelH() * gm.TS - gc.getHeight();
    }

    public void render(GameRender r) {
        r.setCamX((int)offX);
        r.setCamY((int)offY);
    }
}
