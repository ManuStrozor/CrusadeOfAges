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

    public GameMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        buttons.add(new Button("Quit to title", 0));
        buttons.add(new Button("Options", 3));
        buttons.add(new Button("Back to game", 1));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(1);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(2);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            if(btn.getText().contains("Back")) startY += 5;
            btn.setOffX(gc.getWidth()/2-85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
