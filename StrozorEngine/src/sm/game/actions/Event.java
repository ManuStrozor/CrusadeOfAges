package sm.game.actions;

import sm.engine.GameContainer;
import sm.engine.World;
import sm.engine.audio.SoundClip;
import sm.game.GameManager;
import sm.game.objects.Player;

import java.awt.event.KeyEvent;

public class Event {

    private Player pl;
    private World world;
    private SoundClip impaled = new SoundClip("/audio/impaled.wav");
    private SoundClip leverActioned = new SoundClip("/audio/lever.wav");

    public Event(Player pl, World world) {
        this.pl = pl;
        this.world = world;
        leverActioned.setVolume(-10f);
    }

    public void impale() {
        String tag = world.getTag(pl.getTileX(), pl.getTileY());

        if(tag.contains("ground"))
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getCol("ground spikes blooded"));
        else if(tag.contains("ceiling"))
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getCol("ceiling spikes blooded"));

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
        if(pl.getKeys() >= 1 && gc.getInputHandler().isKeyDown(KeyEvent.VK_ENTER)) {
            if(gc.getPlayerStats().getValueOf("Level up") <= GameManager.current) {
                gc.getPlayerStats().upValueOf("Level up");
            }
            pl.setKeys(pl.getKeys() - 1);
            if(GameManager.current < GameManager.levels.length - 1) GameManager.current++;
            else GameManager.current = 0;
            gm.load(GameManager.levels[GameManager.current][0]);
            respawn(world.getSpawnX(), world.getSpawnY());
        }
    }

    public void actionLever(GameContainer gc, String tag) {
        if(gc.getInputHandler().isKeyDown(KeyEvent.VK_ENTER) && tag.contains("left")) {
            world.setBloc(pl.getTileX(), pl.getTileY(), world.getCol("lever right"));
            gc.getPlayerStats().upValueOf("Lever pulled");
            leverActioned.play();
        }
    }
}
