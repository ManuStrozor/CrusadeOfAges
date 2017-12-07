package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.GameMap;
import com.strozor.game.actions.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    private Move move = new Move(this);
    private Collecting collect = new Collecting(this);
    private Event event = new Event(this);

    private GameMap map;
    private ImageTile playerImage = new ImageTile("/player.png", GameManager.TS, GameManager.TS);
    private ArrayList<FlashNotif> notifs = new ArrayList<>();

    private int tileX, tileY, direction = 0;
    private float offX, offY, anim = 0;

    private int speed, ground = 2;
    private float fallDist = 0;

    Player(String tag, GameMap map, int lives) {
        this.map = map;
        this.tag = tag;
        this.lives = lives;
        width = GameManager.TS;
        height = GameManager.TS;
        tileX = map.getSpawnX();
        tileY = map.getSpawnY();
        posX = tileX * GameManager.TS;
        posY = tileY * GameManager.TS;
        offX = 0;
        offY = 0;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        //Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if(notifs.get(i).isEnded()) notifs.remove(i);
        }

        //Blocs
        Bloc curr = map.getBloc(tileX, tileY);
        Bloc bottom = map.getBloc(tileX, tileY+1);

        //Slime bloc
        if(bottom.getName().equals("Slime bloc") && fallDist == 0) {
            move.jump(map, 10);
        }

        //Collectings & Events on current bloc
        switch(curr.getName()) {
            case "Coin": collect.coin(curr); break;
            case "Health pill": collect.pill(curr); break;
            case "Key": collect.key(curr); break;
            case "Ground spikes": event.impale(map); break;
            case "Ceiling spikes": event.impale(map); break;
            case "Ground spikes blooded": event.impale(map); break;
            case "Ceiling spikes blooded": event.impale(map); break;
            case "Check point": event.savePosition(map); break;
            case "Door": event.switchLevel(gc, gm); break;
        }

        //Hit spikes
        if(curr.getId() == 3 || curr.getId() == 4 || curr.getId() == 9 || curr.getId() == 10) {

            if(this.lives == 0) {
                this.setDead(true);
                gc.setState(7);
                gc.setLastState(1);
                gm.getGameOver().play();
            } else {
                respawn(gm, false);
            }

        } else {

            //Left & Right
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q))
                move.toLeft(map, dt, speed);
            if (gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D))
                move.toRight(map, dt, speed);

            //Up & Down ladders
            if(curr.getName().equals("Ladder") || (bottom.getName().equals("Ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if(!curr.getName().equals("Ladder")) move.jump(map, 3);
                    else move.upLadder(map, dt, speed);
                }

                if ((gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S))) {
                    move.downLadder(map, dt, speed);
                }

                //Jump & Gravity
            } else {
                fallDist += dt * 14;

                if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
                    if(ground <= 1) move.jump(map, 5);
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((map.isSolid(tileX, tileY - 1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY - 1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((map.isSolid(tileX, tileY + 1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY + 1)) && offY > 0) {
                        fallDist = 0;
                        offY = 0;
                        ground = 0;
                    }
                }
            }
        }


        //Update Tile position
        if(offY > GameManager.TS / 2) {
            tileY++;
            offY -= GameManager.TS;
        }
        if(offY < -GameManager.TS / 2) {
            tileY--;
            offY += GameManager.TS;
        }
        if(offX > GameManager.TS / 2) {
            tileX++;
            offX -= GameManager.TS;
        }
        if(offX < -GameManager.TS / 2) {
            tileX--;
            offX += GameManager.TS;
        }

        //Snick -> Slow
        if(curr.getName().equals("Ladder")) {
            speed = 140;
            anim += dt * 14;
        } else if(gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) {
            speed = 40;
            anim += dt * 4;
        } else {
            speed = 180;
            anim += dt * 18;
        }

        if((!gc.getInput().isKey(KeyEvent.VK_RIGHT) &&
                !gc.getInput().isKey(KeyEvent.VK_D) &&
                !gc.getInput().isKey(KeyEvent.VK_LEFT) &&
                !gc.getInput().isKey(KeyEvent.VK_Q)) || anim > 4) {
            anim = 0;
        }

        if(fallDist != 0) anim = 3;

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void render(GameContainer gc, GameManager gm, GameRender r) {
        r.drawImageTile(playerImage, (int)posX, (int)posY, direction, (int)anim);
        for(FlashNotif notif : notifs) notif.render(gc, r);
    }

    public void respawn(GameManager gm, boolean reload) {
        if(reload) {
            if(!gm.isMapTesting()) {
                if(gm.getCurrLevel() + 1 < gm.getLevelList().length) {
                    gm.load(gm.getLevelList()[gm.getCurrLevel() + 1]);
                    gm.setCurrLevel(gm.getCurrLevel() + 1);
                } else {
                    gm.load(gm.getLevelList()[0]);
                    gm.setCurrLevel(0);
                }
            } else {
                gm.load(GameManager.getMapTest());
                gm.setCurrLevel(0);
            }
        }
        direction = 0;
        fallDist = 0;
        tileX = map.getSpawnX();
        tileY = map.getSpawnY();
        offX = 0;
        offY = 0;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public float getOffX() {
        return offX;
    }

    public void setOffX(float offX) {
        this.offX = offX;
    }

    public float getOffY() {
        return offY;
    }

    public void setOffY(float offY) {
        this.offY = offY;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getGround() {
        return ground;
    }

    public void setGround(int ground) {
        this.ground = ground;
    }

    public void setFallDist(float fallDist) {
        this.fallDist = fallDist;
    }
}
