package engine.view;

import engine.gfx.Button;
import engine.*;

import java.awt.event.KeyEvent;

public class Credits extends View {

    private Settings s;
    private World world;
    private Button back;

    private String[] devs = {"Majoolwip", "Manu TD"};
    private String[] thanks = {"Marguerite Turbet", "Marie Lys Turbet", "Martin Turbet-Delof"};
    private String[] contribs = {"Anne Sospedra", "Firas Htm", "Gaël Di Malta", "Hamza Makri", "Wissem HF", "Yassine El", "Youssra El"};

    public Credits(Settings s, World world) {
        this.s = s;
        this.world = world;
        buttons.add(back = new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        boolean cursorHand = false;
        for (Button btn : buttons) {

            //Button Selection
            if (isSelected(gc, btn)) {
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
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

        r.drawBackground(world);
        r.drawMenuTitle(s.translate("Game credits").toUpperCase(), s.translate("Development team"));

        r.drawList(gc.getWidth() / 4, gc.getHeight() / 3, s.translate("DEVELOPERS"), devs);
        r.drawList(gc.getWidth() / 2, gc.getHeight() / 3, s.translate("CONTRIBUTORS"), contribs);
        r.drawList(gc.getWidth() - gc.getWidth() / 4, gc.getHeight() / 3, s.translate("THANKS TO"), thanks);

        back.setOffX(5);
        back.setOffY(5);

        for (Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}