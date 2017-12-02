package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Button;
import com.strozor.game.GameManager;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainMenu extends View {

    private ImageTile objectsImage;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button play, crea, credits, opt, exit;

    public MainMenu() {
        objectsImage = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);

        select = new SoundClip("/audio/hover.wav");

        buttons.add(play = new Button(130, 20, "Single player", 1));
        buttons.add(crea = new Button(130, 20, "Creative Mode", 4));
        buttons.add(credits = new Button(130, 20, "Game credits", 6));

        buttons.add(opt = new Button(60, 20, "Options", 3));
        buttons.add(exit = new Button(60, 20, "Quit game", -1));
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

        r.drawBackground(gc, objectsImage, 1, 0);
        r.drawMenuTitle(gc,"SKEWER MAKER", "version beta");

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

        for(Button btn : buttons) r.drawButton(btn);
    }
}
