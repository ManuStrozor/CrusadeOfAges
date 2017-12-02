package com.strozor.view;

import com.strozor.engine.*;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.*;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OptsMenu extends View {

    private Settings settings;
    private ImageTile objectsImage;

    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button tglFps, tglLights, back;

    public OptsMenu(Settings settings) {
        this.settings = settings;
        objectsImage = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);

        select = new SoundClip("/audio/hover.wav");

        buttons.add(tglFps = new Button(130, 20, "Show FPS", 0));
        buttons.add(tglLights = new Button(130, 20, "Disable lights", 0));
        buttons.add(back = new Button(130, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1))
                    select.play();
            } else {
                btn.setBgColor(0xff424242);
            }
        }

        if(mouseIsHover(gc, tglFps)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowFps()) {
                    settings.setShowFps(false);
                    tglFps.setText("Show FPS");
                } else {
                    settings.setShowFps(true);
                    tglFps.setText("Hide FPS");
                }
            }
        } else if(mouseIsHover(gc, tglLights)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowLights()) {
                    settings.setShowLights(false);
                    tglLights.setText("Enable lights");
                } else {
                    settings.setShowLights(true);
                    tglLights.setText("Disable lights");
                }
            }
        } else if(mouseIsHover(gc, back)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                gc.setState(gc.getLastState());
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getLastState() == 0) {
            r.drawBackground(gc, objectsImage, 1, 0);
            r.drawMenuTitle(gc,gc.getTitle().toUpperCase(), "version beta");
        }

        tglFps.setOffX(gc.getWidth() / 2 - tglFps.getWidth() / 2);
        tglFps.setOffY(gc.getHeight() / 3 - tglFps.getHeight() / 2);

        tglLights.setOffX(tglFps.getOffX());
        tglLights.setOffY(tglFps.getOffY() + tglFps.getHeight() + 10);

        back.setOffX(tglLights.getOffX());
        back.setOffY(tglLights.getOffY() + tglLights.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn);
    }
}