package data;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 8119880995263638779L;

    private User user;
    private String msg;
    private String room;
    private LocalTime timestamp;

    public Message(String msg, User user, String room){
        this.user = user;
        this.msg = msg;
        this.room = room;
        this.timestamp = LocalTime.now();
    }

    public String getRoom() {
        return room;
    }

    public User getUser (){return this.user;}

    public String getMsg(){
        return this.msg;
    }

    public String getTimestamp(){
        DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("HH:mm");
        return this.timestamp.format(timestampFormat);
    }
}

