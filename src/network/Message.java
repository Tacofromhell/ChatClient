package network;

import java.io.Serializable;
import java.net.Socket;
import java.time.LocalTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 8119880995263638779L;

    private User user;
    private String msg;
    private LocalTime timestamp;

    public Message(String msg, User user){
        this.user = user;
        this.msg = msg;
        this.timestamp = LocalTime.now();
    }

    User getUser (){return this.user;}

    String getMsg(){
        return this.msg;
    }

    String getTimestamp(){
        String hour = this.timestamp.getHour() < 10 ?  "0" + this.timestamp.getHour() : "" + this.timestamp.getHour();
        String minute = this.timestamp.getMinute() < 10 ?  "0" + this.timestamp.getMinute() : "" + this.timestamp.getMinute();
        String second = this.timestamp.getSecond() < 10 ?  "0" + this.timestamp.getSecond() : "" + this.timestamp.getSecond();
        return hour + "." + minute;
    }



}

