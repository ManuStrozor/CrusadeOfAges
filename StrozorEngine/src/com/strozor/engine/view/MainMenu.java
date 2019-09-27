package com.strozor.engine.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameMap;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;


public class MainMenu extends View {

    private Settings s;
    private GameMap map;
    private SoundClip hover, click;

    public MainMenu(Settings s, GameMap map) {
        this.s = s;
        this.map = map;

        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");

        buttons.add(new Button("Single player", 11));
        buttons.add(new Button("Stats", 10));
        buttons.add(new Button("Creative mode", 8));
        buttons.add(new Button("Game credits", 6));
        buttons.add(new Button(80, 20, "Options", 3));
        buttons.add(new Button(80, 20, "Quit game", -1));
    }

    @Override
    public void update(GameContainer gc, float dt) {
        for(Button btn : buttons) {
            // Button Click
            if (isSelected(gc, btn)) {
                click.play();
                gc.setState(btn.getGoState());
                gc.setLastState(0);
            }
            // Button Hover
            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!hover.isRunning()) hover.play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        r.drawBackground(gc, map, "wall");
        r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("beta version"));

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            if(btn.getText().contains("Creative mode") || btn.getText().contains("Game credits")) startY += 10;
            btn.setOffX(gc.getWidth()/2-85);
            if(btn.getText().contains("Quit game")) btn.setOffX(gc.getWidth()/2+5);
            btn.setOffY(startY);
            if(!btn.getText().contains("Options")) startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
