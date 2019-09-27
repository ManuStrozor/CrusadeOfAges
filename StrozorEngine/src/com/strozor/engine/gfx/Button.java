package com.strozor.engine.gfx;

public class Button {

    private int offX, offY, width = 170, height = 20, bgColor, goState;
    private String text;
    private boolean hover, hoverSounded = false;

    public Button(String text, int goState) {
        bgColor = 0xff616E7A;
        this.goState = goState;
        this.text = text;
    }

    public Button(int w, int h, String text, int goState) {
        bgColor = 0xff616E7A;
        width = w;
        height = h;
        this.text = text;
        this.goState = goState;
    }

    public int getOffX() {
        return offX;
    }

    public void setOffX(int offX) {
        this.offX = offX;
    }

    public int getOffY() {
        return offY;
    }

    public void setOffY(int offY) {
        this.offY = offY;
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

    public int getGoState() {
        return goState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean setHover(boolean hover) {
        this.hover = hover;
        return hover;
    }

    public boolean isHover() {
        return hover;
    }

    public boolean isHoverSounded() {
        return hoverSounded;
    }

    public void setHoverSounded(boolean hoverSounded) {
        this.hoverSounded = hoverSounded;
    }
}