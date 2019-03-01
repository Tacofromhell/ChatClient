package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 8119880995263638778L;

    private String ID;
    private String username;
    private boolean onlineStatus;
    private ArrayList<String> joinedRooms = new ArrayList<>();
    String activeRoom = "general";

    public User() {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = "anon";
        this.onlineStatus = true;
        joinedRooms.add("general");
        joinedRooms.add("other room");
        setActiveRoom("general");
    }

    public User(String name) {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = name.length() > 0 ? name : "new User";
    }

    public void addJoinedRoom(String roomName) {
        if (!joinedRooms.contains(roomName))
            joinedRooms.add(roomName);
    }

    public void removeJoinedRoom(String roomName) {
        if (joinedRooms.contains(roomName))
            joinedRooms.remove(roomName);
    }

    public String getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;
    }

    public ArrayList<String> getJoinedRooms() {
        return joinedRooms;
    }

    public User getUser() {
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getID() {
        return this.ID;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public boolean getOnlineStatus() {
        return this.onlineStatus;
    }
}//class end
