package sm.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    private ArrayList<Map<String, String>> langs;
    private String flag = "fr";
    private float scale = 3f;
    private boolean showFps = true;
    private boolean showLights = false;

    public Settings() {
        langs = new ArrayList<>();
        parseAllLangs(langs);
    }

    public String translate(String key) {
        try {
            return langs.get(getIndexFromFlag(flag)).get(key);
        } catch(Exception e) {
            System.out.println("The sentence '" + key + "' could not be translated");
            return null;
        }
    }

    public int getIndexFromFlag(String flag) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).get("flag").equals(flag)) return i;
        }
        return 0;
    }

    public String getFlagFromIndex(int index) {
        return langs.get(index).get("flag");
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

    private void parseAllLangs(ArrayList<Map<String, String>> langs) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("lang");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String file;
            Map<String, String> map;
            while ((file = br.readLine()) != null) {
                populateLang(map = new HashMap<>(), "lang/"+file);
                langs.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map<String, String>> getLangs() {
        return langs;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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
