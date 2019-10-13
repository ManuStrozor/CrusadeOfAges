package engine.view;

import engine.gfx.Button;
import engine.gfx.Font;
import engine.*;

import java.awt.event.KeyEvent;

public class Stats extends View {

    private Settings s;
    private World world;
    private Button back;

    public Stats(Settings s, World world) {
        this.s = s;
        this.world = world;
        buttons.add(back = new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        //Button selection
        for (Button btn : buttons) {
            if (isSelected(gc, btn)) {
                gc.getClick().play();
                gc.setActiView(gc.getPrevView());
            }

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHover().isRunning()) gc.getHover().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        if (gc.getPrevView().equals("mainMenu")) r.drawBackground(gc, world, "wall");
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(gc, s.translate("Stats").toUpperCase(), "");

        for (int i = 0; i < gc.getPlayerStats().getStates().length; i++) {
            r.drawText(gc.getPlayerStats().getStates()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = " + gc.getPlayerStats().getValues()[i], gc.getWidth() / 2, gc.getHeight() / 4 + i * 15, 1, 1, -1, Font.STANDARD);
        }

        back.setOffX(5);
        back.setOffY(5);

        for (Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
