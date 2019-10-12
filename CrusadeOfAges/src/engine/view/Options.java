package engine.view;

import engine.*;
import engine.audio.SoundClip;
import game.Conf;
import engine.gfx.Button;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Options extends View {

    private Settings s;
    private World world;
    private SoundClip hover, click;
    private Button tglLang, tglFps, tglLights, back;

    public Options(Settings s, World world) {
        this.s = s;
        this.world = world;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(tglLang = new Button("lang", "mainMenu"));
        buttons.add(tglFps = new Button(s.isShowFps() ? "FPS on" : "FPS off", "mainMenu"));
        buttons.add(tglLights = new Button(s.isShowLights() ? "Darkness" : "Full day", "mainMenu"));
        buttons.add(back = new Button("Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) {
            upSettings();
            gc.setActiView(gc.getPrevView());
        }

        //Button selection
        if (isSelected(gc, tglLang)) {
            click.play();
            if (s.getIndexFromFlag(s.getFlag()) < s.getLangs().size() - 1) {
                s.setFlag(s.getFlagFromIndex(s.getIndexFromFlag(s.getFlag()) + 1));
            } else {
                s.setFlag(s.getFlagFromIndex(0));
            }
        } else if (isSelected(gc, tglFps)) {
            click.play();
            s.setShowFps(!s.isShowFps());
            if (s.isShowFps()) {
                tglFps.setText("FPS on");
            } else {
                tglFps.setText("FPS off");
            }
        } else if (isSelected(gc, tglLights)) {
            click.play();
            s.setShowLights(!s.isShowLights());
            if (s.isShowLights()) {
                tglLights.setText("Darkness");
            } else {
                tglLights.setText("Full day");
            }
        } else if (isSelected(gc, back)) {
            click.play();
            upSettings();
            gc.setActiView(gc.getPrevView());
        }

        for (Button btn : buttons) {
            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!hover.isRunning()) hover.play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getPrevView().equals("mainMenu")) {
            r.drawBackground(gc, world, "wall");
            r.drawMenuTitle(gc, gc.getTitle().toUpperCase(), s.translate("The Time Traveller"));
        } else {
            r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        }

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            if (btn.getText().contains("Back")) startY += 5;
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }

    private void upSettings() {
        try {
            List<String> newLines = new ArrayList<>();
            for (String line : Files.readAllLines(Paths.get(Conf.SM_FOLDER + "/settings.txt"), StandardCharsets.UTF_8)) {
                String[] sub = line.split(":");
                switch (sub[0]) {
                    case "lang":
                        newLines.add(line.replace(sub[1], s.getFlag()));
                        break;
                    case "guiScale":
                    case "maxFPS":
                        newLines.add(line);
                        break;
                    case "showFPS":
                        newLines.add(line.replace(sub[1], s.isShowFps() ? "true" : "false"));
                        break;
                    case "showLights":
                        newLines.add(line.replace(sub[1], s.isShowLights() ? "true" : "false"));
                        break;
                }
            }
            Files.write(Paths.get(Conf.SM_FOLDER + "/settings.txt"), newLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}