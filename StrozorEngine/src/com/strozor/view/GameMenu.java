package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;

public class GameMenu extends View {

    private Settings s;
    private SoundClip select;
    private Button play, opt, menu;

    public GameMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(play = new Button(130, 20, "Back to game", 1));
        buttons.add(opt = new Button(130, 20, "Options", 3));
        buttons.add(menu = new Button(130, 20, "Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(1);

        //Focus control
        focusCtrl(gc);

        //Button selection
        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(2);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);
        opt.setOffY(play.getOffY() + play.getHeight() + 10);
        menu.setOffY(opt.getOffY() + opt.getHeight() + 10);

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - btn.getWidth() / 2);
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
