package data;

import gui.Main;
import javafx.application.Platform;
import network.ChatClient;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public class DataHandler {

    private LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();


    public DataHandler( ) {

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
                    receivedMessage(data);
                } else if (data instanceof Room) {
                    receivedUserJoinedRoom(data);
                } else if (data instanceof NetworkMessage.ClientConnect) {
                    receivedClientConnected(data);
                } else if (data instanceof NetworkMessage.RoomCreate){

                } else if (data instanceof NetworkMessage.RoomDelete){

                } else if (data instanceof NetworkMessage.RoomJoin){

                } else if (data instanceof NetworkMessage.RoomLeave){

                } else if (data instanceof NetworkMessage.UserNameChange) {
                    receivedUserChangedName(data);
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

    private void receivedMessage(Object data){
        System.out.println("Received a: " + data);
        Message incoming = (Message) data;
        //Just for print in terminal
        String msg = incoming.getRoom() + ": " + incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
        System.out.println(msg);
        //Send incoming message and currentUser to javaFX
        Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(incoming));}
        
    private void receivedClientConnected(Object data){
        System.out.println("Received a: " + data);
        ChatClient.get().setCurrentUser((User) data);
        System.out.println(ChatClient.get().getCurrentUser().getID());
        Platform.runLater(() -> Main.UIcontrol.initRooms());
    }
    private void receivedClientDisconnected(Object data){}
    private void receivedRoomCreated(Object data){}
    private void receivedRoomDeleted(Object data){}

    private void receivedUserJoinedRoom(Object data){

        Room room = (Room) data;
        ChatClient.get().addRoom(room);
        // print rooms messages on connection
        room.getMessages()
                .forEach(msg ->
                        Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(msg)));
    }
    private void receivedUserLeftRoom(Object data){}

    private void receivedUserChangedName(Object data){
        User userChangedName = (User) data;

        ChatClient.get().getRooms().
                forEach(room -> room.getUsers().stream()
                        .filter(user -> user.getID().equals(userChangedName.getID()))
                        .forEach(user -> user.setUsername(userChangedName.getUsername())));

        Platform.runLater(() -> Main.UIcontrol.updateUserList());
    }


}
