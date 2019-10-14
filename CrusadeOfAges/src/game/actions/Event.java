package game.actions;

import engine.GameContainer;
import engine.World;
import game.GameManager;
import game.objects.Player;

import java.awt.event.KeyEvent;

public class Event {

    private Player pl;
    private World world;

    public Event(Player pl, World world) {
        this.pl = pl;
        this.world = world;
    }

    public void impale(GameContainer gc) {
        String tag = world.getBlocMap(pl.getTileX(), pl.getTileY()).getTag();

        if (tag.contains("ground"))
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getBloc("ground_spikes_blooded").getCode());
        else if (tag.contains("ceiling"))
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getBloc("ceiling_spikes_blooded").getCode());

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
        if (pl.getKeys() >= 1 && gc.getInputHandler().isKeyDown(KeyEvent.VK_ENTER)) {
            if (gc.getPlayerStats().getValueOf("Level up") <= GameManager.current) {
                gc.getPlayerStats().upValueOf("Level up");
            }
            pl.setKeys(pl.getKeys() - 1);
            if (GameManager.current < GameManager.levels.length - 1) GameManager.current++;
            else GameManager.current = 0;
            gm.load(GameManager.levels[GameManager.current][0]);
            respawn(world.getSpawnX(), world.getSpawnY());
        }
    }

    public void actionLever(GameContainer gc, String tag) {
        if (tag.contains("left")) {
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getBloc("lever_right").getCode());
            gc.getPlayerStats().upValueOf("Lever_pulled");
            gc.getLeverSound().play();
        }
    }
}
