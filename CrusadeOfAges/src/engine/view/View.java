package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Button;
import engine.gfx.Checkbox;
import engine.gfx.Image;
import game.GameManager;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class View {

    protected ArrayList<Button> buttons = new ArrayList<>();
    protected ArrayList<Checkbox> checkboxes = new ArrayList<>();

    public abstract void update(GameContainer gc, float dt);

    public abstract void render(GameContainer gc, Renderer r);

    protected boolean isSelected(GameContainer gc, Button b) {
        return isHover(gc, b) && gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected boolean isSelected(GameContainer gc, Checkbox c) {
        return isHover(gc, c) && gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected boolean isHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX() &&
                gc.getInput().getMouseX() < b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY() &&
                gc.getInput().getMouseY() < b.getOffY() + b.getHeight();
    }

    protected boolean isHover(GameContainer gc, Checkbox c) {
        return gc.getInput().getMouseX() > c.getOffX() &&
                gc.getInput().getMouseX() < c.getOffX() + c.getWidth() &&
                gc.getInput().getMouseY() > c.getOffY() &&
                gc.getInput().getMouseY() < c.getOffY() + c.getHeight();
    }

    protected boolean fileSelected(GameContainer gc, int index, int scroll) {

        int highs = GameManager.TS + 10 - scroll + index * (Image.THUMBH + 10);
        int currHigh = Image.THUMBH;

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2 * GameManager.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected boolean levelSelected(GameContainer gc, int index, int scroll) {

        int highs = GameManager.TS + 10 - scroll;
        for (int i = 0; i < index; i++) {
            highs += 30 + 10;
        }
        int currHigh = 30;

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2 * GameManager.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected void inputCtrl(GameContainer gc) {

        StringBuilder tmp = new StringBuilder();

        if (gc.getInput().isKey(KeyEvent.VK_SHIFT)) {
            //Majuscules
            for (int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if (gc.getInput().isKeyDown(k)) tmp.append((char) ('A' + i));
            }
            //Chiffres
            for (int k = KeyEvent.VK_0, i = 0; k <= KeyEvent.VK_9; k++, i++) {
                if (gc.getInput().isKeyDown(k)) tmp.append((char) ('0' + i));
            }
            //Point
            if (gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) tmp.append('.');
        } else {
            //Minuscules
            for (int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if (gc.getInput().isKeyDown(k)) tmp.append((char) ('a' + i));
            }
            //Charactères spéciaux
            if (gc.getInput().isKeyDown(KeyEvent.VK_0)) {
                if (gc.getInput().isKey(KeyEvent.VK_ALT)) tmp.append('@');
                else tmp.append('à');
            }
            if (gc.getInput().isKeyDown(KeyEvent.VK_5)) {
                if (gc.getInput().isKey(KeyEvent.VK_ALT)) tmp.append('[');
                else tmp.append('(');
            }
            if (gc.getInput().isKeyDown(522)) {
                if (gc.getInput().isKey(KeyEvent.VK_ALT)) tmp.append(']');
                else tmp.append(')');
            }
            if (gc.getInput().isKeyDown(KeyEvent.VK_2)) tmp.append('é');
            if (gc.getInput().isKeyDown(KeyEvent.VK_4)) tmp.append('\'');
            if (gc.getInput().isKeyDown(KeyEvent.VK_6)) tmp.append('-');
            if (gc.getInput().isKeyDown(KeyEvent.VK_7)) tmp.append('è');
            if (gc.getInput().isKeyDown(KeyEvent.VK_8)) tmp.append('_');
            if (gc.getInput().isKeyDown(KeyEvent.VK_9)) tmp.append('ç');
            if (gc.getInput().isKeyDown(KeyEvent.VK_COMMA)) tmp.append(',');
            if (gc.getInput().isKeyDown(KeyEvent.VK_EQUALS)) tmp.append('=');
            if (gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) tmp.append(';');
        }
        //Chiffres (NUMPAD)
        for (int k = KeyEvent.VK_NUMPAD0, i = 0; k <= KeyEvent.VK_NUMPAD9; k++, i++) {
            if (gc.getInput().isKeyDown(k)) tmp.append((char) ('0' + i));
        }

        if (gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) tmp.append(' ');
        if (gc.getInput().isKeyDown(KeyEvent.VK_BACK_SPACE) && InputDialog.blink != 0) {
            InputDialog.input = InputDialog.input.substring(0, InputDialog.blink - 1) + InputDialog.input.substring(InputDialog.blink);
            InputDialog.blink--;
        }
        if (gc.getInput().isKeyDown(KeyEvent.VK_DELETE) && InputDialog.blink < InputDialog.input.length())
            InputDialog.input = InputDialog.input.substring(0, InputDialog.blink) + InputDialog.input.substring(InputDialog.blink + 1);

        if (InputDialog.blink == 0) InputDialog.input = tmp + InputDialog.input;
        else if (InputDialog.blink == InputDialog.input.length()) InputDialog.input += tmp;
        else
            InputDialog.input = InputDialog.input.substring(0, InputDialog.blink) + tmp + InputDialog.input.substring(InputDialog.blink);
        InputDialog.blink += tmp.length();
    }
}