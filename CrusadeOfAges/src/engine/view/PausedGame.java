package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;

import java.awt.event.KeyEvent;

public class PausedGame extends View {

    private Settings s;

    public PausedGame(Settings settings) {
        s = settings;
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
                if (btn.getText().contains("Quit")) gc.getPlayerStats().saveData();
                if (btn.getText().contains("Back")) gc.getWindow().setDefaultCursor();
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
        r.drawMenuTitle(s.translate("Paused").toUpperCase(), null);

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            if (btn.getText().contains("Quit")) startY += 10;
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
