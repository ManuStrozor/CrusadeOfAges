package engine.audio;

import java.util.HashMap;

public class SoundBank {

    private String root;
    private HashMap<String, SoundClip> sounds;

    public SoundBank(String root) {
        this.root = root;
        sounds = new HashMap<String, SoundClip>() {{
            put("ambient", new SoundClip(root+"ambient.wav", -15f));
            put("hover", new SoundClip(root+"hover.wav", -30f));
            put("click", new SoundClip(root+"click.wav", -10f));
            put("game over", new SoundClip(root+"gameover.wav", -5f));
            put("lever", new SoundClip(root+"lever.wav", -15f));
            put("impale", new SoundClip(root+"impaled.wav"));
        }};
    }

    public SoundClip get(String name) {
        return sounds.get(name);
    }

    public HashMap<String, SoundClip> getAll() {
        return sounds;
    }
}
