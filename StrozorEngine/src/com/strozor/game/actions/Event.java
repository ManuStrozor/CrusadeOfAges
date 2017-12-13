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
    private SoundClip leverActioned = new SoundClip("/audio/lever.wav");

    public Event(Player player) {
        pl = player;
        leverActioned.setVolume(-10f);
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

    public boolean switchLevel(GameContainer gc, GameManager gm, GameMap map) {
        if(pl.getKeys() >= 1 && gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            pl.setKeys(pl.getKeys() - 1);
            if(gm.getCurrLevel() + 1 < gm.getLevelList().length) {
                gm.load(gm.getLevelList()[gm.getCurrLevel() + 1]);
                gm.setCurrLevel(gm.getCurrLevel() + 1);
            } else {
                gm.load(gm.getLevelList()[0]);
                gm.setCurrLevel(0);
            }
            pl.respawn(map.getSpawnX(), map.getSpawnY());
            return true;
        } else {
            return false;
        }
    }

    public boolean actionLever(GameContainer gc, GameMap map) {
        if(gc.getInput().isKeyDown(KeyEvent.VK_ENTER)) {
            Bloc lever = map.getBloc(pl.getTileX(), pl.getTileY());
            if(lever.getTileX() == 1) lever.setTileX(2);
            else lever.setTileX(1);
            leverActioned.play();
            return true;
        } else {
            return false;
        }
    }
}
