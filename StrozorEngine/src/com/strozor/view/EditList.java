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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class EditList extends View {

    private Settings s;
    private SoundClip select;

    private Button edit, rename, delete, create, reCreate, back;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> parents = new ArrayList<>();

    private int scroll = 0, scrollMax = 0, currIndex = 0;

    static boolean once = false, isHover = false;

    public EditList(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(edit = new Button(170, 20, "Edit", 4));
        buttons.add(rename = new Button(80, 20, "Rename", 8));
        buttons.add(delete = new Button(80, 20, "Delete", 8));
        buttons.add(create = new Button(170, 20, "Create", 4));
        buttons.add(reCreate = new Button(80, 20, "Re-create", 8));
        buttons.add(back = new Button(80, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(!once) {

            String creativeFolder = System.getenv("APPDATA") + "\\.squaremonster\\creative_mode";
            File folder = new File(creativeFolder);
            File[] files = folder.listFiles();

            images.clear();
            names.clear();
            paths.clear();
            parents.clear();

            int hUsed = 10;
            int j = 0;
            if(files != null) {
                for(File file : files) {
                    if (file.isFile()) {
                        images.add(new Image(creativeFolder + "\\" + file.getName(), true));
                        names.add(file.getName());
                        paths.add(file.getPath());
                        parents.add(file.getParentFile().getParentFile().getName()+"\\"+file.getParentFile().getName());
                        hUsed += images.get(j).getH() < 30 ? 30+10 : images.get(j).getH()+10;
                        j++;
                    }
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
            btn.setBgColor(0xff424242);
            if(!isHover && (btn == edit || btn == rename || btn == delete)) {
                btn.setBgColor(0x99ababab);
            } else if(isSelected(gc, btn)) {
                if(btn == back) isHover = false;
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
                if(btn == delete) {
                    try {
                        Files.delete(Paths.get(paths.get(currIndex)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isHover = false;
                    once = false;
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

        r.drawListOfFiles(gc, images, names, parents, scroll, isHover, currIndex, s.translate("Create your first map !"));

        r.drawStripe(gc, new Bloc(0), 0, 1);
        r.drawText(s.translate("Select a map"), gc.getWidth()/2, GameManager.TS/2, 0, 0, -1, Font.STANDARD);
        r.drawStripe(gc, new Bloc(0), gc.getHeight()-GameManager.TS*2, 2);

        edit.setOffX(gc.getWidth()/2-edit.getWidth()-5);
        edit.setOffY(gc.getHeight()-2*GameManager.TS+10);

        create.setOffX(gc.getWidth()/2+5);
        create.setOffY(edit.getOffY());

        rename.setOffX(edit.getOffX());
        rename.setOffY(gc.getHeight()-GameManager.TS+5);

        delete.setOffX(rename.getOffX()+rename.getWidth()+10);
        delete.setOffY(rename.getOffY());

        reCreate.setOffX(create.getOffX());
        reCreate.setOffY(rename.getOffY());

        back.setOffX(reCreate.getOffX()+reCreate.getWidth()+10);
        back.setOffY(rename.getOffY());

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
