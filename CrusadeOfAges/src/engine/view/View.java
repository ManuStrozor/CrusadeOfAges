package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;
import engine.gfx.Checkbox;
import engine.gfx.Image;
import game.Game;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class View {

    protected ArrayList<Button> buttons = new ArrayList<>();
    protected ArrayList<Checkbox> checkboxes = new ArrayList<>();

    public abstract void update(GameContainer gc, float dt);

    public abstract void render(GameContainer gc, Renderer r);

    boolean fileSelected(GameContainer gc, int index, int scroll) {

        int highs = Game.TS + 10 - scroll + index * (Image.THUMBH + 10);
        int currHigh = Image.THUMBH;

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2 * Game.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    boolean levelSelected(GameContainer gc, int index, int scroll) {

        int highs = Game.TS + 10 - scroll;
        for (int i = 0; i < index; i++) {
            highs += 30 + 10;
        }
        int currHigh = 30;

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2 * Game.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }
}