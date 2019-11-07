package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;

import java.awt.event.KeyEvent;

public class GameOver extends View {

    private boolean sounded = false;

    public GameOver() {
        buttons.add(new Button("Try again", "game"));
        buttons.add(new Button("Quit to title", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getPrevView().equals("game") && !sounded) {
            gc.getSb().get("game over").play();
            sounded = true;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            gc.getGame().getLevel().load();
            gc.getWindow().setBlankCursor();
            gc.getWorld().getLevel().getPlayer().setChrono(0);
            gc.getSb().get("click").play();
            gc.getSb().get("game over").stop();
            gc.setActiView("game");
            sounded = false;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.getWorld().getLevel().getPlayer().setChrono(0);
            gc.setActiView("mainMenu");
            sounded = false;
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (btn.isHover(gc.getInput())) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            // Button Selection
            if (btn.isSelected(gc.getInput())) {
                if (btn.getText().contains("Quit")) gc.getPlayerStats().saveData();
                if (btn.getText().contains("Try")) {
                    gc.getGame().getLevel().load();
                    gc.getWindow().setBlankCursor();
                }
                gc.getWorld().getLevel().getPlayer().setChrono(0);
                gc.getSb().get("click").play();
                gc.getSb().get("game over").stop();
                sounded = false;
                gc.setActiView(btn.getTargetView());
            }
            btn.hearHover(gc.getInput(), gc.getSb());
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);

        r.drawMenuTitle("Game Over", "You are dead");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Try again":
                    btn.setCoor(x, y, 0, 1);
                    break;
                case "Quit to title":
                    btn.setCoor(x, y + 30, 0, 1);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
