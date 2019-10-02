package sm.game;

import sm.engine.GameContainer;
import sm.engine.Renderer;

public abstract class AbstractGame {

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, Renderer r);
}
