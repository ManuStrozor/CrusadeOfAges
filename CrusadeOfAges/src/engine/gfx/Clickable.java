package engine.gfx;

import engine.InputHandler;

import java.awt.event.MouseEvent;

public abstract class Clickable {

    private int offX, offY, width, height, bgColor;

    Clickable(int bgColor, int width, int height) {
        this.bgColor = bgColor;
        this.width = width;
        this.height = height;
    }

    public int getOffX() {
        return offX;
    }

    public int getOffY() {
        return offY;
    }

    private void setOffX(int offX, int alignX) {
        this.offX = alignAxe(offX, alignX, width);
    }

    private void setOffY(int offY, int alignY) {
        this.offY = alignAxe(offY, alignY, height);
    }

    public void setCoor(int offX, int offY, int alignX, int alignY) {
        setOffX(offX, alignX);
        setOffY(offY, alignY);
    }

    private int alignAxe(int value, int align, int Axe) {
        if (align == 0) value -= Axe / 2;
        if (align == -1) value -= Axe;
        return value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean isHover(InputHandler input) {
        return input.getMouseX() > offX &&
                input.getMouseX() < offX + width &&
                input.getMouseY() > offY &&
                input.getMouseY() < offY + height;
    }

    public boolean isSelected(InputHandler input) {
        return isHover(input) && input.isButtonUp(MouseEvent.BUTTON1);
    }
}
