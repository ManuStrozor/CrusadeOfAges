package com.strozor.game;

import com.strozor.engine.AbstractGame;
import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Image;
import com.strozor.engine.GameMap;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Edit extends AbstractGame {

    public static Image creaImg;
    public static boolean spawn = false, once = false, newOne = false;
    public static String rename = "";

    private GameMap map;
    private Player player;

    private int width, height;

    private String[] elems = {
            "spawn",
            "floor",
            "slime",
            "ladder",
            "ground spikes",
            "ceiling spikes",
            "pill",
            "coin",
            "key",
            "skull",
            "lever left",
            "torch",
            "door"
    };
    private int color, scroll = 0;

    public Edit(int width, int height) {
        map = new GameMap();
        player = new Player("Tester", map, 999);
        creaImg = new Image(new int[width * height], width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(!once) {
            if(newOne) {
                creaImg = null;
                creaImg = new Image(new int[width * height], width, height);
            }
            map.init(creaImg);
            once = true;
        }

        if(!spawn && (map.getSpawnX() != -1 || map.getSpawnY() != -1)) {
            player = new Player("player", map, 1);
            spawn = true;
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(5);

        //Player update
        if(spawn && (map.getSpawnX() != -1 || map.getSpawnY() != -1))
            player.creativeUpdate(gc, dt);

        if(gc.getInput().getScroll() > 0)
            scroll = (scroll == elems.length - 1) ? 0 : scroll + 1;
        else if(gc.getInput().getScroll() < 0)
            scroll = (scroll == 0) ? elems.length - 1 : scroll - 1;

        color = map.getCol(elems[scroll]);
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getCurrState() == 4) {
            int mouseX = gc.getInput().getMouseX();
            int mouseY = gc.getInput().getMouseY();

            int x = (mouseX + r.getCamX()) / GameManager.TS;
            int y = (mouseY + r.getCamY()) / GameManager.TS;

            int speed = 10;

            if(r.getCamX() + gc.getWidth() < creaImg.getW() * GameManager.TS) {
                if(mouseX == gc.getWidth() - 1) r.setCamX(r.getCamX() + speed);
            }
            if(r.getCamX() > -GameManager.TS) {
                if(mouseX == 0) r.setCamX(r.getCamX() - speed);
            }
            if(r.getCamY() + gc.getHeight() < creaImg.getH() * GameManager.TS) {
                if(mouseY == gc.getHeight() - 1) r.setCamY(r.getCamY() + speed);
            }
            if(r.getCamY() > 0) {
                if(mouseY == 0) r.setCamY(r.getCamY() - speed);
            }

            if(mouseX > GameManager.TS) {
                if(gc.getInput().isButton(MouseEvent.BUTTON1)) {

                    if(elems[scroll].equals("spawn") && map.getSpawnX() != -1 && map.getSpawnY() != -1) {

                        //Delete previous spawn
                        creaImg.setP(map.getSpawnX(), map.getSpawnY(), 0x00000000);
                        map.setBloc(map.getSpawnX(), map.getSpawnY(), 0);

                        //create new spawn
                        creaImg.setP(x, y, color);

                        //Reset player position
                        player.getEvent().respawn(map.getSpawnX(), map.getSpawnY());
                    } else {
                        creaImg.setP(x, y, color);
                    }

                    map.setBloc(x, y, map.getCol(elems[scroll]));

                } else if(gc.getInput().isButton(MouseEvent.BUTTON3)) {
                    creaImg.setP(x, y, 0x00000000);
                    map.setBloc(x, y, 0);
                }
            }
        }

        r.drawMap(map);
        r.drawMiniMap(gc, creaImg);
        r.drawDock(gc, map, elems, scroll);
        r.drawArrows(gc, map, creaImg.getW(), creaImg.getH());

        if(spawn && (map.getSpawnX() != -1 || map.getSpawnY() != -1))
            player.render(gc, r);
    }
}
