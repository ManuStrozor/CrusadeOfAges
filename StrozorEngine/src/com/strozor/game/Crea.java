package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.GameMap;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Crea extends AbstractGame {

    public static Image creaImg;

    private GameMap gameMap;
    private int[] elems = {-1, 1, 12, 8, 3, 4, 2, 7, 5, 6, 11, 13};
    private int color, scroll = 0;

    public Crea(int width, int height) {
        this.gameMap = new GameMap();
        creaImg = new Image(new int[width * height], width, height);
        gameMap.init(creaImg);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        //Animations
        gameMap.animate(dt * 3);

        if(gc.getInput().getScroll() > 0)
            scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
        else if(gc.getInput().getScroll() < 0)
            scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;

        Bloc bloc = new Bloc(elems[scroll]);
        color = bloc.getCode();
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        int mouseTileX = (gc.getInput().getMouseX() + r.getCamX()) / GameManager.TS;
        int mouseTileY = (gc.getInput().getMouseY() + r.getCamY()) / GameManager.TS;

        if(mouseTileX >= 0 && mouseTileX < creaImg.getW() && mouseTileY >= 0 && mouseTileY < creaImg.getH()) {
            if(gc.getCurrState() == 4) {
                int speed = 10;
                if(gc.getInput().getMouseX() == gc.getWidth() - 1) r.setCamX(r.getCamX() + speed);
                if(gc.getInput().getMouseX() == 0) r.setCamX(r.getCamX() - speed);
                if(gc.getInput().getMouseY() == gc.getHeight() - 1) r.setCamY(r.getCamY() + speed);
                if(gc.getInput().getMouseY() == 0) r.setCamY(r.getCamY() - speed);
            }

            if(gc.getInput().isButton(MouseEvent.BUTTON1)) {
                creaImg.setP(mouseTileX, mouseTileY, color);
                gameMap.setBloc(mouseTileX, mouseTileY, elems[scroll]);
            } else if(gc.getInput().isButton(MouseEvent.BUTTON3)) {
                creaImg.setP(mouseTileX, mouseTileY, 0x00000000);
                gameMap.setBloc(mouseTileX, mouseTileY, 0);
            }
        }
        r.drawMap(gameMap);
        r.drawDock(gc, elems, scroll);
    }
}
