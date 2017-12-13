package com.strozor.view;

import com.strozor.engine.GameContainer;
import com.strozor.engine.GameRender;
import com.strozor.engine.Settings;
import com.strozor.engine.View;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Bloc;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Font;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Stats extends View {

    private Map<String, Integer> statsMap = new HashMap<>();
    private String[] statsName = new String[9];
    private int[] statsCount = new int[9];

    private Settings s;
    private SoundClip select;
    private Button back;

    public Stats(Settings settings) {
        s = settings;
        select = new SoundClip("/audio/select.wav");
        buttons.add(back = new Button(60, 20, "Back", 0));

        readStats();
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) gc.setState(gc.getLastState());

        //Button selection
        for(Button btn : buttons) {
            if (isSelected(gc, btn)) {
                select.play();
                gc.setState(btn.getGoState());
                gc.setState(gc.getLastState());
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {

        if(gc.getLastState() == 0) r.drawBackground(gc, new Bloc(0));
        else r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x99000000);
        r.drawMenuTitle(gc, s.translate("Stats").toUpperCase(), "");

        for(int i = 0; i < statsName.length; i++) {
            r.drawText(statsName[i], gc.getWidth()/2, gc.getHeight()/4+i*15, -1, 1, -1, Font.STANDARD);
            r.drawText(" = "+statsCount[i], gc.getWidth()/2, gc.getHeight()/4+i*15, 1, 1, -1, Font.STANDARD);
        }

        back.setOffX(5);
        back.setOffY(5);

        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }

    private void readStats() {
        try(BufferedReader br = new BufferedReader(new FileReader(GameManager.APPDATA + "\\stats.txt"))) {
            String line = br.readLine();
            int i = 0;
            while (line != null) {
                String[] sub = line.split(":");

                statsMap.put(sub[0], i);
                statsName[i] = sub[0];
                statsCount[i] = Integer.valueOf(sub[1]);

                line = br.readLine();
                i++;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void incStat(String key) {
        this.statsCount[statsMap.get(key)]++;
    }

    public void decStat(String key) {
        this.statsCount[statsMap.get(key)]--;
    }
}
