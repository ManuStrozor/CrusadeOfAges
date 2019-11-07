package engine.gfx;

import engine.InputHandler;
import engine.audio.SoundBank;

public class Button extends Clickable {

    private String text, targetView;
    private boolean hoverSounded = false;

    public Button(String text, String targetView) {
        super(0xff616E7A, 170, 20);
        this.targetView = targetView;
        this.text = text;
    }

    public Button(int w, int h, String text, String targetView) {
        super(0xff616E7A, w, h);
        this.targetView = targetView;
        this.text = text;
    }

    public String getTargetView() {
        return targetView;
    }

    public String getText() {
        return text;
    }

    public boolean isHoverSounded() {
        return hoverSounded;
    }

    public void setHoverSounded(boolean hoverSounded) {
        this.hoverSounded = hoverSounded;
    }

    public void hearHover(InputHandler input, SoundBank sb) {
        if (isHover(input)) {
            if (!hoverSounded) {
                if (!sb.get("hover").isRunning()) {
                    sb.get("hover").play();
                }
                hoverSounded = true;
            }
        } else {
            hoverSounded = false;
        }
    }
}