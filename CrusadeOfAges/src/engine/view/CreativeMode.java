package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Font;
import engine.gfx.Button;
import engine.gfx.Image;
import game.Conf;
import game.Editor;
import game.Game;
import game.Notification;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CreativeMode extends View {

    public static boolean focus, once;
    public static int fIndex, scroll, sMax;

    static {
        once = false;
        focus = false;
        fIndex = 0;
        scroll = 0;
        sMax = 0;
    }

    private Button edit, rename, delete, folder;
    private String creativeFolder;
    private File[] files;
    private Image[] imgs;

    private ArrayList<Notification> notifs = new ArrayList<>();

    public CreativeMode() {

        buttons.add(edit = new engine.gfx.Button(170, 20, "Edit", "edit"));
        buttons.add(rename = new engine.gfx.Button(80, 20, "Rename", "inputDialog"));
        buttons.add(delete = new engine.gfx.Button(80, 20, "Delete", "creativeMode"));
        buttons.add(folder = new engine.gfx.Button(80, 48, "Folder", "creativeMode"));
        buttons.add(new engine.gfx.Button(80, 20, "Create", "edit"));
        buttons.add(new engine.gfx.Button(80, 20, "Back", "mainMenu"));

        creativeFolder = Conf.SM_FOLDER + "/creative_mode";
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setActiView("mainMenu");
            once = false;
        }

        if (!once) {
            sMax = 10 - (gc.getHeight() - 3 * Game.TS);
            files = new File(creativeFolder).listFiles();

            int count = 0;
            if (files != null) {
                imgs = new Image[files.length];
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile() && files[i].getName().substring(files[i].getName().length() - 3).equals("png")) {
                        imgs[i] = new Image(creativeFolder + "/" + files[i].getName(), true);
                        sMax += Image.THUMBH + 10;
                        count++;
                    }
                }
            }

            if (count == 0) focus = false;
            if (sMax < 0) sMax = 0;
            once = true;
        }

        //Scroll control
        if (gc.getInput().getScroll() < 0) {
            scroll -= 20;
            if (scroll < 0) scroll = 0;
        } else if (gc.getInput().getScroll() > 0) {
            scroll += 20;
            if (scroll > sMax) scroll = sMax;
        }

        //Hover control
        for (int i = 0; i < files.length; i++) {
            if (fileSelected(gc, i, scroll)) {
                fIndex = i;
                focus = true;
            }
        }

        boolean cursorHand = false;
        for (Button btn : buttons) {
            btn.setBgColor(0xff616E7A);
            switch (btn.getText()) {
                case "Edit":
                case "Rename":
                case "Delete":
                    if (!focus) btn.setBgColor(0xffdedede);
                    break;
            }

            // Hand Cursor
            if (btn.isHover(gc.getInput()) && !((btn == edit || btn == rename || btn == delete) && !focus)) {
                gc.getWindow().setHandCursor();
                cursorHand = true;
            }

            //Button selection
            if (btn.isSelected(gc.getInput())) {
                switch (btn.getText()) {
                    case "Back":
                        focus = false;
                        break;
                    case "Create":
                        Editor.once = true;
                        Editor.newOne = true;
                        Editor.rename = "";
                        gc.getWindow().setDefaultCursor();
                        break;
                    case "Edit":
                        if (focus) {
                            Editor.once = true;
                            Editor.newOne = false;
                            Editor.rename = files[fIndex].getName();
                            Editor.creaImg = new Image(creativeFolder + "/" + files[fIndex].getName(), true);
                            gc.getWindow().setDefaultCursor();
                        }
                        break;
                    case "Rename":
                        if (focus) {
                            String text = files[fIndex].getName().substring(0, files[fIndex].getName().length() - 4);
                            InputDialog.textInput.setText(text);
                            InputDialog.textInput.setBlinkBarPos(text.length());
                            InputDialog.path = files[fIndex].getPath();
                        }
                        break;
                    case "Delete":
                        if (focus) {
                            try {
                                Files.delete(Paths.get(files[fIndex].getPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int high = Image.THUMBH + 10;
                            if (scroll >= high) scroll -= high;
                            if (fIndex == files.length - 1 && fIndex != 0) fIndex--;
                        }
                        break;
                    case "Folder":
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().open(new File(creativeFolder));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        StringSelection data = new StringSelection(creativeFolder);
                        Clipboard cb = gc.getWindow().getTk().getSystemClipboard();
                        cb.setContents(data, data);
                        notifs.add(new Notification(gc.getSettings().translate("Le chemin vers le dossier a été copié"), 3, 50, -1));
                        break;
                }

                if (!((btn == edit || btn == rename || btn == delete) && !focus)) {
                    gc.getSb().get("click").play();
                    gc.setActiView(btn.getTargetView());
                    once = false;
                }
            }

            if (!((btn == edit || btn == rename || btn == delete) && !focus)) {
                btn.hearHover(gc.getInput(), gc.getSb());
            }
        }
        if (!cursorHand) gc.getWindow().setDefaultCursor();

        //////////// NOTIFS UPDATE
        for (int i = 0; i < notifs.size(); i++) {
            notifs.get(i).update(dt);
            if (notifs.get(i).isEnded()) {
                notifs.remove(i--);
            }
        }
        //////////// NOTIFS UPDATE
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawBackground();
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);

        //Draw list of files & scroll bar
        if (sMax <= 0) scroll = 0;
        r.drawCreaList(files, imgs, "Create your first map !");
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / Game.TS + 1, 1, "wall");
        r.drawText("Select a map", gc.getWidth() / 2, Game.TS / 2, 0, 0, -1, Font.STANDARD);
        //Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - Game.TS * 2, gc.getWidth() / Game.TS + 1, 2, "wall");

        int x = gc.getWidth() / 2;
        int y = gc.getHeight() - 2 * Game.TS + 8;

        for (Button btn : buttons) {
            switch (btn.getText()) {
                case "Edit":
                    btn.setCoor(x - 5, y, -1, 1);
                    break;
                case "Rename":
                    btn.setCoor(x - delete.getWidth() - 15, gc.getHeight() - 8, -1, -1);
                    break;
                case "Delete":
                    btn.setCoor(x - 5, gc.getHeight() - 8, -1, -1);
                    break;
                case "Folder":
                    btn.setCoor(x + 5, y, 1, 1);
                    break;
                case "Create":
                    btn.setCoor(x + folder.getWidth() + 15, y, 1, 1);
                    break;
                case "Back":
                    btn.setCoor(x + folder.getWidth() + 15, gc.getHeight() - 8, 1, -1);
                    break;
            }
            r.drawButton(btn, btn.isHover(gc.getInput()));
        }

        for (Notification notif : notifs) notif.render(gc, r);
    }
}
