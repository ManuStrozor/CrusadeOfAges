package engine.view;

import engine.GameContainer;
import engine.World;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;


public class MainMenu extends View {

    private Settings s;
    private World world;

    public MainMenu(Settings s, World world) {
        this.s = s;
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
        r.drawMenuTitle(gc.getTitle().toUpperCase(), s.translate("The Time Traveller"));

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            if (btn.getText().contains("Creative mode") || btn.getText().contains("Game credits")) startY += 10;
            btn.setOffX(gc.getWidth() / 2 - 85);
            if (btn.getText().contains("Quit game")) btn.setOffX(gc.getWidth() / 2 + 5);
            btn.setOffY(startY);
            if (!btn.getText().contains("Options")) startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}