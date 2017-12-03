package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Credits extends View {

    private ImageTile objectsImage;
    private Settings settings;
    private SoundClip select;

    private String[] devs = {"ManuStrozor"};
    private String[] thanks = {"My family", "Beta testers"};
    private String[] contribs = {"Majoolwip"};

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button back;

    public Credits(Settings settings) {
        this.settings = settings;
        objectsImage = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);
        select = new SoundClip("/audio/hover.wav");
        buttons.add(back = new Button(60, 20, 11, 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        for(Button btn : buttons) {
            btn.setText(settings.getWords()[btn.getWordsIndex()][settings.getLangIndex()]);
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(6);
                }
            } else {
                btn.setBgColor(0xff424242);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawBackground(gc, 0);
        r.drawMenuTitle(gc, settings.getWords()[4][settings.getLangIndex()].toUpperCase(), settings.getWords()[17][settings.getLangIndex()]);

        r.drawList(gc.getWidth() / 4, gc.getHeight() / 3, settings.getWords()[18][settings.getLangIndex()], devs);
        r.drawList(gc.getWidth() / 2, gc.getHeight() / 3, settings.getWords()[19][settings.getLangIndex()], thanks);
        r.drawList(gc.getWidth() - gc.getWidth() / 4, gc.getHeight() / 3, settings.getWords()[20][settings.getLangIndex()], contribs);

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn);
    }
}
