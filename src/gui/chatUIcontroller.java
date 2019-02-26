package gui;

import data.NetworkMessage;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import network.ChatClient;
import data.Message;
import data.Room;
import data.User;
import network.SocketStreamHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class chatUIcontroller {

    public Map<String, VBox> VBoxRoomsMessages = new HashMap<>();
    public Map<String, VBox> VBoxRoomsUsers = new HashMap<>();
    public Map<String, Button> roomButtons = new HashMap<>();

    public static UIControllerMessages controllerMessages;
    public static UIControllerRooms controllerRooms;
    public static UIControllerUsers controllerUsers;

    @FXML
    HBox roomButtonsHolder;
    @FXML
    ScrollPane scrollMessages;
    @FXML
    ScrollPane scrollUsers;
    @FXML
    TextField userInputNewMessage;
    @FXML
    TextField newUsername;
    @FXML
    Button sendMessage;


    public void initialize() {
        controllerUsers = new UIControllerUsers();
        controllerRooms = new UIControllerRooms(roomButtonsHolder);
        controllerMessages = new UIControllerMessages();
    }

    public void initRooms() {

        Button addRoomButton = new Button("+");
        addRoomButton.setId("addRoom");
        addRoomButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            // TODO: add room
            SocketStreamHelper.sendData(
                    new NetworkMessage.RoomCreate("test", true),
                    ChatClient.get().getDataOut());
        });
        roomButtonsHolder.getChildren().add(addRoomButton);

        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
            VBox tempMsg = new VBox();
            tempMsg.setId(room);
            VBoxRoomsMessages.putIfAbsent(room, tempMsg);
            VBox tempUsr = new VBox();
            tempUsr.setId(room);
            VBoxRoomsUsers.putIfAbsent(room, tempUsr);

            controllerRooms.printNewJoinedRoom(room);

        }
        roomButtonsHolder.getChildren().forEach(roomButton -> {
            if (roomButton.getId().equals(ChatClient.get().getCurrentUser().getActiveRoom())) {
                roomButton.setStyle("-fx-background-color: lightseagreen");

            } else {
                roomButton.setStyle("-fx-background-color: lightgray");
            }
        });

        scrollMessages.setContent(VBoxRoomsMessages.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
        scrollUsers.setContent(VBoxRoomsUsers.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));

    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        if (userInputNewMessage.getText().trim().length() > 0)
            ChatClient.get().sendMessageToServer(userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());

        userInputNewMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            sendMessageButton();
        }
    }

    public void sendNewUsernameButton() {
        if (newUsername.getText().trim().length() > 0) {
            ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
            System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
            SocketStreamHelper.sendData(
                    new NetworkMessage.UserNameChange(
                            ChatClient.get().getCurrentUser().getUsername(),
                            ChatClient.get().getCurrentUser().getID()), ChatClient.get().getDataOut());

        }
        newUsername.setText("");
    }

    public void sendNewUsernameEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            sendNewUsernameButton();
        }

    }
}
