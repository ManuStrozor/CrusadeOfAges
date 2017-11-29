package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CreaMenu extends AbstractGame {

    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button crea, opt, save, menu;

    public CreaMenu() {
        select = new SoundClip("/audio/hover.wav");

        buttons.add(crea = new Button(130, 20, "Back to creative mode", 4));
        buttons.add(opt = new Button(130, 20, "Options", 3));
        buttons.add(save = new Button(130, 20, "Save level to saved.png", -1));
        buttons.add(menu = new Button(130, 20, "Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(4);

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0x99c0392b);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    if(btn == save)
                        Crea.creaMap.saveImage("saved");
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(5);
                }
            } else {
                btn.setBgColor(0x997f8c8d);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        crea.setOffY(gc.getHeight() / 3 - crea.getHeight() / 2);
        opt.setOffY(crea.getOffY() + crea.getHeight() + 10);
        save.setOffY(opt.getOffY() + opt.getHeight() + 10);
        menu.setOffY(save.getOffY() + save.getHeight() + 10);

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - btn.getWidth() / 2);
            r.drawButton(btn, 0xffababab);
        }
    }

    private boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX()+1 &&
                gc.getInput().getMouseX() <= b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY()+1 &&
                gc.getInput().getMouseY() <= b.getOffY() + b.getHeight();
    }
}
