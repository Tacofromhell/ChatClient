package gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import network.ChatClient;
import network.Message;
import network.Room;
import network.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class chatUIcontroller {

    public Map<String, VBox> VBoxRoomsMessages = new HashMap<>();
    public Map<String, VBox> VBoxRoomsUsers = new HashMap<>();

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
    @FXML
    VBox users;
    @FXML
    Text currentUsername;
    @FXML
    Button changeUser_btn;

    //REDUNDANT: Moved to User.java
    //String activeRoom = "general";

    public void initialize() {




        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
            VBox tempMsg = new VBox();
            tempMsg.setId(room);
            VBoxRoomsMessages.putIfAbsent(room, tempMsg);
            VBox tempUsr = new VBox();
            tempUsr.setId(room);
            VBoxRoomsUsers.putIfAbsent(room, tempUsr);

            Button b = new Button(room);
            b.setOnAction((ActionEvent e) -> switchContent(b.getText()));
            roomButtonsHolder.getChildren().add(b);
        }

        scrollMessages.setContent(VBoxRoomsMessages.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
        scrollUsers.setContent(VBoxRoomsUsers.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));

        System.out.println(Thread.currentThread().toString());

        for(int i = 0; i < 10; i++){
            printUsers(i, "general");
        }

        for(int i = 0; i < 5; i++){
            printUsers(i, "other room");
        }

    }

    public void printMessageFromServer(Message msg) {
        HBox messageContainer = new HBox();
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        Label messageToPrint = new Label(msg.getTimestamp() + " " + msg.getUser().getUsername() + ": " + msg.getMsg());
        messageToPrint.setPadding(new Insets(2, 5, 2, 5));
        messageToPrint.setStyle("-fx-background-color: honeydew; -fx-background-radius: 5px;");
        messageToPrint.setWrapText(true);
        messageToPrint.setMinHeight(Control.USE_PREF_SIZE);
        messageToPrint.setMaxWidth(300);
        messageContainer.getChildren().add(messageToPrint);

        if (msg.getUser().getUser().getID().equals(ChatClient.get().getCurrentUser().getID())) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageToPrint.setStyle("-fx-background-color: #27d5ff; -fx-background-radius: 5px;");
        }

        messageContainer.setMargin(messageToPrint, new Insets(5, 5, 0, 5));
        VBoxRoomsMessages.get(msg.getRoom()).getChildren().add(messageContainer);
        messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> scrollMessages.setVvalue((Double) newValue));
    }

    public void switchContent(String room){
        scrollMessages.setContent(VBoxRoomsMessages.get(room));
        scrollUsers.setContent(VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        if (userInputNewMessage.getText().trim().length() > 0)
            ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());

        userInputNewMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            if (userInputNewMessage.getText().trim().length() > 0)
                ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());

            userInputNewMessage.setText("");
        }
    }

    public void sendNewUsernameButton() {
        if (newUsername.getText().trim().length() > 0) {
            ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
            System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
            ChatClient.get().sendUserToServer();
        }

        newUsername.setText("");
//        updateUsername();
    }

    public void sendNewUsernameEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {

            if (newUsername.getText().trim().length() > 0) {
                ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
                System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
                ChatClient.get().sendUserToServer();
            }

            newUsername.setText("");
//            updateUsername();
        }

    }

    public void printUsers(int i, String room) {
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic = new Circle(10, Color.LIGHTGRAY);
        Label userName = new Label(room + i);
        userName.setStyle("-fx-text-fill: black;" +
                "-fx-pref-width: 100px;");

        onlineUser.getChildren().addAll(userPic, userName);
        onlineUser.setMargin(userPic, new Insets(5, 0, 5, 3));

        VBoxRoomsUsers.get(room).getChildren().add(onlineUser);
    }

    public void updateUsername() {
        currentUsername.setText(ChatClient.get().getCurrentUser().getUsername());
    }


}
