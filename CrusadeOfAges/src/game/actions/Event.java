package game.actions;

import engine.GameContainer;
import engine.Level;
import engine.World;
import game.entity.Player;

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

    public void switchLevel(GameContainer gc) {
        Level level = gc.getGame().getLevel();
        if (pl.getKeys() >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            if (gc.getPlayerStats().getValueOf("Level up") <= level.getCurrLevel()) {
                gc.getPlayerStats().upValueOf("Level up");
            }
            pl.setKeys(pl.getKeys() - 1);
            if (level.getCurrLevel() < level.getLvls().length - 1) {
                level.setCurrLevel(level.getCurrLevel() + 1);
            } else {
                level.setCurrLevel(0);
            }
            level.load();
            respawn(level.getSpawnX(), level.getSpawnY());
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
