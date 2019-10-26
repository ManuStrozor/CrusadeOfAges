package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;
import engine.gfx.Font;
import game.Game;

import java.awt.event.KeyEvent;

public class Confirm extends View {

    private Button btn;
    private String message;

    public Confirm() {
        buttons.add(new Button(80, 20, "Cancel", null));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setButton(Button btn) {
        if (this.btn != null) buttons.remove(this.btn);
        this.btn = btn;
        buttons.add(btn);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView(gc.getPrevView());
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Selection
            if (btn.isSelected(gc.getInput())) {
                gc.getClickSound().play();
                if (btn.getText().equals("Cancel")) {
                    gc.setActiView(gc.getPrevView());
                } else {
                    gc.setActiView(btn.getTargetView());
                }
            }

            // Hover Sound
            if (btn.isHover(gc.getInput())) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }

            // Hand Cursor
            if (btn.isHover(gc.getInput())) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0xcc000000);

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 2;

        r.fillAreaBloc(x - 3* Game.TS, y - Game.TS, 6, 2, "wall");
        r.drawRect(x - 3* Game.TS, y - Game.TS, 6* Game.TS, 2* Game.TS, 0xffababab);
        r.drawText(gc.getSettings().translate(message), x, y - Game.TS + 9, 0, 1, -1, Font.STANDARD);

        for (Button btn : buttons) {
            if (btn.getText().equals("Cancel")) {
                btn.setCoor(x + 5, y + 5, 1, 1);
            } else {
                btn.setCoor(x - 5, y + 5, -1, 1);
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }

    }
}
