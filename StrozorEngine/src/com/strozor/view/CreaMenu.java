package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.game.Crea;

import java.awt.event.KeyEvent;

public class CreaMenu extends View {

    private Settings s;
    private SoundClip select;
    private Button save;

    public CreaMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(new Button("Keep on", 4));
        buttons.add(new Button("Quit to title", 0));
        buttons.add(save = new Button("Save & Quit game", -1));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(4);

        //Focus control
        focusCtrl(gc);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                if(btn == save) Crea.creaImg.saveIt();
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(5);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        int x = gc.getWidth() / 2 - 170 / 2;
        int y = gc.getHeight() / 3 - 20 / 2;

        for(int i = 0; i < buttons.size(); i++) {
            Button btn = buttons.get(i);
            btn.setOffX(x);
            btn.setOffY(y + i * 30);
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
