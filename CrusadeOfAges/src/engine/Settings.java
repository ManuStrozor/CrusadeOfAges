package engine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Settings {

    private ArrayList<Map<String, String>> langs;
    private String flag = "fr";
    private float scale = 3f;
    private boolean showFps = true;
    private boolean showLights = false;

    public Settings() throws IOException {
        langs = new ArrayList<>();
        parseAllLangs(langs);
    }

    public String translate(String key) {
        String trans;
        if ((trans = langs.get(getIFlag(flag)).get(key)) == null) {
            return "@" + key;
        } else {
            return trans;
        }
    }

    public int getIFlag(String flag) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).get("flag").equals(flag)) return i;
        }
        return 0;
    }

    public String getFIndex(int index) {
        return langs.get(index).get("flag");
    }

    private void populateLang(Map<String, String> langMap, String path) {
        try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String[] word;
            while (reader.ready()) {
                word = reader.readLine().split(";");
                langMap.put(word[0], word[1]);
            }
        } catch (IOException e) {
            System.out.println("[Error:Settings] populateLang");
        }
    }

    private void parseAllLangs(ArrayList<Map<String, String>> langs) throws IOException {
        Map<String, String> map;
        String jar;
        if (System.getProperty("user.dir").contains("artifacts")) jar = "CrusadeOfAges.jar";
        else jar = "../out/artifacts/CrusadeOfAges.jar";
        for (String file : getJarContent(jar)) {
            if (file.contains("lang") && file.contains("txt")) {
                populateLang(map = new HashMap<>(), file);
                langs.add(map);
            }
        }
    }

    private List<String> getJarContent(String jarPath) throws IOException {
        List<String> content = new ArrayList<>();
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
            String name = entry.getName();
            content.add(name);
        }
        return content;
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
