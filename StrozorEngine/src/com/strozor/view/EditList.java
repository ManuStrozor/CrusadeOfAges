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
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

public class EditList extends View {

    private Settings s;
    private SoundClip select;

    private Button edit, rename, delete, create, reCreate, back;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> parents = new ArrayList<>();

    static boolean once = false;

    public EditList(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");

        buttons.add(edit = new Button(170, 20, "Edit", 8));
        buttons.add(create = new Button(170, 20, "Create", 4));
        buttons.add(rename = new Button(80, 20, "Rename", 8));
        buttons.add(delete = new Button(80, 20, "Delete", 8));
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
            parents.clear();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    images.add(new Image(creativeFolder + "\\" + files[i].getName(), true));
                    names.add(files[i].getName());
                    parents.add(files[i].getParentFile().getParentFile().getName()+"\\"+files[i].getParentFile().getName());
                }
            }
            once = true;
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            gc.setLastState(8);
            gc.setState(0);
        }

        //Focus control
        focusCtrl(gc);

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
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

        r.drawListOfFiles(gc, images, names, parents);

        r.drawStripe(gc, new Bloc(0), 0, 1);
        r.drawText(s.translate("Select a map"), gc.getWidth()/2, GameManager.TS/2, 0, 0, -1, Font.STANDARD);
        r.drawStripe(gc, new Bloc(18), GameManager.TS, 1);

        r.drawStripe(gc, new Bloc(19), gc.getHeight() - GameManager.TS*3, 1);
        r.drawStripe(gc, new Bloc(0), gc.getHeight() - GameManager.TS*2, 2);

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
