package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserIdHandler{


    private static Path path = Paths.get("./storage/userId.txt");
    private static String userId;

    public UserIdHandler(){}

    public static String getUserId() { return userId;}

    public void writeUserId(String userId){

        try {
            Files.write(path, userId.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String readUserId(){

        try {
            userId = Files.lines(path).toString();
        }
        catch (IOException e){
            System.out.println("UserId not found: Generic user will be created.");
        }
        return userId;
    }

//END OF CLASS
}
