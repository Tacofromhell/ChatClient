package data;

import gui.Main;
import javafx.application.Platform;
import network.ChatClient;

import java.util.ArrayList;
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
                    connectedToServer(data);
                } else if (data instanceof User) {
                    receivedUser(data);
                } else if (data instanceof NetworkMessage.ClientDisconnect){
                    receivedClientDisconnected((NetworkMessage.ClientDisconnect) data);
                } else if (data instanceof NetworkMessage.RoomCreate){

                } else if (data instanceof NetworkMessage.RoomDelete){

                } else if (data instanceof NetworkMessage.RoomJoin){
                    receivedUserJoinedRoom(data);
                } else if (data instanceof NetworkMessage.RoomLeave){
                    receivedUserLeftRoom(data);
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

    private void receivedUser(Object data){
        System.out.println("Received a: " + data);
        ChatClient.get().setCurrentUser((User) data);
        System.out.println(ChatClient.get().getCurrentUser().getID());
        Platform.runLater(() -> Main.UIcontrol.initRooms());
    }
    private void receivedClientDisconnected(NetworkMessage.ClientDisconnect data){
        System.out.println("clientdisconnect" + data.userId);
        Platform.runLater(() -> Main.UIcontrol.userDisconnected(data.userId));
    }
    private void receivedRoomCreated(Object data){}
    private void receivedRoomDeleted(Object data){}

    private void connectedToServer(Object data){

        Room room = (Room) data;


        //GANSKA SÄKER PÅ ATT DENNA IFEN ÄR ONÖDIG DÅ RUMMET SOM SKICKAS FRÅN SERVERN ALLTID
        //KOMMER SES SOM ETT NYTT OBJEKT OCH KAN DÄRFÖR INTE JÄMFÖRAS MED OBJEKTEN I ARRAYEN MED ROOMS SOM KLIENTEN HAR
        //KANSKE SKA FUNDERA PÅ ATT SÄTTA UNIKA IDN PÅ RUM?

        if(!ChatClient.get().getRooms().contains(room)) {
            ChatClient.get().addRoom(room);

            // print rooms messages on connection
            room.getMessages().forEach(msg -> Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(msg)));

            // print users in list
            room.getUsers().forEach(user -> {
                Platform.runLater(() -> Main.UIcontrol.printUser(user, room.getRoomName()));
            });
        }

        }
    private void receivedUserJoinedRoom(Object data) {
        NetworkMessage.RoomJoin roomJoin = (NetworkMessage.RoomJoin) data;

            //Lägg till den nyanslutna klienten i användarlistan till höger
            Platform.runLater(() -> Main.UIcontrol.printUser(roomJoin.user, roomJoin.targetRoom));

            //Lägg till den nyanslutna klienten i arrayen users i Room
            for (Room room : ChatClient.get().getRooms()) {
                if(room.getRoomName().equals(roomJoin.targetRoom)) {
                    room.addUserToRoom(roomJoin.user);
                    System.out.println("STORLEK PÅ " + room.getRoomName() + ": " + room.getUsers().size());
                }
            }
    }


    private void receivedUserLeftRoom(Object data){
        System.out.println("LEFT" + data);
    }

    private void receivedUserChangedName(Object data){
        System.out.println("User changed name");
        NetworkMessage.UserNameChange userNameChange = (NetworkMessage.UserNameChange) data;

        Platform.runLater(() -> {
            Main.UIcontrol.updateUsername(userNameChange);
            Main.UIcontrol.printServerEvent(userNameChange.newName, userNameChange.userId, "nameChange");
        });

    }
}
