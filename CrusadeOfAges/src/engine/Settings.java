package engine;

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
        String trans;
        if ((trans = langs.get(getIndexFromFlag(flag)).get(key)) == null) {
            return "@" + key;
        } else {
            return trans;
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

    private void populateLang(Map<String, String> langMap, String path) {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String[] word;
            while (reader.ready()) {
                word = reader.readLine().split(";");
                langMap.put(word[0], word[1]);
            }
        } catch (IOException e) {
            System.out.println("[Error:Settings] populateLang");
        }
    }

    private void parseAllLangs(ArrayList<Map<String, String>> langs) {
        Map<String, String> map;
        File f = new File("assets/lang");
        File[] allSubFiles = f.listFiles();
        for (File file : allSubFiles) {
            if(!file.isDirectory()) {
                populateLang(map = new HashMap<>(), "lang/" + file.getName());
                langs.add(map);
            }
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
