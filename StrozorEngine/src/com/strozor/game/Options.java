package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Font;
import com.strozor.engine.gfx.ImageTile;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Options extends AbstractGame {

    private Settings settings;
    private ImageTile background;

    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button fps, lights, back;

    public Options(Settings settings) {
        this.settings = settings;
        background = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);

        select = new SoundClip("/audio/hover.wav");

        buttons.add(fps = new Button(130, 20, "Show FPS", 0));
        buttons.add(lights = new Button(130, 20, "Disable lights", 0));
        buttons.add(back = new Button(130, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0x99c0392b);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) select.play();
            } else {
                btn.setBgColor(0x997f8c8d);
            }
        }

        if(mouseIsHover(gc, fps)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowFps()) {
                    settings.setShowFps(false);
                    fps.setText("Show FPS");
                } else {
                    settings.setShowFps(true);
                    fps.setText("Hide FPS");
                }
            }
        } else if(mouseIsHover(gc, lights)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                if(settings.isShowLights()) {
                    settings.setShowLights(false);
                    lights.setText("Enable lights");
                } else {
                    settings.setShowLights(true);
                    lights.setText("Disable lights");
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
            for(int y = 0; y <= gc.getHeight() / GameManager.TS; y++) {
                for(int x = 0; x <= gc.getWidth() / GameManager.TS; x++) {
                    r.drawImageTile(background, x * GameManager.TS, y * GameManager.TS, 1, 0);
                }
            }
            r.drawText("SKEWER MAKER", gc.getWidth() / 2, 50, 0, 1, 0x77f90000, Font.BIG_STANDARD);
        }

        fps.setOffX(gc.getWidth() / 2 - fps.getWidth() / 2);
        fps.setOffY(gc.getHeight() / 3 - fps.getHeight() / 2);

        lights.setOffX(fps.getOffX());
        lights.setOffY(fps.getOffY() + fps.getHeight() + 10);

        back.setOffX(lights.getOffX());
        back.setOffY(lights.getOffY() + lights.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn, 0xffababab);
    }

    private boolean mouseIsHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX()+1 &&
                gc.getInput().getMouseX() <= b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY()+1 &&
                gc.getInput().getMouseY() <= b.getOffY() + b.getHeight();
    }
}