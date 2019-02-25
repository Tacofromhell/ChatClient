package data;

import java.io.Serializable;
import java.time.LocalTime;

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
        String hour = this.timestamp.getHour() < 10 ?  "0" + this.timestamp.getHour() : "" + this.timestamp.getHour();
        String minute = this.timestamp.getMinute() < 10 ?  "0" + this.timestamp.getMinute() : "" + this.timestamp.getMinute();
        String second = this.timestamp.getSecond() < 10 ?  "0" + this.timestamp.getSecond() : "" + this.timestamp.getSecond();
        return hour + "." + minute;
    }



}

