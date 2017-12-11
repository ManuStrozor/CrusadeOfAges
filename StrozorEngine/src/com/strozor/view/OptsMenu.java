package com.strozor.view;

import com.strozor.engine.*;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.*;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OptsMenu extends View {

    private Settings s;
    private SoundClip select;
    private Button tglLang, tglFps, tglLights, back;

    public OptsMenu(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        buttons.add(tglLang = new Button("lang", 0));
        buttons.add(tglFps = new Button(settings.isShowFps() ? "FPS on" : "FPS off", 0));
        buttons.add(tglLights = new Button(settings.isShowLights() ? "Darkness" : "Full day", 0));
        buttons.add(back = new Button("Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            updateOptions();
            gc.setState(gc.getLastState());
        }

        //Button selection
        if(isSelected(gc, tglLang)) {
            select.play();
            if(s.getLangIndex() < s.getLang().size() - 1) s.setLangIndex(s.getLangIndex() + 1);
            else s.setLangIndex(0);
        } else if(isSelected(gc, tglFps)) {
            select.play();
            s.setShowFps(!s.isShowFps());
            if(s.isShowFps()) tglFps.setText("FPS on");
            else tglFps.setText("FPS off");
        } else if(isSelected(gc, tglLights)) {
            select.play();
            s.setShowLights(!s.isShowLights());
            if(s.isShowLights()) tglLights.setText("Darkness");
            else tglLights.setText("Full day");
        } else if(isSelected(gc, back)) {
            select.play();
            updateOptions();
            gc.setState(gc.getLastState());
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getLastState() == 0) {
            r.drawBackground(gc, new Bloc(0));
            r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("beta version"));
        } else {
            r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        }

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            if(btn.getText().contains("Back")) startY += 5;
            btn.setOffX(gc.getWidth()/2-85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
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