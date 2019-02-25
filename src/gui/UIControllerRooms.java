package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import network.ChatClient;

public class UIControllerRooms extends chatUIcontroller{
    @FXML
    ScrollPane scrollMessages;
    @FXML
    ScrollPane scrollUsers;

//    public void initRooms() {
//        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
//            VBox tempMsg = new VBox();
//            tempMsg.setId(room);
//            VBoxRoomsMessages.putIfAbsent(room, tempMsg);
//            VBox tempUsr = new VBox();
//            tempUsr.setId(room);
//            VBoxRoomsUsers.putIfAbsent(room, tempUsr);
//
//            Button b = new Button(room);
//            b.setId(room);
//            b.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
//                roomButtonsHolder.getChildren().forEach(roomCircle -> roomCircle.setStyle("-fx-background-color: lightgray"));
//                b.setStyle("-fx-background-color: lightseagreen");
//                switchContent(b.getId());
//            });
//            roomButtonsHolder.getChildren().add(b);
//        }
//        roomButtonsHolder.getChildren().forEach(roomButton -> {
//            if (roomButton.getId().equals(ChatClient.get().getCurrentUser().getActiveRoom())) {
//                roomButton.setStyle("-fx-background-color: lightseagreen");
//
//            } else {
//                roomButton.setStyle("-fx-background-color: lightgray");
//            }
//        });
//
//        scrollMessages.setContent(VBoxRoomsMessages.get(
//                ChatClient.get().getCurrentUser().getActiveRoom()
//        ));
//        scrollUsers.setContent(VBoxRoomsUsers.get(
//                ChatClient.get().getCurrentUser().getActiveRoom()
//        ));
//    }

    public void switchContent(String room) {
        scrollMessages.setContent(VBoxRoomsMessages.get(room));
        scrollUsers.setContent(VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
    }
}
