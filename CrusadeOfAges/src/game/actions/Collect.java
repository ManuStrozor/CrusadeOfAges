package game.actions;

import engine.World;
import engine.audio.SoundClip;
import game.entity.Player;

public class Collect {

    private Player pl;
    private World world;
    private SoundClip coin;
    private SoundClip bonus;

    public Collect(Player pl, World world) {
        this.pl = pl;
        this.world = world;

        coin = new SoundClip("/audio/getcoin.wav", -20f);
        bonus = new SoundClip("/audio/getlife.wav", -15f);
    }

    public void coin() {
        world.getLevel().clean(pl.getTileX(), pl.getTileY());
        pl.setCoins(pl.getCoins() + 1);
        coin.play();
    }

    public void key() {
        world.getLevel().clean(pl.getTileX(), pl.getTileY());
        pl.setKeys(pl.getKeys() + 1);
        bonus.play();
    }

    public void skull() {
        world.getLevel().clean(pl.getTileX(), pl.getTileY());
        pl.setSkulls(pl.getSkulls() + 1);
        bonus.play();
    }

    public void pill() {
        world.getLevel().clean(pl.getTileX(), pl.getTileY());
        pl.setLives(pl.getLives() + 1);
        bonus.play();
    }
}
