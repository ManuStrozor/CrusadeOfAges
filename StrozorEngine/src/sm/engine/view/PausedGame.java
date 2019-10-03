package sm.engine.view;

import sm.engine.GameContainer;
import sm.engine.Renderer;
import sm.engine.Settings;
import sm.engine.audio.SoundClip;
import sm.engine.gfx.Button;

import java.awt.event.KeyEvent;

public class PausedGame extends View {

    private Settings s;
    private SoundClip hover, click;

    public PausedGame(Settings settings) {
        s = settings;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(new Button("Back to game", 1));
        buttons.add(new Button("Stats", 10));
        buttons.add(new Button("Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(1);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                if(btn.getText().contains("Quit")) gc.getDataStats().saveData();
                click.play();
                gc.setState(btn.getGoState());
                gc.setLastState(1);
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

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(gc, s.translate("Paused").toUpperCase(), "");

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            if(btn.getText().contains("Quit")) startY += 10;
            btn.setOffX(gc.getWidth()/2-85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
