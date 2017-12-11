package com.strozor.engine;

import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Image;
import com.strozor.game.GameManager;
import com.strozor.view.Rename;

import java.awt.event.KeyEvent;
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

    protected void inputCtrl(GameContainer gc) {
        if(gc.getInput().isKey(KeyEvent.VK_SHIFT)) {
            for(int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if(gc.getInput().isKeyDown(k)) Rename.input += (char) ('A' + i);
            }
            for(int k = KeyEvent.VK_0, i = 0; k <= KeyEvent.VK_9; k++, i++) {
                if(gc.getInput().isKeyDown(k)) Rename.input += (char) ('0' + i);
            }
            if(gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) Rename.input += '.';
        } else {
            for(int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if(gc.getInput().isKeyDown(k)) Rename.input += (char) ('a' + i);
            }
            if(gc.getInput().isKeyDown(KeyEvent.VK_0)) Rename.input += 'à';
            if(gc.getInput().isKeyDown(KeyEvent.VK_2)) Rename.input += 'é';
            if(gc.getInput().isKeyDown(KeyEvent.VK_4)) Rename.input += '\'';
            if(gc.getInput().isKeyDown(KeyEvent.VK_5)) Rename.input += '(';
            if(gc.getInput().isKeyDown(KeyEvent.VK_6)) Rename.input += '-';
            if(gc.getInput().isKeyDown(KeyEvent.VK_7)) Rename.input += 'è';
            if(gc.getInput().isKeyDown(KeyEvent.VK_8)) Rename.input += '_';
            if(gc.getInput().isKeyDown(KeyEvent.VK_9)) Rename.input += 'ç';
            if(gc.getInput().isKeyDown(KeyEvent.VK_COMMA)) Rename.input += ',';
            if(gc.getInput().isKeyDown(KeyEvent.VK_EQUALS)) Rename.input += '=';
            if(gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) Rename.input += ';';
        }

        for(int k = KeyEvent.VK_NUMPAD0, i = 0; k <= KeyEvent.VK_NUMPAD9; k++, i++) {
            if(gc.getInput().isKeyDown(k)) Rename.input += (char) ('0' + i);
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) Rename.input += ' ';
        if(gc.getInput().isKeyDown(KeyEvent.VK_BACK_SPACE) && Rename.input.length() != 0)
            Rename.input = Rename.input.substring(0, Rename.input.length() - 1);
    }
}