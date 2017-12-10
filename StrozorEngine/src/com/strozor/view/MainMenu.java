package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Button;


public class MainMenu extends View {

    private Settings s;
    private SoundClip select;

    public MainMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        buttons.add(new Button("Single player", 1));
        buttons.add(new Button("Creative Mode", 8));
        buttons.add(new Button("Game credits", 6));
        buttons.add(new Button("Options", 3));
        buttons.add(new Button("Quit game", -1));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(0);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        r.drawBackground(gc, new Bloc(0));
        r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("beta version"));

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
