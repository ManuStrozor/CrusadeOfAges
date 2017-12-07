package com.strozor.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private ArrayList<Map> lang = new ArrayList<>();

    private int langIndex = 0;
    private float scale = 3f;
    private boolean showFps = false;
    private boolean showLights = true;

    public Settings() {
        Map<String, String> en = new HashMap<>();
        Map<String, String> fr = new HashMap<>();
        en.put("lang", "English");
        en.put("beta version", "beta version");
        en.put("Single player", "Single player");
        en.put("Creative Mode", "Creative Mode");
        en.put("Game credits", "Game credits");
        en.put("Options", "Options");
        en.put("Quit game", "Quit game");
        en.put("FPS off", "FPS off");
        en.put("Darkness", "Darkness");
        en.put("FPS on", "FPS on");
        en.put("Full day", "Full day");
        en.put("Back", "Back");
        en.put("Back to game", "Back to game");
        en.put("Quit to title", "Quit to title");
        en.put("Try again", "Try again");
        en.put("Keep on", "Keep on");
        en.put("Save & Quit game", "Save & Quit game");
        en.put("Development team", "Development team");
        en.put("MAIN DEVELOPERS", "MAIN DEVELOPERS");
        en.put("THANKS TO", "THANKS TO");
        en.put("CONTRIBUTORS", "CONTRIBUTORS");
        en.put("Spawn", "Spawn");
        en.put("Floor", "Floor");
        en.put("Health pill", "Health pill");
        en.put("Ground spikes", "Ground spikes");
        en.put("Ceiling spikes", "Ceiling spikes");
        en.put("Key", "Key");
        en.put("Check point", "Check point");
        en.put("Coin", "Coin");
        en.put("Torch", "Torch");
        en.put("Slime bloc", "Slime bloc");
        en.put("Door", "Door");
        en.put("Ladder", "Ladder");
        en.put("GAME OVER", "GAME OVER");
        en.put("You are dead", "You are dead");
        lang.add(en);
        fr.put("lang", "Français");
        fr.put("beta version", "version beta");
        fr.put("Single player", "Joueur solo");
        fr.put("Creative Mode", "Mode créatif");
        fr.put("Game credits", "Credits");
        fr.put("Options", "Options");
        fr.put("Quit game", "Quitter");
        fr.put("FPS off", "FPS off");
        fr.put("Darkness", "Obscurité");
        fr.put("FPS on", "FPS on");
        fr.put("Full day", "Plein jour");
        fr.put("Back", "Retour");
        fr.put("Back to game", "Retour au jeu");
        fr.put("Quit to title", "Menu principal");
        fr.put("Try again", "Recommencer");
        fr.put("Keep on", "Continuer");
        fr.put("Save & Quit game", "Enregistrer & quitter");
        fr.put("Development team", "Equipe de développement");
        fr.put("MAIN DEVELOPERS", "PRINCIPAUX DEVELOPPEURS");
        fr.put("THANKS TO", "MERCI A");
        fr.put("CONTRIBUTORS", "CONTRIBUTEURS");
        fr.put("Spawn", "Point d'apparition");
        fr.put("Floor", "Sol");
        fr.put("Health pill", "Pillule de santé");
        fr.put("Ground spikes", "Pics au sol");
        fr.put("Ceiling spikes", "Pics au plafond");
        fr.put("Key", "Clé");
        fr.put("Check point", "Point de sauvegarde");
        fr.put("Coin", "Pièce");
        fr.put("Torch", "Torche");
        fr.put("Slime bloc", "Bloc de slime");
        fr.put("Door", "Porte");
        fr.put("Ladder", "Echelle");
        fr.put("GAME OVER", "MANCHE PERDU");
        fr.put("You are dead", "Vous êtes mort(e)");
        lang.add(fr);
    }

    public ArrayList<Map> getLang() {
        return lang;
    }

    public String translate(String key) {
        String value = "";
        try {
            value = lang.get(langIndex).get(key).toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public int getLangIndex() {
        return langIndex;
    }

    public void setLangIndex(int langIndex) {
        this.langIndex = langIndex;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isShowFps() {
        return showFps;
    }

    public void setShowFps(boolean showFps) {
        this.showFps = showFps;
    }

    public boolean isShowLights() {
        return showLights;
    }

    public void setShowLights(boolean showLights) {
        this.showLights = showLights;
    }
}
