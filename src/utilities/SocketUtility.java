package utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SocketUtility {

    public static void sendDataToServer(ObjectOutputStream out, Object object) {
        try {
            out.writeObject(object);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static Object receiveDataFromServer(ObjectInputStream in) {
        try {
            return in.readObject();
        } catch (IOException e1) {
            // returns null
        } catch (ClassNotFoundException e1) {
            System.err.println("Class not found");
        }
        return null;
    }

}
