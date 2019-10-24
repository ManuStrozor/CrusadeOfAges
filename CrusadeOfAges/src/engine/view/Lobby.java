package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;
import engine.gfx.Font;
import game.GameManager;

import java.awt.event.KeyEvent;

public class Lobby extends View {

    private static boolean focus = true, once = false;
    private static int fIndex = 0;
    private Button play, back;

    public Lobby() {
        buttons.add(play = new Button(170, 20, "Play", "lobby")); // Changer lobby par ? plus tard
        buttons.add(back = new Button(170, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView("mainMenu");
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (isHover(gc, btn) && !(btn == play && !focus)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            btn.setBgColor(0xff616E7A);
            if (isSelected(gc, btn)) {
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

        r.drawBackground();
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);

        // Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / GameManager.TS + 1, 1, "wall");
        r.drawText(gc.getSettings().translate("Multiplayer"), gc.getWidth() / 2, GameManager.TS / 2, 0, 0, -1, Font.STANDARD);
        // Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - GameManager.TS * 2, gc.getWidth() / GameManager.TS + 1, 2, "wall");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() - GameManager.TS;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Play":
                    btn.setAlignCoor(x - 5, y, -1, 0);
                    break;
                case "Back":
                    btn.setAlignCoor(x + 5, y, 1, 0);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
