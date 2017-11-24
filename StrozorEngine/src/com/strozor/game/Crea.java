package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;

import java.awt.event.KeyEvent;

public class Crea extends AbstractGame {

    public Crea() {

    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setState(2);
            gc.setLastState(4);
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

    }
}
