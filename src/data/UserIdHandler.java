package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserIdHandler {

    private static String userId = null;
    private static Path path = Paths.get("src/storage/userId.txt");

    public UserIdHandler(){}

    public static String getUserId() { return userId;}

    public static void writeUserId(String userId){

        try {   //Write to file. Will create file if it does not already exist.
            Files.write(path, userId.getBytes(StandardCharsets.UTF_8));
            System.out.println("Writing user ID to " + path.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String readUserId(){

        try {
            userId = Files.readAllLines(path).get(0);
        }
        catch (IOException e){
            System.out.println("UserId not found: Generic user will be created by server.");
            return null;
        }
        return userId;
    }

//END OF CLASS
}
