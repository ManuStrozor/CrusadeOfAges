package game;

import engine.GameContainer;
import engine.Renderer;
import engine.World;

public abstract class AbstractGame {

    protected World world;

    AbstractGame(World world) {
        this.world = world;
    }

    public abstract void update(GameContainer gc, float dt);

    public abstract void render(GameContainer gc, Renderer r);
}
