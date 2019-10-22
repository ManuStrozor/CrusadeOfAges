package game.actions;

import engine.GameContainer;
import engine.TileMap;
import game.GameManager;
import game.objects.Player;

import java.awt.event.KeyEvent;

public class Event {

    private Player pl;
    private TileMap tileMap;

    public Event(Player pl, TileMap tileMap) {
        this.pl = pl;
        this.tileMap = tileMap;
    }

    public void impale(GameContainer gc) {
        String tag = tileMap.getTileFromMap(pl.getTileX(), pl.getTileY()).getTag();

        if (tag.contains("ground"))
            tileMap.setTile(pl.getTileX(), pl.getTileY(), tileMap.getTile("ground_spikes_blooded").getCode());
        else if (tag.contains("ceiling"))
            tileMap.setTile(pl.getTileX(), pl.getTileY(), tileMap.getTile("ceiling_spikes_blooded").getCode());

        gc.getImpaleSound().play();
        pl.setLives(pl.getLives() - 1);
    }

    public void respawn(int x, int y) {
        pl.setDirection(0);
        pl.setFallDist(0);
        pl.setTileX(x);
        pl.setTileY(y);
        pl.setOffX(0);
        pl.setOffY(0);
    }

    public void switchLevel(GameContainer gc, GameManager gm) {
        if (pl.getKeys() >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            if (gc.getPlayerStats().getValueOf("Level up") <= GameManager.current) {
                gc.getPlayerStats().upValueOf("Level up");
            }
            pl.setKeys(pl.getKeys() - 1);
            if (GameManager.current < GameManager.levels.length - 1) GameManager.current++;
            else GameManager.current = 0;
            //gm.load(GameManager.levels[GameManager.current][0]);
            respawn(tileMap.getSpawnX(), tileMap.getSpawnY());
        }
    }

    public void actionLever(GameContainer gc, String tag) {
        if (tag.contains("left")) {
            tileMap.setTile(pl.getTileX(), pl.getTileY(), tileMap.getTile("lever_right").getCode());
            gc.getPlayerStats().upValueOf("Lever_pulled");
            gc.getLeverSound().play();
        }
    }
}
