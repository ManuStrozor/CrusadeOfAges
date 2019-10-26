package engine.gfx;

import engine.InputHandler;

import java.awt.event.KeyEvent;

public class TextInput {

    private String text;
    private int blinkBarPos;

    public TextInput(String text, int blinkBarPos) {
        this.text = text;
        this.blinkBarPos = blinkBarPos;
    }

    public void update(InputHandler input) {
        //Blinking bar control
        if (input.isKeyDown(KeyEvent.VK_LEFT) && blinkBarPos > 0)
            blinkBarPos--;
        if (input.isKeyDown(KeyEvent.VK_RIGHT) && blinkBarPos < text.length())
            blinkBarPos++;
        if (input.isKeyDown(KeyEvent.VK_END))
            blinkBarPos = text.length();
        if (input.isKeyDown(KeyEvent.VK_HOME))
            blinkBarPos = 0;

        StringBuilder tmp = new StringBuilder();

        if (input.isKey(KeyEvent.VK_SHIFT)) {
            //Majuscules
            for (int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if (input.isKeyDown(k)) tmp.append((char) ('A' + i));
            }
            //Chiffres
            for (int k = KeyEvent.VK_0, i = 0; k <= KeyEvent.VK_9; k++, i++) {
                if (input.isKeyDown(k)) tmp.append((char) ('0' + i));
            }
            //Point
            if (input.isKeyDown(KeyEvent.VK_SEMICOLON)) tmp.append('.');
        } else {
            //Minuscules
            for (int k = KeyEvent.VK_A, i = 0; k <= KeyEvent.VK_Z; k++, i++) {
                if (input.isKeyDown(k)) tmp.append((char) ('a' + i));
            }
            //Charactères spéciaux
            if (input.isKeyDown(KeyEvent.VK_0)) {
                if (input.isKey(KeyEvent.VK_ALT)) tmp.append('@');
                else tmp.append('à');
            }
            if (input.isKeyDown(KeyEvent.VK_5)) {
                if (input.isKey(KeyEvent.VK_ALT)) tmp.append('[');
                else tmp.append('(');
            }
            if (input.isKeyDown(522)) {
                if (input.isKey(KeyEvent.VK_ALT)) tmp.append(']');
                else tmp.append(')');
            }
            if (input.isKeyDown(KeyEvent.VK_2)) tmp.append('é');
            if (input.isKeyDown(KeyEvent.VK_4)) tmp.append('\'');
            if (input.isKeyDown(KeyEvent.VK_6)) tmp.append('-');
            if (input.isKeyDown(KeyEvent.VK_7)) tmp.append('è');
            if (input.isKeyDown(KeyEvent.VK_8)) tmp.append('_');
            if (input.isKeyDown(KeyEvent.VK_9)) tmp.append('ç');
            if (input.isKeyDown(KeyEvent.VK_COMMA)) tmp.append(',');
            if (input.isKeyDown(KeyEvent.VK_EQUALS)) tmp.append('=');
            if (input.isKeyDown(KeyEvent.VK_SEMICOLON)) tmp.append(';');
        }
        //Chiffres (NUMPAD)
        for (int k = KeyEvent.VK_NUMPAD0, i = 0; k <= KeyEvent.VK_NUMPAD9; k++, i++) {
            if (input.isKeyDown(k)) tmp.append((char) ('0' + i));
        }

        if (input.isKeyDown(KeyEvent.VK_SPACE)) tmp.append(' ');
        if (input.isKeyDown(KeyEvent.VK_BACK_SPACE) && blinkBarPos != 0) {
            text = text.substring(0, blinkBarPos - 1) + text.substring(blinkBarPos);
            blinkBarPos--;
        }
        if (input.isKeyDown(KeyEvent.VK_DELETE) && blinkBarPos < text.length()) {
            text = text.substring(0, blinkBarPos) + text.substring(blinkBarPos + 1);
        }

        if (blinkBarPos == 0) {
            text = tmp + text;
        } else if (blinkBarPos == text.length()) {
            text += tmp;
        } else {
            text = text.substring(0, blinkBarPos) + tmp + text.substring(blinkBarPos);
        }
        blinkBarPos += tmp.length();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBlinkBarPos() {
        return blinkBarPos;
    }

    public void setBlinkBarPos(int blinkBarPos) {
        this.blinkBarPos = blinkBarPos;
    }
}
