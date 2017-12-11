package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Font;
import com.strozor.engine.gfx.Image;
import com.strozor.game.CreativeMode;
import com.strozor.game.GameManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class EditList extends View {

    private Settings s;
    private SoundClip select;
    private Button edit, rename, delete, create, folder, back;

    private String creativeFolder;
    private File dossier;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Date> dates = new ArrayList<>();

    private int scroll = 0, scrollMax = 0, currIndex = 0;

    static boolean once = false, isHover = false;

    public EditList(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(edit = new Button(170, 20, "Edit", 4));
        buttons.add(rename = new Button(80, 20, "Rename", 9));
        buttons.add(delete = new Button(80, 20, "Delete", 8));
        buttons.add(create = new Button(80, 20, "Create", 4));
        buttons.add(folder = new Button(80, 47, "Folder", 8));
        buttons.add(back = new Button(80, 20, "Back", 0));

        creativeFolder = System.getenv("APPDATA") + "\\.squaremonster\\creative_mode";
        dossier = new File(creativeFolder);
    }

    @Override
    public void update(GameContainer gc, float dt) {

        File[] files = dossier.listFiles();

        if(!once) {
            images.clear();
            names.clear();
            paths.clear();
            dates.clear();

            int hUsed = 10;
            int j = 0;
            for(File file : files) {
                if (file.isFile() && file.getName().substring(file.getName().length() - 3).equals("png")) {
                    images.add(new Image(creativeFolder + "\\" + file.getName(), true));
                    names.add(file.getName());
                    paths.add(file.getPath());
                    dates.add(new Date(file.lastModified()));
                    hUsed += images.get(j).getH() < 30 ? 30+10 : images.get(j).getH()+10;
                    j++;
                }
            }
            scrollMax = hUsed-(gc.getHeight()-3*GameManager.TS);
            once = true;
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            isHover = false;
            gc.setLastState(8);
            gc.setState(0);
        }

        //Scroll control
        if(scrollMax > 0) {
            if(gc.getInput().getScroll() < 0) {
                scroll -= 20;
                if(scroll < 0) scroll = 0;
            } else if(gc.getInput().getScroll() > 0) {
                scroll += 20;
                if(scroll > scrollMax) scroll = scrollMax;
            }
        }

        //Hover control
        for(int i = 0; i < images.size(); i++) {
            if(mouseIsOnYPos(gc, images, i, scroll)) {
                currIndex = i;
                isHover = true;
            }
        }

        //Button selection
        for(Button btn : buttons) {
            btn.setBgColor(0xff616E7A);
            if(!isHover && (btn == edit || btn == rename || btn == delete)) {
                btn.setBgColor(0xffdedede);
            } else if(isSelected(gc, btn)) {
                if(btn == back) {
                    isHover = false;
                    once = false;
                }
                if(btn == create) {
                    CreativeMode.once = false;
                    if(!CreativeMode.newOne) CreativeMode.newOne = true;
                    CreativeMode.rename = "";
                    once = false;
                }
                if(btn == edit) {
                    CreativeMode.once = false;
                    if(CreativeMode.newOne) CreativeMode.newOne = false;
                    CreativeMode.rename = names.get(currIndex);
                    CreativeMode.creaImg = null;
                    CreativeMode.creaImg = images.get(currIndex);
                    once = false;
                }
                if(btn == rename) {
                    Rename.input = names.get(currIndex).substring(0, names.get(currIndex).length() - 4);
                    Rename.path = paths.get(currIndex);
                    isHover = false;
                    once = false;
                }
                if(btn == delete) {
                    try {
                        Files.delete(Paths.get(paths.get(currIndex)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isHover = false;
                    once = false;
                }
                if(btn == folder) {
                    try {
                        Desktop.getDesktop().open(new File(creativeFolder));
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                select.play();
                gc.setState(btn.getGoState());
                gc.setLastState(8);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        r.drawBackground(gc, new Bloc(0));

        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);

        if(scrollMax <= 0) scroll = 0;

        r.drawListOfFiles(gc, images, names, dates, scroll, isHover, currIndex, s.translate("Create your first map !"));

        r.fillAreaBloc(0, 0, gc.getWidth()/GameManager.TS, 1, new Bloc(0));
        r.drawText(s.translate("Select a map"), gc.getWidth()/2, GameManager.TS/2, 0, 0, -1, Font.STANDARD);
        r.fillAreaBloc(0, gc.getHeight()-GameManager.TS*2, gc.getWidth()/GameManager.TS, 2, new Bloc(0));

        edit.setOffX(gc.getWidth()/2-edit.getWidth()-5);
        edit.setOffY(gc.getHeight()-2*GameManager.TS+10);

        create.setOffX(gc.getWidth()/2+5+create.getWidth()+10);
        create.setOffY(edit.getOffY());

        rename.setOffX(edit.getOffX());
        rename.setOffY(gc.getHeight()-GameManager.TS+5);

        delete.setOffX(rename.getOffX()+rename.getWidth()+10);
        delete.setOffY(rename.getOffY());

        folder.setOffX(gc.getWidth()/2+5);
        folder.setOffY(create.getOffY());

        back.setOffX(folder.getOffX()+folder.getWidth()+10);
        back.setOffY(rename.getOffY());

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
