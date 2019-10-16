package engine.view;

import engine.*;
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

    public Options(Settings s, World world) {
        this.s = s;
        this.world = world;
        buttons.add(new Button("lang", null));
        buttons.add(new Button(s.isShowFps() ? "FPS on" : "FPS off", null));
        buttons.add(new Button(s.isShowLights() ? "Darkness" : "Full day", null));
        buttons.add(new Button("Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            saveSettings();
            gc.setActiView(gc.getPrevView());
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            //Button selection
            if (isSelected(gc, btn)) {
                gc.getClickSound().play();
                switch (btn.getText()) {
                    case "lang":
                        if (s.getIFlag(s.getFlag()) < s.getLangs().size() - 1) {
                            s.setFlag(s.getFIndex(s.getIFlag(s.getFlag()) + 1));
                        } else {
                            s.setFlag(s.getFIndex(0));
                        }
                        break;
                    case "Back":
                        saveSettings();
                        gc.setActiView(gc.getPrevView());
                        break;
                    case "FPS on":
                    case "FPS off":
                        s.setShowFps(!s.isShowFps());
                        if (s.isShowFps()) {
                            btn.setText("FPS on");
                        } else {
                            btn.setText("FPS off");
                        }
                        break;
                    case "Darkness":
                    case "Full day":
                        s.setShowLights(!s.isShowLights());
                        if (s.isShowLights()) {
                            btn.setText("Darkness");
                        } else {
                            btn.setText("Full day");
                        }
                        break;
                }
            }

            // Hover Sound
            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }

            // Hand Cursor
            if (isHover(gc, btn)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();

    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getPrevView().equals("mainMenu")) {
            r.drawBackground(world);
            r.drawMenuTitle(gc.getTitle().toUpperCase(), s.translate("The Time Traveller"));
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

    private void saveSettings() {
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