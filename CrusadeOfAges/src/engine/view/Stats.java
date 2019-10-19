package engine.view;

import engine.gfx.Button;
import engine.gfx.Font;
import engine.*;

import java.awt.event.KeyEvent;

public class Stats extends View {

    private Settings s;
    private World world;

    public Stats(Settings s, World world) {
        this.s = s;
        this.world = world;
        buttons.add(new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        boolean cursorHand = false;
        for (Button btn : buttons) {

            //Button selection
            if (isSelected(gc, btn)) {
                gc.getClickSound().play();
                gc.setActiView(gc.getPrevView());
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

        if (gc.getPrevView().equals("mainMenu")) r.drawBackground(world);
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(s.translate("Stats").toUpperCase(), null);

        for (int i = 0; i < gc.getPlayerStats().getStates().length; i++) {
            r.drawText(gc.getPlayerStats().getStates()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = " + gc.getPlayerStats().getValues()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, 1, 1, -1, Font.STANDARD);
        }

        for (Button btn : buttons) {
            btn.setAlignCoor(5, 5, 1, 1);
            r.drawButton(btn);
        }
    }
}
