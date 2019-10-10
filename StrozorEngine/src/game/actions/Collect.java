package game.actions;

import engine.GameContainer;
import engine.World;
import engine.audio.SoundClip;
import game.objects.Player;

public class Collect {

    private Player pl;
    private World world;
    private SoundClip coin;
    private SoundClip bonus;

    public Collect(Player pl, World world) {
        this.pl = pl;
        this.world = world;
        coin = new SoundClip("/audio/getcoin.wav");
        bonus = new SoundClip("/audio/getlife.wav");
        coin.setVolume(-20f);
        bonus.setVolume(-15f);
    }

    public void coin(GameContainer gc) {
        world.clean(pl.getTileX(), pl.getTileY());
        pl.setCoins(pl.getCoins() + 1);
        coin.play();
    }

    public void key(GameContainer gc) {
        world.clean(pl.getTileX(), pl.getTileY());
        pl.setKeys(pl.getKeys() + 1);
        bonus.play();
    }

    public void skull(GameContainer gc) {
        world.clean(pl.getTileX(), pl.getTileY());
        pl.setSkulls(pl.getSkulls() + 1);
        bonus.play();
    }

    public void pill(GameContainer gc) {
        world.clean(pl.getTileX(), pl.getTileY());
        pl.setLives(pl.getLives() + 1);
        bonus.play();
    }
}
