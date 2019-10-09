package sm.engine.view;

import sm.engine.audio.SoundClip;
import sm.engine.gfx.Button;
import sm.engine.*;

import java.awt.event.KeyEvent;

public class Credits extends View {

    private Settings s;
    private World world;
    private SoundClip hover, click;
    private Button back;

    private String[] devs = {"Majoolwip", "Manu TD"};
    private String[] thanks = {"Marguerite Turbet", "Marie Lys Turbet", "Martin Turbet-Delof"};
    private String[] contribs = {"Anne Sospedra", "Firas Htm", "Gaël Di Malta", "Hamza Makri", "Wissem HF", "Yassine El", "Youssra El"};

    public Credits(Settings s, World world) {
        this.s = s;
        this.world = world;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(back = new Button(60, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView(gc.getPrevView());

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                click.play();
                gc.setActiView(btn.getTargetView());
                gc.setPrevView("credits");
            }

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!hover.isRunning()) hover.play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.drawBackground(gc, world, "wall");
        r.drawMenuTitle(gc, s.translate("Game credits").toUpperCase(), s.translate("Development team"));

        r.drawList(gc.getWidth() / 4, gc.getHeight() / 3, s.translate("DEVELOPERS"), devs);
        r.drawList(gc.getWidth() / 2, gc.getHeight() / 3, s.translate("CONTRIBUTORS"), contribs);
        r.drawList(gc.getWidth() - gc.getWidth() / 4, gc.getHeight() / 3, s.translate("THANKS TO"), thanks);

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
