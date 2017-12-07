package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OverMenu extends View {

    private Settings s;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, menu;

    public OverMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(play = new Button(130, 20, "Try again", 1));
        buttons.add(menu = new Button(130, 20, "Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {
        for(Button btn : buttons) {
            if(mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(7);
                }
            } else {
                btn.setBgColor(0xff424242);
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
