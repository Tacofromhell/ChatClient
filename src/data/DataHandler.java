package data;

import gui.Main;
import javafx.application.Platform;
import network.ChatClient;
import java.util.concurrent.LinkedBlockingDeque;

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
                    System.out.println("handledata");
                    receivedUserJoinedRoom(data);
                } else if (data instanceof User) {
                    receivedUser(data);
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
        Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(incoming));}

    private void receivedUser(Object data){
        System.out.println("Received a: " + data);
        ChatClient.get().setCurrentUser((User) data);
        System.out.println(ChatClient.get().getCurrentUser().getID());
        Platform.runLater(() -> Main.UIcontrol.initRooms());
    }
    private void receivedClientDisconnected(Object data){}
    private void receivedRoomCreated(Object data){}
    private void receivedRoomDeleted(Object data){}

    private void receivedUserJoinedRoom(Object data){
        System.out.println("RECEIVED ROOM");
        Room room = (Room) data;
        ChatClient.get().addRoom(room);
        // print rooms messages on connection
        room.getMessages()
                .forEach(msg ->
                        Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(msg)));

        room.getUsers().forEach(user -> {
            Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(user, room.getRoomName()));
        });
    }
    private void receivedUserLeftRoom(Object data){}

    private void receivedUserChangedName(Object data){
        System.out.println("User changed name");
        NetworkMessage.UserNameChange userNameChange = (NetworkMessage.UserNameChange) data;

        Platform.runLater(() -> Main.UIcontrol.controllerUsers.updateUsername(userNameChange));
    }
}
