package sm.game.actions;

import sm.engine.World;
import sm.engine.audio.SoundClip;
import sm.game.objects.NetworkPlayer;
import sm.game.objects.Player;

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
            if (world.isSolid(pl.getTileX() - 1, pl.getTileY()) || world.isSolid(pl.getTileX() - 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY()))) {
                if (pl.getOffX() > 0) {
                    pl.setOffX(pl.getOffX() - dt * speed);
                    if (pl.getOffX() < 0) pl.setOffX(0);
                } else {
                    pl.setOffX(0);
                }
            } else {
                pl.setOffX(pl.getOffX() - dt * speed);
            }
        } else {
            nwPl.setDirection(2);
            if (world.isSolid(nwPl.getTileX() - 1, nwPl.getTileY()) || world.isSolid(nwPl.getTileX() - 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY()))) {
                if (nwPl.getOffX() > 0) {
                    nwPl.setOffX(nwPl.getOffX() - dt * speed);
                    if (nwPl.getOffX() < 0) nwPl.setOffX(0);
                } else {
                    nwPl.setOffX(0);
                }
            } else {
                nwPl.setOffX(nwPl.getOffX() - dt * speed);
            }
        }
    }

    public void toRight(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(1);
            if (world.isSolid(pl.getTileX() + 1, pl.getTileY()) || world.isSolid(pl.getTileX() + 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY()))) {
                if (pl.getOffX() < 0) {
                    pl.setOffX(pl.getOffX() + dt * speed);
                    if (pl.getOffX() > 0) pl.setOffX(0);
                } else {
                    pl.setOffX(0);
                }
            } else {
                pl.setOffX(pl.getOffX() + dt * speed);
            }
        } else {
            nwPl.setDirection(1);
            if (world.isSolid(nwPl.getTileX() + 1, nwPl.getTileY()) || world.isSolid(nwPl.getTileX() + 1, nwPl.getTileY() + (int) Math.signum((int) nwPl.getOffY()))) {
                if (nwPl.getOffX() < 0) {
                    nwPl.setOffX(nwPl.getOffX() + dt * speed);
                    if (nwPl.getOffX() > 0) nwPl.setOffX(0);
                } else {
                    nwPl.setOffX(0);
                }
            } else {
                nwPl.setOffX(nwPl.getOffX() + dt * speed);
            }
        }
    }

    public void jump(int power) {
        if (pl != null) {
            pl.setDirection(3);
            pl.setFallDist(-power);
            if (!world.isSolid(pl.getTileX(), pl.getTileY()-1) && !world.isSolid(pl.getTileX() + (int)Math.signum((int) pl.getOffX()), pl.getTileY() - 1))
                jump.play();
            pl.setGround(pl.getGround() + 1);
        } else {
            nwPl.setDirection(3);
            nwPl.setFallDist(-power);
            if (!world.isSolid(nwPl.getTileX(), nwPl.getTileY()-1) && !world.isSolid(nwPl.getTileX() + (int)Math.signum((int) nwPl.getOffX()), nwPl.getTileY() - 1))
                jump.play();
                nwPl.setGround(nwPl.getGround() + 1);
        }
    }

    public void upLadder(float dt, float speed) {
        if (pl != null) {
            pl.setDirection(3);
            if (world.isSolid(pl.getTileX(), pl.getTileY()-1) || world.isSolid(pl.getTileX() + (int) Math.signum((int) pl.getOffX()), pl.getTileY()-1)) {
                pl.setOffY(pl.getOffY() - dt * speed);
                if (pl.getOffY() < 0) pl.setOffY(0);
            } else {
                pl.setOffY(pl.getOffY() - dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (world.isSolid(nwPl.getTileX(), nwPl.getTileY()-1) || world.isSolid(nwPl.getTileX() + (int) Math.signum((int) nwPl.getOffX()), nwPl.getTileY()-1)) {
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
            if (world.isSolid(pl.getTileX(), pl.getTileY() + 1) || world.isSolid(pl.getTileX() + (int) Math.signum((int) pl.getOffX()), pl.getTileY() + 1)) {
                if (pl.getOffY() < 0) {
                    pl.setOffY(pl.getOffY() + dt * speed);
                    if (pl.getOffY() > 0) pl.setOffY(0);
                } else {
                    pl.setOffY(0);
                }
            } else {
                pl.setOffY(pl.getOffY() + dt * speed);
            }
        } else {
            nwPl.setDirection(3);
            if (world.isSolid(nwPl.getTileX(), nwPl.getTileY() + 1) || world.isSolid(nwPl.getTileX() + (int) Math.signum((int) nwPl.getOffX()), nwPl.getTileY() + 1)) {
                if (nwPl.getOffY() < 0) {
                    nwPl.setOffY(nwPl.getOffY() + dt * speed);
                    if (nwPl.getOffY() > 0) nwPl.setOffY(0);
                } else {
                    nwPl.setOffY(0);
                }
            } else {
                nwPl.setOffY(nwPl.getOffY() + dt * speed);
            }
        }
            
    }
}
