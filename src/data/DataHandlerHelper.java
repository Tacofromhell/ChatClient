package data;

import gui.Main;
import javafx.application.Platform;
import network.ChatClient;
import network.SocketStreamHelper;

import static gui.chatUIcontroller.controllerMessages;
import static gui.chatUIcontroller.controllerRooms;
import static gui.chatUIcontroller.controllerUsers;

public class DataHandlerHelper {
    private boolean firstLogin = false;

    public void receivedMessage(Object data) {
        System.out.println("Received a: " + data);
        Message incoming = (Message) data;
        //Just for print in terminal
        String msg = incoming.getRoom() + ": " + incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
        System.out.println(msg);
        //Send incoming message and currentUser to javaFX
        Platform.runLater(() -> controllerMessages.printMessageFromServer(incoming));
    }

    public void receivedUser(Object data) {
        System.out.println("Received a: " + data);
        ChatClient.get().setCurrentUser((User) data);

        SocketStreamHelper.sendData(new NetworkMessage.ClientConnect(ChatClient.get().getCurrentUser().getID()), ChatClient.get().getDataOut());
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
                            Platform.runLater(() -> controllerUsers.userDisconnected(room.getRoomName(), data.userId)));
        }
    }

    public void receivedRoomCreated(NetworkMessage.RoomCreate data) {
        Platform.runLater(() -> controllerRooms.printNewPublicRoom(data.getRoomName()));
    }

    public void receivedRoomDeleted(NetworkMessage.RoomDelete data) {
        Platform.runLater(() -> controllerRooms.removePublicRoom(data.targetRoom));
    }

    public void receivedUserJoinedRoom(NetworkMessage.RoomJoin data) {

        // if user == this client, add room to joined rooms
        if (ChatClient.get().getCurrentUser().getID().equals(data.user.getID())) {
            ChatClient.get().getCurrentUser().addJoinedRoom(data.targetRoom);

            ChatClient.get().addRoom(data.room);

            Platform.runLater(() -> controllerRooms.printNewJoinedRoom(data.targetRoom));
            // highlight active room color
            Platform.runLater(() -> controllerRooms.activeRoomColor(
                    data.firstConnection ? data.user.getActiveRoom() : data.targetRoom));
            // switch room when joining
            Platform.runLater(() -> controllerRooms.switchContent(
                    data.firstConnection ? data.user.getActiveRoom() : data.targetRoom
            ));

            printDataToRooms(data.room);

        } else {
            if (!ChatClient.get().getRooms().get(data.targetRoom).getUsers().containsKey(data.user.getID())) {
                ChatClient.get().getRooms().get(data.targetRoom).addUserToRoom(data.user);
                Platform.runLater(() -> controllerUsers.printUsers(data.user, data.targetRoom));
            }
        }
        // need to init rooms at correct lifecycle hook
        if (!firstLogin) {
            Platform.runLater(() -> Main.UIcontrol.initRooms());
            firstLogin = true;
        }
    }

    public void receivedUserLeftRoom(NetworkMessage.RoomLeave data) {
        ChatClient.get().getRooms().get(data.targetRoom).getUsers().remove(data.userId);

        if (ChatClient.get().getRooms().get(data.targetRoom).getUsers().size() > 0) {
            Platform.runLater(() -> controllerUsers.removeUserFromRoom(data.targetRoom, data.userId));
        }
    }

    public void receivedRoomNameExists() {
        Platform.runLater(() -> Main.UIcontrol.setErrorMessage("Roomname already exists!"));
    }

    public void receivedUserChangedName(Object data) {
        System.out.println("User changed name");
        NetworkMessage.UserNameChange userNameChange = (NetworkMessage.UserNameChange) data;

        for (Room room : ChatClient.get().getRooms().values()) {
            room.getUsers().values().stream()
                    .filter(user -> user.getID().equals(userNameChange.getUserId()))
                    .forEach(user -> Platform.runLater(() ->
                            controllerUsers.updateUsername(room.getRoomName(), userNameChange)));
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
                            Platform.runLater(() -> controllerUsers.userConnected(room.getRoomName(), event.userId)));
        }
    }

    public void printDataToRooms(Room room) {
        room.getMessages().forEach(msg -> Platform.runLater(() -> controllerMessages.printMessageFromServer(msg)));

        room.getUsers().values().forEach(u -> Platform.runLater(() -> controllerUsers.printUsers(u, room.getRoomName())));
    }
}
