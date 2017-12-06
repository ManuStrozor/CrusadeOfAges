package com.strozor.engine.gfx;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;

public class FlashNotif {

    private String message;
    private float duration, elapsed = 0;
    private int color, shadow, distance;

    public FlashNotif(String message, float duration, int distance, int color) {
        this.message = message;
        this.duration = duration;
        this.distance = distance;
        this.color = color;
        this.shadow = darken(color);
    }

    public void update(GameContainer gc, float dt) {
        elapsed += dt;
        if(elapsed >= duration) elapsed = duration;
    }

    public void render(GameContainer gc, GameRender r) {
        float percent = elapsed / duration;
        float offset = gc.getHeight() - percent * distance;

        int b = ((color >> 24) & 255) - (int) (percent * ((color >> 24) & 255));
        int newColor = b << 24 | ((color >> 16) & 255) << 16 | ((color >> 8) & 255) << 8 | (color & 255);

        int a = ((shadow >> 24) & 255) - (int) (percent * ((shadow >> 24) & 255));
        int newShadow = a << 24 | ((shadow >> 16) & 255) << 16 | ((shadow >> 8) & 255) << 8 | (shadow & 255);

        r.drawText(message, 0, (int) offset - 1, 1, -1, newShadow, Font.STANDARD);
        r.drawText(message, 1, (int) offset, 1, -1, newColor, Font.STANDARD);
    }

    public boolean isEnded() {
        return elapsed == duration;
    }

    private int darken(int col) {
        int a = (col >> 24) & 0xff;
        int r = (col >> 16) & 0xff;
        int g = (col >> 8) & 0xff;
        int b = col & 0xff;
        return ((a - 102) < 0 ? 0 : (a - 102)) << 24 | ((r - 102) < 0 ? 0 : (r - 102)) << 16 | ((g - 102) < 0 ? 0 : (g - 102)) << 8 | ((b - 102) < 0 ? 0 : (b - 102));
    }
}
