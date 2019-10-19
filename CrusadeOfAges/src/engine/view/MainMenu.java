package engine.view;

import engine.GameContainer;
import engine.World;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;


public class MainMenu extends View {

    private Settings settings;
    private World world;

    public MainMenu(Settings settings, World world) {
        this.settings = settings;
        this.world = world;

        buttons.add(new Button("Single player", "gameSelection"));
        buttons.add(new Button("Stats", "stats"));
        buttons.add(new Button("Creative mode", "creativeMode"));
        buttons.add(new Button("Game credits", "credits"));
        buttons.add(new Button(80, 20, "Options", "options"));
        buttons.add(new Button(80, 20, "Quit game", "exit"));
    }

    @Override
    public void update(GameContainer gc, float dt) {
        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Click
            if (isSelected(gc, btn)) {
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
            }

            // Hover Sound
            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }

            // Hand Cursor
            if (isHover(gc, btn)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawBackground(world);
        r.drawMenuTitle(gc.getTitle().substring(0, gc.getTitle().length() - 6).toUpperCase(), settings.translate("The Time Traveller"));

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Single player":
                    btn.setAlignCoor(x, y, 0, 1);
                    break;
                case "Stats":
                    btn.setAlignCoor(x, y + 30, 0, 1);
                    break;
                case "Creative mode":
                    btn.setAlignCoor(x, y + 60, 0, 1);
                    break;
                case "Game credits":
                    btn.setAlignCoor(x, y + 90, 0, 1);
                    break;
                case "Options":
                    btn.setAlignCoor(x - 5, y + 120, -1, 1);
                    break;
                case "Quit game":
                    btn.setAlignCoor(x + 5, y + 120, 1, 1);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
