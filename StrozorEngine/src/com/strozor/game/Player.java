package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.GameMap;
import com.strozor.engine.gfx.Light;
import com.strozor.game.actions.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    private Move move;
    private Collecting collect;
    private Event event;

    private GameMap map;
    private ImageTile plImg;
    private ArrayList<FlashNotif> notifs = new ArrayList<>();

    private int tileX, tileY, direction = 0;
    private float offX, offY, anim = 0;

    private int lastFloorX, lastFlorrY;

    private int speed, ground = 2;
    private float fallDist = 0;

    public Player(String tag, GameMap map, int lives) {
        String path = System.getenv("APPDATA") + "\\.squaremonster\\assets\\player.png";
        plImg = new ImageTile(path, GameManager.TS, GameManager.TS, true);

        move = new Move(this, map);
        collect = new Collecting(this, map);
        event = new Event(this, map);

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
    public void creativeUpdate(GameContainer gc, float dt) {

        String currTag = map.getTag(tileX, tileY);
        String botTag = map.getTag(tileX, tileY+1);

        //Last floor
        if(!currTag.contains("spikes") && botTag.equals("floor")) {
            lastFloorX = tileX;
            lastFlorrY = tileY;
        }

        //Slime bloc
        if(botTag.equals("slime") && fallDist == 0) {
            if(!gc.getInput().isKey(KeyEvent.VK_DOWN) && !gc.getInput().isKey(KeyEvent.VK_S))
                move.jump(10);
        }

        //Hit spikes
        if(currTag.contains("spikes")) {
            event.impale();
            event.respawn(lastFloorX, lastFlorrY);
        } else {

            //Left & Right
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q))
                move.toLeft(dt, speed);
            if (gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D))
                move.toRight(dt, speed);

            //Up & Down ladders
            if(currTag.equals("ladder") || (botTag.equals("ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if(!currTag.equals("ladder")) move.jump(4);
                    else move.upLadder(dt, speed);
                }

                if ((gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S))) {
                    move.downLadder(dt, speed);
                }
            } else {
                //Jump & Gravity
                fallDist += dt * 14;

                if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
                    if(ground <= 1) move.jump(5);
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((map.isSolid(tileX, tileY-1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY-1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((map.isSolid(tileX, tileY+1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY+1)) && offY > 0) {
                        fallDist = 0;
                        offY = 0;
                        ground = 0;
                    }
                }
            }
        }


        //Update Tile position
        if(offY > GameManager.TS / 2.0) {
            tileY++;
            offY -= GameManager.TS;
        }
        if(offY < -GameManager.TS / 2.0) {
            tileY--;
            offY += GameManager.TS;
        }
        if(offX > GameManager.TS / 2.0) {
            tileX++;
            offX -= GameManager.TS;
        }
        if(offX < -GameManager.TS / 2.0) {
            tileX--;
            offX += GameManager.TS;
        }

        //Snick -> Slow
        if(currTag.equals("ladder")) {
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
    public void update(GameContainer gc, GameManager gm, float dt) {

        //Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if(notifs.get(i).isEnded()) notifs.remove(i);
        }

        String currTag = map.getTag(tileX, tileY);
        String botTag = map.getTag(tileX, tileY+1);

        //Last floor
        if(!currTag.contains("spikes") && botTag.equals("floor") && fallDist == 0) {
            lastFloorX = tileX;
            lastFlorrY = tileY;
        }

        //Slime bloc
        if(botTag.equals("slime") && fallDist == 0) {
            if(!gc.getInput().isKey(KeyEvent.VK_DOWN) && !gc.getInput().isKey(KeyEvent.VK_S)) {
                move.jump(10);
                gc.getData().upValueOf("Slime");
            }
        }

        //Collectings & Events on current bloc
        switch(map.getTag(tileX, tileY)) {
            case "coin": collect.coin(gc); break;
            case "pill": collect.pill(gc); break;
            case "key": collect.key(gc); break;
            case "skull": collect.skull(gc); break;
            case "door": event.switchLevel(gc, gm); break;
            case "lever left": event.actionLever(gc, currTag); break;
        }

        //Hit spikes
        if(currTag.contains("spikes")) {

            event.impale();
            gc.getData().upValueOf("Death");
            if(this.lives == 0) {
                this.setDead(true);
                gc.getData().upValueOf("Game over");
                gc.setState(7);
                gc.setLastState(1);
            } else {
                event.respawn(lastFloorX, lastFlorrY);
            }

        } else {

            //Left & Right
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q))
                move.toLeft(dt, speed);
            if (gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D))
                move.toRight(dt, speed);

            //Up & Down ladders
            if(currTag.equals("ladder") || (botTag.equals("ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if(!currTag.equals("ladder")) move.jump(4);
                    else move.upLadder(dt, speed);
                }

                if ((gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S))) {
                    move.downLadder(dt, speed);
                }

                //Jump & Gravity
            } else {
                fallDist += dt * 14;

                if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
                    if(ground <= 1) {
                        move.jump(5);
                        gc.getData().upValueOf("Jump");
                    }
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((map.isSolid(tileX, tileY-1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY-1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((map.isSolid(tileX, tileY+1) || map.isSolid(tileX + (int)Math.signum((int) offX), tileY+1)) && offY > 0) {
                        fallDist = 0;
                        offY = 0;
                        ground = 0;
                    }
                }
            }
        }


        //Update Tile position
        if(offY > GameManager.TS / 2.0) {
            tileY++;
            offY -= GameManager.TS;
        }
        if(offY < -GameManager.TS / 2.0) {
            tileY--;
            offY += GameManager.TS;
        }
        if(offX > GameManager.TS / 2.0) {
            tileX++;
            offX -= GameManager.TS;
        }
        if(offX < -GameManager.TS / 2.0) {
            tileX--;
            offX += GameManager.TS;
        }

        //Snick -> Slow
        if(currTag.equals("ladder")) {
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
    public void render(GameContainer gc, GameRender r) {
        r.drawLight(new Light(200, 0xffffff99),(int)posX + GameManager.TS / 2, (int)posY + GameManager.TS / 2);
        r.drawImageTile(plImg, (int)posX, (int)posY, direction, (int)anim);
        for(FlashNotif notif : notifs) notif.render(gc, r);
    }

    public int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
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

    public Event getEvent() {
        return event;
    }
}
