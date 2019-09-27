package sm.game.actions;

import sm.engine.GameContainer;
import sm.engine.GameMap;
import sm.engine.audio.SoundClip;
import sm.game.GameManager;
import sm.game.Player;

import java.awt.event.KeyEvent;

public class Event {

    private Player pl;
    private GameMap map;
    private SoundClip impaled = new SoundClip("/audio/impaled.wav");
    private SoundClip leverActioned = new SoundClip("/audio/lever.wav");

    public Event(Player pl, GameMap map) {
        this.pl = pl;
        this.map = map;
        leverActioned.setVolume(-10f);
    }

    public void impale() {
        String tag = map.getTag(pl.getTileX(), pl.getTileY());

        if(tag.contains("ground"))
            map.setBloc(pl.getTileX(), pl.getTileY(), map.getCol("ground spikes blooded"));
        else if(tag.contains("ceiling"))
            map.setBloc(pl.getTileX(), pl.getTileY(), map.getCol("ceiling spikes blooded"));

        impaled.play();
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
        if(pl.getKeys() >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            if(gc.getData().getValueOf("Level up") <= GameManager.current) {
                gc.getData().upValueOf("Level up");
            }
            pl.setKeys(pl.getKeys() - 1);
            if(GameManager.current < GameManager.levels.length - 1) GameManager.current++;
            else GameManager.current = 0;
            gm.load(GameManager.levels[GameManager.current][0]);
            respawn(map.getSpawnX(), map.getSpawnY());
        }
    }

    public void actionLever(GameContainer gc, String tag) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ENTER) && tag.contains("left")) {
            map.setBloc(pl.getTileX(), pl.getTileY(), map.getCol("lever right"));
            gc.getData().upValueOf("Lever pulled");
            leverActioned.play();
        }
    }
}
