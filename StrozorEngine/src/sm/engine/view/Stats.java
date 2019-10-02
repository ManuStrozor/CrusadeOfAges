package sm.engine.view;

import sm.engine.audio.SoundClip;
import sm.engine.gfx.Button;
import sm.engine.gfx.Font;
import sm.engine.*;

import java.awt.event.KeyEvent;

public class Stats extends View {

    private Settings s;
    private World world;
    private SoundClip hover, click;
    private Button back;

    public Stats(Settings s, World world) {
        this.s = s;
        this.world = world;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(back = new Button(60, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                click.play();
                gc.setState(btn.getGoState());
                gc.setState(gc.getLastState());
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

        if(gc.getLastState() == 0) r.drawBackground(gc, world, "wall");
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(gc, s.translate("Stats").toUpperCase(), "");

        for(int i = 0; i < gc.getDataStats().getStates().length; i++) {
            r.drawText(gc.getDataStats().getStates()[i], gc.getWidth()/2, gc.getHeight()/4+i*15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = " + gc.getDataStats().getValues()[i], gc.getWidth()/2, gc.getHeight()/4+i*15, 1, 1, -1, Font.STANDARD);
        }

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
