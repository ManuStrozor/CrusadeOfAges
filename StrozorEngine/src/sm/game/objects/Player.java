package sm.game.objects;

import sm.engine.GameContainer;
import sm.engine.Renderer;
import sm.engine.gfx.Font;
import sm.engine.gfx.Sprite;
import sm.engine.World;
import sm.engine.gfx.Light;
import sm.game.Conf;
import sm.game.Notification;
import sm.game.GameManager;
import sm.game.actions.Collect;
import sm.game.actions.Event;
import sm.game.actions.Move;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends GameObject {

    private Move move;
    private Collect collect;
    private Event event;

    private World world;
    private Sprite plSprite;
    private ArrayList<Notification> notifs = new ArrayList<>();

    private int tileX, tileY, direction = 0;
    private float upPosX, upPosY, offX, offY, anim = 0;

    private int lastFloorX, lastFlorrY;

    private int speed, ground = 2;
    private float fallDist = 0;

    public Player(String tag, World world, int lives) {
        String path = Conf.SM_FOLDER + "/assets/player.png";
        plSprite = new Sprite(path, GameManager.TS, GameManager.TS, true);

        this.world = world;
        this.tag = tag;
        this.lives = lives;

        move = new Move(this, world);
        collect = new Collect(this, world);
        event = new Event(this, world);

        width = GameManager.TS;
        height = GameManager.TS;
        tileX = world.getSpawnX();
        tileY = world.getSpawnY();
        posX = tileX * GameManager.TS;
        posY = tileY * GameManager.TS;
        upPosX = posX;
        upPosY = posY;
        offX = 0;
        offY = 0;
    }

    public void creativeUpdate(GameContainer gc, float dt) {

        String currTag = world.getTag(tileX, tileY);
        String botTag = world.getTag(tileX, tileY+1);

        //Last floor
        if(!currTag.contains("spikes") && botTag.equals("floor")) {
            lastFloorX = tileX;
            lastFlorrY = tileY;
        }

        //Slime bloc
        if(botTag.equals("slime") && fallDist == 0) {
            if(!gc.getInputHandler().isKey(KeyEvent.VK_DOWN) && !gc.getInputHandler().isKey(KeyEvent.VK_S))
                move.jump(10);
        }

        //Hit spikes
        if(currTag.contains("spikes")) {
            event.impale();
            event.respawn(lastFloorX, lastFlorrY);
        } else {

            //Left & Right
            if (gc.getInputHandler().isKey(KeyEvent.VK_LEFT) || gc.getInputHandler().isKey(KeyEvent.VK_Q))
                move.toLeft(dt, speed);
            if (gc.getInputHandler().isKey(KeyEvent.VK_RIGHT) || gc.getInputHandler().isKey(KeyEvent.VK_D))
                move.toRight(dt, speed);

            //Up & Down ladders
            if(currTag.equals("ladder") || (botTag.equals("ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInputHandler().isKey(KeyEvent.VK_UP) || gc.getInputHandler().isKey(KeyEvent.VK_Z) || gc.getInputHandler().isKey(KeyEvent.VK_SPACE))) {
                    if(!currTag.equals("ladder")) move.jump(4);
                    else move.upLadder(dt, speed);
                }

                if ((gc.getInputHandler().isKey(KeyEvent.VK_DOWN) || gc.getInputHandler().isKey(KeyEvent.VK_S))) {
                    move.downLadder(dt, speed);
                }
            } else {
                //Jump & Gravity
                fallDist += dt * 14;

                if (gc.getInputHandler().isKeyDown(KeyEvent.VK_UP) || gc.getInputHandler().isKeyDown(KeyEvent.VK_Z) || gc.getInputHandler().isKeyDown(KeyEvent.VK_SPACE)) {
                    if(ground <= 1) move.jump(5);
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((world.isSolid(tileX, tileY-1) || world.isSolid(tileX + (int)Math.signum((int) offX), tileY-1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((world.isSolid(tileX, tileY+1) || world.isSolid(tileX + (int)Math.signum((int) offX), tileY+1)) && offY > 0) {
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
        } else if(gc.getInputHandler().isKey(KeyEvent.VK_DOWN) || gc.getInputHandler().isKey(KeyEvent.VK_S)) {
            speed = 40;
            anim += dt * 4;
        } else {
            speed = 180;
            anim += dt * 18;
        }

        if((!gc.getInputHandler().isKey(KeyEvent.VK_RIGHT) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_D) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_LEFT) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_Q)) || anim > 4) {
            anim = 0;
        }

        if(fallDist != 0) anim = 3;

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

        try {
            if (upPosX != posX || upPosY != posY) {
                upPosX = posX;
                upPosY = posY;
                gm.getDos().writeUTF(gm.getSocket().getLocalPort() + ":" + upPosX + ":" + upPosY + ":" + anim);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Notifications update
        for(int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if(notifs.get(i).isEnded()) {
                notifs.remove(i--);
            }
        }

        String currTag = world.getTag(tileX, tileY);
        String botTag = world.getTag(tileX, tileY+1);

        //Last floor
        if(!currTag.contains("spikes") && botTag.equals("floor") && fallDist == 0) {
            lastFloorX = tileX;
            lastFlorrY = tileY;
        }

        //Slime bloc
        if(botTag.equals("slime") && fallDist == 0) {
            if(!gc.getInputHandler().isKey(KeyEvent.VK_DOWN) && !gc.getInputHandler().isKey(KeyEvent.VK_S)) {
                move.jump(10);
                gc.getPlayerStats().upValueOf("Slime");
            }
        }

        //Collectings & Events on current bloc
        switch(world.getTag(tileX, tileY)) {
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
            gc.getPlayerStats().upValueOf("Death");
            if(this.lives == 0) {
                this.setDead(true);
                gc.getPlayerStats().upValueOf("Game over");
                gc.setState(7);
                gc.setLastState(1);
            } else {
                event.respawn(lastFloorX, lastFlorrY);
            }

        } else {

            //Left & Right
            if (gc.getInputHandler().isKey(KeyEvent.VK_LEFT) || gc.getInputHandler().isKey(KeyEvent.VK_Q))
                move.toLeft(dt, speed);
            if (gc.getInputHandler().isKey(KeyEvent.VK_RIGHT) || gc.getInputHandler().isKey(KeyEvent.VK_D))
                move.toRight(dt, speed);

            //Up & Down ladders
            if(currTag.equals("ladder") || (botTag.equals("ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInputHandler().isKey(KeyEvent.VK_UP) || gc.getInputHandler().isKey(KeyEvent.VK_Z) || gc.getInputHandler().isKey(KeyEvent.VK_SPACE))) {
                    if(!currTag.equals("ladder")) move.jump(4);
                    else move.upLadder(dt, speed);
                }

                if ((gc.getInputHandler().isKey(KeyEvent.VK_DOWN) || gc.getInputHandler().isKey(KeyEvent.VK_S))) {
                    move.downLadder(dt, speed);
                }

                //Jump & Gravity
            } else {
                fallDist += dt * 14;

                if (gc.getInputHandler().isKeyDown(KeyEvent.VK_UP) || gc.getInputHandler().isKeyDown(KeyEvent.VK_Z) || gc.getInputHandler().isKeyDown(KeyEvent.VK_SPACE)) {
                    if(ground <= 1) {
                        move.jump(5);
                        gc.getPlayerStats().upValueOf("Jump");
                    }
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((world.isSolid(tileX, tileY-1) || world.isSolid(tileX + (int)Math.signum((int) offX), tileY-1)) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((world.isSolid(tileX, tileY+1) || world.isSolid(tileX + (int)Math.signum((int) offX), tileY+1)) && offY > 0) {
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
        } else if(gc.getInputHandler().isKey(KeyEvent.VK_DOWN) || gc.getInputHandler().isKey(KeyEvent.VK_S)) {
            speed = 40;
            anim += dt * 4;
        } else {
            speed = 180;
            anim += dt * 18;
        }

        if((!gc.getInputHandler().isKey(KeyEvent.VK_RIGHT) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_D) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_LEFT) &&
                !gc.getInputHandler().isKey(KeyEvent.VK_Q)) || anim > 4) {
            anim = 0;
        }

        if(fallDist != 0) anim = 3;

        posX = tileX * GameManager.TS + offX;
        posY = tileY * GameManager.TS + offY;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawLight(new Light(200, 0xffffff99),(int)posX + GameManager.TS / 2, (int)posY + GameManager.TS / 2);
        r.drawSprite(plSprite, (int)posX, (int)posY, direction, (int)anim);
        r.drawText(tag, (int)posX / GameManager.TS, (int)posY / GameManager.TS, 1, -1, -1, Font.STANDARD);
        for(Notification notif : notifs) notif.render(gc, r);
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
