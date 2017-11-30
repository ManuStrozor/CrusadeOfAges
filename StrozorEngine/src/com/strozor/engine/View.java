package com.strozor.engine;

import com.strozor.engine.gfx.Button;

public abstract class View {

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

    public boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX()+1 &&
                gc.getInput().getMouseX() <= b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY()+1 &&
                gc.getInput().getMouseY() <= b.getOffY() + b.getHeight();
    }
}