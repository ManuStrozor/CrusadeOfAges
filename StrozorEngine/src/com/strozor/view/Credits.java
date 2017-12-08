package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;

public class Credits extends View {

    private Settings s;
    private SoundClip select;
    private String[] devs = {"ManuStrozor"};
    private String[] thanks = {"My family", "Beta testers"};
    private String[] contribs = {"Majoolwip"};
    private Button back;

    public Credits(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        buttons.add(back = new Button(60, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        //Focus control
        focusCtrl(gc);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(6);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawBackground(gc, new Bloc(0));
        r.drawMenuTitle(gc, s.translate("Game credits").toUpperCase(), s.translate("Development team"));

        r.drawList(gc.getWidth() / 4, gc.getHeight() / 3, s.translate("MAIN DEVELOPERS"), devs);
        r.drawList(gc.getWidth() / 2, gc.getHeight() / 3, s.translate("THANKS TO"), thanks);
        r.drawList(gc.getWidth() - gc.getWidth() / 4, gc.getHeight() / 3, s.translate("CONTRIBUTORS"), contribs);

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
