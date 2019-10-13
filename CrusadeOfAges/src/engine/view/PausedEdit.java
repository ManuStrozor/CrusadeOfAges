package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;
import game.Editor;
import game.GameManager;
import game.objects.Player;

import java.awt.event.KeyEvent;

public class PausedEdit extends View {

    private Settings s;

    public PausedEdit(Settings settings) {
        s = settings;

        buttons.add(new Button("Save", "creativeMode"));
        buttons.add(new Button("Cancel", "creativeMode"));
        buttons.add(new Button("Try", "edit"));
        buttons.add(new Button("Blank", "edit"));
        buttons.add(new Button("Back", "edit"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setActiView("edit");

        for (Button btn : buttons) {
            if (isSelected(gc, btn)) {
                switch (btn.getText()) {
                    case "Save":
                        Editor.creaImg.save(Editor.rename);
                        CreativeMode.once = false;
                        Editor.setSpawn(false);
                        Renderer.tileSize = GameManager.TS;
                        Player.tileSize = GameManager.TS;
                        Editor.tileSize = GameManager.TS;
                        break;
                    case "Try": Editor.setSpawn(true); break;
                    case "Blank": Editor.creaImg.blank(); break;
                    case "Cancel":
                        Editor.setSpawn(false);
                        Renderer.tileSize = GameManager.TS;
                        Player.tileSize = GameManager.TS;
                        Editor.tileSize = GameManager.TS;
                        break;
                }
                gc.getClick().play();
                gc.setActiView(btn.getTargetView());
            }

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHover().isRunning()) gc.getHover().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

        r.fillRect(r.getCamX(), r.getCamY(), gc.getWidth(), gc.getHeight(), 0x99000000);

        int startY = gc.getHeight() / 4;

        for (Button btn : buttons) {
            btn.setOffX(gc.getWidth() / 2 - 85);
            btn.setOffY(startY);
            startY += btn.getHeight() + 5;
            r.drawButton(btn, s.translate(btn.getText()));
        }
    }
}
