package com.strozor.engine;

import com.strozor.game.GameManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Data {

    private String[] states;
    private int[] values;

    public Data() {
        states = new String[GameManager.dataStates.length];
        values = new int[GameManager.dataStates.length];
        readData();
    }

    private void readData() {
        try {
            FileInputStream fis = new FileInputStream(GameManager.APPDATA + "\\player.dat");
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
            FileOutputStream fos = new FileOutputStream(GameManager.APPDATA + "\\player.dat");
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

    public void upValueOf(String state) {
        for(int i = 0; i < states.length; i++) {
            if(states[i].equals(state)) values[i]++;
        }
    }
}
