package engine.view;

import engine.GameContainer;
import engine.Level;
import engine.Renderer;
import engine.gfx.Button;
import engine.gfx.Font;
import game.Game;
import game.entity.Player;
import game.entity.PlayerMP;
import network.Client;
import network.Server;
import network.packets.Packet00Login;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class Lobby extends View {

    public Lobby() {
        buttons.add(new Button(70, 20, "Create", "game"));
        buttons.add(new Button(70, 20, "Join", "game"));
        buttons.add(new Button(70, 20, "Back", "mainMenu"));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setActiView(gc.getPrevView());
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {

            // Hand Cursor
            if (btn.isHover(gc.getInput())) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            btn.setBgColor(0xff616E7A);
            if (btn.isSelected(gc.getInput())) {
                gc.getClickSound().play();

                switch (btn.getText()) {
                    case "Create":
                        if (gc.getSocketServer() == null || !gc.getSocketServer().isAlive()) {
                            gc.setSocketServer(new Server(gc));
                            gc.getSocketServer().start();
                        }
                        if (gc.getSocketClient() == null || !gc.getSocketClient().isAlive()) {
                            gc.setSocketClient(new Client(gc, "localhost"));
                            gc.getSocketClient().start();
                        }
                        break;
                    case "Join":
                        if (gc.getSocketClient() == null || !gc.getSocketClient().isAlive()) {
                            String serverLocation =
                                    JOptionPane.showInputDialog("Server location ?", "localhost");
                            gc.setSocketClient(new Client(gc, serverLocation));
                            gc.getSocketClient().start();
                        }
                        break;
                }

                Level level = gc.getGame().getLevel();
                switch (btn.getText()) {
                    case "Create":
                    case "Join":
                        gc.getWindow().setBlankCursor();
                        String playerName =
                                JOptionPane.showInputDialog("Player name ?", "toto");
                        gc.getSocketClient().setPlayerName(playerName);
                        level.load();
                        Player player = new PlayerMP(playerName, gc.getWorld(), null, -1);
                        level.addEntity(player);
                        Packet00Login loginPacket = new Packet00Login(playerName);
                        if (gc.getSocketServer() != null) {
                            gc.getSocketServer().addConnection((PlayerMP)player, loginPacket);
                        }
                        loginPacket.writeData(gc.getSocketClient());
                        break;
                }

                gc.setActiView(btn.getTargetView());
            }

            // Hover Sound
            if (btn.isHover(gc.getInput())) {
                if (!btn.isHoverSounded()) {
                    if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();

    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawBackground();
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);

        // Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / Game.TS + 1, 1, "wall");
        r.drawText(gc.getSettings().translate("Multiplayer lobby"), gc.getWidth() / 2, Game.TS / 2, 0, 0, -1, Font.STANDARD);
        // Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - Game.TS * 2, gc.getWidth() / Game.TS + 1, 2, "wall");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() - Game.TS;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Create":
                    btn.setCoor(x - 45, y, -1, 0);
                    break;
                case "Join":
                    btn.setCoor(x, y, 0, 0);
                    break;
                case "Back":
                    btn.setCoor(x + 45, y, 1, 0);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }
    }
}
