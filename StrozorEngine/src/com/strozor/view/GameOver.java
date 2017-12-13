package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

public class GameOver extends View {

    private Settings s;
    private SoundClip select, gameover;

    private boolean once = false;

    public GameOver(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        gameover = new SoundClip("/audio/gameover.wav");
        buttons.add(new Button("Try again", 1));
        buttons.add(new Button("Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getLastState() == 1 && !once) {
            gameover.play();
            once = true;
        }

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

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);

        r.drawMenuTitle(gc, s.translate("GAME OVER"), s.translate("You are dead"));

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth()/2-85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
