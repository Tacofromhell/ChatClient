package gui;

import data.NetworkMessage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import network.ChatClient;
import data.User;
import network.SocketStreamHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class chatUIcontroller {

    public Map<String, VBox> VBoxRoomsMessages = new HashMap<>();
    public Map<String, VBox> VBoxRoomsUsers = new HashMap<>();

    public static UIControllerMessages controllerMessages;
    public static UIControllerRooms controllerRooms;
    public static UIControllerUsers controllerUsers;

    private Stage stage;

    @FXML
    public Tab joinedTab;
    @FXML
    public TabPane roomTabs;
    @FXML
    public VBox roomLabelsHolder;
    @FXML
    HBox addNewRoomHolder;
    @FXML
    ScrollPane scrollMessages;
    @FXML
    ScrollPane scrollUsers;
    @FXML
    TextField userInputNewMessage;
    @FXML
    TextField newUsername;
    @FXML
    HBox errorHolder;
    @FXML
    Button sendMessage;
    TextField newRoom;
    Label roomCreateError = new Label();
    @FXML
    public VBox publicRooms;
    public ContextMenu tooltip = new ContextMenu();


    public void initialize() {
        controllerUsers = new UIControllerUsers();
        controllerRooms = new UIControllerRooms(roomLabelsHolder);
        controllerMessages = new UIControllerMessages();

        //TODO: add function to tooltip
        MenuItem leaveroomLabel = new MenuItem("Leave room");
        leaveroomLabel.setOnAction(e -> {
            String name = tooltip.getOwnerNode().getId();
            System.out.println("left: " + name);
            leaveRoom(name);
        });
        tooltip.getItems().add(leaveroomLabel);

        newRoom = new TextField();
        newRoom.setPromptText("Create room");
        newRoom.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (newRoom.getText().matches("^[\\wåäöÅÄÖ-]{3,10}$")) {
                if (e.getCode() == KeyCode.ENTER) {
                    createPublicRoom(newRoom.getText());
                } else {
                    setErrorMessage("");
                }
            } else {
                setErrorMessage(newRoom.getText().length() > 0 ?
                        "Roomname must be 3-10 characters long" : "");
            }
        });
        addNewRoomHolder.getChildren().addAll(newRoom);
        errorHolder.getChildren().add(roomCreateError);
    }

    public void initRooms() {

        // highlight active room
        Label roomLabel = (Label) roomLabelsHolder.lookup("#" + ChatClient.get().getCurrentUser().getActiveRoom());
        if (roomLabel != null) {
            roomLabel.setStyle("-fx-font-weight: bold");
            roomLabel.setTextFill(Color.LIGHTSEAGREEN);
        }
        scrollMessages.setContent(VBoxRoomsMessages.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
        scrollUsers.setContent(VBoxRoomsUsers.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
    }

    public void leaveRoom(String roomName) {

        User currentUser = ChatClient.get().getCurrentUser();

        // remove joined room from user
        currentUser.removeJoinedRoom(roomName);
        ChatClient.get().getRooms().remove(roomName);

        // find selected room
        Label roomLabel = (Label) roomLabelsHolder.lookup("#" + roomName);
        roomLabelsHolder.getChildren().remove(roomLabel);

        // clear nodes
        VBoxRoomsMessages.remove(roomName);
        VBoxRoomsUsers.remove(roomName);

        // switch room and re-add room to public rooms
        controllerRooms.switchContent("general");
        controllerRooms.activeRoomColor("general");
        controllerRooms.printNewPublicRoom(roomName);

        SocketStreamHelper.sendData(new NetworkMessage.RoomLeave(roomName, currentUser.getID()), ChatClient.get().getDataOut());
    }

    public void setErrorMessage(String errorMessage) {
        roomCreateError.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");
        roomCreateError.setTextFill(Color.RED);
        roomCreateError.setText(errorMessage);
    }

    public void createPublicRoom(String roomName) {
        if (roomName.trim().length() > 0) {
            SocketStreamHelper.sendData(
                    new NetworkMessage.RoomCreate(roomName, true),
                    ChatClient.get().getDataOut());
        }
        newRoom.setText("");
    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        if (userInputNewMessage.getText().trim().length() > 0 && userInputNewMessage.getText().trim().length() < 500) {
            setErrorMessage("");
            ChatClient.get().sendMessageToServer(userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());
            userInputNewMessage.setText("");
        } else {
            setErrorMessage("Message must be between 1 and 500 characters");
        }
    }

    public void sendImageButton(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            System.out.println(file.toURI().toString());
            try {
                byte[] image = Files.readAllBytes(file.toPath());
                ChatClient.get().sendImageToServer(ChatClient.get().getCurrentUser().getActiveRoom(), image);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Image image1 = new Image(file.toURI().toString());
        }

    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            sendMessageButton();
        }
    }

    public void sendNewUsernameButton() {
        if (newUsername.getText().trim().length() > 0) {
            if (newUsername.getText().matches("^[\\wåäöÅÄÖ-]{3,10}$")) {
                ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
                System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
                SocketStreamHelper.sendData(
                        new NetworkMessage.UserNameChange(
                                ChatClient.get().getCurrentUser().getUsername(),
                                ChatClient.get().getCurrentUser().getID()), ChatClient.get().getDataOut());
                newUsername.setText("");
            } else
                setErrorMessage("Username must be 3-10 characters long");
        }
    }

    public void sendNewUsernameEnter(KeyEvent key) {
        setErrorMessage("");
        if (key.getCode().equals(KeyCode.ENTER)) {
            sendNewUsernameButton();
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}
