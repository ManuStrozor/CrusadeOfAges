package engine.view;

import engine.gfx.Button;
import engine.gfx.Font;
import game.AbstractGame;
import game.GameManager;
import engine.*;

import java.awt.event.KeyEvent;

public class GameSelection extends View {

    public static boolean focus, once;
    public static int fIndex, scroll, sMax;

    static {
        once = false;
        focus = true;
        fIndex = 0;
        scroll = 0;
        sMax = 0;
    }

    private Settings s;
    private World world;
    private GameManager gameManager;
    private Button play, back;

    public GameSelection(Settings s, World world, AbstractGame game) {
        this.s = s;
        this.world = world;
        this.gameManager = (GameManager) game;
        buttons.add(play = new Button(170, 20, "Play", "game"));
        buttons.add(back = new Button(170, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (!once) {
            sMax = 10 - (gc.getHeight() - 3 * GameManager.TS);
            for (int i = 0; i < gc.getPlayerStats().getValueOf("Level up"); i++) {
                sMax += 30 + 10;
            }
            if (sMax < 0) sMax = 0;
            once = true;
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setActiView("mainMenu");
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
            if (isHover(gc, btn) && !(btn == play && !focus)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            btn.setBgColor(0xff616E7A);
            if (!focus && btn == play) {
                btn.setBgColor(0xffdedede);
            } else if (isSelected(gc, btn)) { //Button selection
                if (btn == back) focus = false;
                if (btn == play) {
                    GameManager.current = fIndex;
                    gameManager.load(GameManager.levels[fIndex][0]);
                    gc.getWindow().setBlankCursor();
                }
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
                once = false;
            }

            // Hover Sound
            if (!(btn == play && !focus)) {
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
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        //Fill general background
        r.drawBackground(world);
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);
        //Draw list of files & scroll bar
        if (sMax <= 0) scroll = 0;
        r.drawLevels(GameManager.levels, gc.getPlayerStats());
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / GameManager.TS + 1, 1, "wall");
        r.drawText(s.translate("Choose a level"), gc.getWidth() / 2, GameManager.TS / 2, 0, 0, -1, Font.STANDARD);
        //Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - GameManager.TS * 2, gc.getWidth() / GameManager.TS + 1, 2, "wall");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() - 2 * GameManager.TS;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Play":
                    btn.setAlignCoor(x, y + 10, 0, 1);
                    break;
                case "Back":
                    btn.setAlignCoor(x, gc.getHeight() - 10, 0, -1);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
