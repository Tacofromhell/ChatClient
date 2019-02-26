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
    TextField newRoom;
    public MenuButton publicRooms;
    public ContextMenu tooltip = new ContextMenu();


    public void initialize() {
        controllerUsers = new UIControllerUsers();
        controllerRooms = new UIControllerRooms(roomButtonsHolder);
        controllerMessages = new UIControllerMessages();

        //TODO: add function to tooltip
        MenuItem leaveRoomButton = new MenuItem("Leave room");
        leaveRoomButton.setOnAction(e -> System.out.println("left room"));
        tooltip.getItems().add(leaveRoomButton);

        publicRooms = new MenuButton();
        roomButtonsHolder.getChildren().add(publicRooms);

        Button addRoomButton = new Button("\uD83D\uDFA6");
        addRoomButton.setId("addRoom");
        addRoomButton.setStyle("-fx-background-color: lightgray");
        addRoomButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
            // TODO: add room
//            controllerRooms.printNewJoinedRoom(newRoom.getText());

//            SocketStreamHelper.sendData(
//                    new NetworkMessage.RoomJoin(newRoom.getId(), ChatClient.get().getCurrentUser(), null),
//                    ChatClient.get().getDataOut());

            SocketStreamHelper.sendData(
                    new NetworkMessage.RoomCreate(newRoom.getText(), true),
                    ChatClient.get().getDataOut());

            newRoom.setText("");
        });
        roomButtonsHolder.getChildren().add(addRoomButton);

        newRoom = new TextField();
        newRoom.setPromptText("Roomname");
        newRoom.setPrefWidth(80);
        roomButtonsHolder.getChildren().add(newRoom);


    }

    public void initRooms() {

//        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
//            controllerRooms.addRoomContent(room);
//
//            controllerRooms.printNewJoinedRoom(room);
//        }
        // highlight active room
        Button roomButton = (Button) roomButtonsHolder.lookup("#" + ChatClient.get().getCurrentUser().getActiveRoom());
        roomButton.setStyle("-fx-background-color: lightseagreen");

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
