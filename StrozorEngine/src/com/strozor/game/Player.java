package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.gfx.Map;

import java.awt.event.KeyEvent;

public class Player extends GameObject {

    private Map map;

    private ImageTile playerImage = new ImageTile("/player.png", GameManager.TS, GameManager.TS);

    private int tileX, tileY, direction = 0;
    private float offX, offY, pauseRes = 0, anim = 0;

    private SoundClip jump, impaled, coin, bonus, checkPoint;

    private int speed, ground = 2;
    private float fallDist = 0;

    public Player(String tag, Map map, int lives) {
        this.map = map;
        this.tag = tag;
        this.lives = lives;

        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
        impaled = new SoundClip("/audio/impaled.wav");
        coin = new SoundClip("/audio/getcoin.wav");
        coin.setVolume(-20f);
        bonus = new SoundClip("/audio/getlife.wav");
        bonus.setVolume(-15f);
        checkPoint = new SoundClip("/audio/checkpoint.wav");
        checkPoint.setVolume(-15f);

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

        //Bouncing block
        if(map.getId(tileX, tileY + 1) == 12 && fallDist == 0) {
            fallDist = -10;
            if(!map.getSolid(tileX, tileY - 1) && !map.getSolid(tileX + (int) Math.signum((int) offX), tileY - 1))
                jump.play();
        }

        //Gain life
        if(map.getId(tileX, tileY) == 2) {
            lives++;
            map.setBloc(tileX, tileY, 0);
            bonus.play();
        }

        //Gain coin
        if(map.getId(tileX, tileY) == 7) {
            coins++;
            map.setBloc(tileX, tileY, 0);
            coin.play();
        }

        //Gain key
        if(map.getId(tileX, tileY) == 5) {
            keys++;
            map.setBloc(tileX, tileY, 0);
            bonus.play();
        }

        //Win Time & Switch Level
        if(keys >= 1 && map.getId(tileX, tileY) == 13 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            keys--;
            if(!gm.isMapTesting()) {
                if(gm.getCurrLevel() + 1 < gm.getLevelList().length) {
                    gm.load(gm.getLevelList()[gm.getCurrLevel() + 1]);
                    gm.setCurrLevel(gm.getCurrLevel() + 1);
                } else {
                    gm.load(gm.getLevelList()[0]);
                    gm.setCurrLevel(0);
                }
            } else {
                gm.load(gm.getMapTest());
                gm.setCurrLevel(0);
            }
            respawn(map.getSpawnX(), map.getSpawnY());
        }

        //CheckPoint
        if(map.getId(tileX, tileY) == 6 && tileX != map.getSpawnX() && tileY != map.getSpawnY()) {
            map.setSpawnX(tileX);
            map.setSpawnY(tileY);
            checkPoint.play();
        }

        //Hit trap
        if(map.getId(tileX, tileY) == 3 || map.getId(tileX, tileY) == 4 || map.getId(tileX, tileY) == 9 || map.getId(tileX, tileY) == 10) {
            if(map.getId(tileX, tileY) == 3 || map.getId(tileX, tileY) == 3)
                map.setBloc(tileX, tileY, 9);
            else if(map.getId(tileX, tileY) == 4 || map.getId(tileX, tileY) == 10)
                map.setBloc(tileX, tileY, 10);

            if(pauseRes == 0) {
                lives--;
                impaled.play();
            }

            if(this.lives == 0) {
                this.setDead(true);
                gc.setState(7);
                gc.setLastState(1);
                gm.getGameOver().play();
            } else {
                pauseRes += dt * 10;
                if(pauseRes > 3) {
                    respawn(map.getSpawnX(), map.getSpawnY());
                    pauseRes = 0;
                }
            }
        } else {
            //Left & Right
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q)) {
                if (map.getSolid(tileX - 1, tileY) || map.getSolid(tileX - 1, tileY + (int) Math.signum((int) offY))) {
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
                if (map.getSolid(tileX + 1, tileY) || map.getSolid(tileX + 1, tileY + (int) Math.signum((int) offY))) {
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

            if(map.getName(tileX, tileY).equals("Ladder") || (map.getName(tileX, tileY + 1).equals("Ladder") && fallDist == 0)) {
                //Climbing ladders
                fallDist = 0;
                ground = 0;
                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if(!map.getName(tileX, tileY).equals("Ladder")) {
                        fallDist = -5;
                        if (!map.getSolid(tileX, tileY - 1) && !map.getSolid(tileX + (int)Math.signum((int) offX), tileY - 1))
                            jump.play();
                        ground++;
                    } else if (map.getSolid(tileX, tileY - 1) || map.getSolid(tileX + (int) Math.signum((int) offX), tileY - 1)) {
                        if (offY > 0) {
                            offY -= dt * speed;
                            if (offY < 0) offY = 0;
                        } else {
                            offY = 0;
                        }
                    } else {
                        offY -= dt * speed;
                    }
                } else if ((gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S))) {
                    if (map.getSolid(tileX, tileY + 1) || map.getSolid(tileX + (int) Math.signum((int) offX), tileY + 1)) {
                        if (offY < 0) {
                            offY += dt * speed;
                            if (offY > 0) offY = 0;
                        } else {
                            offY = 0;
                        }
                    } else {
                        offY += dt * speed;
                    }
                }
            } else {
                //Jump & Gravity
                fallDist += dt * 14;

                if ((gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) && ground <= 1) {
                    fallDist = -5;
                    if (!map.getSolid(tileX, tileY - 1) && !map.getSolid(tileX + (int)Math.signum((int) offX), tileY - 1))
                        jump.play();
                    ground++;
                }
                offY += fallDist;
                if (fallDist < 0) {
                    if ((map.getSolid(tileX, tileY - 1) || map.getSolid(tileX + (int)Math.signum((int) offX), tileY - 1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }
                if (fallDist > 0) {
                    if ((map.getSolid(tileX, tileY + 1) || map.getSolid(tileX + (int)Math.signum((int) offX), tileY + 1)) && offY > 0) {
                        fallDist = 0;
                        offY = 0;
                        ground = 0;
                    }
                }
            }
        }

        //Final Position
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
        if(map.getName(tileX, tileY).equals("Ladder")) {
            speed = 180;
            anim += dt * 18;
        } else if(gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) {
            speed = 40;
            anim += dt * 4;
        } else {
            speed = 180;
            anim += dt * 18;
        }

        if(anim > 4) anim = 0;
        if(fallDist != 0) anim = 3;

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void render(GameContainer gc, GameManager gm, GameRender r) {
        r.drawImageTile(playerImage, (int)posX, (int)posY, direction, (int)anim);
        if(gc.getSettings().isShowLights()) {
            //r.drawLight(gm.getlPlayer(), (int) posX + GameManager.TS / 2, (int) posY + GameManager.TS / 2);
        }
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
