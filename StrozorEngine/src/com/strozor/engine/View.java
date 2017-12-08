package com.strozor.engine;

import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class View {

    protected ArrayList<Button> buttons = new ArrayList<>();
    protected int focus = 0;

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

    protected boolean isSelected(GameContainer gc, Button b) {
        return (mouseIsHover(gc, b) && gc.getInput().isButtonDown(MouseEvent.BUTTON1)) ||
                (b == buttons.get(focus) && gc.getInput().isKeyDown(KeyEvent.VK_ENTER));
    }

    protected boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX() &&
                gc.getInput().getMouseX() < b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY() &&
                gc.getInput().getMouseY() < b.getOffY() + b.getHeight();
    }

    protected void focusCtrl(GameContainer gc) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_DOWN)) focus++;
        if(gc.getInput().isKeyDown(KeyEvent.VK_UP)) focus--;
        if(gc.getInput().isKeyDown(KeyEvent.VK_RIGHT)) focus++;
        if(gc.getInput().isKeyDown(KeyEvent.VK_LEFT)) focus--;

        if(focus < 0) focus = buttons.size() - 1;
        else if(focus == buttons.size()) focus = 0;

        for(int i = 0; i < buttons.size(); i++) {
            if(i == focus) buttons.get(i).setBgColor(0xff263238);
            else buttons.get(i).setBgColor(0xff424242);
        }
    }
}