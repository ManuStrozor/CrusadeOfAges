package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Crea extends AbstractGame {

    public static Image creaMap;

    private ImageTile objectsImage = new ImageTile("/objects.png", GameManager.TS, GameManager.TS);

    private int[] bloc;
    private int[] elems = {-1, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13};
    private int selected = 0;
    private int sltColor;
    private float animTorch = 0, animCoin = 0;

    public Crea(int width, int height) {
        creaMap = new Image(new int[width * height], width, height);
        bloc = new int[creaMap.getW() * creaMap.getH()];
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        //Animations
        animTorch += dt * 3;
        animCoin += dt * 3;

        if(gc.getInput().getScroll() > 0)
            selected = (selected == elems.length - 1) ? 0 : selected + 1;
        else if(gc.getInput().getScroll() < 0)
            selected = (selected == 0) ? elems.length - 1 : selected - 1;

        switch(elems[selected]) {
            case -1: sltColor = 0xff00ff00; break;
            case 1: sltColor = 0xff000000; break;
            case 2: sltColor = 0xffff648c; break;
            case 3: sltColor = 0xffff0000; break;
            case 4: sltColor = 0xffff00ff; break;
            case 5: sltColor = 0xff0000ff; break;
            case 6: sltColor = 0xffff7700; break;
            case 7: sltColor = 0xffffff00; break;
            case 11: sltColor = 0xff00ffff; break;
            case 12: sltColor = 0xff777777; break;
            case 13: sltColor = 0xff999999; break;
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getCurrState() == 4) {
            if(gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D)) r.setCamX(r.getCamX() + 6);
            if(gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q)) r.setCamX(r.getCamX() - 6);
            if(gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) r.setCamY(r.getCamY() + 6);
            if(gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z)) r.setCamY(r.getCamY() - 6);
        }

        int mouseTileX = (gc.getInput().getMouseX() + r.getCamX()) / GameManager.TS;
        int mouseTileY = (gc.getInput().getMouseY() + r.getCamY()) / GameManager.TS;

        if(mouseTileX >= 0 && mouseTileX < creaMap.getW() && mouseTileY >= 0 && mouseTileY < creaMap.getH()) {
            if(gc.getInput().isButton(MouseEvent.BUTTON1))
                creaMap.setP(mouseTileX, mouseTileY, sltColor);
            else if(gc.getInput().isButton(MouseEvent.BUTTON3))
                creaMap.setP(mouseTileX, mouseTileY, 0x00000000);
        }

        r.setAnimTorch(animTorch > 3 ? animTorch = 0 : animTorch);
        r.setAnimCoin(animCoin > 6 ? animCoin = 0 : animCoin);

        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                int index = x + y * creaMap.getW();
                switch(creaMap.getP()[index]) {
                    case 0xff00ff00: bloc[index] = -1;break;//Spawn
                    case 0x00000000: bloc[index] = 0; break;//Wall
                    case 0xff000000: bloc[index] = 1; break;//Floor
                    case 0xffff648c: bloc[index] = 2; break;//Heart
                    case 0xffff0000: bloc[index] = 3; break;//Bottom trap
                    case 0xffff00ff: bloc[index] = 4; break;//Top trap
                    case 0xff0000ff: bloc[index] = 5; break;//Key
                    case 0xffff7700: bloc[index] = 6; break;//Check point
                    case 0xffffff00: bloc[index] = 7; break;//Coin
                    case 0xff00ffff: bloc[index] = 11;break;//Torch
                    case 0xff777777: bloc[index] = 12;break;//Bouncing bloc
                    case 0xff999999: bloc[index] = 13;break;//Door
                }
                r.drawBloc(bloc[index], objectsImage, x * GameManager.TS, y * GameManager.TS, false);
            }
        }
        r.drawDock(gc, objectsImage, elems, selected);
    }
}
