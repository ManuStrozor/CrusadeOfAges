package sm.game.objects;

import java.util.ArrayList;

import sm.engine.GameContainer;
import sm.engine.Renderer;
import sm.engine.World;
import sm.engine.gfx.Font;
import sm.engine.gfx.Light;
import sm.engine.gfx.Sprite;
import sm.game.Game;
import sm.game.Notification;
import sm.game.actions.Move;

public class NetworkPlayer extends GameObject {

    private Move move;

    private World world;
    private Sprite plSprite;
    private ArrayList<Notification> notifs = new ArrayList<>();

    private int tileX, tileY, direction = 0;
    private float upPosX, upPosY, offX, offY, anim = 0;

    private int lastFloorX, lastFlorrY;

    private int speed, ground = 2;
    private float fallDist = 0;

    public NetworkPlayer(String tag, World world, int lives) {
        String path = Game.APPDATA + "/assets/player.png";
        plSprite = new Sprite(path, Game.TS, Game.TS, true);

        this.world = world;
        this.tag = tag;
        this.lives = lives;

        move = new Move(this, world);

        width = Game.TS;
        height = Game.TS;
        tileX = world.getSpawnX();
        tileY = world.getSpawnY();
        posX = tileX * Game.TS;
        posY = tileY * Game.TS;
        upPosX = posX;
        upPosY = posY;
        offX = 0;
        offY = 0;
    }

    @Override
    public void update(GameContainer gc, Game gm, float dt) {

    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawLight(new Light(200, 0xffffff99),(int)posX + Game.TS / 2, (int)posY + Game.TS / 2);
        r.drawSprite(plSprite, (int)posX, (int)posY, direction, (int)anim);
        r.drawText(tag, (int)posX / Game.TS, (int)posY / Game.TS, 1, -1, -1, Font.STANDARD);
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
}
