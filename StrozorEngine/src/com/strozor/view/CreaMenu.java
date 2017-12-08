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
    private Button crea, save, menu;

    public CreaMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(crea = new Button(170, 20, "Keep on", 4));
        buttons.add(save = new Button(170, 20, "Save & Quit game", -1));
        buttons.add(menu = new Button(170, 20, "Quit to title", 0));
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

        crea.setOffY(gc.getHeight() / 3 - crea.getHeight() / 2);
        save.setOffY(crea.getOffY() + crea.getHeight() + 10);
        menu.setOffY(save.getOffY() + save.getHeight() + 10);

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - btn.getWidth() / 2);
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
