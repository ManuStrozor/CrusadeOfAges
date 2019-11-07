package engine.view;

import engine.gfx.Button;
import engine.gfx.Font;
import engine.*;

import java.awt.event.KeyEvent;

public class Stats extends View {

    public Stats() {
        buttons.add(new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Hover
            if (btn.isHover(gc.getInput())) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            btn.hearHover(gc.getInput(), gc.getSb());

            //Button selection
            if (btn.isSelected(gc.getInput())) {
                gc.getSb().get("click").play();
                gc.setActiView(gc.getPrevView());
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getPrevView().equals("mainMenu")) r.drawBackground();
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle("Stats", null);

        for (int i = 0; i < gc.getPlayerStats().getStates().length; i++) {
            r.drawText(gc.getPlayerStats().getStates()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = " + gc.getPlayerStats().getValues()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, 1, 1, -1, Font.STANDARD);
        }

        for (Button btn : buttons) {
            btn.setCoor(5, 5, 1, 1);
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
