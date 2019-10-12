package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.audio.SoundClip;
import engine.gfx.Button;

import java.awt.event.KeyEvent;

public class PausedGame extends View {

    private Settings s;
    private SoundClip hover, click;

    public PausedGame(Settings settings) {
        s = settings;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(new Button("Back to game", "game"));
        buttons.add(new Button("Stats", "stats"));
        buttons.add(new Button("Quit to title", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("game");

        //Button selection
        for (Button btn : buttons) {
            if (isSelected(gc, btn)) {
                if (btn.getText().contains("Quit")) gc.getPlayerStats().saveData();
                click.play();
                gc.setActiView(btn.getTargetView());
                gc.setPrevView(gc.getPrevView());
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

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            if (btn.getText().contains("Quit")) startY += 10;
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
