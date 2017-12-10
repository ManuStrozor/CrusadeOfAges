package com.strozor.engine;

import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Image;
import com.strozor.game.GameManager;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class View {

    protected ArrayList<Button> buttons = new ArrayList<>();

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

    protected boolean isSelected(GameContainer gc, Button b) {
        return mouseIsHover(gc, b) && gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    private boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX() &&
                gc.getInput().getMouseX() < b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY() &&
                gc.getInput().getMouseY() < b.getOffY() + b.getHeight();
    }

    protected boolean mouseIsOnYPos(GameContainer gc, ArrayList<Image> images, int index, int scroll) {

        int heights = GameManager.TS+10-scroll;
        for(int i = 0; i < index; i++) {
            heights += images.get(i).getH() < 30 ? 30+10 : images.get(i).getH()+10;
        }
        int imageSize = images.get(index).getH() < 30 ? 30 : images.get(index).getH();

        return gc.getInput().getMouseY() > heights &&
                gc.getInput().getMouseY() < heights+imageSize &&
                gc.getInput().getMouseY() < gc.getHeight()-2*GameManager.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }
}