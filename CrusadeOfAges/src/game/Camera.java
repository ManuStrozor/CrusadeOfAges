package game;

import engine.GameContainer;
import engine.Renderer;
import engine.World;
import game.objects.GameObject;

public class Camera {

    private float offX, offY;
    private World world;
    private String targetTag;
    private GameObject target = null;

    private int tileSize = GameManager.TS;

    Camera(String tag, World world) {
        this.targetTag = tag;
        this.world = world;
    }

    public void update(GameContainer gc, GameManager gm, float dt) {

        if (target == null) target = gm.getObject(targetTag);
        if (target == null) return;

        float targetX = (target.getPosX() + target.getWidth() / 2f) - gc.getWidth() / 2f;
        float targetY = (target.getPosY() + target.getHeight() / 2f) - gc.getHeight() / 2f;

        offX -= dt * (offX - targetX) * 10;
        offY -= dt * (offY - targetY) * 10;

        if (offX < 0) offX = 0;
        if (offY < 0) offY = 0;
        if (offX + gc.getWidth() > world.getWidth() * tileSize)
            offX = world.getWidth() * tileSize - gc.getWidth();
        if (offY + gc.getHeight() > world.getHeight() * tileSize)
            offY = world.getHeight() * tileSize - gc.getHeight();
    }

    public void render(Renderer r) {
        r.setCoorCam((int) offX, (int) offY);
    }
}
