package game.actions;

import engine.TileMap;
import engine.audio.SoundClip;
import game.objects.NetworkPlayer;
import game.objects.Player;

public class Move {

    private Player pl;
    private NetworkPlayer nwPl;
    private TileMap tileMap;
    private SoundClip jump;

    public Move(Player pl, TileMap tileMap) {
        this.pl = pl;
        nwPl = null;
        this.tileMap = tileMap;
        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
    }

    public Move(NetworkPlayer nwPl, TileMap tileMap) {
        pl = null;
        this.nwPl = nwPl;
        this.tileMap = tileMap;
        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
    }

    public void toLeft(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(2);
            if (tileMap.getTileFromMap(pl.getTileX() - 1, pl.getTileY()).isSolid() ||
                    tileMap.getTileFromMap(pl.getTileX() - 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
                pl.setOffX(pl.getOffX() - dt * speed);
                if (pl.getOffX() < -pl.getPadding()) pl.setOffX(-pl.getPadding());
            } else {
                pl.setOffX(pl.getOffX() - dt * speed);
            }
        } else {
            nwPl.setDirection(2);
            if (tileMap.getTileFromMap(nwPl.getTileX() - 1, nwPl.getTileY()).isSolid() ||
                    tileMap.getTileFromMap(nwPl.getTileX() - 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY())).isSolid()) {
                nwPl.setOffX(nwPl.getOffX() - dt * speed);
                if (nwPl.getOffX() < -nwPl.getPadding()) nwPl.setOffX(-nwPl.getPadding());
            } else {
                nwPl.setOffX(nwPl.getOffX() - dt * speed);
            }
        }
    }

    public void toRight(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(1);
            if (tileMap.getTileFromMap(pl.getTileX() + 1, pl.getTileY()).isSolid() ||
                    tileMap.getTileFromMap(pl.getTileX() + 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
                pl.setOffX(pl.getOffX() + dt * speed);
                if (pl.getOffX() > pl.getPadding()) pl.setOffX(pl.getPadding());
            } else {
                pl.setOffX(pl.getOffX() + dt * speed);
            }
        } else {
            nwPl.setDirection(1);
            if (tileMap.getTileFromMap(nwPl.getTileX() + 1, nwPl.getTileY()).isSolid() ||
                    tileMap.getTileFromMap(nwPl.getTileX() + 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY())).isSolid()) {
                nwPl.setOffX(nwPl.getOffX() + dt * speed);
                if (nwPl.getOffX() > nwPl.getPadding()) nwPl.setOffX(nwPl.getPadding());
            } else {
                nwPl.setOffX(nwPl.getOffX() + dt * speed);
            }
        }
    }

    public void jump(int power) {
        if (pl != null) {
            pl.setDirection(3);
            pl.setFallDist(-power);
            if (!tileMap.getTileFromMap(pl.getTileX(), pl.getTileY() - 1).isSolid() &&
                    !tileMap.getTileFromMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid())
                jump.play();
            pl.setGround(pl.getGround() + 1);
        } else {
            nwPl.setDirection(3);
            nwPl.setFallDist(-power);
            if (!tileMap.getTileFromMap(nwPl.getTileX(), nwPl.getTileY() - 1).isSolid() &&
                    !tileMap.getTileFromMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() - 1).isSolid())
                jump.play();
            nwPl.setGround(nwPl.getGround() + 1);
        }
    }

    public void upLadder(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(3);
            if (tileMap.getTileFromMap(pl.getTileX(), pl.getTileY() - 1).isSolid() ||
                    tileMap.getTileFromMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid()) {
                pl.setOffY(pl.getOffY() - dt * speed);
                if (pl.getOffY() < 0) pl.setOffY(0);
            } else {
                pl.setOffY(pl.getOffY() - dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (tileMap.getTileFromMap(nwPl.getTileX(), nwPl.getTileY() - 1).isSolid() ||
                    tileMap.getTileFromMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() - 1).isSolid()) {
                nwPl.setOffY(nwPl.getOffY() - dt * speed);
                if (nwPl.getOffY() < 0) nwPl.setOffY(0);
            } else {
                nwPl.setOffY(nwPl.getOffY() - dt * speed);
            }
        }
    }

    public void downLadder(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(3);
            if (tileMap.getTileFromMap(pl.getTileX(), pl.getTileY() + 1).isSolid() ||
                    tileMap.getTileFromMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() + 1).isSolid()) {
                pl.setOffY(pl.getOffY() + dt * speed);
                if (pl.getOffY() > 0) pl.setOffY(0);
            } else {
                pl.setOffY(pl.getOffY() + dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (tileMap.getTileFromMap(nwPl.getTileX(), nwPl.getTileY() + 1).isSolid() ||
                    tileMap.getTileFromMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() + 1).isSolid()) {
                nwPl.setOffY(nwPl.getOffY() + dt * speed);
                if (nwPl.getOffY() > 0) nwPl.setOffY(0);
            } else {
                nwPl.setOffY(nwPl.getOffY() + dt * speed);
            }
        }

    }
}
