package engine.gfx;

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
}