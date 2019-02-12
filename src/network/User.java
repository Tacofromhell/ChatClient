package network;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.IDN;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String username;
   // private ArrayList<Room> joinedRooms;
   // private Room activeRoom;

    public User(){
    //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = "anonymous";
    }
    public User(String name){
    //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = name.length() > 0 ? name : "anon";
    }

   public String getUsername (){return this.username;}


}//class end
