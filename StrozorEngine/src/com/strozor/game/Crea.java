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
    public static Image creaMap;

    private ImageTile objectsImage = new ImageTile("/objects.png", TS, TS);

    private int[] bloc;
    private int[] elems = {0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13};
    private int selected = 0;
    private int sltColor = 0xff000000;

    public Crea() {
        creaMap = new Image(new int[1800], 60, 30);
        bloc = new int[creaMap.getW() * creaMap.getH()];
    }

    @Override
    public void update(GameContainer gc, float dt) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        if(gc.getInput().getScroll() > 0)
            selected = (selected == elems.length - 1) ? 0 : selected + 1;
        else if(gc.getInput().getScroll() < 0)
            selected = (selected == 0) ? elems.length - 1 : selected - 1;

        switch(elems[selected]) {
            case 0: sltColor = 0xff00ff00; break;
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

        if(gc.getInput().isButton(MouseEvent.BUTTON1))
            creaMap.setP(gc.getInput().getMouseX() / TS, gc.getInput().getMouseY() / TS, sltColor);
        else if(gc.getInput().isButton(MouseEvent.BUTTON3))
            creaMap.setP(gc.getInput().getMouseX() / TS, gc.getInput().getMouseY() / TS, 0x00000000);

        updateMap();
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                int index = x + y * creaMap.getW();
                if(bloc[index] != 1 || bloc[index] == -1)
                    r.drawImageTile(objectsImage, x * TS, y * TS, 1, 0);
                switch(bloc[index]) {
                    case 0: r.drawImageTile(objectsImage, x * TS, y * TS, 2, 0); break;
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

        r.drawDock(gc.getWidth() / 2 - (elems.length * (TS + 5)) / 2, gc.getHeight() - TS - 6, objectsImage, elems, selected);
    }

    public void updateMap() {
        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                int index = x + y * creaMap.getW();
                switch(creaMap.getP()[index]) {
                    case 0x00000000: bloc[index] = -1; break;//empty
                    case 0xff00ff00: bloc[index] = 0; break;//spawn
                    case 0xff000000: bloc[index] = 1; break;//walls
                    case 0xffff648c: bloc[index] = 2; break;//heart
                    case 0xffff0000: bloc[index] = 3; break;//skewer top
                    case 0xffff00ff: bloc[index] = 4; break;//skewer down
                    case 0xff0000ff: bloc[index] = 5; break;//level key
                    case 0xffff7700: bloc[index] = 6; break;//check point
                    case 0xffffff00: bloc[index] = 7; break;//coin
                    case 0xff00ffff: bloc[index] = 11; break;//torch
                    case 0xff777777: bloc[index] = 12; break;//bouncing
                    case 0xff999999: bloc[index] = 13; break;//exit door
                }
            }
        }
    }
}
