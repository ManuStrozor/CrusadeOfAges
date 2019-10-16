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

    private Settings s;
    private World world;
    private Button cancel, rename;

    private boolean once = false;

    public InputDialog(Settings s, World world) {
        this.s = s;
        this.world = world;
        buttons.add(cancel = new Button(80, 20, "Cancel", "creativeMode"));
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


        //Button selection
        for (Button btn : buttons) {
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

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
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

        int x = gc.getWidth() / 2 - GameManager.TS * 3;
        int y = gc.getHeight() / 3 - GameManager.TS;

        r.fillAreaBloc(x, y, 6, 2, world, "wall");
        r.drawInput(x + 6, y + 9, GameManager.TS * 6 - 12, GameManager.TS - 12, 0xff333333);

        cancel.setOffX(gc.getWidth() / 2 - cancel.getWidth() - 5);
        cancel.setOffY(gc.getHeight() / 3 + 5);

        rename.setOffX(gc.getWidth() / 2 + 5);
        rename.setOffY(cancel.getOffY());

        for (Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
