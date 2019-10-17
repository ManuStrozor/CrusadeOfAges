package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;

public class GameOver extends View {

    private Settings s;

    private boolean once = false;

    public GameOver(Settings settings) {
        s = settings;
        buttons.add(new Button("Try again", "game"));
        buttons.add(new Button("Quit to title", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getPrevView().equals("game") && !once) {
            gc.getGameoverSound().play();
            once = true;
        }

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
                if (btn.getText().contains("Try")) gc.getWindow().setBlankCursor();
                gc.getClickSound().play();
                gc.getGameoverSound().stop();
                once = false;
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

        r.drawMenuTitle(s.translate("GAME OVER"), s.translate("You are dead"));

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
