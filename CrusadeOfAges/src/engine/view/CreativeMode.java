package engine.view;

import engine.GameContainer;
import engine.Renderer;
import engine.Settings;
import engine.World;
import engine.gfx.Button;
import engine.gfx.Image;
import game.Conf;
import game.Editor;
import game.GameManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

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

    private Settings s;
    private World world;
    private Button edit, rename, delete, create, folder, back;
    private String creativeFolder;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Date> dates = new ArrayList<>();

    public CreativeMode(Settings s, World world) {
        this.s = s;
        this.world = world;

        buttons.add(edit = new engine.gfx.Button(170, 20, "Edit", "edit"));
        buttons.add(rename = new engine.gfx.Button(80, 20, "Rename", "inputDialog"));
        buttons.add(delete = new engine.gfx.Button(80, 20, "Delete", "creativeMode"));
        buttons.add(create = new engine.gfx.Button(80, 20, "Create", "edit"));
        buttons.add(folder = new engine.gfx.Button(80, 47, "Folder", "creativeMode"));
        buttons.add(back = new engine.gfx.Button(80, 20, "Back", "mainMenu"));

        creativeFolder = Conf.SM_FOLDER + "/creative_mode";
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if (gc.getInputHandler().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setActiView("mainMenu");
            once = false;
        }

        if (!once) {
            images.clear();
            names.clear();
            paths.clear();
            dates.clear();

            sMax = 10 - (gc.getHeight() - 3 * GameManager.TS);
            int count = 0;
            File dossier = new File(creativeFolder);
            File[] files = dossier.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().substring(file.getName().length() - 3).equals("png")) {
                        images.add(new Image(creativeFolder + "/" + file.getName(), true));
                        names.add(file.getName());
                        paths.add(file.getPath());
                        dates.add(new Date(file.lastModified()));
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
        if (gc.getInputHandler().getScroll() < 0) {
            scroll -= 20;
            if (scroll < 0) scroll = 0;
        } else if (gc.getInputHandler().getScroll() > 0) {
            scroll += 20;
            if (scroll > sMax) scroll = sMax;
        }

        //Hover control
        for (int i = 0; i < images.size(); i++) {
            if (fileSelected(gc, i, scroll)) {
                fIndex = i;
                focus = true;
            }
        }

        //Button selection
        for (Button btn : buttons) {
            btn.setBgColor(0xff616E7A);
            switch (btn.getText()) {
                case "Edit":
                case "Rename":
                case "Delete":
                    if (!focus) btn.setBgColor(0xffdedede);
                    break;
            }
            if (isSelected(gc, btn)) {
                switch (btn.getText()) {
                    case "Back":
                        focus = false;
                        break;
                    case "Create":
                        Editor.once = true;
                        Editor.newOne = true;
                        Editor.rename = "";
                        break;
                    case "Edit":
                        if (focus) {
                            Editor.once = true;
                            Editor.newOne = false;
                            Editor.rename = names.get(fIndex);
                            Editor.creaImg = images.get(fIndex);
                        }
                        break;
                    case "Rename":
                        if (focus) {
                            InputDialog.input = names.get(fIndex).substring(0, names.get(fIndex).length() - 4);
                            InputDialog.path = paths.get(fIndex);
                        }
                        break;
                    case "Delete":
                        if (focus) {
                            try {
                                Files.delete(Paths.get(paths.get(fIndex)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int high = Image.THUMBH + 10;
                            if (scroll >= high) scroll -= high;
                            if (fIndex == images.size() - 1 && fIndex != 0) fIndex--;
                        }
                        break;
                    case "Folder":
                        try {
                            Desktop.getDesktop().open(new File(creativeFolder));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (!((btn == edit || btn == rename || btn == delete) && !focus)) {
                    gc.getClickSound().play();
                    gc.setActiView(btn.getTargetView());
                    once = false;
                }
            }

            if (!((btn == edit || btn == rename || btn == delete) && !focus)) {
                if (btn.setHover(isHover(gc, btn))) {
                    if (!btn.isHoverSounded()) {
                        if (!gc.getHoverSound().isRunning()) gc.getHoverSound().play();
                        btn.setHoverSounded(true);
                    }
                } else {
                    btn.setHoverSounded(false);
                }
            }
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        //Fill general background
        r.drawBackground(world);
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);
        //Draw list of files & scroll bar
        if (sMax <= 0) scroll = 0;
        r.drawListOfFiles(images, names, dates, s.translate("Create your first map !"));
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth() / GameManager.TS + 1, 1, world, "wall");
        r.drawText(s.translate("Select a map"), gc.getWidth() / 2, GameManager.TS / 2, 0, 0, -1, engine.gfx.Font.STANDARD);
        //Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight() - GameManager.TS * 2, gc.getWidth() / GameManager.TS + 1, 2, world, "wall");
        edit.setOffX(gc.getWidth() / 2 - edit.getWidth() - 5);
        edit.setOffY(gc.getHeight() - 2 * GameManager.TS + 10);

        create.setOffX(gc.getWidth() / 2 + 5 + create.getWidth() + 10);
        create.setOffY(edit.getOffY());

        rename.setOffX(edit.getOffX());
        rename.setOffY(gc.getHeight() - GameManager.TS + 5);

        delete.setOffX(rename.getOffX() + rename.getWidth() + 10);
        delete.setOffY(rename.getOffY());

        folder.setOffX(gc.getWidth() / 2 + 5);
        folder.setOffY(create.getOffY());

        back.setOffX(folder.getOffX() + folder.getWidth() + 10);
        back.setOffY(rename.getOffY());
        //Draw Buttons
        for (Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
