package com.strozor.engine.gfx;

public class Font {

    public static final Font STANDARD = new Font("/fonts/standard.png");
    public static final Font BIG_STANDARD = new Font("/fonts/big_standard.png");

    private Image fontImage;
    private int[] offsets;
    private int[] widths;

    public Font(String path) {
        fontImage = new Image(path, false);

        offsets = new int[95];
        widths = new int[95];

        int unicode = 0;
        boolean next = true;

        for(int i = 0; i < fontImage.getW(); i++) {

            if(!next && fontImage.getP()[i] == 0xff0000ff) {
                widths[unicode] = i - offsets[unicode];
                unicode++;
            } else if(fontImage.getP()[i] == 0xffff0000) {
                widths[unicode] = i - offsets[unicode];
                unicode++;
                next = true;
            }

            if(unicode < offsets.length && fontImage.getP()[i] == 0xff0000ff) {
                offsets[unicode] = i;
                next = false;
            }
        }
    }

    public Image getFontImage() {
        return fontImage;
    }

    public int[] getOffsets() {
        return offsets;
    }

    public int[] getWidths() {
        return widths;
    }
}
