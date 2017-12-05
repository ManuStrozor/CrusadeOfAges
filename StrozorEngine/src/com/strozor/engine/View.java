package com.strozor.engine;

import com.strozor.engine.gfx.Button;

public abstract class View {

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

    protected boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX() &&
                gc.getInput().getMouseX() < b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY() &&
                gc.getInput().getMouseY() < b.getOffY() + b.getHeight();
    }
}