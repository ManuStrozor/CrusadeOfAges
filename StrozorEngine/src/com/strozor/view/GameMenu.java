package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameMenu extends View {

    private Settings settings;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, opt, menu;

    public GameMenu(Settings settings) {
        this.settings = settings;
        select = new SoundClip("/audio/hover.wav");

        buttons.add(play = new Button(130, 20, 12, 1));
        buttons.add(opt = new Button(130, 20, 5, 3));
        buttons.add(menu = new Button(130, 20, 13, 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(1);

        for(Button btn : buttons) {
            btn.setText(settings.getWords()[btn.getWordsIndex()][settings.getLangIndex()]);
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(2);
                }
            } else {
                btn.setBgColor(0xff424242);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);
        opt.setOffY(play.getOffY() + play.getHeight() + 10);
        menu.setOffY(opt.getOffY() + opt.getHeight() + 10);

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - btn.getWidth() / 2);
            r.drawButton(btn);
        }
    }
}
