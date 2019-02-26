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
//        Platform.runLater(() -> Main.UIcontrol.initRooms());
    }

    public void receivedClientDisconnected(NetworkMessage.ClientDisconnect data) {
        System.out.println("user disconnected");
        ChatClient.get().getRooms().forEach((roomName, room) -> room.getUsers().values().forEach(user -> {
            if (user.getID().equals(data.userId)) {
                user.setOnlineStatus(false);
            }
        }));
        Platform.runLater(() -> Main.UIcontrol.controllerUsers.userDisconnected(data.userId));
    }

    public void receivedRoomCreated(NetworkMessage.RoomCreate data) {
        if (!Main.UIcontrol.publicRooms.getItems().contains(data.getRoomName()))
            Platform.runLater(() -> Main.UIcontrol.controllerRooms.printNewPublicRoom(data.getRoomName()));
    }

    public void receivedRoomDeleted(Object data) {
    }

    public void receivedRoom(Object data) {

        Room room = (Room) data;
        ChatClient.get().addRoom(room);
        // print rooms messages on connection
//        room.getMessages().forEach(msg ->
//                Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(msg)));
//
//        room.getUsers().values().forEach(user -> {
//            Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(user, room.getRoomName()));
//        });
    }

    public void receivedUserJoinedRoom(String targetRoom, User user, Room room) {

        // if user == this client, add room to joined rooms
        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            ChatClient.get().getCurrentUser().addJoinedRoom(targetRoom);

            ChatClient.get().addRoom(room);

            Platform.runLater(() -> Main.UIcontrol.controllerRooms.printNewJoinedRoom(targetRoom));

            room.getMessages().forEach(msg ->
                    Platform.runLater(() -> Main.UIcontrol.controllerMessages.printMessageFromServer(msg)));

            room.getUsers().values().forEach(u -> {
                Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(u, targetRoom));
            });

            // switch room when joining
            Platform.runLater(() -> Main.UIcontrol.controllerRooms.switchContent(targetRoom));

        } else {
            ChatClient.get().getRooms().get(targetRoom).addUserToRoom(user);
            Platform.runLater(() -> Main.UIcontrol.controllerUsers.printUsers(user, targetRoom));
        }

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

        Platform.runLater(() -> Main.UIcontrol.controllerUsers.updateUsername(userNameChange));
    }

    public void receivedClientConnected(NetworkMessage.ClientConnect event) {
        ChatClient.get().getRooms().forEach((roomName, room) -> room.getUsers().values().forEach(user -> {
            if (user.getID().equals(event.userId)) {
                user.setOnlineStatus(true);
            }
        }));
        Platform.runLater(() -> Main.UIcontrol.controllerUsers.userConnected(event.userId));
    }
}
