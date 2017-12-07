package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Button;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainMenu extends View {

    private Settings s;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, crea, credits, opt, exit;

    public MainMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(play = new Button(170, 20, "Single player", 1));
        buttons.add(crea = new Button(170, 20, "Creative Mode", 4));
        buttons.add(credits = new Button(170, 20, "Game credits", 6));
        buttons.add(opt = new Button(80, 20, "Options", 3));
        buttons.add(exit = new Button(80, 20, "Quit game", -1));
    }

    @Override
    public void update(GameContainer gc, float dt) {
        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(0);
                }
            } else {
                btn.setBgColor(0xff424242);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawBackground(gc, new Bloc(0));
        r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("beta version"));

        play.setOffX(gc.getWidth() / 2 - play.getWidth() / 2);
        play.setOffY(gc.getHeight() / 3 - play.getHeight() / 2);

        crea.setOffX(play.getOffX());
        crea.setOffY(play.getOffY() + play.getHeight() + 10);

        credits.setOffX(crea.getOffX());
        credits.setOffY(crea.getOffY() + crea.getHeight() + 10);

        opt.setOffX(credits.getOffX());
        opt.setOffY(credits.getOffY() + credits.getHeight() + 10);

        exit.setOffX(gc.getWidth() / 2 + 5);
        exit.setOffY(opt.getOffY());

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
