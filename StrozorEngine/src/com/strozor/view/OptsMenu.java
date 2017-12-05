package com.strozor.view;

import com.strozor.engine.*;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OptsMenu extends View {

    private Settings settings;

    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button tglLang, tglFps, tglLights, back;

    public OptsMenu(Settings settings) {
        this.settings = settings;

        select = new SoundClip("/audio/hover.wav");

        buttons.add(tglLang = new Button(130, 20, 0, 0));
        buttons.add(tglFps = new Button(130, 20, 9, 0));
        buttons.add(tglLights = new Button(130, 20, 8, 0));
        buttons.add(back = new Button(130, 20, 11, 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        for(Button btn : buttons) {
            btn.setText(settings.getWords()[btn.getWordsIndex()][settings.getLangIndex()]);
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1))
                    select.play();
            } else {
                btn.setBgColor(0xff424242);
            }
        }

        if(mouseIsHover(gc, tglLang)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.getLangIndex() < settings.getWords()[0].length - 1) {
                    settings.setLangIndex(settings.getLangIndex() + 1);
                } else {
                    settings.setLangIndex(0);
                }
            }
        } else if(mouseIsHover(gc, tglFps)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowFps()) {
                    settings.setShowFps(false);
                    tglFps.setWordsIndex(7);
                } else {
                    settings.setShowFps(true);
                    tglFps.setWordsIndex(9);
                }
            }
        } else if(mouseIsHover(gc, tglLights)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowLights()) {
                    settings.setShowLights(false);
                    tglLights.setWordsIndex(10);
                } else {
                    settings.setShowLights(true);
                    tglLights.setWordsIndex(8);
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
            r.drawBackground(gc, 0);
            r.drawMenuTitle(gc,gc.getTitle().toUpperCase(), settings.getWords()[1][settings.getLangIndex()]);
        }

        tglLang.setOffX(gc.getWidth() / 2 - tglLang.getWidth() / 2);
        tglLang.setOffY(gc.getHeight() / 3 - tglLang.getHeight() / 2);

        tglFps.setOffX(tglLang.getOffX());
        tglFps.setOffY(tglLang.getOffY() + tglLang.getHeight() + 20);

        tglLights.setOffX(tglFps.getOffX());
        tglLights.setOffY(tglFps.getOffY() + tglFps.getHeight() + 10);

        back.setOffX(tglLights.getOffX());
        back.setOffY(tglLights.getOffY() + tglLights.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn);
    }
}