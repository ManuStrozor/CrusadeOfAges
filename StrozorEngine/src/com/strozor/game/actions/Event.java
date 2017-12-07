package com.strozor.game.actions;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameMap;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.game.GameManager;
import com.strozor.game.Player;

import java.awt.event.KeyEvent;

public class Event {

    private Player pl;
    private SoundClip impaled = new SoundClip("/audio/impaled.wav");
    private SoundClip checkPoint = new SoundClip("/audio/checkpoint.wav");

    public Event(Player player) {
        pl = player;
        checkPoint.setVolume(-15f);
    }

    public void impale(GameMap map) {
        Bloc b = map.getBloc(pl.getTileX(), pl.getTileY());

        if(b.getName().equals("Ground spikes"))
            map.setBloc(pl.getTileX(), pl.getTileY(), 9);
        else if(b.getName().equals("Ceiling spikes"))
            map.setBloc(pl.getTileX(), pl.getTileY(), 10);

        impaled.play();
        pl.setLives(pl.getLives() - 1);
    }

    public void savePosition(GameMap map) {
        if(pl.getTileX() != map.getSpawnX() && pl.getTileY() != map.getSpawnY()) {
            map.setSpawnX(pl.getTileX());
            map.setSpawnY(pl.getTileY());
            checkPoint.play();
        }
    }

    public void switchLevel(GameContainer gc, GameManager gm) {
        if(pl.getKeys() >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            pl.setKeys(pl.getKeys() - 1);
            pl.respawn(gm, true);
        }
    }
}
