package sm.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private ArrayList<Map<String, String>> langs;
    private int lang = 1;
    private float scale = 3f;
    private boolean showFps = true;
    private boolean showLights = false;

    public Settings() throws IOException {
        Map<String, String> en = new HashMap<>();
        Map<String, String> fr = new HashMap<>();
        populateLang(en, "lang/en.txt");
        populateLang(fr, "lang/fr.txt");
        langs = new ArrayList<>();
        langs.add(0, en);
        langs.add(1, fr);
    }

    public String translate(String key) {
        try {
            return langs.get(lang).get(key);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateLang(Map<String, String> langMap, String path) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(path)) {
            if (is == null) return;
            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {
                String[] word;
                while (reader.ready()) {
                    word = reader.readLine().split(";");
                    langMap.put(word[0], word[1]);
                }
            }
        }
    }

    public ArrayList<Map<String, String>> getLangs() {
        return langs;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
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
