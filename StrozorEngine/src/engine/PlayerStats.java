package engine;

import game.Conf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerStats {

    private String[] states;
    private int[] values;

    public PlayerStats() {
        states = new String[Conf.STATS.length];
        values = new int[Conf.STATS.length];
        readData();
    }

    private void readData() {
        try {
            FileInputStream fis = new FileInputStream(Conf.SM_FOLDER + "/stats.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            for(int i = 0; i < states.length; i++) {
                states[i] = ois.readUTF();
                values[i] = ois.readInt();
            }
            ois.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            FileOutputStream fos = new FileOutputStream(Conf.SM_FOLDER + "/stats.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(int i = 0; i < states.length; i++) {
                oos.writeUTF(states[i]);
                oos.writeInt(values[i]);
            }
            oos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getStates() {
        return states;
    }

    public int[] getValues() {
        return values;
    }

    public int getValueOf(String state) {
        for(int i = 0; i < states.length; i++) {
            if(states[i].equals(state)) return values[i];
        }
        return 0;
    }

    public void upValueOf(String state) {
        for(int i = 0; i < states.length; i++) {
            if(states[i].equals(state)) values[i]++;
        }
    }
}
