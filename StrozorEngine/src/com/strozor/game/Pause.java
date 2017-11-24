package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Pause extends AbstractGame {

    private SoundClip hover, select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, opt, menu;

    public Pause() {
        hover = new SoundClip("/audio/hover.wav");
        select = new SoundClip("/audio/select.wav");

        buttons.add(play = new Button(130, 20, "Continuer", 1));
        buttons.add(opt = new Button(130, 20, "Options", 3));
        buttons.add(menu = new Button(130, 20, "Menu principal", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(1);

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                if(!btn.isHoverSounded()) {
                    hover.stop();
                    hover.play();
                }
                btn.setBgColor(0x99c0392b);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(2);
                }
            } else {
                btn.setBgColor(0x997f8c8d);
            }
            btn.setHoverSounded(mouseIsHover(gc, btn));
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        play.setOffX(gc.getWidth() / 2 - play.getWidth() / 2);
        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);

        opt.setOffX(play.getOffX());
        opt.setOffY(play.getOffY() + play.getHeight() + 10);

        menu.setOffX(play.getOffX());
        menu.setOffY(opt.getOffY() + opt.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn, 0xffababab);
    }

    private boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX()+1 &&
                gc.getInput().getMouseX() <= b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY()+1 &&
                gc.getInput().getMouseY() <= b.getOffY() + b.getHeight();
    }
}
