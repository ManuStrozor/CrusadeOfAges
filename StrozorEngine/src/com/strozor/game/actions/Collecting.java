package com.strozor.game.actions;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameMap;
import com.strozor.engine.audio.SoundClip;
import com.strozor.game.Player;

public class Collecting {

    private Player pl;
    private GameMap map;
    private SoundClip coin = new SoundClip("/audio/getcoin.wav");
    private SoundClip bonus = new SoundClip("/audio/getlife.wav");

    public Collecting(Player pl, GameMap map) {
        this.pl = pl;
        this.map = map;
        coin.setVolume(-20f);
        bonus.setVolume(-15f);
    }

    public void coin(GameContainer gc) {
        map.clean(pl.getTileX(), pl.getTileY());
        pl.setCoins(pl.getCoins() + 1);
        coin.play();
    }

    public void key(GameContainer gc) {
        map.clean(pl.getTileX(), pl.getTileY());
        pl.setKeys(pl.getKeys() + 1);
        bonus.play();
    }

    public void skull(GameContainer gc) {
        map.clean(pl.getTileX(), pl.getTileY());
        pl.setSkulls(pl.getSkulls() + 1);
        bonus.play();
    }

    public void pill(GameContainer gc) {
        map.clean(pl.getTileX(), pl.getTileY());
        pl.setLives(pl.getLives() + 1);
        bonus.play();
    }
}
