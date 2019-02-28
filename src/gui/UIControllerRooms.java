package gui;

import data.NetworkMessage;
import javafx.scene.control.*;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import network.ChatClient;
import network.SocketStreamHelper;

import java.util.Set;

public class UIControllerRooms extends chatUIcontroller {
    HBox roomButtonsHolder;

    public UIControllerRooms(HBox roomButtonsHolder) {
        this.roomButtonsHolder = roomButtonsHolder;
    }

    public void switchContent(String room) {
        Main.UIcontrol.scrollMessages.setContent(Main.UIcontrol.VBoxRoomsMessages.get(room));
        Main.UIcontrol.scrollUsers.setContent(Main.UIcontrol.VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
    }

    public void activeRoomColor(String activeRoom) {

        for (var lastRoom : this.roomButtonsHolder.getChildren()) {
            lastRoom.setStyle("-fx-background-color: lightgray");
        }

        Button roomButton = (Button) this.roomButtonsHolder.lookup("#" + activeRoom);
        roomButton.setStyle("-fx-background-color: lightseagreen");
    }

    public void addRoomContent(String room) {
        // add placeholder for messages
        VBox VBoxMessages = new VBox();
        VBoxMessages.setId(room);
        Main.UIcontrol.VBoxRoomsMessages.putIfAbsent(room, VBoxMessages);

        // add placeholder for users
        VBox VBoxUsers = new VBox();
        VBoxUsers.setId(room);
        Main.UIcontrol.VBoxRoomsUsers.putIfAbsent(room, VBoxUsers);
    }

    public void printNewJoinedRoom(String room) {
        addRoomContent(room);

        Button b = new Button(room);
        b.setId(room);
        b.setStyle("-fx-background-color: lightgray");
        b.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                if (!b.getId().equals("general"))
                    Main.UIcontrol.tooltip.show(b, e.getScreenX(), e.getScreenY());
            } else {
                this.roomButtonsHolder.getChildren().forEach(roomCircle ->
                        roomCircle.setStyle("-fx-background-color: lightgray"));
                b.setStyle("-fx-background-color: lightseagreen");
                switchContent(b.getId());
            }
        });

        roomButtons.putIfAbsent(room, b);
        this.roomButtonsHolder.getChildren().add(b);
    }

    public void printNewPublicRoom(String room) {
        MenuItem newRoom = new MenuItem(room);
        newRoom.setId(room);
        newRoom.setOnAction(e -> {
            SocketStreamHelper.sendData(
                    new NetworkMessage.RoomJoin(newRoom.getId(), ChatClient.get().getCurrentUser(), null),
                    ChatClient.get().getDataOut());

            // removes when user joins
            removePublicRoom(room);
            switchContent(room);
        });
        Main.UIcontrol.publicRooms.getItems().add(newRoom);
    }

    @SuppressWarnings("all")
    public void removePublicRoom(String targetRoom) {
        MenuItem item = null;

        for (MenuItem room : Main.UIcontrol.publicRooms.getItems()) {
            if (room.getId().equals(targetRoom)) {
                item = room;
            }
        }

        Main.UIcontrol.publicRooms.getItems().remove(item);
    }
}
