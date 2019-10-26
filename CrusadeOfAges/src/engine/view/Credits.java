package engine.view;

import engine.gfx.Button;
import engine.*;

import java.awt.event.KeyEvent;

public class Credits extends View {

    private String[] devs = {"Majoolwip", "Manu TD"};
    private String[] thanks = {"Marguerite Turbet", "Marie Lys Turbet", "Martin Turbet-Delof"};
    private String[] contribs = {"Anne Sospedra", "Firas Htm", "Gaël Di Malta", "Hamza Makri", "Wissem HF", "Yassine El", "Youssra El"};

    public Credits() {
        buttons.add(new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        boolean cursorHand = false;
        for (Button btn : buttons) {

            //Button Selection
            if (btn.isSelected(gc.getInput())) {
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
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
        r.drawBackground();

        r.drawMenuTitle("Game credits", "Development team");

        r.drawList("DEVELOPERS", devs, gc.getWidth() / 4, gc.getHeight() / 3);
        r.drawList("CONTRIBUTORS", contribs, gc.getWidth() / 2, gc.getHeight() / 3);
        r.drawList("THANKS TO", thanks, gc.getWidth() - gc.getWidth() / 4, gc.getHeight() / 3);

        for (Button btn : buttons) {
            btn.setCoor(5, 5, 1, 1);
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
