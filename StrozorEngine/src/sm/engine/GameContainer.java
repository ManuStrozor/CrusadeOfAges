package sm.engine;

import sm.engine.gfx.Font;
import sm.engine.gfx.Light;
import sm.engine.view.*;
import sm.game.AbstractGame;
import sm.game.Game;
import sm.game.Editor;

import java.io.IOException;


public class GameContainer implements Runnable {

    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Game gm;
    private InputHandler inputHandler;
    private Settings settings;
    private DataStats dataStats;
    private View
            mainMenu,
            options,
            pausedGame,
            pausedEdit,
            gameOver,
            credits,
            creativeMode,
            inputDialog,
            stats,
            gameSelection;
    private AbstractGame game, edit;

    private boolean running = false;
    private int width, height;
    private float scale;
    private String title;

    private enum STATE{
        MAINMENU,
        OPTSMENU,
        GAME,
        PAUSEDGAME,
        GAMEOVER,
        EDIT,
        PAUSEDEDIT,
        CREATIVEMODE,
        INPUTDIALOG,
        CREDITS,
        STATS,
        GAMESELECTION,
        EXIT
    }

    private STATE State;
    private int currState = 0, lastState = 0;

    public GameContainer(AbstractGame game, Settings settings, World world, DataStats dataStats) {
        this.game = game;
        gm = (Game) game;
        this.settings = settings;
        this.dataStats = dataStats;

        this.mainMenu = new MainMenu(this.settings, world);
        this.options = new Options(this.settings, world);
        this.pausedGame = new PausedGame(this.settings);
        this.gameOver = new GameOver(this.settings);
        this.pausedEdit = new PausedEdit(this.settings);
        this.credits = new Credits(this.settings, world);
        this.creativeMode = new CreativeMode(this.settings, world);
        this.inputDialog = new InputDialog(this.settings, world);
        this.stats = new Stats(this.settings, world);
        this.gameSelection = new GameSelection(this.settings, world, game);
        this.edit = new Editor(60, 30);

        State = STATE.MAINMENU;
    }

    public synchronized void start() {
        window = new Window(this);
        renderer = new Renderer(this);
        inputHandler = new InputHandler(this);

        thread = new Thread(this);
        thread.run();
    }

    private synchronized void stop() {
        try {
            thread.join();
            running = false;
            System.exit(0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        STATE upState = STATE.EXIT;
        double startTime, passedTime, frameTime = 0, unprocessedTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0;
        int fps = 0, frames = 0;
        double UPDATE_CAP = 1.0/60.0;

        running = true;

        while(running && State != STATE.EXIT) {

            startTime = System.nanoTime() / 1000000000.0;
            passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;


            while(unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;

                if(State == STATE.MAINMENU) {
                    mainMenu.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREDITS) {
                    credits.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.OPTSMENU) {
                    options.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.PAUSEDGAME) {
                    pausedGame.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.PAUSEDEDIT) {
                    pausedEdit.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAMEOVER) {
                    gameOver.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAME) {
                    game.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.EDIT) {
                    edit.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.CREATIVEMODE) {
                    creativeMode.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.INPUTDIALOG) {
                    inputDialog.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.STATS) {
                    stats.update(this, (float)UPDATE_CAP);
                } else if(State == STATE.GAMESELECTION) {
                    gameSelection.update(this, (float)UPDATE_CAP);
                }

                inputHandler.update();

                if(frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            renderer.clear();

            if(State == STATE.GAME || State == STATE.PAUSEDGAME || State == STATE.GAMEOVER || (State == STATE.STATS && lastState == 2)) {
                game.render(this, renderer);
                renderer.setCoorCam(0, 0);
                if(gm.getObject(""+gm.getSocket().getLocalPort()) != null)
                    renderer.drawGameStates(this, gm.getObject(""+gm.getSocket().getLocalPort()));
            } else if(State == STATE.EDIT || State == STATE.PAUSEDEDIT) {
                edit.render(this, renderer);
            }

            if(State == STATE.MAINMENU) {
                mainMenu.render(this, renderer);
                renderer.setCoorCam(0, 0);
            } else if(State == STATE.CREDITS) {
                credits.render(this, renderer);
            } else if(State == STATE.OPTSMENU) {
                options.render(this, renderer);
            } else if(State == STATE.PAUSEDGAME) {
                pausedGame.render(this, renderer);
            } else if(State == STATE.PAUSEDEDIT) {
                pausedEdit.render(this, renderer);
            } else if(State == STATE.GAMEOVER) {
                gameOver.render(this, renderer);
            } else if(State == STATE.CREATIVEMODE) {
                creativeMode.render(this, renderer);
                renderer.setCoorCam(0, 0);
            } else if(State == STATE.INPUTDIALOG) {
                creativeMode.render(this, renderer);
                inputDialog.render(this, renderer);
            } else if(State == STATE.STATS) {
                stats.render(this, renderer);
            } else if(State == STATE.GAMESELECTION) {
                gameSelection.render(this, renderer);
            }

            if(settings.isShowLights() && State != STATE.CREATIVEMODE && State != STATE.EDIT && State != STATE.PAUSEDEDIT && State != STATE.INPUTDIALOG) {
                if (State != STATE.GAME && State != STATE.PAUSEDGAME) {
                    renderer.drawLight(new Light(150, 0xffffff99), this.getInputHandler().getMouseX(), this.getInputHandler().getMouseY());
                }
                renderer.process();
            }

            if(State == STATE.MAINMENU || State == STATE.OPTSMENU || State == STATE.STATS || State == STATE.CREDITS) {
                renderer.drawText(title + " 2.0.1/beta", 0, getHeight(), 1, -1, 0xffababab, Font.STANDARD);
                renderer.drawText("Strozor Inc.", getWidth(), getHeight(), -1, -1, 0xffababab, Font.STANDARD);
            }

            if(settings.isShowFps())
                renderer.drawText(fps + "fps", getWidth(), 0, -1, 1, 0xffababab, Font.STANDARD);

            window.update();
            frames++;

            if (upState != State) {
                upState = State;
                try {
                    gm.getDos().writeUTF(gm.getSocket().getLocalPort() + " " + upState);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            gm.getDis().close();
            gm.getDos().close();
            gm.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
    }

    public int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Window getWindow() {
        return window;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public Settings getSettings() {
        return settings;
    }

    public DataStats getDataStats() {
        return dataStats;
    }

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }

    public int getCurrState() {
        return currState;
    }

    public void setCurrState(int currState) {
        this.currState = currState;
    }

    public void setState(int value) {
        setLastState(currState);
        setCurrState(value);
        switch(value) {
            case -1: State = STATE.EXIT; break;
            case  0: State = STATE.MAINMENU; break;
            case  1: State = STATE.GAME; break;
            case  2: State = STATE.PAUSEDGAME; break;
            case  3: State = STATE.OPTSMENU; break;
            case  4: State = STATE.EDIT; break;
            case  5: State = STATE.PAUSEDEDIT; break;
            case  6: State = STATE.CREDITS; break;
            case  7: State = STATE.GAMEOVER; break;
            case  8: State = STATE.CREATIVEMODE; break;
            case  9: State = STATE.INPUTDIALOG; break;
            case 10: State = STATE.STATS; break;
            case 11: State = STATE.GAMESELECTION; break;
        }
    }
}
