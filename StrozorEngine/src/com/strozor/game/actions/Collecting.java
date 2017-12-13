package com.strozor.game.actions;

import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.game.Player;

public class Collecting {

    private Player pl;
    private SoundClip coin = new SoundClip("/audio/getcoin.wav");
    private SoundClip bonus = new SoundClip("/audio/getlife.wav");

    public Collecting(Player player) {
        pl = player;
        coin.setVolume(-20f);
        bonus.setVolume(-15f);
    }

    public void coin(Bloc bloc) {
        bloc.remove();
        pl.setCoins(pl.getCoins() + 1);
        coin.play();
    }

    public void key(Bloc bloc) {
        bloc.remove();
        pl.setKeys(pl.getKeys() + 1);
        bonus.play();
    }

    public void skull(Bloc bloc) {
        bloc.remove();
        pl.setSkulls(pl.getSkulls() + 1);
        bonus.play();
    }

    public void pill(Bloc bloc) {
        bloc.remove();
        pl.setLives(pl.getLives() + 1);
        bonus.play();
    }
}
