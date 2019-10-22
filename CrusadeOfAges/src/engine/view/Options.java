package engine.view;

import engine.*;
import engine.gfx.Checkbox;
import engine.gfx.Font;
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
    private TileMap tileMap;

    public Options(Settings s, TileMap tileMap) {
        this.s = s;
        this.tileMap = tileMap;
        buttons.add(new Button(20, 20, "<", null));
        buttons.add(new Button(20, 20, ">", null));
        checkboxes.add(new Checkbox("fps", s.isShowFps()));
        checkboxes.add(new Checkbox("light", s.isShowLights()));
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
                    case "<":
                        if (s.getIFlag(s.getFlag()) > 0) {
                            s.setFlag(s.getFIndex(s.getIFlag(s.getFlag()) - 1));
                        } else {
                            s.setFlag(s.getFIndex(s.getLangs().size() - 1));
                        }
                        break;
                    case ">":
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

        for (Checkbox chk : checkboxes) {

            // Checkbox Selection
            if (isSelected(gc, chk)) {
                ////// Sound Checkbox //////
                switch (chk.getTag()) {
                    case "fps":
                        s.setShowFps(!s.isShowFps());
                        chk.setChecked(s.isShowFps());
                        break;
                    case "light":
                        s.setShowLights(!s.isShowLights());
                        chk.setChecked(s.isShowLights());
                        break;
                }
            }
        }

        if (!cursorHand) gc.getWindow().setDefaultCursor();

    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getPrevView().equals("mainMenu")) {
            r.drawBackground(tileMap);
            r.drawMenuTitle(s.translate("Options").toUpperCase(), null);
        } else {
            r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        }

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        r.drawText(s.translate("lang"), x, y + 5, 0, 1, -1, Font.STANDARD);
        r.drawText(s.translate("Show the FPS"), x - 55, y + 35, 1, 1, -1, Font.STANDARD);
        r.drawText(s.translate("Activate the light"), x - 55, y + 60, 1, 1, -1, Font.STANDARD);

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "<":
                    btn.setAlignCoor(x - 40, y, -1, 1);
                    break;
                case ">":
                    btn.setAlignCoor(x + 40, y, 1, 1);
                    break;
                case "Back":
                    btn.setAlignCoor(x, y + 85, 0, 1);
                    break;
            }
            r.drawButton(btn);
        }

        for (Checkbox chk : checkboxes) {
            switch (chk.getTag()) {
                case "fps":
                    chk.setAlignCoor(x - 65, y + 30, -1, 1);
                    break;
                case "light":
                    chk.setAlignCoor(x - 65, y + 55, -1, 1);
                    break;
            }
            r.drawCheckbox(chk);
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