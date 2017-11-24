package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.ImageTile;

import java.awt.event.KeyEvent;

public class Player extends GameObject {

    private ImageTile playerImage = new ImageTile("/player.png", GameManager.TS, GameManager.TS);

    private int tileX, tileY, direction = 0;
    private float offX, offY, pauseRes = 0, anim = 0;

    private SoundClip jump, impaled, coin, bonus, checkPoint;

    private int speed, ground = 2;
    private float fallDist = 0;

    public Player(String tag, int posX, int posY, int lives) {
        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
        impaled = new SoundClip("/audio/impaled.wav");
        coin = new SoundClip("/audio/getcoin.wav");
        coin.setVolume(-20f);
        bonus = new SoundClip("/audio/getlife.wav");
        bonus.setVolume(-15f);
        checkPoint = new SoundClip("/audio/checkpoint.wav");
        checkPoint.setVolume(-15f);

        tileX = posX;
        tileY = posY;
        offX = 0;
        offY = 0;
        width = GameManager.TS;
        height = GameManager.TS;

        this.tag = tag;
        this.posX = posX * GameManager.TS;
        this.posY = posY * GameManager.TS;
        this.lives = lives;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        //Blood prints
        if(tileY + 1 < gm.getLevelH()) {
            if (gm.getBloc(tileX, tileY + 1) == 1 && fallDist == 0)
                gm.setBloc(tileX, tileY + 1, 8);
        }

        //Bouncing block
        if(gm.getBloc(tileX, tileY) == 12 && fallDist == 0) {
            fallDist = -6;
            if(!gm.getSolid(tileX, tileY - 1) && !gm.getSolid(tileX + (int) Math.signum((int) offX), tileY - 1))
                jump.play();
        }

        //Gain life
        if(gm.getBloc(tileX, tileY) == 2) {
            lives++;
            gm.setBloc(tileX, tileY, 0);
            bonus.play();
        }

        //Gain coin
        if(gm.getBloc(tileX, tileY) == 7) {
            coins++;
            gm.setBloc(tileX, tileY, 0);
            coin.play();
        }

        //Gain key
        if(gm.getBloc(tileX, tileY) == 5) {
            keys++;
            gm.setBloc(tileX, tileY, 0);
            bonus.play();
        }

        //Win Time & Switch Level
        if(keys >= 1 && gm.getBloc(tileX, tileY) == 13 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            keys--;
            if(!gm.isMapTesting()) {
                if(gm.getCurrLevel() + 1 < gm.getLevelList().length) {
                    gm.loadLevel(gm.getLevelList()[gm.getCurrLevel() + 1]);
                    gm.setCurrLevel(gm.getCurrLevel() + 1);
                } else {
                    gm.loadLevel(gm.getLevelList()[0]);
                    gm.setCurrLevel(0);
                }
            } else {
                gm.loadLevel(gm.getMapTest());
                gm.setCurrLevel(0);
            }
            respawn(gm.getSpawnX(), gm.getSpawnY());
        }

        //CheckPoint
        if(gm.getBloc(tileX, tileY) == 6 && tileX != gm.getSpawnX() && tileY != gm.getSpawnY()) {
            gm.setSpawnX(tileX);
            gm.setSpawnY(tileY);
            checkPoint.play();
        }

        //Hit skewers
        if(gm.getBloc(tileX, tileY) == 3 || gm.getBloc(tileX, tileY) == 4 || gm.getBloc(tileX, tileY) == 9 || gm.getBloc(tileX, tileY) == 10) {
            if(gm.getBloc(tileX, tileY) == 3 || gm.getBloc(tileX, tileY) == 3)
                gm.setBloc(tileX, tileY, 9);
            else if(gm.getBloc(tileX, tileY) == 4 || gm.getBloc(tileX, tileY) == 10)
                gm.setBloc(tileX, tileY, 10);

            if(pauseRes == 0) {
                lives--;
                impaled.play();
            }

            if(this.lives == 0) {
                this.setDead(true);
                gm.getGameOver().play();
            } else {
                pauseRes += dt * 10;
                if(pauseRes > 3) {
                    respawn(gm.getSpawnX(), gm.getSpawnY());
                    pauseRes = 0;
                }
            }
        } else {
            //Left & Right
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q)) {
                if (gm.getSolid(tileX - 1, tileY) || gm.getSolid(tileX - 1, tileY + (int) Math.signum((int) offY))) {
                    if (offX > 0) {
                        offX -= dt * speed;
                        if (offX < 0) offX = 0;
                    } else {
                        offX = 0;
                    }
                } else {
                    offX -= dt * speed;
                }
            }

            if (gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D)) {
                if (gm.getSolid(tileX + 1, tileY) || gm.getSolid(tileX + 1, tileY + (int) Math.signum((int) offY))) {
                    if (offX < 0) {
                        offX += dt * speed;
                        if (offX > 0) offX = 0;
                    } else {
                        offX = 0;
                    }
                } else {
                    offX += dt * speed;
                }
            }

            //Jump & Gravity
            fallDist += dt * 10;

            if ((gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) && ground <= 1) {
                fallDist = -3;
                if (!gm.getSolid(tileX, tileY - 1) && !gm.getSolid(tileX + (int)Math.signum((int) offX), tileY - 1))
                    jump.play();
                ground++;
            }

            offY += fallDist;

            if (fallDist < 0) {
                if ((gm.getSolid(tileX, tileY - 1) || gm.getSolid(tileX + (int)Math.signum((int) offX), tileY - 1)) && offY < 0) {
                    fallDist = 0;
                    offY = 0;
                }
            }

            if (fallDist > 0) {
                if ((gm.getSolid(tileX, tileY + 1) || gm.getSolid(tileX + (int)Math.signum((int) offX), tileY + 1)) && offY > 0) {
                    fallDist = 0;
                    offY = 0;
                    ground = 0;
                }
            }
        }

        //Final Position
        if(offY > gm.TS / 2) {
            tileY++;
            offY -= gm.TS;
        }
        if(offY < -gm.TS / 2) {
            tileY--;
            offY += gm.TS;
        }
        if(offX > gm.TS / 2) {
            tileX++;
            offX -= gm.TS;
        }
        if(offX < -gm.TS / 2) {
            tileX--;
            offX += gm.TS;
        }
        //END Final Position

        //Player faces & animations
        if(gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) {
            direction = 0;
        } else if(gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            direction = 3;
        }

        if(gc.getInput().isKey(KeyEvent.VK_RIGHT) || gc.getInput().isKey(KeyEvent.VK_D)) {
            direction = 1;
        } else if(gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q)) {
            direction = 2;
        } else {
            anim = 0;
        }

        //Snick -> Slow
        if(gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) {
            speed = 30;
            anim += dt * 6;
        } else {
            speed = 100;
            anim += dt * 20;
        }

        if(anim > 4) anim = 0;
        if(fallDist != 0) anim = 3;
        //END Player faces & animation

        posX = tileX * gm.TS + offX;
        posY = tileY * gm.TS + offY;
    }

    @Override
    public void render(GameContainer gc, GameManager gm, GameRender r) {
        r.drawImageTile(playerImage, (int)posX, (int)posY, direction, (int)anim);
        if(gc.getSettings().isShowLights())
            r.drawLight(gm.getlPlayer(), (int)posX + gm.TS / 2, (int)posY + gm.TS / 2);
    }

    private void respawn(int x, int y) {
        direction = 0;
        fallDist = 0;
        tileX = x;
        tileY = y;
        offX = 0;
        offY = 0;
    }
}
