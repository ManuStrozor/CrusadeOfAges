package game.entity;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Font;
import engine.gfx.Sprite;
import engine.World;
import engine.gfx.Light;
import game.Conf;
import game.Game;
import game.actions.Collect;
import game.actions.Event;
import game.actions.Move;

import java.awt.event.KeyEvent;

public class Player extends Entity {

    private Move move;
    private Collect collect;
    private Event event;

    private World world;
    private Sprite plSprite;

    private int tileX, tileY, direction = 0;
    private float offX, offY, anim = 0;

    private int lastFloorX, lastFlorrY;

    private int speed, ground = 2;
    private float fallDist = 0;
    private int padding = 8;

    public static int tileSize = Game.TS;

    public Player(String name, World world) {
        plSprite = new Sprite(Conf.SM_FOLDER + "/assets/player.png", tileSize, tileSize, true);

        this.world = world;
        tag = name;
        setLives(1);

        move = new Move(this, world);
        collect = new Collect(this, world);
        event = new Event(this, world);

        width = tileSize;
        height = tileSize;
        tileX = world.getLevel().getSpawnX();
        tileY = world.getLevel().getSpawnY();
        posX = tileX * tileSize;
        posY = tileY * tileSize;
        offX = 0;
        offY = 0;
    }

    @Override
    public void update(GameContainer gc, float dt) {

        String currTag = world.getBlocMap(tileX, tileY).getTag();
        String botTag = world.getBlocMap(tileX, tileY + 1).getTag();

        //Last floor
        if (!currTag.contains("spikes") && botTag.equals("floor") && fallDist == 0) {
            lastFloorX = tileX;
            lastFlorrY = tileY;
        }

        //Slime bloc
        if (botTag.equals("slime") && fallDist == 0) {
            if (!gc.getInput().isKey(KeyEvent.VK_DOWN) && !gc.getInput().isKey(KeyEvent.VK_S)) {
                move.jump(10);
                gc.getPlayerStats().upValueOf("Slime");
            }
        }

        //Collectings & Events on current bloc
        switch (world.getBlocMap(tileX, tileY).getTag()) {
            case "coin":
                collect.coin();
                break;
            case "pill":
                collect.pill();
                break;
            case "key":
                collect.key();
                break;
            case "skull":
                collect.skull();
                break;
            case "door":
                event.switchLevel(gc);
                break;
            case "lever_left":
                event.actionLever(gc, currTag);
                break;
        }

        //Hit spikes
        if (currTag.contains("spikes")) {

            event.impale(gc);
            gc.getPlayerStats().upValueOf("Death");
            if (getLives() == 0) {
                this.death();
                gc.getPlayerStats().upValueOf("Game over");
                gc.setActiView("gameOver");
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
            if (currTag.equals("ladder") || (botTag.equals("ladder") && fallDist == 0)) {

                fallDist = 0;
                ground = 0;

                if ((gc.getInput().isKey(KeyEvent.VK_UP) || gc.getInput().isKey(KeyEvent.VK_Z) || gc.getInput().isKey(KeyEvent.VK_SPACE))) {
                    if (!currTag.equals("ladder")) move.jump(4);
                    else move.upLadder(dt, speed);
                }

                if ((gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S))) {
                    move.downLadder(dt, speed);
                }

                //Jump & Gravity
            } else {
                fallDist += dt * 14;

                if (gc.getInput().isKeyDown(KeyEvent.VK_UP) || gc.getInput().isKeyDown(KeyEvent.VK_Z) || gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
                    if (ground <= 1) {
                        move.jump(5);
                        gc.getPlayerStats().upValueOf("Jump");
                    }
                }

                offY += fallDist;

                if (fallDist < 0) {
                    if ((world.getBlocMap(tileX, tileY - 1).isSolid() || world.getBlocMap(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY - 1).isSolid()) && offY < 0) {
                        fallDist = 0;
                        offY = 0;
                    }
                }

                if (fallDist > 0) {
                    if ((world.getBlocMap(tileX, tileY + 1).isSolid() || world.getBlocMap(tileX + (int) Math.signum((int) Math.abs(offX) > padding ? offX : 0), tileY + 1).isSolid()) && offY > 0) {
                        fallDist = 0;
                        offY = 0;
                        ground = 0;
                    }
                }
            }
        }

        //Update Tile position
        if (offY > tileSize / 2.0) {
            tileY++;
            offY -= tileSize;
        }
        if (offY < -tileSize / 2.0) {
            tileY--;
            offY += tileSize;
        }
        if (offX > tileSize / 2.0) {
            tileX++;
            offX -= tileSize;
        }
        if (offX < -tileSize / 2.0) {
            tileX--;
            offX += tileSize;
        }

        //Snick -> Slow
        if (currTag.equals("ladder")) {
            speed = 140;
            anim += dt * 6;
        } else if (gc.getInput().isKey(KeyEvent.VK_DOWN) || gc.getInput().isKey(KeyEvent.VK_S)) {
            speed = 40;
            anim += dt * 3;
        } else {
            speed = 180;
            anim += dt * 8;
        }

        if ((!gc.getInput().isKey(KeyEvent.VK_RIGHT) &&
                !gc.getInput().isKey(KeyEvent.VK_D) &&
                !gc.getInput().isKey(KeyEvent.VK_LEFT) &&
                !gc.getInput().isKey(KeyEvent.VK_Q)) || anim > 4) {
            anim = 0;
        }

        if (fallDist != 0) anim = 3;

        posX = tileX * tileSize + offX;
        posY = tileY * tileSize + offY;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawLight(new Light(200, 0xffffff99), (int) posX + tileSize / 2, (int) posY + tileSize / 2);
        r.drawSprite(plSprite, (int) posX, (int) posY, direction, (int) anim, tileSize);
        r.drawText(tag, (int) posX - r.getCamX(), (int) posY - r.getCamY(), 1, -1, -1, Font.STANDARD);
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

    public int getPadding() {
        return padding;
    }
}
