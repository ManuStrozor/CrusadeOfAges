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
import com.strozor.game.EditBoard;
import com.strozor.game.GameManager;

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
    private SoundClip select;
    private Button edit, rename, delete, create, folder, back;
    private String creativeFolder;
    private File dossier;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Date> dates = new ArrayList<>();

    public CreativeMode(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(edit = new Button(170, 20, "Edit", 4));
        buttons.add(rename = new Button(80, 20, "InputDialog", 9));
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

            sMax = 10-(gc.getHeight()-3*GameManager.TS);
            int count = 0;
            for(File file : files) {
                if (file.isFile() && file.getName().substring(file.getName().length() - 3).equals("png")) {
                    images.add(new Image(creativeFolder + "\\" + file.getName(), true));
                    names.add(file.getName());
                    paths.add(file.getPath());
                    dates.add(new Date(file.lastModified()));
                    sMax += (images.get(count).getH() < 30 ? 30 : images.get(count).getH())+10;
                    count++;
                }
            }
            if(count == 0) focus = false;
            if(sMax < 0) sMax = 0;
            once = true;
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setLastState(8);
            gc.setState(0);
            once = false;
        }
        //Scroll control
        if(gc.getInput().getScroll() < 0) {
            scroll -= 20;
            if(scroll < 0) scroll = 0;
        } else if(gc.getInput().getScroll() > 0) {
            scroll += 20;
            if(scroll > sMax) scroll = sMax;
        }
        //Hover control
        for(int i = 0; i < images.size(); i++) {
            if(mouseIsOnYPos(gc, images, i, scroll)) {
                fIndex = i;
                focus = true;
            }
        }
        //Button selection
        for(Button btn : buttons) {
            btn.setBgColor(0xff616E7A);
            if(!focus && (btn == edit || btn == rename || btn == delete)) {
                btn.setBgColor(0xffdedede);
            } else if(isSelected(gc, btn)) {
                if(btn == back) focus = false;
                if(btn == create) {
                    EditBoard.once = false;
                    if(!EditBoard.newOne) EditBoard.newOne = true;
                    EditBoard.rename = "";
                }
                if(btn == edit) {
                    EditBoard.once = false;
                    if(EditBoard.newOne) EditBoard.newOne = false;
                    EditBoard.rename = names.get(fIndex);
                    EditBoard.creaImg = null;
                    EditBoard.creaImg = images.get(fIndex);
                }
                if(btn == rename) {
                    InputDialog.input = names.get(fIndex).substring(0, names.get(fIndex).length() - 4);
                    InputDialog.path = paths.get(fIndex);
                    focus = false;
                }
                if(btn == delete) {
                    try {
                        Files.delete(Paths.get(paths.get(fIndex)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int high = (images.get(fIndex).getH() < 30 ? 30 : images.get(fIndex).getH())+10;
                    if(scroll >= high) scroll -= high;
                    if(fIndex == images.size()-1 && fIndex != 0) fIndex--;
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
                once = false;
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        //Fill general background
        r.drawBackground(gc, new Bloc(0));
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);
        //Draw list of files & scroll bar
        if(sMax <= 0) scroll = 0;
        r.drawListOfFiles(gc, images, names, dates, s.translate("Create your first map !"));
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth()/GameManager.TS, 1, new Bloc(0));
        r.drawText(s.translate("Select a map"), gc.getWidth()/2, GameManager.TS/2, 0, 0, -1, Font.STANDARD);
        //Draw background & buttons
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
        //Draw Buttons
        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
