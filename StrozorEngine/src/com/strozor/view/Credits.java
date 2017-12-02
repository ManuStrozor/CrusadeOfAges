package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Credits extends View {

    private ImageTile objectsImage;
    private SoundClip select;

    private ArrayList<Button> buttons = new ArrayList<>();
    private Button back;

    public Credits() {
        objectsImage = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);
        select = new SoundClip("/audio/hover.wav");
        buttons.add(back = new Button(60, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        for(Button btn : buttons) {
            if (mouseIsHover(gc, btn)) {
                btn.setBgColor(0xff263238);
                if(gc.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                    select.play();
                    gc.setState(btn.getGoState());
                    gc.setLastState(6);
                }
            } else {
                btn.setBgColor(0xff424242);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawBackground(gc, objectsImage, 1, 0);
        r.drawMenuTitle(gc,"GAME CREDITS", "Strozor Inc.");

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn);
    }
}
