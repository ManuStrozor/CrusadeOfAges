package com.strozor.game;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.ImageTile;
import com.strozor.engine.GameMap;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player extends GameObject {

    private GameMap gameMap;

    private ImageTile playerImage = new ImageTile("/player.png", GameManager.TS, GameManager.TS);

    private ArrayList<FlashNotif> notifs = new ArrayList<>();

    private int tileX, tileY, direction = 0;
    private float offX, offY, anim = 0;

    private SoundClip jump, impaled, coin, bonus, checkPoint;

    private int speed, ground = 2;
    private float fallDist = 0;

    public Player(String tag, GameMap gameMap, int lives) {
        this.gameMap = gameMap;
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
        tileX = gameMap.getSpawnX();
        tileY = gameMap.getSpawnY();
        posX = tileX * GameManager.TS;
        posY = tileY * GameManager.TS;
        offX = 0;
        offY = 0;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        //Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(gc, dt);
            if(notifs.get(i).isEnded()) notifs.remove(i);
        }

        //Blocs
        Bloc curr = gameMap.getBloc(tileX, tileY);
        Bloc bottom = gameMap.getBloc(tileX, tileY+1);

        //Bouncing bloc
        if(bottom.getName().equals("Slime bloc") && fallDist == 0) {
            fallDist = -10;
            if(!gameMap.isSolid(tileX, tileY - 1) && !gameMap.isSolid(tileX + (int) Math.signum((int) offX), tileY - 1))
                jump.play();
        }

        //Events on current bloc
        switch(curr.getName()) {
            case "Coin":
                curr.remove();
                coins++;
                coin.play();
                break;
            case "Check point":
                if(tileX != gameMap.getSpawnX() && tileY != gameMap.getSpawnY()) {
                    gameMap.setSpawnX(tileX);
                    gameMap.setSpawnY(tileY);
                    checkPoint.play();
                }
                break;
            case "Heart":
                curr.remove();
                lives++;
                bonus.play();
                break;
            case "Key":
                curr.remove();
                keys++;
                bonus.play();
                break;
            case "Ground trap":
                lives--;
                impaled.play();
                break;
            case "Ceiling trap":
                lives--;
                impaled.play();
                break;
            case "Ground trap blooded":
                lives--;
                impaled.play();
                break;
            case "Ceiling trap blooded":
                lives--;
                impaled.play();
                break;
        }

        if(curr.getName().equals("Door") && keys >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            keys--;
            respawn(gm, true);
        }

        //Hit trap
        if(curr.getId() == 3 || curr.getId() == 4 || curr.getId() == 9 || curr.getId() == 10) {
            if(curr.getId() == 3) gameMap.setBloc(tileX, tileY, 9);
            else if(curr.getId() == 4) gameMap.setBloc(tileX, tileY, 10);

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
            if (gc.getInput().isKey(KeyEvent.VK_LEFT) || gc.getInput().isKey(KeyEvent.VK_Q)) {
                if (gameMap.isSolid(tileX - 1, tileY) || gameMap.isSolid(tileX - 1, tileY + (int) Math.signum((int) offY))) {
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
                if (gameMap.isSolid(tileX + 1, tileY) || gameMap.isSolid(tileX + 1, tileY + (int) Math.signum((int) offY))) {
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

            if(curr.getName().equals("Ladder") || (bottom.getName().equals("Ladder") && fallDist == 0)) {
                //Climbing ladders
                fallDist = 0;
                ground = 0;
                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if(!curr.getName().equals("Ladder")) {
                        fallDist = -5;
                        if (!gameMap.isSolid(tileX, tileY - 1) && !gameMap.isSolid(tileX + (int)Math.signum((int) offX), tileY - 1))
                            jump.play();
                        ground++;
                    } else if (gameMap.isSolid(tileX, tileY - 1) || gameMap.isSolid(tileX + (int) Math.signum((int) offX), tileY - 1)) {
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
                    if (gameMap.isSolid(tileX, tileY + 1) || gameMap.isSolid(tileX + (int) Math.signum((int) offX), tileY + 1)) {
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
                    if (!gameMap.isSolid(tileX, tileY - 1) && !gameMap.isSolid(tileX + (int)Math.signum((int) offX), tileY - 1))
                        jump.play();
                    ground++;
                }
                offY += fallDist;
                if (fallDist < 0) {
                    if ((gameMap.isSolid(tileX, tileY - 1) || gameMap.isSolid(tileX + (int)Math.signum((int) offX), tileY - 1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }
                if (fallDist > 0) {
                    if ((gameMap.isSolid(tileX, tileY + 1) || gameMap.isSolid(tileX + (int)Math.signum((int) offX), tileY + 1)) && offY > 0) {
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

        if(anim > 4) anim = 0;
        if(fallDist != 0) anim = 3;

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void render(GameContainer gc, GameManager gm, GameRender r) {
        r.drawImageTile(playerImage, (int)posX, (int)posY, direction, (int)anim);
        for(FlashNotif notif : notifs) notif.render(gc, r);
    }

    private void respawn(GameManager gm, boolean reload) {
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
                gm.load(gm.getMapTest());
                gm.setCurrLevel(0);
            }
        }
        direction = 0;
        fallDist = 0;
        tileX = gameMap.getSpawnX();
        tileY = gameMap.getSpawnY();
        offX = 0;
        offY = 0;
    }
}
