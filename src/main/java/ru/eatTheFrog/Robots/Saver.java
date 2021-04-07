package ru.eatTheFrog.Robots;

import ru.eatTheFrog.Robots.Savables.ISavable;

import java.io.*;

public class Saver {
    public static void saveToFile(ISavable savable, String savePath) throws IOException {
        //System.out.println("saved to " + savePath);
        var fos = new FileOutputStream(savePath);
        var oos = new ObjectOutputStream(fos);
        savable.writeExternal(oos);
    }

    public static void updateFromFile(ISavable oldSavable, String updatePath) throws IOException, ClassNotFoundException {
        var fis = new FileInputStream(updatePath);
        var ois = new ObjectInputStream(fis);
        //System.out.println("loaded from " + updatePath);
        oldSavable.readExternal(ois);
    }
}
