package sm.engine.view;

import sm.engine.audio.SoundClip;
import sm.engine.gfx.Button;
import sm.engine.gfx.Font;
import sm.engine.*;

import java.awt.event.KeyEvent;

public class Stats extends View {

    private Settings s;
    private GameMap map;
    private SoundClip hover, click;
    private Button back;

    public Stats(Settings s, GameMap map) {
        this.s = s;
        this.map = map;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(back = new Button(60, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

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
    public void render(GameContainer gc, GameRender r) {

        if(gc.getLastState() == 0) r.drawBackground(gc, map, "wall");
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(gc, s.translate("Stats").toUpperCase(), "");

        for(int i = 0; i < gc.getData().getStates().length; i++) {
            r.drawText(gc.getData().getStates()[i], gc.getWidth()/2, gc.getHeight()/4+i*15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = " + gc.getData().getValues()[i], gc.getWidth()/2, gc.getHeight()/4+i*15, 1, 1, -1, Font.STANDARD);
        }

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
