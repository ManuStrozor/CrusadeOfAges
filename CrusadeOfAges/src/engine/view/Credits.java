package engine.view;

import engine.gfx.Button;
import engine.*;

import java.awt.event.KeyEvent;

public class Credits extends View {

    private String[] devs = {"Majoolwip", "Manu TD"};
    private String[] thanks = {"Marguerite Turbet", "Marie Lys Turbet", "Martin Turbet-Delof"};
    private String[] contribs = {"Anne Sospedra", "Gaël Di Malta", "Hamza Makri", "Wissem HF", "Yassine El", "Youssra El"};

    public Credits() {
        buttons.add(new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView(gc.getPrevView());
        }

        updateButtons(gc);

        if (btnSelected != null) {
            gc.setActiView(btnSelected.getTargetView());
        }
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
