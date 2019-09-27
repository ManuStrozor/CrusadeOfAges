package sm.engine;

import sm.engine.gfx.Button;
import sm.engine.gfx.Image;
import sm.game.GameManager;
import sm.engine.view.InputDialog;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class View {

    protected ArrayList<Button> buttons = new ArrayList<>();

    public abstract void update(GameContainer gc, float dt);
    public abstract void render(GameContainer gc, GameRender r);

    protected boolean isSelected(GameContainer gc, Button b) {
        return isHover(gc, b) && gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected boolean isHover(GameContainer gc, Button b) {
        return gc.getInput().getMouseX() > b.getOffX() &&
                gc.getInput().getMouseX() < b.getOffX() + b.getWidth() &&
                gc.getInput().getMouseY() > b.getOffY() &&
                gc.getInput().getMouseY() < b.getOffY() + b.getHeight();
    }

    protected boolean fileSelected(GameContainer gc, ArrayList<Image> images, int index, int scroll) {

        int highs = GameManager.TS+10-scroll;
        for(int i = 0; i < index; i++) {
            highs += images.get(i).getH() < 30 ? 30+10 : images.get(i).getH()+10;
        }
        int currHigh = images.get(index).getH() < 30 ? 30 : images.get(index).getH();

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2*GameManager.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected boolean levelSelected(GameContainer gc, int index, int scroll) {

        int highs = GameManager.TS+10-scroll;
        for(int i = 0; i < index; i++) {
            highs += 30+10;
        }
        int currHigh = 30;

        return gc.getInput().getMouseY() > highs &&
                gc.getInput().getMouseY() < highs + currHigh &&
                gc.getInput().getMouseY() < gc.getHeight() - 2*GameManager.TS &&
                gc.getInput().isButtonUp(MouseEvent.BUTTON1);
    }

    protected void inputCtrl(GameContainer gc) {

        String tmp = "";

        if(gc.getInput().isKey(KeyEvent.VK_SHIFT)) {
            //Majuscules
            for(int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if(gc.getInput().isKeyDown(k)) tmp += (char) ('A' + i);
            }
            //Chiffres
            for(int k = KeyEvent.VK_0, i = 0; k <= KeyEvent.VK_9; k++, i++) {
                if(gc.getInput().isKeyDown(k)) tmp += (char) ('0' + i);
            }
            //Point
            if(gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) tmp += '.';
        } else {
            //Minuscules
            for(int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if(gc.getInput().isKeyDown(k)) tmp += (char) ('a' + i);
            }
            //Charactères spéciaux
            if(gc.getInput().isKeyDown(KeyEvent.VK_0)) {
                if(gc.getInput().isKey(KeyEvent.VK_ALT)) tmp += '@';
                else tmp += 'à';
            }
            if(gc.getInput().isKeyDown(KeyEvent.VK_5)) {
                if(gc.getInput().isKey(KeyEvent.VK_ALT)) tmp += '[';
                else tmp += '(';
            }
            if(gc.getInput().isKeyDown(522)) {
                if(gc.getInput().isKey(KeyEvent.VK_ALT)) tmp += ']';
                else tmp += ')';
            }
            if(gc.getInput().isKeyDown(KeyEvent.VK_2)) tmp += 'é';
            if(gc.getInput().isKeyDown(KeyEvent.VK_4)) tmp += '\'';
            if(gc.getInput().isKeyDown(KeyEvent.VK_6)) tmp += '-';
            if(gc.getInput().isKeyDown(KeyEvent.VK_7)) tmp += 'è';
            if(gc.getInput().isKeyDown(KeyEvent.VK_8)) tmp += '_';
            if(gc.getInput().isKeyDown(KeyEvent.VK_9)) tmp += 'ç';
            if(gc.getInput().isKeyDown(KeyEvent.VK_COMMA)) tmp += ',';
            if(gc.getInput().isKeyDown(KeyEvent.VK_EQUALS)) tmp += '=';
            if(gc.getInput().isKeyDown(KeyEvent.VK_SEMICOLON)) tmp += ';';
        }
        //Chiffres (NUMPAD)
        for(int k = KeyEvent.VK_NUMPAD0, i = 0; k <= KeyEvent.VK_NUMPAD9; k++, i++) {
            if(gc.getInput().isKeyDown(k)) tmp += (char) ('0' + i);
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) tmp += ' ';
        if(gc.getInput().isKeyDown(KeyEvent.VK_BACK_SPACE) && InputDialog.blink != 0) {
            InputDialog.input = InputDialog.input.substring(0, InputDialog.blink - 1) + InputDialog.input.substring(InputDialog.blink);
            InputDialog.blink--;
        }
        if(gc.getInput().isKeyDown(KeyEvent.VK_DELETE) && InputDialog.blink < InputDialog.input.length())
            InputDialog.input = InputDialog.input.substring(0, InputDialog.blink) + InputDialog.input.substring(InputDialog.blink + 1);

        if(InputDialog.blink == 0) InputDialog.input = tmp + InputDialog.input;
        else if(InputDialog.blink == InputDialog.input.length()) InputDialog.input += tmp;
        else InputDialog.input = InputDialog.input.substring(0, InputDialog.blink) + tmp + InputDialog.input.substring(InputDialog.blink);
        InputDialog.blink += tmp.length();
    }
}