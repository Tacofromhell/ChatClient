package data;

import gui.Main;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class DataHandler {

    private LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();


    public DataHandler() {
        Thread handleData = new Thread(this::handleDataQueue);
        handleData.setDaemon(true);
        handleData.start();
    }

    public void addToQueue (Object obj){
        this.dataQueue.addLast(obj);
    }

    private void handleDataQueue() {
        while (true) {
            if (dataQueue.size() > 0) {

                Object data = dataQueue.poll();

                if (data instanceof Message) {
                    System.out.println("Received a: " + data);
                    Message incoming = (Message) data;
                    //Just for print in terminal
                    String msg = incoming.getRoom() + ": " + incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
                    System.out.println(msg);

                    //Send incoming message and currentUser to javaFX
                    Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(incoming));

                } else if (data instanceof Room) {

                    Room room = (Room) data;
                    rooms.add(room);
                    // print rooms messages on connection
                    room.getMessages()
                            .forEach(msg ->
                                    Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(msg)));
                } else if (data instanceof ArrayList) {
                    System.out.println("ARRAYLIST a " + data);
                    this.rooms = (ArrayList<Room>) data;


                    Platform.runLater(() -> Main.UIcontrol.updateUserList());

                } else if (data instanceof User) {
                    System.out.println("Received a: " + data);

                    this.currentUser = (User) data;
                    System.out.println(currentUser.getID());

                    Platform.runLater(() -> Main.UIcontrol.initRooms());
                }
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
