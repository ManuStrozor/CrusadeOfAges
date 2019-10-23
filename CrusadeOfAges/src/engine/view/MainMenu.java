package engine.view;

import engine.GameContainer;
import engine.World;
import engine.Renderer;
import engine.Settings;
import engine.gfx.Button;

import javax.swing.*;


public class MainMenu extends View {

    public MainMenu(Confirm confirm) {

        buttons.add(new Button("Single player", "gameSelection"));
        buttons.add(new Button("Multiplayer", "lobby"));
        buttons.add(new Button("Stats", "stats"));
        buttons.add(new Button("Editor", "creativeMode"));
        buttons.add(new Button("Game credits", "credits"));
        buttons.add(new Button(80, 20, "Options", "options"));
        confirm.setMessage("Quit game ?");
        confirm.setButton(new Button(80, 20, "Quit game", "exit"));
        buttons.add(new Button(80, 20, "Quit game", "confirmExit"));
    }

    @Override
    public void update(GameContainer gc, float dt) {
        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Button Click
            if (isSelected(gc, btn) && !btn.getText().equals("Multiplayer")) {
                gc.getClickSound().play();
                gc.setActiView(btn.getTargetView());
            } else if (isSelected(gc, btn)) {
                String ip = JOptionPane.showInputDialog("IP ADDRESS ?", "localhost");
                System.out.println(ip);
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
        r.drawBackground();
        r.drawMenuTitle(gc.getTitle().substring(0, gc.getTitle().length() - 6).toUpperCase(), "The Time Traveller");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() / 4;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Single player":
                    btn.setAlignCoor(x, y, 0, 1);
                    break;
                case "Multiplayer":
                    btn.setAlignCoor(x, y + 25, 0, 1);
                    break;
                case "Editor":
                    btn.setAlignCoor(x, y + 55, 0, 1);
                    break;
                case "Stats":
                    btn.setAlignCoor(x, y + 85, 0, 1);
                    break;
                case "Game credits":
                    btn.setAlignCoor(x, y + 110, 0, 1);
                    break;
                case "Options":
                    btn.setAlignCoor(x - 5, y + 135, -1, 1);
                    break;
                case "Quit game":
                    btn.setAlignCoor(x + 5, y + 135, 1, 1);
                    break;
            }
            r.drawButton(btn);
        }
    }
}
