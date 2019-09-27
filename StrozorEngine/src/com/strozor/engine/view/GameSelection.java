package com.strozor.engine.view;

import com.strozor.engine.*;
import com.strozor.engine.audio.SoundClip;
import com.strozor.engine.gfx.Button;
import com.strozor.engine.gfx.Font;
import com.strozor.game.GameManager;

import java.awt.event.KeyEvent;

public class GameSelection extends View {

    public static boolean focus, once;
    public static int fIndex, scroll, sMax;

    static {
        once = false;
        focus = true;
        fIndex = 0;
        scroll = 0;
        sMax = 0;
    }

    private Settings s;
    private GameMap map;
    private GameManager game;
    private Button play, back;
    private SoundClip hover, click;

    public GameSelection(Settings s, GameMap map, AbstractGame game) {
        this.s = s;
        this.map = map;
        this.game = (GameManager) game;
        hover = new SoundClip("/audio/hover.wav");
        click = new SoundClip("/audio/click.wav");
        buttons.add(play = new Button(170, 20, "Play", 1));
        buttons.add(back = new Button(170, 20, "Back", 0));
    }

    @Override
    public void update(GameContainer gc, float dt) {

        if(!once) {
            sMax = 10-(gc.getHeight()-3*GameManager.TS);
            for(int i = 0; i < gc.getData().getValueOf("Level up"); i++) {
                sMax += 30+10;
            }
            if(sMax < 0) sMax = 0;
            once = true;
        }

        if(gc.getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            focus = false;
            gc.setLastState(11);
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
        //Selected control
        for(int i = 0; i <= gc.getData().getValueOf("Level up"); i++) {
            if(levelSelected(gc, i, scroll)) {
                fIndex = i;
                focus = true;
            }
        }
        //Button selection
        for(Button btn : buttons) {
            btn.setBgColor(0xff616E7A);
            if(!focus && btn == play) {
                btn.setBgColor(0xffdedede);
            } else if(isSelected(gc, btn)) {
                if(btn == back) focus = false;
                if(btn == play) {
                    GameManager.current = fIndex;
                    game.load(GameManager.levels[fIndex][0]);
                }
                click.play();
                gc.setState(btn.getGoState());
                gc.setLastState(11);
                once = false;
            }

            if (btn.setHover(isHover(gc, btn))) {
                if (!btn.isHoverSounded()) {
                    if (!hover.isRunning()) hover.play();
                    btn.setHoverSounded(true);
                }
            } else {
                btn.setHoverSounded(false);
            }
        }
    }

    @Override
    public void render(GameContainer gc, GameRender r) {
        //Fill general background
        r.drawBackground(gc, map, "wall");
        r.fillRect(0, 0, gc.getWidth(), gc.getHeight(), 0x55000000);
        //Draw list of files & scroll bar
        if(sMax <= 0) scroll = 0;
        r.drawLevels(gc, GameManager.levels);
        //Draw background & Top title
        r.fillAreaBloc(0, 0, gc.getWidth()/GameManager.TS, 1, map, "wall");
        r.drawText(s.translate("Choose a level"), gc.getWidth()/2, GameManager.TS/2, 0, 0, -1, Font.STANDARD);
        //Draw background & buttons
        r.fillAreaBloc(0, gc.getHeight()-GameManager.TS*2, gc.getWidth()/GameManager.TS, 2, map, "wall");
        play.setOffX(gc.getWidth()/2-play.getWidth()/2);
        play.setOffY(gc.getHeight()-2*GameManager.TS+10);
        back.setOffX(play.getOffX());
        back.setOffY(gc.getHeight()-GameManager.TS+5);
        //Draw Buttons
        for(Button btn : buttons) r.drawButton(btn, s.translate(btn.getText()));
    }
}
