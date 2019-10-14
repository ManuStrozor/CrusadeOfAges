package game.objects;

import engine.GameContainer;
import engine.Renderer;
import engine.World;
import engine.gfx.Font;
import engine.gfx.Light;
import engine.gfx.Sprite;
import game.Conf;
import game.GameManager;
import game.actions.Move;

public class NetworkPlayer extends GameObject {

    private Move move;

    private World world;
    private Sprite plSprite;

    private int tileX, tileY, direction = 0;
    private float upPosX, upPosY, offX, offY, anim = 0;

    private int ground = 2;
    private float fallDist = 0;

    public static int tileSize = GameManager.TS;

    public NetworkPlayer(String tag, World world, int lives) {
        String path = Conf.APPDATA + "/assets/player.png";
        plSprite = new Sprite(path, tileSize, tileSize, true);

        this.world = world;
        this.tag = tag;
        this.lives = lives;

        move = new Move(this, world);

        width = tileSize;
        height = tileSize;
        tileX = world.getSpawnX();
        tileY = world.getSpawnY();
        posX = tileX * tileSize;
        posY = tileY * tileSize;
        upPosX = posX;
        upPosY = posY;
        offX = 0;
        offY = 0;
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) {

    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawLight(new Light(200, 0xffffff99), (int) posX + tileSize / 2, (int) posY + tileSize / 2);
        r.drawSprite(plSprite, (int) posX, (int) posY, direction, (int) anim, tileSize);
        r.drawText(tag, (int) posX / tileSize, (int) posY / tileSize, 1, -1, -1, Font.STANDARD);
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
