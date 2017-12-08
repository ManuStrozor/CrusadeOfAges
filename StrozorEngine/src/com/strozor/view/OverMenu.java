package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

public class OverMenu extends View {

    private Settings s;
    private SoundClip select, gameover;
    private Button play, menu;

    private boolean once = false;

    public OverMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        gameover = new SoundClip("/audio/gameover.wav");

        buttons.add(play = new Button(130, 20, "Try again", 1));
        buttons.add(menu = new Button(130, 20, "Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getLastState() == 1 && !once) {
            gameover.play();
            focus = 0;
            once = true;
        }

        //Focus control
        focusCtrl(gc);

        //Button selection
        for(Button btn : buttons) {
            if(isSelected(gc, btn)) {
                select.play();
                gameover.stop();
                once = false;
                gc.setState(btn.getGoState());
                gc.setLastState(7);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawMenuTitle(gc, s.translate("GAME OVER"), s.translate("You are dead"));

        play.setOffX(gc.getWidth() / 2 - play.getWidth() / 2);
        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);

        menu.setOffX(play.getOffX());
        menu.setOffY(play.getOffY() + play.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
