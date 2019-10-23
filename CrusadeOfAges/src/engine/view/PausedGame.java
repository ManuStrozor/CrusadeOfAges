package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;

import java.awt.event.KeyEvent;

public class PausedGame extends View {

    public PausedGame() {

        buttons.add(new Button("Back to game", "game"));
        buttons.add(new Button("Stats", "stats"));
        buttons.add(new Button("Quit to title", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("game");

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (isHover(gc, btn)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            // Button Selection
            if (isSelected(gc, btn)) {
                if (btn.getText().contains("Quit")) {
                    gc.getPlayerStats().saveData();
                }
                if (btn.getText().contains("Back")) gc.getWindow().setBlankCursor();
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

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle("Paused", null);

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Back to game":
                    btn.setAlignCoor(x, y, 0, 1);
                    break;
                case "Stats":
                    btn.setAlignCoor(x, y + 30, 0, 1);
                    break;
                case "Quit to title":
                    btn.setAlignCoor(x, y + 70, 0, 1);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
