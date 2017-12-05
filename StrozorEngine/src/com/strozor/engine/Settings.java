package com.strozor.engine;

public class Settings {

    private String[][] words = {
            {"English", "Français"},
            {"beta version", "version beta"},
            {"Single player", "Joueur solo"},
            {"Creative Mode", "Mode créatif"},
            {"Game credits", "Credits"},
            {"Options", "Options"},
            {"Quit game", "Quitter"},
            {"FPS off", "FPS off"},
            {"Darkness", "Obscurité"},
            {"FPS on", "FPS on"},
            {"Full day", "Plein jour"},
            {"Back", "Retour"},
            {"Back to game", "Retour au jeu"},
            {"Quit to title", "Menu principal"},
            {"Try again", "Recommencer"},
            {"Keep on", "Continuer"},
            {"Save & Quit game", "Enregistrer & quitter"},
            {"Development team", "Equipe de développement"},
            {"MAIN DEVELOPERS", "PRINCIPAUX DEVELOPPEURS"},
            {"THANKS TO", "MERCI A"},
            {"CONTRIBUTORS", "CONTRIBUTEURS"},
            {"Spawn", "Point d'apparition"},
            {"Floor", "Sol"},
            {"Health bonus", "Bonus de santé"},
            {"Ground trap", "Piège au sol"},
            {"Ceiling trap", "Piège au plafond"},
            {"Key", "Clé"},
            {"Check point", "Point de sauvegarde"},
            {"Coin", "Pièce"},
            {"Torch", "Torche"},
            {"Slime bloc", "Bloc de slime"},
            {"Door", "Porte"},
            {"Ladder", "Echelle"},
            {"GAME OVER", "MANCHE PERDU"},
            {"You are dead", "Vous êtes mort(e)"}
    };
    private int langIndex = 1;
    private boolean showFps = true;
    private boolean showLights = true;

    public Settings() {

    }

    public String[][] getWords() {
        return words;
    }

    public int getLangIndex() {
        return langIndex;
    }

    public void setLangIndex(int langIndex) {
        this.langIndex = langIndex;
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
