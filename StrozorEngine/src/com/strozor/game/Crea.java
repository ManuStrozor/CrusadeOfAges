package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.gfx.ImageTile;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Crea extends AbstractGame {

    public static final int TS = 16;

    private Image creaMap;
    private ImageTile objectsImage = new ImageTile("/objects.png", TS, TS);

    private int[] bloc;

    public Crea() {
        creaMap = new Image(new int[1800], 35, 20);
        bloc = new int[creaMap.getW() * creaMap.getH()];
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        if(gc.getInput().isButton(MouseEvent.BUTTON1))
            creaMap.setP(gc.getInput().getMouseX() / TS, gc.getInput().getMouseY() / TS, 0xff000000);
        else if(gc.getInput().isButton(MouseEvent.BUTTON3))
            creaMap.setP(gc.getInput().getMouseX() / TS, gc.getInput().getMouseY() / TS, -1);

        updateMap();
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                int index = x + y * creaMap.getW();
                if(bloc[index] != 1 || bloc[index] == -1) r.drawImageTile(objectsImage, x * TS, y * TS, 1, 0);
                switch(bloc[index]) {
                    case 1: r.drawImageTile(objectsImage, x * TS, y * TS, 0, 0); break;
                    case 2: r.drawImageTile(objectsImage, x * TS, y * TS, 3, 2); break;
                    case 3: r.drawImageTile(objectsImage, x * TS, y * TS, 1, 1); break;
                    case 4: r.drawImageTile(objectsImage, x * TS, y * TS, 2, 1); break;
                    case 5: r.drawImageTile(objectsImage, x * TS, y * TS, 3, 1); break;
                    case 6: r.drawImageTile(objectsImage, x * TS, y * TS, 3, 0); break;
                    case 7: r.drawImageTile(objectsImage, x * TS, y * TS, 5, 0); break;
                    case 11: r.drawImageTile(objectsImage, x * TS, y * TS, 4, 0); break;
                    case 12: r.drawImageTile(objectsImage, x * TS, y * TS, 0, 2); break;
                    case 13:
                        r.drawImageTile(objectsImage, x * TS, (y - 1) * TS, 4, 3);
                        r.drawImageTile(objectsImage, x * TS, y * TS, 4, 4);
                        break;
                }
            }
        }


    }

    public void updateMap() {
        int index;
        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                index = x + y * creaMap.getW();
                switch(creaMap.getP()[index]) {
                    case -1://empty
                        bloc[index] = -1;
                        break;
                    case 0xff00ff00://spawn
                        bloc[index] = 0;
                        break;
                    case 0xff000000://walls
                        bloc[index] = 1;
                        break;
                    case 0xffff648c://heart
                        bloc[index] = 2;
                        break;
                    case 0xffff0000://skewer top
                        bloc[index] = 3;
                        break;
                    case 0xffff00ff://skewer down
                        bloc[index] = 4;
                        break;
                    case 0xff0000ff://level key
                        bloc[index] = 5;
                        break;
                    case 0xffff7700://check point
                        bloc[index] = 6;
                        break;
                    case 0xffffff00://coin
                        bloc[index] = 7;
                        break;
                    case 0xff00ffff://torch
                        bloc[index] = 11;
                        break;
                    case 0xff777777://bouncing
                        bloc[index] = 12;
                        break;
                    case 0xff999999://exit door
                        bloc[index] = 13;
                        break;
                }
            }
        }
    }
}
