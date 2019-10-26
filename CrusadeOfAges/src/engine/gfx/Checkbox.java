package engine.gfx;

public class Checkbox extends Clickable {

    private String tag;
    private boolean checked = false;

    public Checkbox(String tag) {
        super(0x66818E9A, 20, 20);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
