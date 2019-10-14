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
            gc.getGameover().play();
            once = true;
        }

        //Button selection
        for (Button btn : buttons) {
            if (isSelected(gc, btn)) {
                if (btn.getText().contains("Quit")) gc.getPlayerStats().saveData();
                gc.getClick().play();
                gc.getGameover().stop();
                once = false;
                gc.setActiView(btn.getTargetView());
            }

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHover().isRunning()) gc.getHover().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
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
