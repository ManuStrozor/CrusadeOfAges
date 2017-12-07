package com.strozor.game.actions;

import com.strozor.engine.GameMap;
import com.strozor.engine.audio.SoundClip;
import com.strozor.game.Player;

public class Move {

    private Player pl;
    private SoundClip jump = new SoundClip("/audio/jump.wav");

    public Move(Player player) {
        pl = player;
        jump.setVolume(-10f);
    }

    public void toLeft(GameMap map, float dt, float speed) {
        pl.setDirection(2);
        if (map.isSolid(pl.getTileX() - 1, pl.getTileY()) || map.isSolid(pl.getTileX() - 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY()))) {
            if (pl.getOffX() > 0) {
                pl.setOffX(pl.getOffX() - dt * speed);
                if (pl.getOffX() < 0) pl.setOffX(0);
            } else {
                pl.setOffX(0);
            }
        } else {
            pl.setOffX(pl.getOffX() - dt * speed);
        }
    }

    public void toRight(GameMap map, float dt, float speed) {
        pl.setDirection(1);
        if (map.isSolid(pl.getTileX() + 1, pl.getTileY()) || map.isSolid(pl.getTileX() + 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY()))) {
            if (pl.getOffX() < 0) {
                pl.setOffX(pl.getOffX() + dt * speed);
                if (pl.getOffX() > 0) pl.setOffX(0);
            } else {
                pl.setOffX(0);
            }
        } else {
            pl.setOffX(pl.getOffX() + dt * speed);
        }
    }

    public void jump(GameMap map, int power) {
        pl.setDirection(3);
        pl.setFallDist(-power);
        if (!map.isSolid(pl.getTileX(), pl.getTileY() - 1) && !map.isSolid(pl.getTileX() + (int)Math.signum((int) pl.getOffX()), pl.getTileY() - 1))
            jump.play();
        pl.setGround(pl.getGround() + 1);
    }

    public void upLadder(GameMap map, float dt, float speed) {
        pl.setDirection(3);
        if (map.isSolid(pl.getTileX(), pl.getTileY() - 1) || map.isSolid(pl.getTileX() + (int) Math.signum((int) pl.getOffX()), pl.getTileY() - 1)) {
            if (pl.getOffY() > 0) {
                pl.setOffY(pl.getOffY() - dt * speed);
                if (pl.getOffY() < 0) pl.setOffY(0);
            } else {
                pl.setOffY(0);
            }
        } else {
            pl.setOffY(pl.getOffY() - dt * speed);
        }
    }

    public void downLadder(GameMap map, float dt, float speed) {
        pl.setDirection(3);
        if (map.isSolid(pl.getTileX(), pl.getTileY() + 1) || map.isSolid(pl.getTileX() + (int) Math.signum((int) pl.getOffX()), pl.getTileY() + 1)) {
            if (pl.getOffY() < 0) {
                pl.setOffY(pl.getOffY() + dt * speed);
                if (pl.getOffY() > 0) pl.setOffY(0);
            } else {
                pl.setOffY(0);
            }
        } else {
            pl.setOffY(pl.getOffY() + dt * speed);
        }
    }
}