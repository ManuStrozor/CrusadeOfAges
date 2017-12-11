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
        buttons.add(new Button(80, 20, "Options", 3));
        buttons.add(new Button(80, 20, "Quit game", -1));
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

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            if(btn.getText().contains("credits")) startY += 5;
            btn.setOffX(gc.getWidth()/2-85);
            if(btn.getText().contains("Quit")) btn.setOffX(gc.getWidth()/2+5);
            btn.setOffY(startY);
            if(!btn.getText().contains("Options")) startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
