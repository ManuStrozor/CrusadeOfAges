package game.actions;

import engine.World;
import engine.audio.SoundClip;
import game.objects.NetworkPlayer;
import game.objects.Player;

public class Move {

    private Player pl;
    private NetworkPlayer nwPl;
    private World world;
    private SoundClip jump;

    public Move(Player pl, World world) {
        this.pl = pl;
        nwPl = null;
        this.world = world;
        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
    }

    public Move(NetworkPlayer nwPl, World world) {
        pl = null;
        this.nwPl = nwPl;
        this.world = world;
        jump = new SoundClip("/audio/jump.wav");
        jump.setVolume(-10f);
    }

    public void toLeft(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(2);
            if (world.getBlocMap(pl.getTileX() - 1, pl.getTileY()).isSolid() ||
                    world.getBlocMap(pl.getTileX() - 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
                pl.setOffX(pl.getOffX() - dt * speed);
                if (pl.getOffX() < -pl.getPadding()) pl.setOffX(-pl.getPadding());
            } else {
                pl.setOffX(pl.getOffX() - dt * speed);
            }
        } else {
            nwPl.setDirection(2);
            if (world.getBlocMap(nwPl.getTileX() - 1, nwPl.getTileY()).isSolid() ||
                    world.getBlocMap(nwPl.getTileX() - 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY())).isSolid()) {
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
            if (world.getBlocMap(pl.getTileX() + 1, pl.getTileY()).isSolid() ||
                    world.getBlocMap(pl.getTileX() + 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
                pl.setOffX(pl.getOffX() + dt * speed);
                if (pl.getOffX() > pl.getPadding()) pl.setOffX(pl.getPadding());
            } else {
                pl.setOffX(pl.getOffX() + dt * speed);
            }
        } else {
            nwPl.setDirection(1);
            if (world.getBlocMap(nwPl.getTileX() + 1, nwPl.getTileY()).isSolid() ||
                    world.getBlocMap(nwPl.getTileX() + 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY())).isSolid()) {
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
            if (!world.getBlocMap(pl.getTileX(), pl.getTileY() - 1).isSolid() &&
                    !world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid())
                jump.play();
            pl.setGround(pl.getGround() + 1);
        } else {
            nwPl.setDirection(3);
            nwPl.setFallDist(-power);
            if (!world.getBlocMap(nwPl.getTileX(), nwPl.getTileY() - 1).isSolid() &&
                    !world.getBlocMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() - 1).isSolid())
                jump.play();
            nwPl.setGround(nwPl.getGround() + 1);
        }
    }

    public void upLadder(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(3);
            if (world.getBlocMap(pl.getTileX(), pl.getTileY() - 1).isSolid() ||
                    world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid()) {
                pl.setOffY(pl.getOffY() - dt * speed);
                if (pl.getOffY() < 0) pl.setOffY(0);
            } else {
                pl.setOffY(pl.getOffY() - dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (world.getBlocMap(nwPl.getTileX(), nwPl.getTileY() - 1).isSolid() ||
                    world.getBlocMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() - 1).isSolid()) {
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
            if (world.getBlocMap(pl.getTileX(), pl.getTileY() + 1).isSolid() ||
                    world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() + 1).isSolid()) {
                pl.setOffY(pl.getOffY() + dt * speed);
                if (pl.getOffY() > 0) pl.setOffY(0);
            } else {
                pl.setOffY(pl.getOffY() + dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (world.getBlocMap(nwPl.getTileX(), nwPl.getTileY() + 1).isSolid() ||
                    world.getBlocMap(nwPl.getTileX() + (int) Math.signum((int) Math.abs(nwPl.getOffX()) > nwPl.getPadding() ? nwPl.getOffX() : 0), nwPl.getTileY() + 1).isSolid()) {
                nwPl.setOffY(nwPl.getOffY() + dt * speed);
                if (nwPl.getOffY() > 0) nwPl.setOffY(0);
            } else {
                nwPl.setOffY(nwPl.getOffY() + dt * speed);
            }
        }

    }
}