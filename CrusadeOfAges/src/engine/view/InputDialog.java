package engine.view;

import engine.gfx.Button;
import engine.gfx.TextInput;
import game.Game;
import engine.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputDialog extends View {

    static String path;
    static TextInput textInput;
    private boolean once = false;

    public InputDialog() {
        textInput = new TextInput(null, 0);

        buttons.add(new Button(80, 20, "Rename", null));
        buttons.add(new Button(80, 20, "Cancel", null));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (!once) {
            textInput.setBlinkBarPos(textInput.getText().length());
            once = true;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView(gc.getPrevView());
            once = false;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            renameFile();
            gc.setActiView(gc.getPrevView());
            once = false;
        }

        textInput.update(gc.getInput());

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Selection
            if (btn.isSelected(gc.getInput())) {
                if (btn.getText().equals("Rename")) renameFile();
                gc.getSb().get("click").play();
                gc.setActiView(btn.getTargetView());
                once = false;
            }

            btn.hearHover(gc.getInput(), gc.getSb());

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
        r.drawTextInput(textInput, x - 3* Game.TS + 6, y - Game.TS + 9, Game.TS * 6 - 12, Game.TS - 12, 0xff333333);

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Rename":
                    btn.setCoor(x - 5, y + 5, -1, 1);
                    break;
                case "Cancel":
                    btn.setCoor(x + 5, y + 5, 1, 1);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }

    private void renameFile() {
        try {
            Path src = Paths.get(path);
            Files.move(src, src.resolveSibling(textInput.getText() + ".png"));
        } catch (IOException e) {
            System.out.println("[IOException] " + e.getMessage());
        }
    }
}
