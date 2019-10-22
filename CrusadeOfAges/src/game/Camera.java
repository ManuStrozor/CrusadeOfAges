package game;

import engine.GameContainer;
import engine.Renderer;
import engine.TileMap;
import game.objects.GameObject;

public class Camera {

    private float offX, offY;
    private TileMap tileMap;
    private String targetTag;
    private GameObject target = null;

    private int tileSize = GameManager.TS;

    Camera(String tag, TileMap tileMap) {
        this.targetTag = tag;
        this.tileMap = tileMap;
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
        if (offX + gc.getWidth() > tileMap.getWidth() * tileSize)
            offX = tileMap.getWidth() * tileSize - gc.getWidth();
        if (offY + gc.getHeight() > tileMap.getHeight() * tileSize)
            offY = tileMap.getHeight() * tileSize - gc.getHeight();
    }

    public void render(Renderer r) {
        r.setCoorCam((int) offX, (int) offY);
    }
}
