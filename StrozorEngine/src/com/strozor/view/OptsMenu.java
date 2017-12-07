package com.strozor.view;

import com.strozor.engine.*;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.*;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OptsMenu extends View {

    private Settings s;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button tglLang, tglFps, tglLights, back;

    public OptsMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(tglLang = new Button(130, 20, "lang", 0));
        buttons.add(tglFps = new Button(130, 20, settings.isShowFps() ? "FPS on" : "FPS off", 0));
        buttons.add(tglLights = new Button(130, 20, settings.isShowLights() ? "Darkness" : "Full day", 0));
        buttons.add(back = new Button(130, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            updateOptions();
            gc.setState(gc.getLastState());
        }

        for(Button btn : buttons) {
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
                if(s.getLangIndex() < s.getLang().size() - 1) s.setLangIndex(s.getLangIndex() + 1);
                else s.setLangIndex(0);
            }
        } else if(mouseIsHover(gc, tglFps)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                s.setShowFps(!s.isShowFps());
                if(s.isShowFps()) tglFps.setText("FPS on");
                else tglFps.setText("FPS off");
            }
        } else if(mouseIsHover(gc, tglLights)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                s.setShowLights(!s.isShowLights());
                if(s.isShowLights()) tglLights.setText("Darkness");
                else tglLights.setText("Full day");
            }
        } else if(mouseIsHover(gc, back)) {
            if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                updateOptions();
                gc.setState(gc.getLastState());
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getLastState() == 0) {
            r.drawBackground(gc, new Bloc(0));
            r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("beta version"));
        }

        tglLang.setOffX(gc.getWidth() / 2 - tglLang.getWidth() / 2);
        tglLang.setOffY(gc.getHeight() / 3 - tglLang.getHeight() / 2);

        tglFps.setOffX(tglLang.getOffX());
        tglFps.setOffY(tglLang.getOffY() + tglLang.getHeight() + 20);

        tglLights.setOffX(tglFps.getOffX());
        tglLights.setOffY(tglFps.getOffY() + tglFps.getHeight() + 10);

        back.setOffX(tglLights.getOffX());
        back.setOffY(tglLights.getOffY() + tglLights.getHeight() + 10);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }

    private void updateOptions() {
        try {
            List<String> newLines = new ArrayList<>();
            for (String line : Files.readAllLines(Paths.get(GameManager.APPDATA + "\\options.txt"), StandardCharsets.UTF_8)) {
                String[] sub = line.split(":");
                switch(sub[0]) {
                    case "lang":
                        switch(s.getLangIndex()) {
                            case 0: newLines.add(line.replace(sub[1], "en")); break;
                            case 1: newLines.add(line.replace(sub[1], "fr")); break;
                        }
                        break;
                    case "guiScale": newLines.add(line); break;
                    case "maxFPS": newLines.add(line); break;
                    case "showFPS": newLines.add(line.replace(sub[1], s.isShowFps() ? "true" : "false")); break;
                    case "showLights": newLines.add(line.replace(sub[1], s.isShowLights() ? "true" : "false")); break;
                }
            }
            Files.write(Paths.get(GameManager.APPDATA + "\\options.txt"), newLines, StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}