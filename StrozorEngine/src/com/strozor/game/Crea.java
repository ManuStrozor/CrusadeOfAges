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
    private int[] elems = {0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13};
    private int selected = 1;
    private int sltColor;

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
            creaMap.setP(gc.getInput().getMouseX() / GameManager.TS, gc.getInput().getMouseY() / GameManager.TS, sltColor);
        else if(gc.getInput().isButton(MouseEvent.BUTTON3))
            creaMap.setP(gc.getInput().getMouseX() / GameManager.TS, gc.getInput().getMouseY() / GameManager.TS, 0x00000000);

        updateMap();
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                r.drawBloc(bloc[x + y * creaMap.getW()], objectsImage, x, y);
            }
        }
        r.drawDock(gc, objectsImage, elems, selected);
    }

    public void updateMap() {
        for(int y = 0; y < creaMap.getH(); y++) {
            for(int x = 0; x < creaMap.getW(); x++) {
                int index = x + y * creaMap.getW();
                switch(creaMap.getP()[index]) {
                    case 0x00000000: bloc[index] = -1;break;//empty
                    case 0xff00ff00: bloc[index] = 0; break;//spawn
                    case 0xff000000: bloc[index] = 1; break;//walls
                    case 0xffff648c: bloc[index] = 2; break;//heart
                    case 0xffff0000: bloc[index] = 3; break;//skewer top
                    case 0xffff00ff: bloc[index] = 4; break;//skewer down
                    case 0xff0000ff: bloc[index] = 5; break;//level key
                    case 0xffff7700: bloc[index] = 6; break;//check point
                    case 0xffffff00: bloc[index] = 7; break;//coin
                    case 0xff00ffff: bloc[index] = 11;break;//torch
                    case 0xff777777: bloc[index] = 12;break;//bouncing
                    case 0xff999999: bloc[index] = 13;break;//exit door
                }
            }
        }
    }
}
