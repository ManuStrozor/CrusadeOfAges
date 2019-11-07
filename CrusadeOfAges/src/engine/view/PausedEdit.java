package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;
import game.Editor;
import game.Game;
import game.entity.Player;

import java.awt.event.KeyEvent;

public class PausedEdit extends View {

    public PausedEdit() {

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
            if (btn.isHover(gc.getInput())) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            // Button Selection
            if (btn.isSelected(gc.getInput())) {
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
                        gc.getGame().getLevel().blank();
                        break;
                    case "Quit":
                        Editor.setSpawn(false);
                        gc.getR().setTs(Game.TS);
                        Player.tileSize = Game.TS;
                        Editor.ts = Game.TS;
                        break;
                }
                gc.getSb().get("click").play();
                gc.setActiView(btn.getTargetView());
            }
            btn.hearHover(gc.getInput(), gc.getSb());
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(r.getCamX(), r.getCamY(), gc.getWidth(), gc.getHeight(), 0x99000000);

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Back":
                    btn.setCoor(x, y, 0, 1);
                    break;
                case "Try":
                    btn.setCoor(x, y + 30, 0, 1);
                    break;
                case "Blank":
                    btn.setCoor(x, y + 60, 0, 1);
                    break;
                case "Save":
                    btn.setCoor(x, y + 90, 0, 1);
                    break;
                case "Quit":
                    btn.setCoor(x, y + 120, 0, 1);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
