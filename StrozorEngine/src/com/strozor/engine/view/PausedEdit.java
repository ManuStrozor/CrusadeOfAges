package com.strozor.engine.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.game.Edit;

import java.awt.event.KeyEvent;

public class PausedEdit extends View {

    private Settings s;
    private SoundClip hover, click;
    private Button save;

    public PausedEdit(Settings settings) {
        s = settings;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");

        buttons.add(save = new Button("Save", 8));
        buttons.add(new Button("Cancel", 8));
        buttons.add(new Button("Back", 4));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(4);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                if(btn == save) {
                    Edit.creaImg.saveIt(Edit.rename);
                    CreativeMode.once = false;
                }
                click.play();
                gc.setState(btn.getGoState());
                gc.setLastState(5);
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

        r.fillRect(r.getCamX(), r.getCamY(), gc.getWidth(), gc.getHeight(), 0x99000000);

        int startY = gc.getHeight()/4;

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth()/2-85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
