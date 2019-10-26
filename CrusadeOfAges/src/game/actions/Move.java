package game.actions;

import engine.World;
import engine.audio.SoundClip;
import game.entity.Player;

public class Move {

    private Player pl;
    private World world;
    private SoundClip jump;

    public Move(Player pl, World world) {
        this.pl = pl;
        this.world = world;

        jump = new SoundClip("/audio/jump.wav", -10f);
    }

    public void toLeft(float dt, float speed) {
        pl.setDirection(2);
        if (world.getBlocMap(pl.getTileX() - 1, pl.getTileY()).isSolid() ||
                world.getBlocMap(pl.getTileX() - 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
            pl.setOffX(pl.getOffX() - dt * speed);
            if (pl.getOffX() < -pl.getPadding()) pl.setOffX(-pl.getPadding());
        } else {
            pl.setOffX(pl.getOffX() - dt * speed);
        }
    }

    public void toRight(float dt, float speed) {
        pl.setDirection(1);
        if (world.getBlocMap(pl.getTileX() + 1, pl.getTileY()).isSolid() ||
                world.getBlocMap(pl.getTileX() + 1, pl.getTileY() + (int) Math.signum((int) pl.getOffY())).isSolid()) {
            pl.setOffX(pl.getOffX() + dt * speed);
            if (pl.getOffX() > pl.getPadding()) pl.setOffX(pl.getPadding());
        } else {
            pl.setOffX(pl.getOffX() + dt * speed);
        }
    }

    public void jump(int power) {
        pl.setDirection(3);
        pl.setFallDist(-power);
        if (!world.getBlocMap(pl.getTileX(), pl.getTileY() - 1).isSolid() &&
                !world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid())
            jump.play();
        pl.setGround(pl.getGround() + 1);
    }

    public void upLadder(float dt, float speed) {
        pl.setDirection(3);
        if (world.getBlocMap(pl.getTileX(), pl.getTileY() - 1).isSolid() ||
                world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() - 1).isSolid()) {
            pl.setOffY(pl.getOffY() - dt * speed);
            if (pl.getOffY() < 0) pl.setOffY(0);
        } else {
            pl.setOffY(pl.getOffY() - dt * speed);
        }
    }

    public void downLadder(float dt, float speed) {
        pl.setDirection(3);
        if (world.getBlocMap(pl.getTileX(), pl.getTileY() + 1).isSolid() ||
                world.getBlocMap(pl.getTileX() + (int) Math.signum((int) Math.abs(pl.getOffX()) > pl.getPadding() ? pl.getOffX() : 0), pl.getTileY() + 1).isSolid()) {
            pl.setOffY(pl.getOffY() + dt * speed);
            if (pl.getOffY() > 0) pl.setOffY(0);
        } else {
            pl.setOffY(pl.getOffY() + dt * speed);
        }
    }
}
