package utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileUtility {

    private static void createNewTextFile(String filename) {
        Path path = Paths.get("src/files/" + filename + ".txt");
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createNewSerFile(String filename) {
        Path path = Paths.get("src/files/" + filename + ".ser");
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new file if path is not found
     * <p>
     * Option to use StandardOpenOption is present
     *
     * @param o        Object to save
     * @param filename Filename to save object to
     */
    public static void saveObject(Object o, String filename) {
        saveObject(o, filename, StandardOpenOption.WRITE);
    }

    public static void saveObject(Object o, String filename, StandardOpenOption option) {
        Path path = Paths.get("src/files/" + filename + ".ser");

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path, option))) {
            out.writeObject(o);
        } catch (NoSuchFileException e) {
            createNewSerFile(filename);
            saveObject(o, filename, option);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object loadObject(String filename) {
        Path path = Paths.get("src/files/" + filename + ".ser");
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            return in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates new file if path is not found
     * <p>
     * Option to use StandardOpenOption is present
     *
     * @param text     Text to save
     * @param filename Filename to save text to
     */
    public static void saveText(String filename, String text) {
        saveText(filename, text, StandardOpenOption.WRITE);
    }

    public static void saveText(String filename, String text, StandardOpenOption option) {
        Path path = Paths.get("src/files/" + filename + ".txt");

//        adds text to new line if option equals append
        if (option == StandardOpenOption.APPEND) text = "\n" + text;
        try {
            Files.write(path, text.getBytes(StandardCharsets.UTF_8), option);
        } catch (NoSuchFileException e) {
            createNewTextFile(filename);
            saveText(filename, text, option);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadText(String filename) {
        Path path = Paths.get("src/files/" + filename + ".txt");
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}