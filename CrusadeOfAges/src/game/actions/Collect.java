package game.actions;

import engine.Level;
import engine.World;
import engine.audio.SoundClip;
import game.entity.Player;

public class Collect {

    private Player pl;
    private Level level;
    private SoundClip coin, key, skull, pill;

    public Collect(Player pl, World world) {
        this.pl = pl;
        level = world.getLevel();
        pill = new SoundClip("/audio/swallow.wav");
        key = new SoundClip("/audio/key.wav", -10f);
        coin = new SoundClip("/audio/coin.wav");
        skull = new SoundClip("/audio/getlife.wav", -10f);
    }

    public void coin() {
        level.clean(pl.getTileX(), pl.getTileY());
        pl.setCoins(pl.getCoins() + 1);
        coin.play();
    }

    public void key() {
        level.clean(pl.getTileX(), pl.getTileY());
        pl.setKeys(pl.getKeys() + 1);
        key.play();
    }

    public void skull() {
        level.clean(pl.getTileX(), pl.getTileY());
        pl.setSkulls(pl.getSkulls() + 1);
        skull.play();
    }

    public void pill() {
        level.clean(pl.getTileX(), pl.getTileY());
        pl.setLives(pl.getLives() + 1);
        pill.play();
    }
}
