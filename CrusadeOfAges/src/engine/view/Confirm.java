package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.World;
import engine.gfx.Button;
import engine.gfx.Font;
import game.GameManager;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            if (isSelected(gc, btn)) {
                gc.getClickSound().play();
                if (btn.getText().equals("Cancel")) {
                    gc.setActiView(gc.getPrevView());
                } else {
                    gc.setActiView(btn.getTargetView());
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
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0xcc000000);

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 2;

        r.fillAreaBloc(x - 3*GameManager.TS, y - GameManager.TS, 6, 2, "wall");
        r.drawRect(x - 3*GameManager.TS, y - GameManager.TS, 6*GameManager.TS, 2*GameManager.TS, 0xffababab);
        r.drawText(gc.getSettings().translate(message), x, y - GameManager.TS + 9, 0, 1, -1, Font.STANDARD);

        for (Button btn : buttons) {
            if (btn.getText().equals("Cancel")) {
                btn.setAlignCoor(x + 5, y + 5, 1, 1);
            } else {
                btn.setAlignCoor(x - 5, y + 5, -1, 1);
            }
            r.drawButton(btn);
        }

    }
}
