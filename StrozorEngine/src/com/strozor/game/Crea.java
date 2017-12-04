package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Light;
import com.strozor.engine.gfx.Map;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Crea extends AbstractGame {

    public static Image creaImg;

    private Map map;
    private int[] elems = {-1, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13};
    private int color, curr = 0;

    public Crea(int width, int height) {
        this.map = new Map();
        creaImg = new Image(new int[width * height], width, height);
        load(creaImg);
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        //Animations
        map.animate(dt * 3);

        if(gc.getInput().getScroll() > 0)
            curr = (curr == elems.length - 1) ? 0 : curr + 1;
        else if(gc.getInput().getScroll() < 0)
            curr = (curr == 0) ? elems.length - 1 : curr - 1;

        switch(elems[curr]) {
            case -1: color = 0xff00ff00; break;
            case 1: color = 0xff000000; break;
            case 2: color = 0xffff648c; break;
            case 3: color = 0xffff0000; break;
            case 4: color = 0xffff00ff; break;
            case 5: color = 0xff0000ff; break;
            case 6: color = 0xffff7700; break;
            case 7: color = 0xffffff00; break;
            case 11: color = 0xff00ffff; break;
            case 12: color = 0xff777777; break;
            case 13: color = 0xff999999; break;
        }
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
                map.setBloc(mouseTileX, mouseTileY, elems[curr]);
            } else if(gc.getInput().isButton(MouseEvent.BUTTON3)) {
                creaImg.setP(mouseTileX, mouseTileY, 0x00000000);
                map.setBloc(mouseTileX, mouseTileY, 0);
            }
        }
        r.drawMap(map);
        r.drawDock(gc, elems, curr);
    }

    private void load(Image img) {
        map.initMap(img.getW(), img.getH());
        for(int y = 0; y < map.getHeight(); y++) {
            for(int x = 0; x < map.getWidth(); x++) {
                map.initBloc(x, y, img.getP()[x + y * map.getWidth()]);
            }
        }
    }
}
