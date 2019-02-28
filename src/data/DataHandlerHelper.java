package data;

import gui.Main;
import javafx.application.Platform;
import network.ChatClient;
import network.SocketStreamHelper;

public class DataHandlerHelper {
    private boolean firstLogin = false;

    public void receivedMessage(Object data) {
        System.out.println("Received a: " + data);
        Message incoming = (Message) data;
        //Just for print in terminal
        String msg = incoming.getRoom() + ": " + incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
        System.out.println(msg);
        //Send incoming message and currentUser to javaFX
        Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(incoming));
    }

    public void receivedUser(Object data) {
        System.out.println("Received a: " + data);
        ChatClient.get().setCurrentUser((User) data);


        ChatClient.get().startAutoUpdatingActiveRoom();

        System.out.println(ChatClient.get().getCurrentUser().getID());
    }

    public void receivedClientDisconnected(NetworkMessage.ClientDisconnect data) {
        System.out.println("user disconnected");
        ChatClient.get().getRooms().forEach((roomName, room) -> room.getUsers().values().forEach(user -> {
            if (user.getID().equals(data.userId)) {
                user.setOnlineStatus(false);
            }
        }));
        for (Room room : ChatClient.get().getRooms().values()) {
            room.getUsers().values().stream()
                    .filter(user -> user.getID().equals(data.userId))
                    .forEach(user ->
                            Platform.runLater(() -> Main.UIcontrol.controllerUsers.userDisconnected(room.getRoomName(), data.userId)));
        }
    }

    public void receivedRoomCreated(NetworkMessage.RoomCreate data) {
        Platform.runLater(() -> Main.UIcontrol.controllerRooms.printNewPublicRoom(data.getRoomName()));
    }

    public void receivedRoomDeleted(Object data) {
    }

    public void receivedRoom(Object data) {

        Room room = (Room) data;
        ChatClient.get().addRoom(room);
        // print rooms messages on connection

//        printDataToRooms(room);
    }

    public void receivedUserJoinedRoom(String targetRoom, User user, Room room) {

        // if user == this client, add room to joined rooms
        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            ChatClient.get().getCurrentUser().addJoinedRoom(targetRoom);

            ChatClient.get().addRoom(room);

            Platform.runLater(() -> Main.UIcontrol.controllerRooms.printNewJoinedRoom(targetRoom));

            printDataToRooms(room);

            // TODO: highlight active room color
            Platform.runLater(() -> Main.UIcontrol.controllerRooms.activeRoomColor(targetRoom, ChatClient.get().getCurrentUser().getActiveRoom()));

            // switch room when joining
            Platform.runLater(() -> Main.UIcontrol.controllerRooms.switchContent(targetRoom));


        } else {
            ChatClient.get().getRooms().get(targetRoom).addUserToRoom(user);
            Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(user, targetRoom));
        }

        // need to init rooms at correct lifecycle hook
        if (!firstLogin) {
            Platform.runLater(() -> Main.UIcontrol.initRooms());
            firstLogin = true;
        }
    }

    public void receivedUserLeftRoom(Object data) {
    }

    public void receivedUserChangedName(Object data) {
        System.out.println("User changed name");
        NetworkMessage.UserNameChange userNameChange = (NetworkMessage.UserNameChange) data;

        for (Room room : ChatClient.get().getRooms().values()) {
            room.getUsers().values().stream()
                    .filter(user -> user.getID().equals(userNameChange.getUserId()))
                    .forEach(user -> Platform.runLater(() ->
                            Main.UIcontrol.controllerUsers.updateUsername(room.getRoomName(), userNameChange)));
        }
    }

    public void receivedClientConnected(NetworkMessage.ClientConnect event) {
        ChatClient.get().getRooms().forEach((roomName, room) -> room.getUsers().values().forEach(user -> {
            if (user.getID().equals(event.userId)) {
                user.setOnlineStatus(true);
            }
        }));

        for (Room room : ChatClient.get().getRooms().values()) {
            room.getUsers().values().stream()
                    .filter(user -> user.getID().equals(event.userId))
                    .forEach(user ->
                            Platform.runLater(() -> Main.UIcontrol.controllerUsers.userConnected(room.getRoomName(), event.userId)));
        }
    }

    public void printDataToRooms(Room room) {
        room.getMessages().forEach(msg ->
                Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(msg)));

        room.getUsers().values().forEach(u -> {
            Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(u, room.getRoomName()));
        });
    }

}
