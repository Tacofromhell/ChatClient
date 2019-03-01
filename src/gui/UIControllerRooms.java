package gui;

import data.NetworkMessage;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import network.ChatClient;
import network.SocketStreamHelper;

public class UIControllerRooms extends chatUIcontroller {
    VBox roomLabelsHolder;

    public UIControllerRooms(VBox roomLabelsHolder) {
        this.roomLabelsHolder = roomLabelsHolder;
    }

    public void switchContent(String room) {
        Main.UIcontrol.scrollMessages.setContent(Main.UIcontrol.VBoxRoomsMessages.get(room));
        Main.UIcontrol.scrollUsers.setContent(Main.UIcontrol.VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
        SocketStreamHelper.sendData(new NetworkMessage.UserActiveRoom(room), ChatClient.get().getDataOut());
    }

    public void activeRoomColor(String activeRoom) {
        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
            Label roomLabel = (Label) this.roomLabelsHolder.lookup("#" + room);
            if (roomLabel != null) {
                roomLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 13px; -fx-cursor: hand");
                roomLabel.setTextFill(Color.BLACK);
            }
        }
        Label roomLabel = (Label) this.roomLabelsHolder.lookup("#" + activeRoom);
        if (roomLabel != null) {
            roomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand");
            roomLabel.setTextFill(Color.LIGHTSEAGREEN);
        }
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

        Label item = new Label(room);
        item.setId(room);
        item.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;" +
                "-fx-cursor: hand");
        item.setTextFill(Color.LIGHTSEAGREEN);
        item.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                if (!item.getId().equals("general"))
                    Main.UIcontrol.tooltip.show(item, e.getScreenX(), e.getScreenY());
            } else {
                activeRoomColor(room);
                switchContent(item.getId());
            }
        });
        roomLabelsHolder.getChildren().add(item);
    }

    public void printNewPublicRoom(String room) {
        Label newRoom = new Label(room);
        newRoom.setId(room);
        newRoom.setStyle("-fx-font-size: 13px; -fx-cursor: hand");
        newRoom.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            switchContent(room);
            SocketStreamHelper.sendData(
                    new NetworkMessage.RoomJoin(newRoom.getId(), ChatClient.get().getCurrentUser(), null, false),
                    ChatClient.get().getDataOut());

            // removes when user joins
            removePublicRoom(room);
            Main.UIcontrol.roomTabs
                    .getSelectionModel().select(0);
        });
        Main.UIcontrol.publicRooms.getChildren().add(newRoom);
    }

    public void removePublicRoom(String targetRoom) {
        Label item = (Label) Main.UIcontrol.publicRooms.lookup("#" + targetRoom);
        Main.UIcontrol.publicRooms.getChildren().remove(item);
    }
}
