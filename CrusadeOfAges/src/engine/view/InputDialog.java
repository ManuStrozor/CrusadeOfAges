package engine.view;

import engine.gfx.Button;
import game.GameManager;
import engine.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputDialog extends View {

    public static String input, path;
    public static int blink;

    private Button rename;

    private boolean once = false;

    public InputDialog() {
        buttons.add(new Button(80, 20, "Cancel", "creativeMode"));
        buttons.add(rename = new Button(80, 20, "Rename", "creativeMode"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (!once) {
            blink = input.length();
            once = true;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView("creativeMode");
            once = false;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            try {
                Path src = Paths.get(path);
                Files.move(src, src.resolveSibling(input + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            gc.setActiView("creativeMode");
            once = false;
        }

        //Blinking bar control
        if (gc.getInput().isKeyDown(KeyEvent.VK_LEFT) && blink > 0)
            blink--;
        if (gc.getInput().isKeyDown(KeyEvent.VK_RIGHT) && blink < input.length())
            blink++;
        if (gc.getInput().isKeyDown(KeyEvent.VK_END))
            blink = input.length();
        if (gc.getInput().isKeyDown(KeyEvent.VK_HOME))
            blink = 0;

        //Input control
        inputCtrl(gc);


        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Selection
            if (isSelected(gc, btn)) {
                if (btn == rename) {
                    try {
                        Path src = Paths.get(path);
                        Files.move(src, src.resolveSibling(input + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
                once = false;
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
        r.drawInput(x - 3*GameManager.TS + 6, y - GameManager.TS + 9, GameManager.TS * 6 - 12, GameManager.TS - 12, 0xff333333);

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Rename":
                    btn.setAlignCoor(x - 5, y + 5, -1, 1);
                    break;
                case "Cancel":
                    btn.setAlignCoor(x + 5, y + 5, 1, 1);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
