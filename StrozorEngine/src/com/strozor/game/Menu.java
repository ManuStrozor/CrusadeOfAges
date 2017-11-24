package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Button;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Menu extends AbstractGame {

    private ImageTile background;
    private SoundClip hover, select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, crea, opt, exit;

    public Menu() {
        background = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);

        hover = new SoundClip("/audio/hover.wav");
        select = new SoundClip("/audio/select.wav");

        buttons.add(play = new Button(130, 20, "Un joueur", 1));
        buttons.add(crea = new Button(130, 20, "Creer un niveau", 4));
        buttons.add(opt = new Button(60, 20, "Options", 3));
        buttons.add(exit = new Button(60, 20, "Quitter", -1));
    }

    @Override
    public void update(GameContainer gc, float dt) {

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
                    gc.setLastState(0);
                }
            } else {
                btn.setBgColor(0x997f8c8d);
            }
            btn.setHoverSounded(mouseIsHover(gc, btn));
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        for(int y = 0; y <= gc.getHeight() / GameManager.TS; y++) {
            for(int x = 0; x <= gc.getWidth() / GameManager.TS; x++) {
                r.drawImageTile(background, x * GameManager.TS, y * GameManager.TS, 1, 0);
            }
        }

        play.setOffX(gc.getWidth() / 2 - play.getWidth() / 2);
        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);

        crea.setOffX(play.getOffX());
        crea.setOffY(play.getOffY() + play.getHeight() + 10);

        opt.setOffX(play.getOffX());
        opt.setOffY(crea.getOffY() + crea.getHeight() + 10);

        exit.setOffX(gc.getWidth() / 2 + 5);
        exit.setOffY(opt.getOffY());

        for(Button btn : buttons) r.drawButton(btn, 0xffababab);
    }

    private boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX()+1 &&
                gc.getInput().getMouseX() <= b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY()+1 &&
                gc.getInput().getMouseY() <= b.getOffY() + b.getHeight();
    }
}
