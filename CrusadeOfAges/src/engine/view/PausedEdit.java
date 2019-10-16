package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;
import game.Editor;
import game.GameManager;
import game.objects.Player;

import java.awt.event.KeyEvent;

public class PausedEdit extends View {

    private Settings s;

    public PausedEdit(Settings settings) {
        s = settings;

        buttons.add(new Button("Back", "edit"));

        buttons.add(new Button("Try", "edit"));
        buttons.add(new Button("Blank", "pausedEdit"));

        buttons.add(new Button("Save", "pausedEdit"));
        buttons.add(new Button("Quit", "creativeMode"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("edit");

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (isHover(gc, btn)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            // Button Selection
            if (isSelected(gc, btn)) {
                switch (btn.getText()) {
                    case "Back":
                        gc.getWindow().setDefaultCursor();
                        break;
                    case "Save":
                        Editor.creaImg.save(Editor.rename);
                        break;
                    case "Try":
                        Editor.setSpawn(true);
                        gc.getWindow().setDefaultCursor();
                        break;
                    case "Blank":
                        Editor.creaImg.blank();
                        Editor.world.blank();
                        break;
                    case "Quit":
                        Editor.setSpawn(false);
                        gc.getR().setTs(GameManager.TS);
                        Player.tileSize = GameManager.TS;
                        Editor.tileSize = GameManager.TS;
                        break;
                }
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
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
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(r.getCamX(), r.getCamY(), gc.getWidth(), gc.getHeight(), 0x99000000);

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
