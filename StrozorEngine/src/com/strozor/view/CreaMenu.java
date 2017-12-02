package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.game.Crea;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CreaMenu extends View {

    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button crea, save, menu;

    public CreaMenu() {
        select = new SoundClip("/audio/hover.wav");

        buttons.add(crea = new Button(130, 20, "Back to creative mode", 4));
        buttons.add(save = new Button(130, 20, "Save & Quit game", -1));
        buttons.add(menu = new Button(130, 20, "Quit to title", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(4);

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    if(btn == save)
                        Crea.creaMap.saveImage();
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(5);
                }
            } else {
                btn.setBgColor(0xff424242);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        crea.setOffY(gc.getHeight() / 3 - crea.getHeight() / 2);
        save.setOffY(crea.getOffY() + crea.getHeight() + 10);
        menu.setOffY(save.getOffY() + save.getHeight() + 10);

        for(Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - btn.getWidth() / 2);
            r.drawButton(btn);
        }
    }
}
