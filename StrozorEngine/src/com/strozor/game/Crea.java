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

        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();

        int x = (mouseX + r.getCamX()) / GameManager.TS;
        int y = (mouseY + r.getCamY()) / GameManager.TS;

        if(x >= 0 && x < creaImg.getW() && y >= 0 && y < creaImg.getH() && gc.getCurrState() == 4) {
            int speed = 10;
            if(mouseX == gc.getWidth() - 1) r.setCamX(r.getCamX() + speed);
            if(mouseX == 0) r.setCamX(r.getCamX() - speed);
            if(mouseY == gc.getHeight() - 1) r.setCamY(r.getCamY() + speed);
            if(mouseY == 0) r.setCamY(r.getCamY() - speed);

            if(mouseX > GameManager.TS) {
                if(gc.getInput().isButton(MouseEvent.BUTTON1)) {
                    creaImg.setP(x, y, color);
                    gameMap.setBloc(x, y, elems[scroll]);
                } else if(gc.getInput().isButton(MouseEvent.BUTTON3)) {
                    creaImg.setP(x, y, 0x00000000);
                    gameMap.setBloc(x, y, 0);
                }
            }
        }

        r.drawMap(gameMap);
        r.drawDock(gc, elems, scroll);
        r.drawArrows(gc, creaImg.getW(), creaImg.getH());
    }
}
