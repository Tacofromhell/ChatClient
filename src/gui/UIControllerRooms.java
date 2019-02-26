package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import network.ChatClient;

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

    public void printNewJoinedRoom(String room) {
        Button b = new Button(room);
        b.setId(room);
        b.setStyle("-fx-background-color: lightgray");
        b.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            this.roomButtonsHolder.getChildren().forEach(roomCircle -> roomCircle.setStyle("-fx-background-color: lightgray"));
            b.setStyle("-fx-background-color: lightseagreen");
            controllerRooms.switchContent(b.getId());
        });
        roomButtons.putIfAbsent(room, b);
        this.roomButtonsHolder.getChildren().add(b);
    }
}
