package engine.view;

import engine.gfx.Button;
import engine.gfx.Font;
import game.Camera;
import game.Game;
import engine.*;
import game.entity.Player;

import java.awt.event.KeyEvent;

public class GameSelection extends View {

    public static boolean focus = true, once = false;
    public static int fIndex = 0, scroll = 0, sMax = 0;
    private Button play, back;

    public GameSelection() {
        buttons.add(play = new Button(170, 20, "Play", "game"));
        buttons.add(back = new Button(170, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (!once) {
            sMax = 10 - (gc.getHeight() - 3 * Game.TS);
            for (int i = 0; i < gc.getPlayerStats().getValueOf("Level up"); i++) {
                sMax += 30 + 10;
            }
            if (sMax < 0) sMax = 0;
            once = true;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setActiView(gc.getPrevView());
            once = false;
        }

        //Scroll control
        if (gc.getInput().getScroll() < 0) {
            scroll -= 20;
            if (scroll < 0) scroll = 0;
        } else if (gc.getInput().getScroll() > 0) {
            scroll += 20;
            if (scroll > sMax) scroll = sMax;
        }

        //Selected control
        for (int i = 0; i <= gc.getPlayerStats().getValueOf("Level up"); i++) {
            if (levelSelected(gc, i, scroll)) {
                fIndex = i;
                focus = true;
            }
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (btn.isHover(gc.getInput()) && !(btn == play && !focus)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            btn.setBgColor(0xff616E7A);
            if (!focus && btn == play) {
                btn.setBgColor(0xffdedede);
            } else if (btn.isSelected(gc.getInput())) {
                if (btn == back) focus = false;
                if (btn == play) {
                    gc.getGame().getLevel().setCurrLevel(fIndex);
                    gc.getGame().getLevel().load();
                    gc.getWindow().setBlankCursor();
                }
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
                once = false;
            }

            // Hover Sound
            if (!(btn == play && !focus)) {
                if (btn.isHover(gc.getInput())) {
                    if (!btn.isHoverSounded()) {
                        if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                        btn.setHoverSounded(true);
                    }
                } else {
                    btn.setHoverSounded(false);
                }
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawBackground();
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);

        //Draw list of files & scroll bar
        if (sMax <= 0) scroll = 0;
        r.drawLevels(gc.getGame().getLevel().getLvls(), gc.getPlayerStats());
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / Game.TS + 1, 1, "wall");
        r.drawText(gc.getSettings().translate("Choose a level"), gc.getWidth() / 2, Game.TS / 2, 0, 0, -1, Font.STANDARD);
        //Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - Game.TS * 2, gc.getWidth() / Game.TS + 1, 2, "wall");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() - 2 * Game.TS;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Play":
                    btn.setCoor(x, y + 10, 0, 1);
                    break;
                case "Back":
                    btn.setCoor(x, gc.getHeight() - 10, 0, -1);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
